package org.talend.dataquality.datamasking.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.AbstractGenerateWithSecret;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.datamasking.generic.patterns.GenerateUniqueRandomPatterns;
import org.talend.dataquality.sampling.exception.DQRuntimeException;

/**
 *
 */
public class BijectiveSubstitutionFunction extends AbstractGenerateWithSecret {

    private static final long serialVersionUID = 8900059408697610292L;

    private static final Logger LOGGER = LoggerFactory.getLogger(BijectiveSubstitutionFunction.class);

    public BijectiveSubstitutionFunction(List<FieldDefinition> fieldDefinitionList) throws IOException {

        keepFormat = true;

        List<AbstractField> fieldList = new ArrayList<>();
        int nbFields = fieldDefinitionList.size();
        for (int i = 0; i < nbFields; i++) {
            FieldDefinition definition = fieldDefinitionList.get(i);
            switch (definition.getType()) {
            case DATEPATTERN:
                handleDatePatternCase(fieldList, definition);
                break;
            case INTERVAL:
                handleIntervalCase(fieldList, definition);
                break;
            case ENUMERATION:
                fieldList.add(new FieldEnum(Arrays.asList(definition.getValue().split(",")), i == nbFields - 1)); //$NON-NLS-1$
                break;
            case ENUMERATION_FROM_FILE:
                handleEnumerationFromFileCase(fieldList, definition, i == nbFields - 1);
                break;
            default:
                break;
            }
        }

        pattern = new GenerateUniqueRandomPatterns(fieldList);
    }

    /**
     * Deal with enumeration from file case
     * 
     * @param fieldList
     * @param definition
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void handleEnumerationFromFileCase(List<AbstractField> fieldList, FieldDefinition definition, boolean isLastField)
            throws FileNotFoundException, IOException {
        File file = new File(definition.getValue());
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            fieldList.add(new FieldEnum(IOUtils.readLines(fis), isLastField));
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
    protected boolean isValidWithoutFormat(String str) {
        return !str.isEmpty() && str.codePointCount(0, str.length()) >= pattern.getFieldsCharsLength();
    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        int currentPos = 0;
        int nbFields = pattern.getFields().size();
        for (int i = 0; i < nbFields - 1; i++) {
            AbstractField field = pattern.getFields().get(i);
            int length = field.getLength();
            int beginCPOffset = str.offsetByCodePoints(0, currentPos);
            int endCPOffset = str.offsetByCodePoints(0, currentPos + length);
            strs.add(str.substring(beginCPOffset, endCPOffset));
            currentPos += length;
        }
        // Last field: take the remaining chain
        strs.add(str.substring(currentPos));

        Optional<StringBuilder> result = pattern.generateUniqueString(strs, secretMng);

        return result.orElse(null);
    }
}
