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
 */
public final class SampleData {

    public static final String RULE_PATH = "src/test/resources/generated/";

    public static final String PKG_NAME = "org.talend.survivorship.sample";

    public static final Object[][] SAMPLE_INPUT = {
            { "GRIZZARD CO.", "110 N MARYLAND AVE", "GLENDALE", "CA", "912066", "FR", "8185431314",
                    stringToDate("20110101", "yyyyMMdd"), 1.0, 18, 1985, "Something" },
            { "GRIZZARD", "110 NORTH MARYLAND AVENUE", "GLENDALE", "CA", "91205", "US", "9003254892",
                    stringToDate("20110118", "yyyyMMdd"), 0.9879999999999999, 25, 0, "" },
            { "GRIZZARD INC", "110 N. MARYLAND AVENUE", "GLENDALE", "CA", "91206", "US", "(818) 543-1315",
                    stringToDate("20110103", "yyyyMMdd"), 0.8572727272727272, 31, 1970, null },
            { "GRIZZARD CO", "1480 S COLORADO BOULEVARD", "LOS ANGELES", "CA", "91206", "US", "(800) 325-4892",
                    stringToDate("20110115", "yyyyMMdd"), 0.742319482, 35, 0, null } };

    public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "String");
            put("addr", "String");
            put("city", "String");
            put("state", "String");
            put("zip", "String");
            put("country", "String");
            put("phone", "String");
            put("date", "java.util.Date");
            put("score", "double");
            put("age", "int");
            put("birthyear", "int");
            put("completeness", "String");
        }
    };

    public static final RuleDefinition[] RULES = {
            new RuleDefinition(Order.SEQ, "CompletenessRule", null, Function.MostComplete, null, "completeness", true),
            new RuleDefinition(Order.SEQ, "LengthAcct", "acctName", Function.Expression, ".length > 11", "acctName", true),
            new RuleDefinition(Order.SEQ, "LongestAddr", "addr", Function.Longest, null, "addr", true),
            new RuleDefinition(Order.SEQ, "HighScore", "score", Function.Expression, " > 0.95", "score", true),
            new RuleDefinition(Order.SEQ, "MostCommonCity", "city", Function.MostCommon, null, "city", true),
            new RuleDefinition(Order.SEQ, "MostCommonZip", "zip", Function.MostCommon, null, "zip", true),
            // new RuleDefinition(Order.MC, "ZipRegex", "zip", Function.MatchRegex, "\\\\d{5}",null, true),
            new RuleDefinition(Order.MT, null, null, null, null, "state", true),
            new RuleDefinition(Order.MT, null, null, null, null, "country", true),
            new RuleDefinition(Order.SEQ, "LatestPhone", "date", Function.MostRecent, null, "date", true),
            new RuleDefinition(Order.MT, null, null, null, null, "phone", true) };

    public static final HashMap<String, Object> EXPECTED_SURVIVOR = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "GRIZZARD CO.");
            put("addr", "110 NORTH MARYLAND AVENUE");
            put("city", "GLENDALE");
            put("state", "CA");
            put("zip", "91206");
            put("country", "US");
            put("phone", "9003254892");
            put("date", stringToDate("20110118", "yyyyMMdd"));
            put("score", 1.0);
            put("age", null);
            put("birthyear", null);
            put("completeness", "Something");
        }
    };

    public static final HashSet<String> EXPECTED_CONFLICT_OF_SURVIVOR = new HashSet<String>() {

        private static final long serialVersionUID = 1L;
        {
            add("acctName");
            add("score");
            add("addr");
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
