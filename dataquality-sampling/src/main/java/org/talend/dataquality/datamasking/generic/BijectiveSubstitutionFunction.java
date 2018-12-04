package org.talend.dataquality.datamasking.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.generic.patterns.AbstractGeneratePattern;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;
import org.talend.dataquality.datamasking.utils.crypto.BasicSpec;
import org.talend.dataquality.sampling.exception.DQRuntimeException;

/**
 *
 */
public class BijectiveSubstitutionFunction extends Function<String> {

    private static final long serialVersionUID = 8900059408697610292L;

    private static final Logger LOGGER = LoggerFactory.getLogger(BijectiveSubstitutionFunction.class);

    private AbstractGeneratePattern uniqueGenericPattern;

    public BijectiveSubstitutionFunction(List<FieldDefinition> fieldDefinitionList) throws IOException {

        keepFormat = true;

        List<AbstractField> fieldList = new ArrayList<>();

        for (FieldDefinition definition : fieldDefinitionList) {
            switch (definition.getType()) {
            case DATEPATTERN:
                handleDatePatternCase(fieldList, definition);
                break;
            case INTERVAL:
                handleIntervalCase(fieldList, definition);
                break;
            case ENUMERATION:
                fieldList.add(new FieldEnum(Arrays.asList(definition.getValue().split(",")))); //$NON-NLS-1$
                break;
            case ENUMERATION_FROM_FILE:
                handleEnumerationFromFileCase(fieldList, definition);
                break;
            default:
                break;
            }
        }

        uniqueGenericPattern = new GenerateUniqueRandomPatterns(fieldList);
    }

    /**
     * Deal with enumeration from file case
     * 
     * @param fieldList
     * @param definition
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void handleEnumerationFromFileCase(List<AbstractField> fieldList, FieldDefinition definition)
            throws FileNotFoundException, IOException {
        File file = new File(definition.getValue());
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            fieldList.add(new FieldEnum(IOUtils.readLines(fis)));
        } else {
            LOGGER.error("File does not exist"); //$NON-NLS-1$
            throw new DQRuntimeException("File " + definition.getValue() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Deal with interval case
     * 
     * @param fieldList
     * @param definition
     */
    private void handleIntervalCase(List<AbstractField> fieldList, FieldDefinition definition) {
        if (definition.getMin().signum() < 0) {
            throw new DQRuntimeException("The minimum value " + definition.getMin() + " must be positive"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (definition.getMin().compareTo(definition.getMax()) > 0) {
            throw new DQRuntimeException("The minimum value " + definition.getMin() //$NON-NLS-1$
                    + " has to be less than the maximum value " + definition.getMax()); //$NON-NLS-1$
        }
        fieldList.add(new FieldInterval(definition.getMin(), definition.getMax()));
    }

    /**
     * Deal with date pattern from file case
     * 
     * @param fieldList
     * @param definition
     */
    private void handleDatePatternCase(List<AbstractField> fieldList, FieldDefinition definition) {
        if (definition.getMin().compareTo(BigInteger.valueOf(1000)) < 0
                || definition.getMin().compareTo(BigInteger.valueOf(9999)) > 0) {
            throw new DQRuntimeException("The minimum value " + definition.getMin() + " must be between 1000 and 9999"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (definition.getMax().compareTo(BigInteger.valueOf(1000)) < 0
                || definition.getMax().compareTo(BigInteger.valueOf(9999)) > 0) {
            throw new DQRuntimeException("The maximum value " + definition.getMax() + " must be between 1000 and 9999"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        fieldList.add(new FieldDate(definition.getMin().intValue(), definition.getMax().intValue()));
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        secretMng.setKey(rand.nextInt() % BasicSpec.BASIC_KEY_BOUND + BasicSpec.BASIC_KEY_OFFSET);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null) {
            return null;
        }

        String strWithoutSpaces = super.removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty()
                || strWithoutSpaces.codePointCount(0, strWithoutSpaces.length()) != uniqueGenericPattern.getFieldsCharsLength()) {
            if (keepInvalidPattern) {
                return str;
            } else {
                return null;
            }
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
        if (result == null) {
            if (keepInvalidPattern) {
                return str;
            } else {
                return null;
            }
        }
        if (keepFormat) {
            return insertFormatInString(str, result);
        } else {
            return result.toString();
        }
    }

    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        int currentPos = 0;
        for (AbstractField f : uniqueGenericPattern.getFields()) {
            int length = f.getLength();
            int beginCPOffset = str.offsetByCodePoints(0, currentPos);
            int endCPOffset = str.offsetByCodePoints(0, currentPos + length);
            strs.add(str.substring(beginCPOffset, endCPOffset));
            currentPos += length;
        }

        StringBuilder result = uniqueGenericPattern.generateUniqueString(strs, secretMng);
        if (result == null) {
            return null;
        }
        return result;
    }
}
