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
public final class HermesData {

    public static final String RULE_PATH = "src/test/resources/generated/";

    public static final String PKG_NAME = "hermes";

    public static final Object[][] SAMPLE_INPUT_1 = {
            { "1", "Internet", stringToDate("01/01/2016", "dd/MM/yyyy"), "test1@taland.com", "0123456789", "A", 2 },
            { "2", "Europe", stringToDate("02/02/2016", "dd/MM/yyyy"), "test2@talend.com", "", "A", 0 }, };

    public static final Object[][] SAMPLE_INPUT_2 = {
            { "3", "Europe", stringToDate("01/01/2016", "dd/MM/yyyy"), "titi@test.com", "+33(0)3 33 33 33 33", "B", 3 },
            { "4", "Amérique", stringToDate("05/05/2016", "dd/MM/yyyy"), "toto@free.fr", "+33 1 44 44 44 44", "B", 0 },
            { "5", "Asie", stringToDate("05/05/2016", "dd/MM/yyyy"), "tata@company.com", "05 55 55 55 55", "B", 0 }, };

    public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {

        private static final long serialVersionUID = 1L;
        {
            put("client", "String");
            put("filiale", "String");
            put("modif_date", "java.util.Date");
            put("email", "String");
            put("phone", "String");
            put("GID", "String");
            put("GRP_SIZE", "int");
        }
    };

    public static final RuleDefinition[] RULES_1 = {
            new RuleDefinition(Order.SEQ, "EmFiliale", "filiale", Function.Expression, ".equals(\"Internet\")", "email", true), //
            new RuleDefinition(Order.SEQ, "EmRecent", "modif_date", Function.MostRecent, "", "email", true), //

    };

    public static final RuleDefinition[] RULES_2 = {
            new RuleDefinition(Order.SEQ, "EmRecent", "modif_date", Function.MostRecent, "", "modif_date", true), //
            new RuleDefinition(Order.MT, null, null, null, null, "email", true), //
            new RuleDefinition(Order.SEQ, "Emfiliale", "filiale", Function.Expression, ".equals(\"Internet\")", "email", true), //
    };

    public static final HashMap<String, Object> EXPECTED_SURVIVOR_GRP1_WITH_RULESET_1 = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {

            put("client", null);
            put("filiale", null);
            put("modif_date", null);
            put("email", "test1@taland.com");
            put("phone", null);
            put("GID", null);
            put("GRP_SIZE", null);

        }
    };

    public static final HashMap<String, Object> EXPECTED_SURVIVOR_GRP1_WITH_RULESET_2 = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {

            put("client", null);
            put("filiale", null);
            put("modif_date", stringToDate("20160202", "yyyyMMdd"));
            put("email", "test2@talend.com");
            put("phone", null);
            put("GID", null);
            put("GRP_SIZE", null);

        }
    };

    public static final HashMap<String, Object> EXPECTED_SURVIVOR_GRP2_WITH_RULESET_1 = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {

            put("client", null);
            put("filiale", null);
            put("modif_date", null);
            put("email", "toto@free.fr");
            put("phone", null);
            put("GID", null);
            put("GRP_SIZE", null);

        }
    };

    public static final HashMap<String, Object> EXPECTED_SURVIVOR_GRP2_WITH_RULESET_2 = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;
        {

            put("client", null);
            put("filiale", null);
            put("modif_date", stringToDate("20160505", "yyyyMMdd"));
            put("email", "toto@free.fr");
            put("phone", null);
            put("GID", null);
            put("GRP_SIZE", null);

        }
    };

    public static final HashSet<String> EXPECTED_CONFLICT_OF_SURVIVOR = new HashSet<String>() {

        private static final long serialVersionUID = 1L;
        {
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
