package org.talend.dataquality.semantic.recognizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

public class LFUCacheTest {

    private static final List<String[]> RECORDS_FIRST_NAME = getRecords("firstnames_with_duplicates.csv");

    private static final int MAX_CAPACITY = 1000;

    public static void main(String[] args) {
        LFUCache<String, String> lfu = new LFUCache<>(50, MAX_CAPACITY, 0.01f);

        System.out.println("start...");
        long begin = System.currentTimeMillis();
        for (int i = 0; i < RECORDS_FIRST_NAME.size(); i++) {
            final String firstName = RECORDS_FIRST_NAME.get(i)[0];

            String get = lfu.get(firstName);
            if (get == null) {
                try { // search in index
                    Thread.sleep(2);
                    get = "FIRST_NAME";
                } catch (InterruptedException e) {
                    //
                }
                lfu.put(firstName, get);
            }
            if ((i + 1) % 1000 == 0) {
                System.out.println(i + 1);
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("the analysis took: " + (end - begin) + " ms.");

        int count = 0;
        for (String key : lfu.keySet()) {
            System.out.println("key: " + key + " value: " + lfu.get(key) + " freq:" + lfu.frequencyOf(key));
            count++;
            if (count == 20) {
                break;
            }
        }
    }

    private static List<String[]> getRecords(String path) {
        List<String[]> records = new ArrayList<String[]>();
        try {
            Reader reader = new FileReader(LFUCacheTest.class.getResource(path).getPath());
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader();
            Iterable<CSVRecord> csvRecords = csvFormat.parse(reader);

            for (CSVRecord csvRecord : csvRecords) {
                String[] values = new String[csvRecord.size()];
                for (int i = 0; i < csvRecord.size(); i++) {
                    values[i] = csvRecord.get(i);
                }
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Test
    public void testLFU() {
        LFUCache<String, String> lfu = new LFUCache<>(50, MAX_CAPACITY, 0.01f);
        for (int i = 0; i < RECORDS_FIRST_NAME.size(); i++) {
            final String firstName = RECORDS_FIRST_NAME.get(i)[1];
            String get = lfu.get(firstName);
            if (get == null) {
                lfu.put(firstName, "FIRST_NAME");
            }
        }
        Assert.assertEquals(8, lfu.frequencyOf("SAKARI"));
        lfu.remove("SAKARI");
        Assert.assertEquals(null, lfu.get("SAKARI"));
        lfu.clear();
        Assert.assertEquals(Collections.emptyMap(), lfu);
    }

    @Test
    public void testLFUWithLowFrequency() {
        LFUCache<String, String> lfu = new LFUCache<>(50, 1, 0.01f);

        String firstname = RECORDS_FIRST_NAME.get(0)[1];
        lfu.put(firstname, "FIRST_NAME");
        String get = lfu.get(firstname);
        Assert.assertEquals("FIRST_NAME", get);
    }
}