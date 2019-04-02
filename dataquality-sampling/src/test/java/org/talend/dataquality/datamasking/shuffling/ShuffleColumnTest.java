package org.talend.dataquality.datamasking.shuffling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ShuffleColumnTest {

    private String file5000 = "Shuffling_test_data_5000.csv";

    private String file50000 = "Shuffling_test_data_50000.csv";

    private String file1000Compared = "Shuffling_test_data_1000 _result.csv";

    private static List<Integer> data = new ArrayList<Integer>();

    private static GenerateData generation = new GenerateData();

    private static List<List<String>> columns = new ArrayList<List<String>>();

    private static List<String> allColumns = Arrays
            .asList(new String[] { "id", "first_name", "last_name", "email", "gender", "birth", "city", "zip_code", "country" });

    @BeforeClass
    public static void generateData() {
        for (int i = 0; i < 14; i++) {
            data.add(i);
        }
        List<String> column1 = Arrays.asList(new String[] { "id", "first_name" });
        List<String> column2 = Arrays.asList(new String[] { "email" });
        List<String> column3 = Arrays.asList(new String[] { "city", "zip_code" });
        columns.add(column1);
        columns.add(column2);
        columns.add(column3);

    }

    @Test
    public void testBufferDemo() {

        String file = "demo_test.csv";
        String fileCompared = "demo_test _result.csv";
        List<List<String>> columns = new ArrayList<List<String>>();
        List<String> column1 = Arrays.asList(new String[] { "id" });
        List<String> column2 = Arrays.asList(new String[] { "fn", "ln" });
        columns.add(column1);
        columns.add(column2);
        List<String> allColumns = Arrays.asList(new String[] { "id", "fn", "ln", "City", "Addr", "Country" });
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(10);
        service.setRandomSeed(77);
        List<List<Object>> fileData = generation.getTableValue(file);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        List<List<Object>> fileDataCompared = generation.getTableValue(fileCompared);
        for (int i = 0; i < 2; i++) {
            List<List<Object>> rows = result.peek();
            for (int j = 0; j < rows.size(); j++) {
                List<Object> shuffled = rows.get(j);
                List<Object> compared = fileDataCompared.get(i * 10 + j);
                for (int k = 0; k < 3; k++) {
                    assertEquals(shuffled.get(k).toString(), compared.get(k).toString().trim());
                }
            }
        }

    }

    @Test
    public void testReplacementBigInteger() {
        int size = 23000000;
        int prime = 198491329;
        // System.out.println((long) Integer.MAX_VALUE * Integer.MAX_VALUE);

        for (long i = 0; i < size; i++) {
            int result = (int) (((i + 1) * prime) % size);
            if (result == i || (result < 0)) {
                System.out.println(i + " => " + result);
                fail("result is identical");
            }
        }
    }

    @Test
    public void testOneColumnBigInteger() {
        int partition = 10000;
        int size = 1000000;

        List<List<String>> id = new ArrayList<List<String>>();
        List<String> idc = new ArrayList<String>();
        idc.add("id");
        id.add(idc);

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();
        ShufflingService service = new ShufflingService(id, idc);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(partition);

        for (int i = 0; i < size; i++) {
            List<Object> row = Arrays.asList((Object) (i + ""));
            service.addOneRow(row);
        }

        service.setHasFinished(true);
        assertEquals(size / partition, result.size());
        for (int i = 0; i < result.size(); i++) {
            List<List<Object>> rows = result.poll();
            for (int position = 0; position < rows.size(); position++) {
                int item = Integer.parseInt(rows.get(position).get(0).toString());
                // the partition is good
                assertTrue(item < partition * (i + 1));
                assertTrue(item >= partition * i);
                // the position changes
                assertTrue(item != position);
            }

        }
    }

    @Test
    @Ignore
    public void testOneColumnBigIntegerHasModulo() {
        int partition = 100000;
        int size = 10000999;

        List<List<String>> id = new ArrayList<List<String>>();
        List<String> idc = new ArrayList<String>();
        idc.add("id");
        id.add(idc);

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();
        ShufflingService service = new ShufflingService(id, idc);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(partition);

        service.setSeperationSize(partition);
        for (int i = 0; i < size; i++) {
            List<Object> row = Arrays.asList((Object) (i + ""));
            service.addOneRow(row);
        }
        service.setHasFinished(true);
        assertEquals(size / partition, result.size() - 1);
        for (int i = 0; i < size / partition; i++) {
            List<List<Object>> rows = result.poll();
            for (int position = 0; position < rows.size(); position++) {
                int item = Integer.parseInt(rows.get(position).get(0).toString());
                // the partition is good
                assertTrue(item < partition * (i + 1));
                assertTrue(item >= partition * i);
                // the position changes
                assertTrue(item != position);
            }
        }
        // test last rows
        List<List<Object>> rows = result.poll();
        for (int position = 0; position < rows.size(); position++) {
            int item = Integer.parseInt(rows.get(position).get(0).toString());
            // the partition is good
            assertTrue(item < size);
            assertTrue(item >= partition * (size / partition));
            // the position changes
            assertTrue(item != position);
        }
    }

    @Test
    public void testshuffleColumnsData1000() throws InterruptedException {
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);
        service.setRandomSeed(77);
        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(GenerateData.SHUFFLING_DATA_PATH);
        List<List<Object>> fileDataCompared = generation.getTableValue(file1000Compared);

        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("1000 line generation time " + (time2 - time1));

        assertEquals(1, result.size());
        List<Object> idColumnSL = new ArrayList<Object>();
        List<Object> firstNameColumnSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idColumnL = new ArrayList<Object>();
        List<Object> firstNameColumnL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            // Compare the shuffling results' positions
            for (int i = 0; i < rows.size(); i++) {
                List<Object> shuffled = rows.get(i);
                List<Object> compared = fileDataCompared.get(i);
                for (int j = 0; j < 4; j++) {
                    assertEquals(shuffled.get(j).toString(), compared.get(j).toString().trim());
                }
            }

            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idColumnL.add(id);
            firstNameColumnL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);

        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            assertTrue(oName.equals(sFirstName));
        }

    }

    @Test
    public void testshuffleColumnsData5000() throws InterruptedException {
        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(file5000);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        Thread.sleep(100);
        System.out.println("5000 line generation time " + (time2 - time1));

        assertEquals(1, result.size());

        List<Object> idColumnSL = new ArrayList<Object>();
        List<Object> firstNameColumnSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idColumnL = new ArrayList<Object>();
        List<Object> firstNameColumnL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idColumnL.add(id);
            firstNameColumnL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);

        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            assertTrue(oName.equals(sFirstName));

        }

    }

    @Test
    @Ignore
    public void testshuffleColumnsData50000() {

        Queue<List<List<Object>>> result = new ConcurrentLinkedQueue<List<List<Object>>>();

        ShufflingService service = new ShufflingService(columns, allColumns);

        ShufflingHandler handler = new ShufflingHandler(service, result);
        service.setShufflingHandler(handler);
        service.setSeperationSize(100000);

        List<List<Object>> fileData = generation.getTableValue(file50000);
        long time1 = System.currentTimeMillis();
        service.setRows(fileData);
        long time2 = System.currentTimeMillis();
        service.setHasFinished(true);
        System.out.println("50000 line generation time " + (time2 - time1));

        assertEquals(1, result.size());
        System.out.println("result size " + result.size());
        long time3 = System.currentTimeMillis();
        List<Object> idColumnSL = new ArrayList<Object>();
        List<Object> firstNameColumnSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idColumnL = new ArrayList<Object>();
        List<Object> firstNameColumnL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();
        // Initialize the shuffled data
        for (int group = 0; group < result.size(); group++) {
            List<List<Object>> rows = result.poll();
            for (List<Object> row : rows) {
                Object idS = row.get(0);
                Object firstNameS = row.get(1);
                Object emailS = row.get(3);
                Object cityS = row.get(6);
                Object zipS = row.get(7);

                idColumnSL.add(idS);
                firstNameColumnSL.add(firstNameS);
                emailSL.add(emailS);
                citySL.add(cityS);
                zipSL.add(zipS);
            }
        }

        // Initialize the original data set
        for (int i = 0; i < fileData.size(); i++) {
            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idColumnL.add(id);
            firstNameColumnL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);

        }

        for (int i = 0; i < fileData.size(); i++) {
            // test whether all email address retain
            assertTrue(emailSL.contains(emailL.get(i)));
            // test whether all name retain
            assertTrue(firstNameColumnSL.contains(firstNameColumnSL.get(i)));

            Object oid = idColumnL.get(i);
            Object nid = idColumnSL.get(i);

            Object oemail = emailL.get(i);
            Object nemail = emailSL.get(i);
            Object oName = firstNameColumnL.get(i);
            // test whether email and id information have all changed
            assertTrue(!oid.equals(nid) || !oemail.equals(nemail));

            // test whether the id and first name's relation retains
            int sIdIndex = idColumnSL.indexOf(oid);
            Object sFirstName = firstNameColumnSL.get(sIdIndex);
            assertTrue(oName.equals(sFirstName));

        }
        long time4 = System.currentTimeMillis();
        System.out.println("50000 line generation time " + (time4 - time3));
    }

}
