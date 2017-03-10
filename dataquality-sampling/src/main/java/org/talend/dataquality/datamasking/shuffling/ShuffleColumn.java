package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The class ShuffleColumn defines the basic common methods used in the "shuffling" functions. As with shuffling, this
 * technique is effective only on a large data set.<br>
 * DOC qzhao class global comment.
 */
public class ShuffleColumn {

    private static final int[] PRIME_NUMBERS = { 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
            97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223,
            227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353,
            359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491,
            499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643,
            647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809,
            811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967,
            971, 977, 983, 991, 997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069 };

    private List<List<Integer>> numColumns = new ArrayList<List<Integer>>();

    private List<Integer> partitionColumns = new ArrayList<Integer>();

    private List<String> allInputColumns = new ArrayList<String>();

    private Random random = new Random();

    /**
     * Constructor without the partition choice
     * 
     * @param shuffledColumns the 2D list of shuffled columns
     * @param allInputColumns the list of all input columns name
     */
    public ShuffleColumn(List<List<String>> shuffledColumns, List<String> allInputColumns) {
        this.allInputColumns = allInputColumns;
        this.numColumns = getNumColumn(shuffledColumns);
    }

    /**
     * ShuffleColumn constructor comment.
     * 
     * @param shuffledColumns the 2D list of shuffled columns
     * @param allInputColumns the list of all input columns name
     * @param partitionColumns the partitioned columns names
     */
    public ShuffleColumn(List<List<String>> shuffledColumns, List<String> allInputColumns, List<String> partitionColumns) {
        this.allInputColumns = allInputColumns;
        this.numColumns = getNumColumn(shuffledColumns);
        this.partitionColumns = partitionColumns == null ? null : getPartitionIndex(partitionColumns);
    }

    /**
     * Comment method "shuffle".
     * 
     * @param rows a list of partitioned rows
     */
    public void shuffle(List<List<Object>> rows) {
        if (partitionColumns == null || partitionColumns.isEmpty()) {
            shuffleTable(rows);
        } else {
            shuffleColumnWithPartition(rows);
        }
    }

    private List<List<Integer>> getNumColumn(List<List<String>> shuffledColumns) {
        if (shuffledColumns.isEmpty() || shuffledColumns == null)
            throw new IllegalArgumentException("At least one column name should be given");
        List<Integer> noDuplicated = new ArrayList<Integer>();
        for (List<String> subList : shuffledColumns) {
            List<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < subList.size(); i++) {
                int index = allInputColumns.indexOf(subList.get(i));
                if (index != -1 && !noDuplicated.contains(index)) {
                    indexes.add(index);
                    noDuplicated.add(index);
                } else if (index != -1) {
                    throw new IllegalArgumentException(
                            "At least one column name in the shuffled columns does not match the input column names");
                } else if (noDuplicated.contains(index)) {
                    throw new IllegalArgumentException("One column can be only set in one shuffling group");
                }
            }
            numColumns.add(indexes);
        }
        return numColumns;
    }

    private List<Integer> getPartitionIndex(List<String> partitionColumns) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < partitionColumns.size(); i++) {
            if (allInputColumns.contains(partitionColumns.get(i))) {
                int index = allInputColumns.indexOf(partitionColumns.get(i));
                if (list.contains(index))
                    throw new IllegalArgumentException("Partitioning column should be set once");
                else
                    list.add(allInputColumns.indexOf(partitionColumns.get(i)));
            } else {
                throw new IllegalArgumentException(
                        "At least one column name in the partition columns does not match the input column names");
            }
        }
        return list;
    }

    /**
     * This methods shuffles the input 2D list by the give columns number.<br>
     * 
     * The row indexes shift back by a random number between 1 and the input 2D list size one column by one column. Then
     * we find a prime number bigger than the row number.
     * 
     * @param rowList a list of partitioned objects
     */
    protected void shuffleTable(List<List<Object>> rowList) {
        List<Row> rows = generateRows(rowList, null);
        processShuffleTable(rowList, rows);
        rows.clear();

    }

    private void processShuffleTable(List<List<Object>> rowList, List<Row> rows) {
        int size = rows.size();
        List<Integer> replacements = calculateReplacementInteger(size, getPrimeNumber(size));
        List<Integer> shifts = new ArrayList<Integer>();

        if (numColumns.size() == 1) {
            adjustReplacements(replacements);
            for (int row = 0; row < size; row++) {
                for (int column : numColumns.get(0)) {
                    rowList.get(rows.get(row).rIndex).set(column, rows.get(replacements.get(row)).rItems.get(column));
                }
            }
        } else {
            for (int group = 0; group < numColumns.size(); group++) {
                int shift = getShift(shifts, size);
                shifts.add(shift);
                for (int row = 0; row < size; row++) {
                    int replacement = replacements.get((row + shift) % size);

                    List<Object> aux = rowList.get(rows.get(row).rIndex);
                    List<Object> auxRow = rows.get(replacement).rItems;
                    for (int column : numColumns.get(group)) {
                        aux.set(column, auxRow.get(column));
                    }
                }
            }
        }

    }

    private void adjustReplacements(List<Integer> replacements) {
        for (int i = 0; i < replacements.size(); i++) {
            if (i == replacements.get(i)) {
                if (i != replacements.size() - 1) {
                    replacements.set(i, replacements.get(i + 1));
                    replacements.set(i + 1, i);
                } else {
                    replacements.set(i, replacements.get(i - 1));
                    replacements.set(i - 1, i);
                }
            }
        }

    }

    /**
     * Gets the shift of row index. Generally, the values in the shifts list should be unique and inferior than integer.
     * But when the integer is smaller than shifts' size, the method cannot guarantee the unique value in the shifts
     * list, which means that the shift list has the at least one value exits more than one times.
     * 
     * @param shifts
     * @param integer
     * @return
     */
    private int getShift(List<Integer> shifts, int integer) {
        int shift = 0;
        if (shifts.size() >= integer) {
            return random.nextInt(integer);
        }
        do {
            shift = random.nextInt(integer);
        } while (shifts.contains(shift));
        return shift;
    }

    /**
     * 
     * Shuffles the columns by a given group<br>
     * 
     * @param rowList input table value
     */
    protected void shuffleColumnWithPartition(List<List<Object>> rowList) {
        List<Row> rows = generateRows(rowList, partitionColumns);
        Collections.sort(rows);
        List<List<Row>> subRows = seperateRowsByPartition(rows);

        for (List<Row> subRow : subRows) {
            if (subRow.size() != 1) {
                processShuffleTable(rowList, subRow);
            }
        }
    }

    /**
     * Separates the list of Row object by the same group. Tow pointers are needed. The first pointer points to the
     * first line of the partition, then the second pointer slips down until the first line who has the different value
     * with the first pointer.
     * 
     * @param rows the list of rows to be separated
     * @return a list of separated list
     */
    private List<List<Row>> seperateRowsByPartition(List<Row> rows) {
        List<List<Row>> subRows = new ArrayList<List<Row>>();
        int i = 0;
        int j = 1;
        do {
            List<Object> compared = rows.get(i).rPartition;
            do {
                List<Object> comparing = rows.get(j).rPartition;
                for (int k = 0; k < compared.size(); k++) {
                    if (!compared.get(k).equals(comparing.get(k))) {
                        subRows.add(rows.subList(i, j));
                        i = j;
                        break;
                    }
                }
                j++;
                if (j == rows.size()) {
                    subRows.add(rows.subList(i, j));
                    i = j - 1;
                }

            } while (i != (j - 1));

        } while (i != (rows.size() - 1));
        return subRows;
    }

    /**
     * 
     * Sets the random seed.
     * 
     * @param seed a long number
     */
    public void setRandomSeed(long seed) {
        this.random.setSeed(seed);
    }

    /**
     * Gets a prime number, prime with size.
     * 
     * @param size
     * @return a prime number with size
     */
    protected int getPrimeNumber(int size) {
        int res;
        do {
            res = PRIME_NUMBERS[random.nextInt(PRIME_NUMBERS.length)];
        } while (size % res == 0);
        return res;
    }

    /**
     * This methods calculates the replaced index.<br>
     * The replaced index is calculated by the equation (original_index * prime_number) modulo (input_size)<br>
     * 
     * @param size the input size
     * @param prime the prime number
     * @return a list of replacements
     */
    protected List<Integer> calculateReplacementInteger(int size, int prime) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            long aux = ((i + 1L) % size) * (prime % size);
            list.add((int) (aux % size));
        }
        return list;
    }

    /**
     * Generates a list of Row with input row values and saves the information of group.
     * 
     * @param input the 2D arrays to be cloned
     * @param columnsPartition a list of grouped columns' indexes
     * @return a list of Row object
     */
    protected List<Row> generateRows(List<List<Object>> input, List<Integer> columnsPartition) {
        List<Row> rows = new ArrayList<Row>();
        int rIndex = 0;

        for (List<Object> subInput : input) {
            List<Object> partition = new ArrayList<Object>();
            if (columnsPartition != null) {
                for (int cIndex : columnsPartition) {
                    partition.add(subInput.get(cIndex));
                }
            }
            rows.add(new Row(rIndex, subInput, partition));
            rIndex++;
        }
        return rows;
    }

    /**
     * This class abstracts a Row with its index and the group items. It implements {@link Comparable} interface to
     * compare the value by the group items.<br>
     * DOC qzhao ShuffleColumnWithPartition class global comment. Detailled comment
     */
    class Row implements Comparable<Row> {

        int rIndex;

        List<Object> rPartition = new ArrayList<Object>();

        List<Object> rItems = new ArrayList<Object>();

        public Row(int rIndex, List<Object> rItems, List<Object> rGroup) {
            super();
            this.rIndex = rIndex;
            for (Object o : rItems) {
                this.rItems.add(o);
            }

            if (rGroup == null) {
                this.rPartition = null;
            } else {
                for (Object o : rGroup) {
                    this.rPartition.add(o);
                }
            }

        }

        @Override
        public String toString() {
            return "( " + rIndex + " " + " rItems " + rItems + " rGroup " + rPartition + " )";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((rPartition == null) ? 0 : rPartition.hashCode());
            result = prime * result + rIndex;
            result = prime * result + ((rItems == null) ? 0 : rItems.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Row)) {
                return false;
            }
            Row r = (Row) o;
            if (r.rIndex != rIndex || r.rPartition.size() != rPartition.size()) {
                return false;
            }
            for (int i = 0; i < rPartition.size(); i++) {
                if (!rPartition.get(i).equals(r.rPartition.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int compareTo(Row r) {
            int limit = Math.min(rPartition.size(), r.rPartition.size());
            int cmp = Integer.MIN_VALUE;
            for (int i = 0; i < limit; i++) {
                cmp = ((String) rPartition.get(i)).compareTo((String) r.rPartition.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }

            return cmp;
        }

        Object getItem(int index) {
            return rItems.get(index);
        }

        private ShuffleColumn getOuterType() {
            return ShuffleColumn.this;
        }

    }
}
