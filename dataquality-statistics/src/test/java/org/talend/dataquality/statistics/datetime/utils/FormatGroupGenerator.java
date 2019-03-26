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
package org.talend.dataquality.statistics.datetime.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.talend.dataquality.statistics.datetime.SystemDateTimePatternManager;

/**
 * Generator of date format groups to improve the performance.
 * Before, we have to try over all regex patterns in order to find out all possible matches.
 * Now, We made exlusive groups of patterns, and we just need to go over by group, and stops at the group where at least one of
 * regexes matches the data.
 */
public class FormatGroupGenerator {

    private static DateTimeFormatCode calculateFormatCode(String format, String regex) {

        String dateSeparator = "?", timeSeparator = "?";
        StringBuilder code = new StringBuilder();

        if (format.contains("dd")) {
            code.append("d");
        } else if (format.contains("d")) {
            code.append("d");
        }

        if (format.contains("MMMM")) {
            code.append("M4");
        } else if (format.contains("MMM")) {
            code.append("M4");
        } else if (format.contains("MM")) {
            code.append("M2");
        } else if (format.contains("M")) {
            code.append("M2");
        }

        if (format.contains("yyyy")) {
            code.append("y4");
            int idx = format.indexOf("yyyy");
            if (idx > 2) {
                dateSeparator = format.charAt(idx - 1) + "";
            } else {
                dateSeparator = format.charAt(idx + 4) + "";
            }
        } else if (format.contains("YYYY")) {
            code.append("y4");
        } else if (format.contains("yy")) {
            code.append("y2");
            int idx = format.indexOf("yy");
            if (idx > 2) {
                dateSeparator = format.charAt(idx - 1) + "";
            } else {
                dateSeparator = format.charAt(idx + 2) + "";
            }
        }
        if (dateSeparator.equals(String.valueOf('M'))) {
            dateSeparator = "?";
        }
        if (dateSeparator.equals(String.valueOf('\''))) {
            dateSeparator = "?";
        }

        if (format.contains("'T'")) {
            code.append("T");
        }

        if (format.contains("hh")) {
            code.append("H");
        } else if (format.contains("h")) {
            code.append("H");
        } else if (format.contains("HH")) {
            code.append("H");
        } else if (format.contains("H")) {
            code.append("H");
        }
        if (format.contains("mm")) {
            code.append("m");
            int idx = format.indexOf("mm");
            if (idx > 2) {
                timeSeparator = format.charAt(idx - 1) + "";
            }
        } else if (format.contains("m")) {
            code.append("m");
            int idx = format.indexOf("m");
            if (idx > 2) {
                timeSeparator = format.charAt(idx - 1) + "";
            }
        }
        if (format.contains("ss")) {
            code.append("s");
        } else if (format.contains("s")) {
            code.append("s");
        }
        if (timeSeparator.equals(String.valueOf('\''))) {
            timeSeparator = "?";
        }
        return new DateTimeFormatCode(format, regex, code.toString(), dateSeparator, timeSeparator);
    }

    public static void generateDateRegexGroups() throws IOException {

        InputStream stream = new FileInputStream(SystemDateTimePatternManager.class.getResource("DateRegexes.txt").getFile()
                .replace("target" + File.separator + "classes", "src" + File.separator + "main" + File.separator + "resources"));
        List<String> lines = IOUtils.readLines(stream);
        Map<String, String> formatRegexMap = new LinkedHashMap<String, String>();
        for (String line : lines) {
            if (!"".equals(line.trim())) {
                String[] lineArray = line.trim().split("\t");
                String format = lineArray[0];
                String regex = lineArray[1];
                formatRegexMap.put(format, regex);
            }
        }
        List<DateTimeFormatCode> formatCodes = new ArrayList<DateTimeFormatCode>();
        for (String format : formatRegexMap.keySet()) {
            formatCodes.add(calculateFormatCode(format, formatRegexMap.get(format)));
        }
        // sortDateTimeFormatCode(formatCodes);

        Map<String, List<DateTimeFormatCode>> formatGroupMap = new LinkedHashMap<String, List<DateTimeFormatCode>>();

        for (DateTimeFormatCode fc : formatCodes) {
            String aggreratedCode = fc.dateSeparator + fc.timeSeparator + fc.code;
            List<DateTimeFormatCode> formatCodeSet = formatGroupMap.get(aggreratedCode);
            if (formatCodeSet == null) {
                formatCodeSet = new ArrayList<DateTimeFormatCode>();
            }
            formatCodeSet.add(fc);
            formatGroupMap.put(aggreratedCode, formatCodeSet);
        }

        StringBuilder sb = new StringBuilder();
        int groupNo = 0;
        List<DateTimeFormatCode> patternsFromSmallGroups = new ArrayList<DateTimeFormatCode>();
        for (String key : formatGroupMap.keySet()) {
            List<DateTimeFormatCode> formatCodeSet = formatGroupMap.get(key);
            if (formatCodeSet.size() < 1) {
                patternsFromSmallGroups.addAll(formatCodeSet);
            } else {
                sb.append("--------Group ").append(++groupNo).append(": [").append(key).append("]---------\n");
                for (DateTimeFormatCode fc : formatCodeSet) {
                    sb.append(fc).append("\n");
                }
            }
        }
        if (patternsFromSmallGroups.size() > 0) {
            System.out.println("--------Group " + (++groupNo) + ": [OTHERS]---------");

            for (DateTimeFormatCode fc : patternsFromSmallGroups) {
                System.out.println(fc);
            }
        }

        // Date Formats
        String pathSeparator = "/"; //$NON-NLS-1$
        String targetPath = SystemDateTimePatternManager.class.getResource("DateRegexesGrouped.txt").getFile(); //$NON-NLS-1$
        String srcPath = targetPath.replace("target" + pathSeparator + "classes",
                "src" + pathSeparator + "main" + pathSeparator + "resources");
        IOUtils.write(sb.toString(), new FileOutputStream(new File(srcPath)));
        // Update DateRegexesGrouped.txt in "dataquality-sampling" at the same time.
        String samplingParent = new File(targetPath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getPath();
        String samplingPath = samplingParent + pathSeparator
                + "dataquality-sampling/src/main/resources/org/talend/dataquality/datamasking/semantic/DateRegexesGrouped.txt"; //$NON-NLS-1$
        IOUtils.write(sb.toString(), new FileOutputStream(new File(samplingPath)));
    }
}

class DateTimeFormatCode {

    String format;

    String regex;

    String code;

    String dateSeparator;

    String timeSeparator;

    public DateTimeFormatCode(String format, String regex, String code, String dateSeparator, String timeSeparator) {
        this.format = format;
        this.regex = regex;
        this.code = code;
        this.dateSeparator = dateSeparator;
        this.timeSeparator = timeSeparator;
    }

    @Override
    public String toString() {
        return format + "\t" + regex;
    }
}
