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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

public class WordNetDictionary {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordNetDictionary.class);

    private static IDictionary dict;

    private static WordNetDictionary instance = null;

    private static final List<String> ENGLISH_STOP_WORDS_SET = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but",
            "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then",
            "there", "these", "they", "this", "to", "was", "will", "with");

    private WordNetDictionary() {
        initDictinary();
    }

    public static WordNetDictionary getInstance() {
        if (instance == null) {
            instance = new WordNetDictionary();
        }
        if (dict != null && !dict.isOpen()) {
            try {
                dict.open();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return instance;
    }

    private void initDictinary() {
        String path;
        URL url = WordNetDictionary.class.getResource("/dict");
        if ("file".equals(url.getProtocol())) {
            // nothing to do
        } else if ("bundleresource".equals(url.getProtocol())) { // for OSGI environment
            try {
                path = getFilePathByPlatformURL(url);
                url = new URL("file", null, path);//$NON-NLS-1$
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open bundleresource '" + url + "'.", e);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported protocol '" + url.getProtocol() + "'.");
        }
        dict = new Dictionary(url);
    }

    private String getFilePathByPlatformURL(URL url) throws IOException {
        String filePath = url.getPath();
        try {
            url.openConnection().getInputStream();
        } catch (FileNotFoundException e) {
            LOGGER.debug(e.getMessage(), e);
            // try to get the absolute path from exception message
            final String msg = e.getMessage();
            if (filePath.endsWith("/")) {
                filePath = filePath.substring(0, filePath.length() - 1);
            }
            final String osFilePath = filePath.replace("/", File.separator); //$NON-NLS-1$
            final int idx = msg.indexOf(osFilePath);
            if (idx > 0) {
                return msg.substring(0, idx + filePath.length());
            }
        }
        return filePath;
    }

    boolean isValidWord(String input) {
        if (ENGLISH_STOP_WORDS_SET.contains(input.toLowerCase())) {
            return true;
        }
        IIndexWord idxWord;
        for (POS pos : POS.values()) {
            idxWord = dict.getIndexWord(input, pos);
            if (idxWord != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidTerm(String input) {
        if (input == null) {
            return false;
        }
        final String[] tokens = TextUtils.cutTextAndSplit(input);
        for (String token : tokens) {
            if (!isValidWord(token)) {
                return false;
            }
        }
        return true;
    }

    public void close() {
        if (dict != null && dict.isOpen()) {
            dict.close();
        }
    }

}
