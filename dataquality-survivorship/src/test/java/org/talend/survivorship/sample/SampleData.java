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
package org.talend.survivorship.sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.talend.survivorship.model.RuleDefinition;
import org.talend.survivorship.model.RuleDefinition.Function;
import org.talend.survivorship.model.RuleDefinition.Order;

/**
 * Sample input data and result expectation for unit tests.
 *
 *
 */
public final class SampleData {

    public static final String RULE_PATH = "src/test/resources/generated/"; //$NON-NLS-1$

    public static final String PKG_NAME = "org.talend.survivorship.sample"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_NULL_FRE = "org.talend.survivorship.conflict.fre_null_fre"; //$NON-NLS-1$

    public static final Object[][] SAMPLE_INPUT = {
            { "GRIZZARD CO.", "Lili", "110 N MARYLAND AVE", "GLENDALE", "CA", "912066", "FR", "8185431314", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                    stringToDate("20110101", "yyyyMMdd"), 1.0, 18, 1985, "Something" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "GRIZZARD", "Tony", "110 NORTH MARYLAND AVENUE", "GLENDALE", "CA", "91205", "US", "9003254892", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                    stringToDate("20110118", "yyyyMMdd"), 0.9879999999999999, 25, 0, "" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "GRIZZARD INC", "Tom", "110 N. MARYLAND AVENUE", "GLENDALE", "CA", "91206", "US", "(818) 543-1315", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                    stringToDate("20110103", "yyyyMMdd"), 0.8572727272727272, 31, 1970, null }, //$NON-NLS-1$ //$NON-NLS-2$
            { "GRIZZARD CO", "li", "1480 S COLORADO BOULEVARD", "LOS ANGELES", "CA", "91206", "US", "(800) 325-4892", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                    stringToDate("20110115", "yyyyMMdd"), 0.742319482, 35, 0, null } }; //$NON-NLS-1$ //$NON-NLS-2$

    public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("addr", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("state", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("zip", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("country", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("phone", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("date", "java.util.Date"); //$NON-NLS-1$ //$NON-NLS-2$
            put("score", "double"); //$NON-NLS-1$ //$NON-NLS-2$
            put("age", "int"); //$NON-NLS-1$ //$NON-NLS-2$
            put("birthyear", "int"); //$NON-NLS-1$ //$NON-NLS-2$
            put("completeness", "String"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    public static final RuleDefinition[] RULES = {
            new RuleDefinition(Order.SEQ, "CompletenessRule", null, Function.MostComplete, null, "completeness", true), //$NON-NLS-1$ //$NON-NLS-2$
            new RuleDefinition(Order.SEQ, "LengthAcct", "acctName", Function.Expression, ".length > 11", "acctName", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new RuleDefinition(Order.SEQ, "LongestAddr", "addr", Function.Longest, null, "addr", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new RuleDefinition(Order.SEQ, "HighScore", "score", Function.Expression, " > 0.95", "score", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new RuleDefinition(Order.SEQ, "MostCommonCity", "city", Function.MostCommon, null, "city", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new RuleDefinition(Order.SEQ, "MostCommonZip", "zip", Function.MostCommon, null, "zip", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // new RuleDefinition(Order.MC, "ZipRegex", "zip", Function.MatchRegex, "\\\\d{5}",null, true),
            new RuleDefinition(Order.MT, null, null, null, null, "state", true), //$NON-NLS-1$
            new RuleDefinition(Order.MT, null, null, null, null, "country", true), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "LatestPhone", "date", Function.MostRecent, null, "date", true), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            new RuleDefinition(Order.MT, null, null, null, null, "phone", true), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "firstNameRule", "firstName", Function.MatchRegex, "^\\\\w{2}$", "firstName", true) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_NULL_FRE = {
            new RuleDefinition(Order.SEQ, "more_common_middleName", "middleName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "middleName", false) }; //$NON-NLS-1$

    public static final HashMap<String, Object> EXPECTED_SURVIVOR = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "GRIZZARD CO."); //$NON-NLS-1$ //$NON-NLS-2$
            put("firstName", "li"); //$NON-NLS-1$ //$NON-NLS-2$
            put("addr", "110 NORTH MARYLAND AVENUE"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city", "GLENDALE"); //$NON-NLS-1$ //$NON-NLS-2$
            put("state", "CA"); //$NON-NLS-1$ //$NON-NLS-2$
            put("zip", "91206"); //$NON-NLS-1$ //$NON-NLS-2$
            put("country", "US"); //$NON-NLS-1$ //$NON-NLS-2$
            put("phone", "9003254892"); //$NON-NLS-1$ //$NON-NLS-2$
            put("date", stringToDate("20110118", "yyyyMMdd")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            put("score", 1.0); //$NON-NLS-1$
            put("age", null); //$NON-NLS-1$
            put("birthyear", null); //$NON-NLS-1$
            put("completeness", "Something"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    public static final HashSet<String> EXPECTED_CONFLICT_OF_SURVIVOR = new HashSet<String>() {

        private static final long serialVersionUID = 1L;
        {
            add("acctName"); //$NON-NLS-1$
            add("score"); //$NON-NLS-1$
            add("addr"); //$NON-NLS-1$
        }
    };

    public static Date stringToDate(String dateString, String dateFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date, String dateFormat) {
        String str = null;
        str = new SimpleDateFormat(dateFormat).format(date);
        return str;
    }
}
