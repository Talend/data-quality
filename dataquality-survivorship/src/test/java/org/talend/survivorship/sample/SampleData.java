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
 * FIXME create a SampleData class for each case: this class should manage only one data set and one set of rules.
 * FIXME we won't be able to read it anymore when we put more than one data set and one set of rules.
 *
 *
 */
public final class SampleData {

    public static final String RULE_PATH = "src/test/resources/generated/"; //$NON-NLS-1$

    public static final String PKG_NAME = "org.talend.survivorship.sample"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT = "org.talend.survivorship.conflict"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_LONG = "org.talend.survivorship.conflict.fre_long"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_LONG_RECENT = "org.talend.survivorship.conflict.fre_long_recent"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_LONG_RECENT_WITHOUT_IGNORE_BLANK = "org.talend.survivorship.conflict.fre_long_recent_without_ignore_blank"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN = "org.talend.survivorship.conflict.short_reference_column"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_NULL_CONSTANT = "org.talend.survivorship.conflict.fre_null_constant"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_FRE_NULL_FRE = "org.talend.survivorship.conflict.fre_null_fre"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_ONE_COLUMN = "org.talend.survivorship.conflict.two_target_one_column"; //$NON-NLS-1$

    public static final String PKG_NAME_CONFLICT_TWO_TARGET_SAME_VALUE = "org.talend.survivorship.conflict.two_target_same_value"; //$NON-NLS-1$

    public static final Object[][] SAMPLE_INPUT = {
            { "GRIZZARD CO.", "110 N MARYLAND AVE", "GLENDALE", "CA", "912066", "FR", "8185431314", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                    stringToDate("20110101", "yyyyMMdd"), 1.0, 18, 1985, "Something" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "GRIZZARD", "110 NORTH MARYLAND AVENUE", "GLENDALE", "CA", "91205", "US", "9003254892", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                    stringToDate("20110118", "yyyyMMdd"), 0.9879999999999999, 25, 0, "" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            { "GRIZZARD INC", "110 N. MARYLAND AVENUE", "GLENDALE", "CA", "91206", "US", "(818) 543-1315", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                    stringToDate("20110103", "yyyyMMdd"), 0.8572727272727272, 31, 1970, null }, //$NON-NLS-1$ //$NON-NLS-2$
            { "GRIZZARD CO", "1480 S COLORADO BOULEVARD", "LOS ANGELES", "CA", "91206", "US", "(800) 325-4892", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
                    stringToDate("20110115", "yyyyMMdd"), 0.742319482, 35, 0, null } }; //$NON-NLS-1$ //$NON-NLS-2$

    public static final Object[][] SAMPLE_INPUT_CONFLICT = {
            { "Lili", "Cheng", "Walker", "beijing", 1, stringToDate("04-04-2000", "dd-MM-yyyy"), "1", 7 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            { "Tony", null, "Martain", "shanghai", 1, stringToDate("06-06-2000", "dd-MM-yyyy"), "1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
            { "Tom", "Green", null, "shanghai", 3, stringToDate("04-04-2000", "dd-MM-yyyy"), "1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            { "san", "Green", null, "hebeihebei", 2, stringToDate("08-08-2000", "dd-MM-yyyy"), "1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            { "li", "Brown", "Joseph", "beijing", 2, stringToDate("08-08-2000", "dd-MM-yyyy"), "1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            { "Tony", null, "William", null, 2, null, "1", 0 }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            { "Lili", "si", "Allen", null, 2, null, "1", 0 } }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 

    public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
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

    public static final LinkedHashMap<String, String> COLUMNS_CONFLICT = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {

            put("firstName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("lastName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("middleName", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city1", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("city2", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("id", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$
            put("birthday", "java.util.Date"); //$NON-NLS-1$ //$NON-NLS-2$
            put("gid", "String"); //$NON-NLS-1$ //$NON-NLS-2$
            put("grp_size", "Integer"); //$NON-NLS-1$ //$NON-NLS-2$

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
            new RuleDefinition(Order.MT, null, null, null, null, "phone", true) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT = { new RuleDefinition(Order.SEQ, "more_common_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
            Function.MostCommon, null, "birthday", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_LONG = { new RuleDefinition(Order.SEQ, "more_common_city", "city1", //$NON-NLS-1$ //$NON-NLS-2$
            Function.MostCommon, null, "city1", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_LONG_RECENT = {
            new RuleDefinition(Order.SEQ, "more_common_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", true) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_LONG_RECENT_NO_IGNORE_BLANK = {
            new RuleDefinition(Order.SEQ, "more_common_firstName", "firstName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "firstName", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_NULL_CONTSTANT = {
            new RuleDefinition(Order.SEQ, "more_common_lastName", "lastName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "lastName", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_FRE_NULL_FRE = {
            new RuleDefinition(Order.SEQ, "more_common_middleName", "middleName", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "middleName", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_ONE_COLUMN = {
            new RuleDefinition(Order.SEQ, "more_recent_birthday", "birthday", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostRecent, null, "birthday", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "Longest_birthday", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Longest, null, "birthday", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_SAME_RESULT = {
            new RuleDefinition(Order.SEQ, "more_common_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "more_common_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.MostCommon, null, "city2", false) }; //$NON-NLS-1$

    public static final RuleDefinition[] RULES_CONFLICT_TWO_TARGET_SAME_RESULT_REFERENCE_COLUMN = {
            new RuleDefinition(Order.SEQ, "shortest_city1", "city1", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Shortest, null, "city1", false), //$NON-NLS-1$
            new RuleDefinition(Order.SEQ, "shortest_city2", "city2", //$NON-NLS-1$ //$NON-NLS-2$
                    Function.Shortest, null, "city2", false) }; //$NON-NLS-1$

    public static final HashMap<String, Object> EXPECTED_SURVIVOR = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {
            put("acctName", "GRIZZARD CO."); //$NON-NLS-1$ //$NON-NLS-2$
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
