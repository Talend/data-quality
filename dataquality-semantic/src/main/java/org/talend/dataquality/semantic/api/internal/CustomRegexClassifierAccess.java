package org.talend.dataquality.semantic.api.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Low level API for operations on regex file.
 */
public class CustomRegexClassifierAccess {

    private final Logger LOGGER = Logger.getLogger(CustomRegexClassifierAccess.class);

    private ObjectMapper mapper = new ObjectMapper();

    private File regExFile;

    public CustomRegexClassifierAccess(CustomDictionaryHolder holder) {
        String regexFilePath = CategoryRegistryManager.getLocalRegistryPath() + File.separator + holder.getTenantID()
                + File.separator + CategoryRegistryManager.PRODUCTION_FOLDER_NAME + File.separator
                + CategoryRegistryManager.REGEX_SUBFOLDER_NAME + File.separator
                + CategoryRegistryManager.REGEX_CATEGRIZER_FILE_NAME;
        regExFile = new File(regexFilePath);
        if (!regExFile.exists()) {
            try {
                writeRegExs(new ArrayList<>(holder.getRegexClassifier().getClassifiers()));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * add the regEx to the lucene index and share it on HDFS
     *
     * @param regEx the document to add
     */
    public void createRegex(ISubCategory regEx) {
        LOGGER.debug("createRegex: " + regEx);
        List<ISubCategory> regExs = getRegExs();
        if (regExs == null)
            regExs = new ArrayList<>();

        regExs.add(regEx);

        writeRegExs(regExs);
    }

    private void writeRegExs(List<ISubCategory> regExs) {
        if (!regExFile.exists()) {
            regExFile.getParentFile().mkdirs();
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(regExFile, regExs);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public List<ISubCategory> getRegExs() {
        List<ISubCategory> regExs = null;
        try {
            regExs = mapper.readValue(regExFile, new TypeReference<List<UserDefinedCategory>>() {
            });
        } catch (JsonMappingException jsonE) {
            if (!jsonE.getMessage().contains("No content"))
                LOGGER.error(jsonE.getMessage(), jsonE);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return regExs;
    }

    public UserDefinedClassifier readUserDefinedClassifier() {
        UserDefinedClassifier udc = new UserDefinedClassifier();
        List<ISubCategory> regExs = getRegExs();
        for (ISubCategory cat : regExs) {
            udc.addSubCategory(cat);
        }
        return udc;
    }

    public void copyStagingContent(String srcPath) throws IOException {
        FileUtils.copyFile(new File(srcPath), regExFile);
    }
}
