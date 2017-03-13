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
package org.talend.dataquality.datamasking.functions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Load keys from file
 * 
 * @since 2.1.1
 * @author mzhao
 */
public class KeysLoader {

    /**
     * 
     * @param filePath the file path where keys to be loaded.
     * @return keys array
     * @throws FileNotFoundException
     * @throws NullPointerException
     */
    public static List<String> loadKeys(String filePath) throws IOException, NullPointerException {
        List<String> keys = new ArrayList<>();
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(filePath));
            while (in.ready()) {
                keys.add(in.readLine().trim());
            }
            in.close();
        } catch (NullPointerException | IOException e) {
            throw e;
        }
        return keys;
    }

    /**
     * 
     * @param ins The input stream
     * @return keys array
     * @throws FileNotFoundException
     * @throws NullPointerException
     */
    public static List<String> loadKeys(InputStream ins) throws IOException {
        List<String> keys = new ArrayList<>();
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(ins, Charset.forName("UTF-8")));
        while (in.ready()) {
            keys.add(in.readLine().trim());
        }
        in.close();
        return keys;
    }
}
