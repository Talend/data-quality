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
package org.talend.dataquality.statistics.frequency.recognition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericCharPatternRecognizerTest {

    private GenericCharPatternRecognizer recognizer = new GenericCharPatternRecognizer();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRecognize() {

        // Assert empty
        RecognitionResult result = recognizer.recognize("");
        Assert.assertFalse(result.isComplete());
        Assert.assertEquals(Collections.singleton(""), result.getPatternStringSet());

        // Assert blank and compare the result instance
        RecognitionResult result2 = recognizer.recognize(" ");
        Assert.assertFalse(result2.isComplete());
        Assert.assertEquals(Collections.singleton(" "), result2.getPatternStringSet());

        // Assert null
        RecognitionResult result3 = recognizer.recognize(null);
        Assert.assertFalse(result3.isComplete());
        Assert.assertEquals(Collections.singleton(null), result3.getPatternStringSet());

        // Assert correctness of Ascii character replacement.
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789"; //$NON-NLS-1$
        String replChars = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa9999999999"; //$NON-NLS-1$
        checkPattern(chars, replChars, true, recognizer);

        // Assert incomplete when the chars including a none-ascii character "ィ".
        checkPattern("abcィd", "aaaKa", true, recognizer);

        // Assert incomplete when the chars including a none-ascii character "-".
        checkPattern("abc-d", "aaa-a", false, recognizer);
        checkPattern("Straße", "Aaaaaa", true, recognizer);
        checkPattern("トンキン", "KKKK", true, recognizer);
        checkPattern("とうきょう", "HHHhH", true, recognizer);
        checkPattern("서울", "GG", true, recognizer);
        checkPattern("北京", "CC", true, recognizer);

        // Assert more patterns
        Map<String, String> str2Pattern = new HashMap<>();
        str2Pattern.put("*-!", "*-!");
        str2Pattern.put("1-3", "9-9");
        str2Pattern.put("2001-9-10 - 2009-09-08", "9999-9-99 - 9999-99-99");
        str2Pattern.put("22. fdsud 1975", "99. aaaaa 9999");
        testRecognition(str2Pattern);
        str2Pattern.clear();
        str2Pattern.put("2001-8-20", "yyyy-M-d");
        str2Pattern.put("22. july 1975", "d. MMMM yyyy");
        testDateRecognition(str2Pattern);
    }

    private void checkPattern(String searchedCharacters, String expectedCharacters, boolean isComplete,
            GenericCharPatternRecognizer recognizer) {

        RecognitionResult result6 = recognizer.recognize(searchedCharacters);
        Assert.assertFalse(isComplete ^ result6.isComplete());
        Assert.assertEquals(Collections.singleton(expectedCharacters), result6.getPatternStringSet());

    }

    private void testRecognition(Map<String, String> str2Pattern) {
        GenericCharPatternRecognizer recognizer = new GenericCharPatternRecognizer();
        Iterator<String> strIterator = str2Pattern.keySet().iterator();
        while (strIterator.hasNext()) {
            String str = strIterator.next();
            Set<String> pattern = recognizer.recognize(str).getPatternStringSet();
            Assert.assertEquals(Collections.singleton(str2Pattern.get(str)), pattern);

        }

    }

    private void testDateRecognition(Map<String, String> str2Pattern) {
        DateTimePatternRecognizer recognizer = new DateTimePatternRecognizer();
        Iterator<String> strIterator = str2Pattern.keySet().iterator();
        while (strIterator.hasNext()) {
            String str = strIterator.next();
            Set<String> pattern = recognizer.recognize(str).getPatternStringSet();
            Assert.assertEquals(Collections.singleton(str2Pattern.get(str)), pattern);

        }

    }

}
