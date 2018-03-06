package org.talend.dataquality.datamasking.shuffling;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a handler of {@link ShuffleColumn} who has an internal class implemented runnable to call the shuffle
 * method in the {@link ShuffleColumn} DOC qzhao class global comment.
 */
public class ShufflingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShufflingHandler.class);

    protected ShufflingService shufflingService;

    protected AsynchronizedOutputRunnable runnable = null;

    protected Queue<List<List<Object>>> resultQueue;

    protected Thread t = null;

    /**
     * Constructor
     * 
     * @param shufflingService ShufflingService object
     * @param resultQueue a queue with separated table
     */
    public ShufflingHandler(ShufflingService shufflingService, Queue<List<List<Object>>> resultQueue) {
        super();
        this.shufflingService = shufflingService;
        this.resultQueue = (resultQueue == null) ? new ConcurrentLinkedQueue<List<List<Object>>>() : resultQueue;
    }

    class AsynchronizedOutputRunnable implements Runnable {

        @Override
        public void run() {
            try {
                ConcurrentLinkedQueue<Future<List<List<Object>>>> queue = shufflingService.getConcurrentQueue();
                while (!shufflingService.hasFinished() || !queue.isEmpty()) {
                    if (queue.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }
                    Future<List<List<Object>>> future = queue.poll();
                    List<List<Object>> rows = future.get();
                    resultQueue.add(rows);
                }
            } catch (InterruptedException | ExecutionException | NullPointerException e) {
                LOGGER.error(e.getMessage(), e);
                shufflingService.shutDown();
            }

        }

    }

    public void start() {
        if (runnable == null) {
            runnable = new AsynchronizedOutputRunnable();
        }
        t = new Thread(runnable);
        t.start();
    }

    public void join() {
        try {
            t.join();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            // clean up state...
            Thread.currentThread().interrupt();
        }

    }

}
