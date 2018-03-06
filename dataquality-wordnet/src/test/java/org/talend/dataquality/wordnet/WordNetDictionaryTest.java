// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.wordnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordNetDictionaryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordNetDictionaryTest.class);

    private static WordNetDictionary wordnet;

    @BeforeClass
    public static void prepare() {
        try {
            wordnet = WordNetDictionary.getInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testIsValidWord() {
        assertTrue(wordnet.isValidWord("talent"));
        assertTrue(wordnet.isValidWord("postal_code"));
        assertTrue(wordnet.isValidWord("Of"));

        assertFalse(wordnet.isValidWord("foobar"));
        assertFalse(wordnet.isValidWord("PostalCode"));

    }

    @Test
    public void testIsValidTerm() {

        assertTrue(wordnet.isValidTerm("talent"));
        assertTrue(wordnet.isValidTerm("PostalCode"));
        assertTrue(wordnet.isValidTerm("CodePostal"));
        assertTrue(wordnet.isValidTerm("Postal Code"));
        assertTrue(wordnet.isValidTerm("CareOfName"));

        assertFalse(wordnet.isValidTerm("foobar"));
        assertFalse(wordnet.isValidTerm("country_code"));
        assertFalse(wordnet.isValidTerm("Code_Postal"));
        assertFalse(wordnet.isValidTerm("CodePays"));
        assertFalse(wordnet.isValidTerm(null));
    }

    @AfterClass
    public static void close() {
        wordnet.close();
    }

    @Test
    public void testIsValidTermMultiThread() throws InterruptedException {
        final AtomicBoolean atomBoolean = new AtomicBoolean(true);

        final String[] validTerms = new String[] { "talent", "PostalCode", "CodePostal", "CareOfName" };
        Runnable runable1 = new Runnable() {

            @Override
            public void run() {
                try {
                    for (String term : validTerms) {
                        boolean flag = wordnet.isValidTerm(term);
                        assertTrue("the term <" + term + "> is expected to be valid", flag);
                    }
                } catch (Exception exc) {
                    atomBoolean.set(false);
                } catch (AssertionError error) {
                    LOGGER.error("Assertion Error: " + error.getMessage());
                    atomBoolean.set(false);
                }

            }
        };

        final String[] invalidTerms = new String[] { "foobar", "country_code", "Code_Postal", "CodePays" };
        Runnable runable2 = new Runnable() {

            @Override
            public void run() {
                try {
                    for (String term : invalidTerms) {
                        boolean flag = wordnet.isValidTerm(term);
                        assertFalse("the term <" + term + "> is expected to be invalid", flag);
                    }
                } catch (Exception exc) {
                    atomBoolean.set(false);
                } catch (AssertionError error) {
                    LOGGER.error("Assertion Error: " + error.getMessage());
                    atomBoolean.set(false);
                }

            }
        };

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            workers.add(new Thread(runable1));
            workers.add(new Thread(runable2));
        }
        for (Thread worker : workers) {
            worker.start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        assertEquals("ConcurrentAccess failed", true, atomBoolean.get());
    }
}
