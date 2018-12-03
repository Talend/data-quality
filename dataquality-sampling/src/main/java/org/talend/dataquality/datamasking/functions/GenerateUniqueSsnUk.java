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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldEnum;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 * @author jteuladedenantes
 * 
 * UK pattern: aa-bbbbbb-c aa: all the letters except D,F,I,Q,U,V for both, O for the second letter or
 * BG,GB,NK,KN,TN,NT,ZZ bbbbbb: 000000 -> 999999 c: A, B, C ou D
 */
public class GenerateUniqueSsnUk extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = 2583289679952923493L;

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<Character> forbiddenLetters = new ArrayList<Character>(Arrays.asList('D', 'F', 'I', 'Q', 'U', 'V'));

        List<String> forbiddenTwoLetters = new ArrayList<String>(Arrays.asList("BG", "GB", "NK", "KN", "TN", "NT", "ZZ"));

        List<AbstractField> fields = new ArrayList<AbstractField>();

        List<String> firstField = new ArrayList<String>();
        for (char firstLetter = 'A'; firstLetter <= 'Z'; firstLetter++)
            if (!forbiddenLetters.contains(firstLetter))
                for (char secondLetter = 'A'; secondLetter <= 'Z'; secondLetter++)
                    if (!forbiddenLetters.contains(secondLetter) && secondLetter != 'O') {
                        String twoLetters = new StringBuilder().append(firstLetter).append(secondLetter).toString();
                        if (!forbiddenTwoLetters.contains(twoLetters))
                            firstField.add(twoLetters);
                    }
        fields.add(new FieldEnum(firstField, 2));
        fields.add(new FieldInterval(BigInteger.ZERO, BigInteger.valueOf(999999)));
        List<String> thirdField = new ArrayList<String>();
        for (char letter = 'A'; letter <= 'D'; letter++)
            thirdField.add(String.valueOf(letter));
        fields.add(new FieldEnum(thirdField, 1));
        return fields;

    }

    @Override
    protected StringBuilder doValidGenerateMaskedField(String str) {
        // read the input strWithoutSpaces
        List<String> strs = new ArrayList<String>();
        strs.add(str.substring(0, 2));
        strs.add(str.substring(2, 8));
        strs.add(str.substring(8, 9));

        return ssnPattern.generateUniqueString(strs);
    }

}
