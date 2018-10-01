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
package org.talend.dataquality.semantic.classifier.custom;

import static org.talend.dataquality.semantic.api.CategoryRegistryManager.DEFAULT_RE_PATH;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class UDCategorySerDeser {

    private static final Logger LOGGER = LoggerFactory.getLogger(UDCategorySerDeser.class);

    private static final String BUNDLE_NAME = "org.talend.dataquality.semantic"; //$NON-NLS-1$

    private static UserDefinedClassifier udc;

    private static ObjectMapper mapper = new ObjectMapper();

    public static UserDefinedClassifier getRegexClassifier() throws IOException {
        if (udc == null) {
            udc = readJsonFile();
        }
        return udc;
    }

    /**
     * 
     * Read json file and get the Object UserDefinedClassifier.
     * 
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    static UserDefinedClassifier readJsonFile() throws IOException {
        try {
            InputStream inputStream = UDCategorySerDeser.class.getResourceAsStream(DEFAULT_RE_PATH);
            return readJsonFile(inputStream);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            URL url = new URL("platform:/plugin/" + BUNDLE_NAME //$NON-NLS-1$
                    + DEFAULT_RE_PATH); // $NON-NLS-1$
            InputStream inputStream = url.openConnection().getInputStream();
            return readJsonFile(inputStream);
        }
    }

    static UserDefinedClassifier readJsonFile(InputStream inputStream) throws IOException {
        try {
            return mapper.readValue(inputStream, UserDefinedClassifier.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    static UserDefinedClassifier readJsonFile(String content) throws IOException {
        try {
            return mapper.readValue(content, UserDefinedClassifier.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * In local registry, the json file is composed by an array of regex classifiers without the "classifiers" json object node.
     * 
     * @throws IOException
     */
    public static UserDefinedClassifier readJsonFile(URI uri) throws IOException {
        InputStream ins = uri.toURL().openStream();
        final String content = IOUtils.toString(ins);
        IOUtils.closeQuietly(ins);
        try {
            List<UserDefinedCategory> list = mapper.readValue(content, new TypeReference<List<UserDefinedCategory>>() {
            });
            JsonNode arrayNode = mapper.valueToTree(list);
            ObjectNode objNode = mapper.createObjectNode();
            objNode.set("classifiers", arrayNode);
            return readJsonFile(objNode.toString());
        } catch (Exception e) {
            LOGGER.trace(e.getMessage(), e);
            // try another format with "classifier" node.
            return readJsonFile(content);
        }
    }

    /**
     * 
     * Write the content to json file.
     * 
     * @param userDefinedClassifier the classifiers to persist in a json file
     * @param outputStream the stream to write in
     * @return
     */
    public boolean writeToJsonFile(UserDefinedClassifier userDefinedClassifier, OutputStream outputStream) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, userDefinedClassifier);
            outputStream.close();
        } catch (Exception exc) {
            LOGGER.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

}
