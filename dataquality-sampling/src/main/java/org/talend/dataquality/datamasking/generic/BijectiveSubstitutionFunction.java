package org.talend.dataquality.datamasking.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldDate;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;
import org.talend.dataquality.sampling.exception.DQRuntimeException;

/**
 *
 */
public class BijectiveSubstitutionFunction extends Function<String> {

    private static final long serialVersionUID = 8900059408697610292L;

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(BijectiveSubstitutionFunction.class);

    private GenerateUniqueRandomPatterns uniqueGenericPattern;

    public BijectiveSubstitutionFunction(List<FieldDefinition> fieldDefinitionList) throws IOException {

        keepFormat = true;

        List<AbstractField> fieldList = new ArrayList<AbstractField>();

        for (FieldDefinition definition : fieldDefinitionList) {
            switch (definition.getType()) {
            case DATEPATTERN:
                if (definition.getMin() < 1000 || definition.getMin() > 9999)
                    throw new DQRuntimeException("The minimum value " + definition.getMin() + " must be between 1000 and 9999");
                if (definition.getMax() < 1000 || definition.getMax() > 9999)
                    throw new DQRuntimeException("The maximum value " + definition.getMax() + " must be between 1000 and 9999");
                fieldList.add(new FieldDate(definition.getMin().intValue(), definition.getMax().intValue()));
                break;
            case INTERVAL:
                if (definition.getMin() < 0)
                    throw new DQRuntimeException("The minimum value " + definition.getMin() + " must be positive");
                if (definition.getMin() > definition.getMax())
                    throw new DQRuntimeException("The minimum value " + definition.getMin()
                            + " has to be less than the maximum value " + definition.getMax());
                fieldList.add(new FieldInterval(definition.getMin(), definition.getMax()));
                break;
            case ENUMERATION:
                fieldList.add(new FieldEnum(Arrays.asList(definition.getValue().split(","))));
                break;
            case ENUMERATION_FROM_FILE:
                File file = new File(definition.getValue());
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    fieldList.add(new FieldEnum(IOUtils.readLines(fis)));
                } else {
                    LOGGER.error("File does not exist");
                    throw new DQRuntimeException("File " + definition.getValue() + " does not exist");
                }
                break;
            default:
                break;
            }
        }

        uniqueGenericPattern = new GenerateUniqueRandomPatterns(fieldList);
    }

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        uniqueGenericPattern.setKey(rand.nextInt() % 10000 + 1000);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        if (str == null)
            return null;

        String strWithoutSpaces = super.removeFormatInString(str);
        // check if the pattern is valid
        if (strWithoutSpaces.isEmpty() || strWithoutSpaces.length() != uniqueGenericPattern.getFieldsCharsLength()) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }

        StringBuilder result = doValidGenerateMaskedField(strWithoutSpaces);
        if (result == null) {
            if (keepInvalidPattern)
                return str;
            else
                return null;
        }
        if (keepFormat)
            return insertFormatInString(str, result);
        else
            return result.toString();
    }

    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input str
        List<String> strs = new ArrayList<String>();

        int currentPos = 0;
        for (AbstractField f : uniqueGenericPattern.getFields()) {
            int length = f.getLength();
            strs.add(str.substring(currentPos, currentPos + length));
            currentPos += length;
        }

        StringBuilder result = uniqueGenericPattern.generateUniqueString(strs);
        if (result == null) {
            return null;
        }
        return result;
    }
}
