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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * created by jgonzalez on 19 juin 2015. This function will replace digits by other digits and everything else by ”X”.
 * Moreover, there is a list of keywords that won’t be transformed.
 *
 */
public class MaskAddress extends FunctionString {

    private static final long serialVersionUID = -4661073390672757141L;

    private Set<String> keys = new HashSet<>(Arrays.asList("ALLEE", "ALLEY", "ALLÉE", "AREA", "AUFFAHRT", "AV", "AV.", "AVDA",
            "AVE", "AVE.", "AVENIDA", "AVENUE", "BACKROAD", "BANLIEUE", "BD", "BD.", "BLV", "BLV.", "BLVD", "BOULEVARD", "BREVE",
            "BULEVAR", "BVD", "BVD.", "BYWAY", "CALLE", "CAMINHO", "CAMINO", "CARREFOUR", "CARREGGIATA", "CARRETERA", "CHAUSSEE",
            "CHAUSSÉE", "CHEMIN", "CITE", "CITÉ", "CORTO", "COUR", "COURT", "CRT", "CT", "CT.", "CURTO", "DR", "DR.", "DRIVE",
            "DRIVEWAY", "ESD", "ESPLANADA", "ESPLANADE", "ESTRADA", "FAUBOURG", "FORUM", "FREEWAY", "GLEIS", "HIGHWAY", "HWY",
            "IMPASSE", "INDUSTRIAL", "INDUSTRIALE", "INDUSTRIELLE", "KURZ", "LANE", "LUNGOMARE", "MANEIRA", "MODO", "PARKWAY",
            "PARVIS", "PASSAGE", "PASSERELLE", "PERIFERIA", "PERIFERICO", "PERIFÉRICO", "PERIPHERAL", "PERIPHERIQUE", "PIAZZA",
            "PISTA", "PL", "PL.", "PLACE", "PLATZ", "PLAZA", "PONT", "PORTE", "PROMENADE", "PERIPHERIQUE", "PÉRIPHÉRIQUE",
            "QUADRADO", "QUAI", "R", "R.", "RD", "RD.", "ROAD", "ROUTE", "RTE", "RUA", "RUE", "SQUARE", "ST", "ST.", "STD", "STR",
            "STRADA", "STRASSE", "STREET", "SUBURB", "SUBURBIO", "SUBÚRBIO", "TERRASSE", "TRACK", "UBER", "VIA", "VIALE", "VILLA",
            "VLE", "VOIE", "VORORT", "VÍA", "WAY", "WEG", "ZONA", "ZONE", "ÁREA", "ÜBER"));

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder sb = new StringBuilder(EMPTY_STRING);
        if (str != null && !EMPTY_STRING.equals(str) && !(" ").equals(str)) { //$NON-NLS-1$
            String[] address = str.split(",| "); //$NON-NLS-1$
            boolean isOnlyKeyOrDigit = isOnlyKeyOrDigit(address);
            for (String word : address) {
                if (keys.contains(word.toUpperCase()) && !isOnlyKeyOrDigit) {
                    sb.append(word + " "); //$NON-NLS-1$
                } else {
                    int cp;
                    // one surrogate pair character will take two unicode point so that we just judge first one and if it is
                    // surrogate pair we make index+2
                    for (int i = 0; i < word.length(); i += Character.charCount(cp)) {
                        cp = word.codePointAt(i);
                        if (Character.isDigit(cp)) {
                            sb.append(r.nextInt(9) + 1);
                        } else {
                            sb.append("X"); //$NON-NLS-1$
                        }
                    }
                    sb.append(" "); //$NON-NLS-1$ma
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return EMPTY_STRING;
        }
    }

    private boolean isOnlyKeyOrDigit(String[] address) {
        for (String word : address) {
            if (!keys.contains(word.toUpperCase())) {
                int cp;
                // one surrogate pair character will take two unicode point so that we just judge first one and if it is
                // surrogate pair we make index+2
                for (int i = 0; i < word.length(); i += Character.charCount(cp)) {
                    cp = word.codePointAt(i);
                    if (!Character.isDigit(cp)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        if (parameters != null && parameters.length > 0 && !ERROR_MESSAGE.equals(parameters[0])) // If only one element, it means that it's a path and not a list of size >= 2
            for (String element : parameters) {
                keys.add(element.toUpperCase());
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#resetParameterTo(java.lang.String)
     */
    @Override
    protected void resetParameterTo(String errorMessage) {
        parameters = new String[] { errorMessage };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#isNeedCheckPath()
     */
    @Override
    protected boolean isNeedCheckPath() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.datamasking.functions.Function#isBothValidForFileOrNot()
     */
    @Override
    protected boolean isBothValidForFileOrNot() {
        return true;
    }

}
