package org.talend.dataquality.datamasking.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 *
 */
public class BijectiveSubstitutionFunction extends Function<String> {

    private static final long serialVersionUID = 8900059408697610292L;

    private GenerateUniqueRandomPatterns uniqueGenericPattern;

    public BijectiveSubstitutionFunction(List<FieldDefinition> fieldDefinitionList) {

        List<AbstractField> fieldList = new ArrayList<AbstractField>();

        for (FieldDefinition definition : fieldDefinitionList) {
            switch (definition.getType()) {
            case CONSTANT:
                fieldList.add(new FieldEnum(Arrays.asList(definition.getValue())));
                break;
            case INTERVAL:
                fieldList.add(new FieldInterval(definition.getMin(), definition.getMax()));
                break;
            case ENUMERATION:
                fieldList.add(new FieldEnum(Arrays.asList(definition.getValue().split(",", 0))));
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
