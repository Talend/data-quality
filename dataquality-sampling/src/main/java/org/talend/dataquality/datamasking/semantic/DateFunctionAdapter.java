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
package org.talend.dataquality.datamasking.semantic;

import static org.talend.dataquality.datamasking.FunctionMode.CONSISTENT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.Function;

public class DateFunctionAdapter extends Function<String> {

    private static final long serialVersionUID = -2845447810365033162L;

    private static final Logger LOG = LoggerFactory.getLogger(DateFunctionAdapter.class);

    private AbstractDateFunction function;

    private List<SimpleDateFormat> dataFormatList = new ArrayList<SimpleDateFormat>();

    @Override
    public void setRandom(Random rand) {
        super.setRandom(rand);
        function.setRandom(rand);
    }

    public DateFunctionAdapter(AbstractDateFunction functionToAdapt, List<String> datePatternList) {
        function = functionToAdapt;
        rnd = functionToAdapt.getRandom();
        if (datePatternList != null) {
            for (String pattern : datePatternList) {
                try {
                    dataFormatList.add(new SimpleDateFormat(pattern));
                } catch (IllegalArgumentException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    protected String doGenerateMaskedField(String input, FunctionMode mode) {
        Random r = rnd;
        if (CONSISTENT == mode) {
            r = getRandomForObject(input);
        }

        return doGenerateMaskedField(input, r);
    }

    @Override
    protected String doGenerateMaskedField(String input) {
        return doGenerateMaskedField(input, rnd);
    }

    private String doGenerateMaskedField(String input, Random r) {
        if (input == null || EMPTY_STRING.equals(input.trim())) {
            return input;
        }
        for (SimpleDateFormat sdf : dataFormatList) {
            try {
                if (!sdf.toPattern().contains("H") && input.contains(":")) {
                    continue;
                }
                final Date inputDate = sdf.parse(input);
                final Date result = function.doGenerateMaskedField(inputDate, r);
                return sdf.format(result);
            } catch (ParseException e) {
                LOG.warn(e.getMessage());
            }
        }
        // no pattern from column metadata is applicable to the input, continue to guess and parse
        final String guess = DatePatternHelper.guessDatePattern(input);
        if (!EMPTY_STRING.equals(guess)) {
            final SimpleDateFormat sdf = new SimpleDateFormat(guess);
            try {
                final Date inputDate = sdf.parse(input);
                final Date result = function.doGenerateMaskedField(inputDate, r);
                return sdf.format(result);
            } catch (ParseException e) {
                LOG.warn(e.getMessage());
            }
        }
        return ReplaceCharacterHelper.replaceCharacters(input, r);
    }
}
