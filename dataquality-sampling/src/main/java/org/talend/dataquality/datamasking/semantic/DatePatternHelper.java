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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

class DatePatternHelper {

    private static final Logger LOGGER = Logger.getLogger(DatePatternHelper.class);

    private static List<Map<Pattern, String>> DATE_PATTERN_GROUP_LIST = new ArrayList<Map<Pattern, String>>();

    private DatePatternHelper() {

    }

    static {
        try {
            // Load date patterns
            loadPatterns("DateRegexesGrouped.txt", DATE_PATTERN_GROUP_LIST);
        } catch (IOException e) {
            LOGGER.error("Unable to get date patterns.", e);
        }
    }

    private static void loadPatterns(String patternFileName, List<Map<Pattern, String>> patternParsers) throws IOException {
        InputStream stream = DatePatternHelper.class.getResourceAsStream(patternFileName);
        try {
            List<String> lines = IOUtils.readLines(stream, "UTF-8");
            Map<Pattern, String> currentGroupMap = new LinkedHashMap<Pattern, String>();
            patternParsers.add(currentGroupMap);
            for (String line : lines) {
                if (!"".equals(line.trim())) { // Not empty
                    if (line.startsWith("--")) { // group separator
                        currentGroupMap = new LinkedHashMap<Pattern, String>();
                        patternParsers.add(currentGroupMap);
                    } else {
                        String[] lineArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t");
                        String format = lineArray[0];
                        Pattern pattern = Pattern.compile(lineArray[1]);
                        currentGroupMap.put(pattern, format);
                    }
                }
            }
            stream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String guessDatePattern(String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        for (Map<Pattern, String> patternMap : DATE_PATTERN_GROUP_LIST) {
            for (Pattern parser : patternMap.keySet()) {
                if (parser.matcher(value).find()) {
                    return patternMap.get(parser);
                }
            }
        }
        return StringUtils.EMPTY;
    }

}
