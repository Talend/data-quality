package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class offers a shuffling service to manipulates the {@link ShuffleColumn} action and the {@link ShufflingHandler} action
 * together.
 */
public class ShufflingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShufflingService.class);

    protected ConcurrentLinkedQueue<Future<List<List<Object>>>> concurrentQueue = new ConcurrentLinkedQueue<>();

    protected ShufflingHandler shufflingHandler;

    protected List<List<String>> shuffledColumns;

    protected List<String> allInputColumns;

    protected List<String> partitionColumns;

    protected ExecutorService executor;

    protected long seed;

    protected boolean hasSeed;

    private List<List<Object>> rows = new ArrayList<>();

    private int seperationSize = Integer.MAX_VALUE;

    private boolean hasLaunched = false;

    private boolean hasFinished = false;

    private boolean hasSubmitted = false;

    /**
     * Constructor without the partition choice
     * 
     * @param shuffledColumns the 2D list of shuffled columns
     * @param allInputColumns the list of all input columns name
     * @throws IllegalArgumentException when the some columns in the shuffledColumns do not exist in the allInputColumns
     */
    public ShufflingService(List<List<String>> shuffledColumns, List<String> allInputColumns) {
        this(shuffledColumns, allInputColumns, null);
    }

    public ShufflingService(List<List<String>> shuffledColumns, List<String> allInputColumns, List<String> partitionColumns) {
        this.shuffledColumns = shuffledColumns;
        this.allInputColumns = allInputColumns;
        this.partitionColumns = partitionColumns;
    }

    public void setShufflingHandler(ShufflingHandler shufflingHandler) {
        this.shufflingHandler = shufflingHandler;
    }

    /**
     * Executes a row list value.<br>
     * 
     * The row is not executed immediately but is submitted to the {@link java.util.concurrent.ExecutorService}.<br>
     * The results will be retrieved from the {@link java.util.concurrent.Future} objects which are appended to a
     * {@link java.util.concurrent.ConcurrentLinkedQueue}<br>
     * 
     * If the variable hasFinished equals true, it means this service has been closed. Tests whether the rows is empty
     * or not. If the rows have still the values, submits those values to a callable process.<br>
     * 
     * If the variable hasFinished equals false, adds the new value into the rows. Tests whether the rows' size equals
     * the partition demand. When the size equals the partition size, submits those values to a callable process.<br>
     * 
     * @param row
     */
    protected synchronized void execute(List<Object> row) {
        launcheHandler();
        if (hasSubmitted) {
            if (!rows.isEmpty()) {
                executeFutureCall();
            }
        } else {
            if (!row.isEmpty()) {
                rows.add(row);
                if (rows.size() == seperationSize) {
                    executeFutureCall();
                }
            }
        }
    }

    /**
     * Deep copies the rows value to another 2D list. Submits the rows' value to a callable process. Then submits the
     * process to the executor.
     */
    private void executeFutureCall() {
        List<List<Object>> copyRow = deepCopyListTo(rows);
        ShuffleColumn shuffle = new ShuffleColumn(shuffledColumns, allInputColumns, partitionColumns);
        if (hasSeed) {
            shuffle.setRandomSeed(seed);
        }
        Future<List<List<Object>>> future = executor.submit(new RowDataCallable<List<List<Object>>>(shuffle, copyRow));
        concurrentQueue.add(future);
    }

    private void launcheHandler() {
        if (!hasLaunched) {
            shufflingHandler.start();
            hasLaunched = true;
        }

        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        } else {
            if (executor.isShutdown()) {
                throw new IllegalArgumentException("executor shutdown"); //$NON-NLS-1$
            }
        }
    }

    private synchronized List<List<Object>> deepCopyListTo(List<List<Object>> parameterRows) {
        List<List<Object>> copyRows = new ArrayList<>(parameterRows.size());
        for (List<Object> row : parameterRows) {
            List<Object> copyRow = new ArrayList<>(row.size());
            for (Object o : row) {
                copyRow.add(o);
            }
            copyRows.add(copyRow);
        }
        parameterRows.clear();
        return copyRows;

    }

    public void setSeperationSize(int seperationSize2) {
        this.seperationSize = seperationSize2;
    }

    /**
     * Adds a new row in the waiting list and check the size of waiting list. When the waiting list fulfills the
     * partition size then launches the shuffle algorithm.
     * 
     * @param row a list of row data
     */
    public void addOneRow(List<Object> row) {
        execute(row);
    }

    public Queue<Future<List<List<Object>>>> getConcurrentQueue() {
        return concurrentQueue;
    }

    /**
     * Gets the hasFinished.
     * 
     * @return hasFinished
     */
    public boolean hasFinished() {
        return hasFinished;
    }

    /**
     * <ul>
     * <li>First sets the hasSubmitted variable to be true and launches the execute() method with the global variable hasSubmitted
     * equals true. This allows the resting rows to be submitted to a callable process.
     * <li>To avoid the handler stopping scanning the result, lets the thread sleep 100 miliseconds. This allows the last callable
     * job to stand by</li>
     * <li>Sets the hasFinished variable true to announce the handler to finish the scan</li>
     * </ul>
     * 
     * @param hasFinished
     */
    public void setHasFinished(boolean hasFinished) {
        this.hasSubmitted = hasFinished;
        execute(new ArrayList<>());
        this.hasFinished = hasFinished;
        shufflingHandler.join();
    }

    /**
     * Sets the a table value directly by giving a 2D list.
     * 
     * @param rows list of list of object
     */
    public void setRows(List<List<Object>> rows) {
        for (List<Object> row : rows) {
            execute(row);
        }

    }

    /**
     * Shuts down the shuffling execution
     */
    public void shutDown() {
        if (executor != null) {
            try {
                executor.shutdown();
                while (!concurrentQueue.isEmpty()) {
                    Thread.sleep(200);
                }
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                // clean up state...
                Thread.currentThread().interrupt();
            }
        }

    }

    public void setRandomSeed(long seed) {
        this.hasSeed = true;
        this.seed = seed;
    }

}
