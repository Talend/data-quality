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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * created by jgonzalez on 19 juin 2015. This function will replace digits by other digits and everithing else by ”x”.
 * Moreover, there is a list of key words that won’t be transformed.
 *
 */
public class MaskAddress extends Function<String> {

    private static final long serialVersionUID = -4661073390672757141L;

    private List<String> keys = new ArrayList<>(Arrays.asList("Rue", "rue", "r.", "strasse", "Strasse", "Street", "street", "St.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "St", "Straße", "Strada", "Rua", "Calle", "Ave.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "avenue", "Av.", "Allee", "allee", "allée", "Avenue", "Avenida", "Bvd.", "Bd.", "Boulevard", "boulevard", "Blv.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
            "Viale", //$NON-NLS-1$
            "Avenida", "Bulevar", "Route", "route", "road", "Road", "Rd.", "Chemin", "Way", "Cour", "Court", "Ct.", "Place", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
            "place", "Pl.", //$NON-NLS-1$ //$NON-NLS-2$
            "Square", "Impasse", "Allée", "Driveway", "Auffahrt", "Viale", "Esplanade", "Esplanade", "Promenade", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
            "Lungomare", "Esplanada", "Esplanada", "Faubourg", "faubourg", "Suburb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "Vorort", "Periferia", "Subúrbio", "Suburbio", "Via", "Via", "industrial", "area", "zone", "industrielle", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
            "Périphérique", "Peripheral", "Voie", "voie", "Track", "Gleis", "Carreggiata", "Caminho", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
            "Pista", "Forum", "STREET", "RUE", "ST.", "AVENUE", "BOULEVARD", "BLV.", "BD", "ROAD", "ROUTE", "RD.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
            "RTE", "WAY", "CHEMIN", "COURT", "CT.", "SQUARE", "DRIVEWAY", "ALLEE", "DR.", "ESPLANADE", "SUBURB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
            "BANLIEUE", "VIA", "PERIPHERAL", "PERIPHERIQUE", "TRACK", "VOIE", "FORUM", "INDUSTRIAL", "AREA", "ZONE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
            "INDUSTRIELLE")); //$NON-NLS-1$

    @Override
    protected String doGenerateMaskedField(String str) {
        StringBuilder sb = new StringBuilder(EMPTY_STRING);
        if (str != null && !EMPTY_STRING.equals(str) && !(" ").equals(str)) { //$NON-NLS-1$
            String[] address = str.split(",| "); //$NON-NLS-1$
            for (String tmp : address) {
                if (keys.contains(tmp)) {
                    sb.append(tmp + " "); //$NON-NLS-1$
                } else {
                    for (int i = 0; i < tmp.length(); ++i) {
                        if (Character.isDigit(tmp.charAt(i))) {
                            sb.append(rnd.nextInt(8) + 1);
                        } else {
                            sb.append("X"); //$NON-NLS-1$
                        }
                    }
                    sb.append(" "); //$NON-NLS-1$
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return EMPTY_STRING;
        }
    }

    @Override
    public void parse(String extraParameter, boolean keepNullValues, Random rand) {
        super.parse(extraParameter, keepNullValues, rand);
        for (String element : parameters) {
            keys.add(element);
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

}
