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
package org.talend.dataquality.statistics.datetime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTest {

    private static List<String> DATE_SAMPLES;

    private static List<String> TIME_SAMPLES;

    private final Map<String, Set<String>> EXPECTED_FORMATS = new LinkedHashMap<String, Set<String>>() {

        private static final long serialVersionUID = 1L;

        {
            put("3/22/99", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy" })));
            put("22/03/99", new HashSet<String>(Arrays.asList(new String[] //
            { "d/MM/yy", "dd/MM/yy" })));
            put("22.03.99", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy", "d.MM.yy" })));
            put("99-03-22", new HashSet<String>(Arrays.asList(new String[] //
            { "yy-MM-dd" })));
            put("99/03/22", new HashSet<String>(Arrays.asList(new String[] //
            { "yy/MM/dd" })));
            put("99-3-22", new HashSet<String>(Arrays.asList(new String[] //
            { "yy-M-d" })));
            put("Mar 22, 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMM d, yyyy" })));
            put("22 mars 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("22.03.1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d.MM.yyyy", "dd.MM.yyyy" })));
            put("22-Mar-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("22-mar-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("22-Mar-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MMM-yyyy", "d-MMM-yyyy" })));
            put("1999-03-22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd" })));
            put("1999/03/22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy/MM/dd" })));
            put("1999-3-22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d" })));
            put("March 22, 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMMM d, yyyy" })));
            put("22 mars 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("22. März 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d. MMMM yyyy" })));
            put("22 March 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy", "dd MMMM yyyy" })));
            put("22 marzo 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy", "dd MMMM yyyy" })));
            put("March 22, 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMMM d, yyyy" })));
            put("22 mars 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy" })));
            put("1999年3月22日", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy'年'M'月'd'日'" })));
            put("Monday, March 22, 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, MMMM d, yyyy" })));
            put("lundi 22 mars 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy" })));
            put("Montag, 22. März 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, d. MMMM yyyy" })));
            put("Monday, 22 March 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, d MMMM yyyy" })));
            put("lunedì 22 marzo 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy" })));
            put("Monday, March 22, 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, MMMM d, yyyy" })));
            put("lundi 22 mars 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy" })));
            put("1999年3月22日 星期一", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy'年'M'月'd'日' EEEE" })));
            put("3/22/99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy h:mm a" })));
            put("22/03/99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d/MM/yy H:mm", "dd/MM/yy HH:mm" })));
            put("22.03.99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy HH:mm", "dd.MM.yy H:mm", "d.MM.yy H:mm" })));
            put("22/03/99 5.06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yy H.mm" })));
            put("22/03/99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yy h:mm a" })));
            put("99-03-22 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy-MM-dd HH:mm" })));
            put("99/03/22 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy/MM/dd H:mm" })));
            put("99-3-22 上午5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy-M-d ah:mm" })));
            put("Mar 22, 1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MMM d, yyyy h:mm:ss a" })));
            put("22 mars 1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMM yyyy HH:mm:ss" })));
            put("22.03.1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy H:mm:ss", "dd.MM.yyyy HH:mm:ss", "d.MM.yyyy H:mm:ss" })));
            put("22-Mar-1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MMM-yyyy HH:mm:ss" })));
            put("22-mar-1999 5.06.07", new HashSet<String>(Arrays.asList(new String[] //
            { "d-MMM-yyyy H.mm.ss" })));
            put("22-Mar-1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "d-MMM-yyyy h:mm:ss a" })));
            put("1999-03-22 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd H:mm:ss", "yyyy-MM-dd HH:mm:ss" })));
            put("1999/03/22 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy/MM/dd H:mm:ss" })));
            put("1999-3-22 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d H:mm:ss" })));
            put("March 22, 1999 5:06:07 AM CET", new HashSet<String>(Arrays.asList(new String[] //
            { "MMMM d, yyyy h:mm:ss a z" })));
            put("22 mars 1999 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("22. März 1999 05:06:07 MEZ", new HashSet<String>(Arrays.asList(new String[] //
            { "d. MMMM yyyy HH:mm:ss z" })));
            put("22 March 1999 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("22 marzo 1999 5.06.07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMMM yyyy H.mm.ss z" })));
            put("March 22, 1999 5:06:07 CET AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MMMM d, yyyy h:mm:ss z a" })));
            put("22 mars 1999 05:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z" })));
            put("1999/03/22 5:06:07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy/MM/dd H:mm:ss z" })));
            put("1999年3月22日 上午05时06分07秒", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy'年'M'月'd'日' ahh'时'mm'分'ss'秒'" })));
            put("Monday, March 22, 1999 5:06:07 AM CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, MMMM d, yyyy h:mm:ss a z" })));
            put("lundi 22 mars 1999 05 h 06 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy HH' h 'mm z", "EEEE d MMMM yyyy H' h 'mm z" })));
            put("Montag, 22. März 1999 05:06 Uhr MEZ", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, d. MMMM yyyy HH:mm' Uhr 'z" })));
            put("Monday, 22 March 1999 05:06:07 o'clock CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, d MMMM yyyy HH:mm:ss 'o''clock' z" })));
            put("lunedì 22 marzo 1999 5.06.07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy H.mm.ss z" })));
            put("Monday, March 22, 1999 5:06:07 o'clock AM CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE, MMMM d, yyyy h:mm:ss 'o''clock' a z" })));
            put("lundi 22 mars 1999 5 h 06 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "EEEE d MMMM yyyy H' h 'mm z" })));
            put("1999年3月22日 5時06分07秒 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy'年'M'月'd'日' H'時'mm'分'ss'秒' z" })));
            put("1999年3月22日 星期一 上午05时06分07秒 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy'年'M'月'd'日' EEEE ahh'时'mm'分'ss'秒' z" })));
            put("22/03/99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yy HH:mm:ss" })));
            put("22.03.99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy HH:mm:ss" })));
            put("22.03.1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy HH:mm" })));
            put("99/03/22 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yy/MM/dd H:mm:ss" })));
            put("1999/03/22 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy/MM/dd H:mm" })));
            put("22/03/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy" })));
            put("22/03/1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy h:mm a" })));
            put("22/03/1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy h:mm:ss a" })));
            put("22/03/1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy H:mm", "dd/MM/yyyy HH:mm" })));
            put("22/03/1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy H:mm:ss" })));
            put("22/03/1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy H:mm" })));
            put("22/03/1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MM/yyyy H:mm:ss" })));
            put("22/3/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy" })));
            put("22/3/1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy h:mm a" })));
            put("22/3/1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy h:mm:ss a" })));
            put("22/3/1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy HH:mm", "d/M/yyyy H:mm" })));
            put("22/3/1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy H:mm:ss", "d/M/yyyy HH:mm:ss" })));
            put("22/3/1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy H:mm" })));
            put("22/3/1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "d/M/yyyy H:mm:ss" })));
            put("03/22/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy" })));
            put("03/22/1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy h:mm a" })));
            put("03/22/1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy h:mm:ss a" })));
            put("03/22/1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy H:mm", "MM/dd/yyyy HH:mm" })));
            put("03/22/1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy H:mm:ss" })));
            put("03/22/1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy H:mm" })));
            put("03/22/1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yyyy H:mm:ss" })));
            put("3/22/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy" })));
            put("3/22/1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy h:mm a" })));
            put("3/22/1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy h:mm:ss a" })));
            put("3/22/1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy HH:mm", "M/d/yyyy H:mm" })));
            put("3/22/1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy HH:mm:ss", "M/d/yyyy H:mm:ss" })));
            put("3/22/1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy H:mm" })));
            put("3/22/1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yyyy H:mm:ss" })));
            put("03-22-99", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy" })));
            put("03-22-99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy h:mm a" })));
            put("03-22-99 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy h:mm:ss a" })));
            put("03-22-99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy HH:mm", "MM-dd-yy H:mm" })));
            put("03-22-99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy HH:mm:ss", "MM-dd-yy H:mm:ss" })));
            put("03-22-99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy H:mm" })));
            put("03-22-99 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yy H:mm:ss" })));
            put("3-22-99", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy" })));
            put("3-22-99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy h:mm a" })));
            put("3-22-99 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy h:mm:ss a" })));
            put("3-22-99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy H:mm", "M-d-yy HH:mm" })));
            put("3-22-99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy HH:mm:ss", "M-d-yy H:mm:ss" })));
            put("3-22-99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy H:mm" })));
            put("3-22-99 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yy H:mm:ss" })));
            put("03-22-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy" })));
            put("03-22-1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy h:mm a" })));
            put("03-22-1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy h:mm:ss a" })));
            put("03-22-1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy H:mm", "MM-dd-yyyy HH:mm" })));
            put("03-22-1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy H:mm:ss", "MM-dd-yyyy HH:mm:ss" })));
            put("03-22-1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy H:mm" })));
            put("03-22-1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM-dd-yyyy H:mm:ss" })));
            put("3-22-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy" })));
            put("3-22-1999 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy h:mm a" })));
            put("3-22-1999 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy h:mm:ss a" })));
            put("3-22-1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy H:mm", "M-d-yyyy HH:mm" })));
            put("3-22-1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy HH:mm:ss", "M-d-yyyy H:mm:ss" })));
            put("3-22-1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy H:mm" })));
            put("3-22-1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M-d-yyyy H:mm:ss" })));
            put("1999-03-22 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd h:mm a" })));
            put("1999-03-22 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd h:mm:ss a" })));
            put("1999-03-22 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd HH:mm", "yyyy-MM-dd H:mm" })));
            put("1999-03-22 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd H:mm" })));
            put("1999-03-22 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd H:mm:ss" })));
            put("1999-3-22 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d h:mm a" })));
            put("1999-3-22 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d h:mm:ss a" })));
            put("1999-3-22 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d HH:mm", "yyyy-M-d H:mm" })));
            put("1999-3-22 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d HH:mm:ss", "yyyy-M-d H:mm:ss" })));
            put("1999-3-22 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-M-d H:mm" })));
            put("03/22/99", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy" })));
            put("03/22/99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy h:mm a" })));
            put("03/22/99 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy h:mm:ss a" })));
            put("03/22/99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy H:mm", "MM/dd/yy HH:mm" })));
            put("03/22/99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy HH:mm:ss", "MM/dd/yy H:mm:ss" })));
            put("03/22/99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy H:mm" })));
            put("03/22/99 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "MM/dd/yy H:mm:ss" })));
            put("3/22/99 5:06:07 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy h:mm:ss a" })));
            put("3/22/99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy HH:mm", "M/d/yy H:mm" })));
            put("3/22/99 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy HH:mm:ss", "M/d/yy H:mm:ss" })));
            put("3/22/99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy H:mm" })));
            put("3/22/99 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "M/d/yy H:mm:ss" })));
            put("Mar 22 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMM d yyyy" })));
            put("Mar.22.1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMM.dd.yyyy" })));
            put("March 22 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "MMMM d yyyy" })));
            put("1999-03-22 05:06:07.0", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd HH:mm:ss.S" })));
            put("22/Mar/1999 5:06:07 +0100", new HashSet<String>(Arrays.asList(new String[] //
            { "d/MMM/yyyy H:mm:ss Z" })));
            put("22-Mar-99 05.06.07.000000888 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MMM-yy hh.mm.ss.nnnnnnnnn a" })));
            put("Mon Mar 22 05:06:07 CET 1999", new HashSet<String>(Arrays.asList(new String[] //
            { "EEE MMM dd HH:mm:ss z yyyy" })));
            put("22/Mar/99 5:06 AM", new HashSet<String>(Arrays.asList(new String[] //
            { "dd/MMM/yy h:mm a" })));
            put("1999/3/22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy/M/d" })));
            put("19990322+0100", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyyMMddZ" })));
            put("19990322", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyyMMdd" })));
            put("1999-03-22 AD", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd G" })));
            put("1999-03-22+01:00", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-ddXXX" })));
            put("1999-03-22T05:06:07.000[Europe/Paris]", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'" })));
            put("1999-03-22T05:06:07.000", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss.SSS" })));
            put("1999-03-22T05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss" })));
            put("1999-03-22T05:06:07.000Z", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" })));
            put("1999-03-22T05:06:07.000+01:00", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" })));
            put("1999-03-22T05:06:07+01:00", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ssXXX" })));
            put("1999-081+01:00", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-DDDXXX" })));
            put("1999W132", new HashSet<String>(Arrays.asList(new String[] //
            { "YYYY'W'wc" })));
            put("1999-W13-2", new HashSet<String>(Arrays.asList(new String[] //
            { "YYYY-'W'w-c" })));
            put("1999-03-22T05:06:07.000+01:00[Europe/Paris]", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'" })));
            put("1999-03-22T05:06:07+01:00[Europe/Paris]", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'" })));
            put("Mon, 22 Mar 1999 05:06:07 +0100", new HashSet<String>(Arrays.asList(new String[] //
            { "EEE, d MMM yyyy HH:mm:ss Z" })));
            put("22 Mar 1999 05:06:07 +0100", new HashSet<String>(Arrays.asList(new String[] //
            { "d MMM yyyy HH:mm:ss Z" })));
            put("22.3.99", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy" })));
            put("22-03-99", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MM-yy" })));
            put("22/03/99", new HashSet<String>(Arrays.asList(new String[] //
            { "d/MM/yy", "dd/MM/yy" })));
            put("22.03.99", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy", "d.MM.yy" })));
            put("22.3.1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy" })));
            put("1999.03.22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd" })));
            put("1999.03.22.", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd." })));
            put("99. 3. 22", new HashSet<String>(Arrays.asList(new String[] //
            { "yy. M. d" })));
            put("99.3.22", new HashSet<String>(Arrays.asList(new String[] //
            { "yy.M.d" })));
            put("99.22.3", new HashSet<String>(Arrays.asList(new String[] //
            { "yy.d.M" })));
            put("22-3-99", new HashSet<String>(Arrays.asList(new String[] //
            { "d-M-yy" })));
            put("22-03-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MM-yyyy" })));
            put("22.3.99.", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy." })));
            put("22.03.1999", new HashSet<String>(Arrays.asList(new String[] //
            { "d.MM.yyyy", "dd.MM.yyyy" })));
            put("1999. 3. 22", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy. M. d" })));
            put("1999.22.3", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.d.M" })));
            put("22.03.1999.", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy." })));
            put("22.3.99 5.06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy H.mm" })));
            put("22.3.99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy H:mm" })));
            put("22-03-99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MM-yy HH:mm" })));
            put("22/03/99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d/MM/yy H:mm" })));
            put("22.03.99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy H:mm", "d.MM.yy H:mm" })));
            put("22.3.1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy H:mm" })));
            put("99/03/22 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy/MM/dd HH:mm", "yy/MM/dd H:mm" })));
            put("05:06 22/03/99", new HashSet<String>(Arrays.asList(new String[] //
            { "HH:mm dd/MM/yy" })));
            put("1999.03.22 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd HH:mm" })));
            put("1999.03.22. 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd. H:mm" })));
            put("22.3.1999 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy HH:mm", "d.M.yyyy H:mm" })));
            put("99.3.22 05.06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy.M.d HH.mm" })));
            put("99.22.3 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "yy.d.M HH:mm" })));
            put("22.3.99 05:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy H:mm", "d.M.yy HH:mm" })));
            put("22-3-99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "d-M-yy H:mm" })));
            put("22-03-1999 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MM-yyyy H:mm" })));
            put("22.03.99 5:06", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yy H:mm", "d.MM.yy H:mm" })));
            put("99-03-22 5.06.PD", new HashSet<String>(Arrays.asList(new String[] //
            { "yy-MM-dd h.mm.a" })));
            put("22.3.99. 05.06", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yy. HH.mm" })));
            put("05:06 22/03/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "HH:mm dd/MM/yyyy" })));
            put("22.3.1999 5.06.07", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy H.mm.ss" })));
            put("22.3.1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy H:mm:ss" })));
            put("22-03-1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd-MM-yyyy HH:mm:ss" })));
            put("22.03.1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss" })));
            put("05:06:07 22/03/1999", new HashSet<String>(Arrays.asList(new String[] //
            { "HH:mm:ss dd/MM/yyyy" })));
            put("1999.03.22 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd HH:mm:ss" })));
            put("1999.03.22. 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.MM.dd. H:mm:ss" })));
            put("22.3.1999 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy HH:mm:ss", "d.M.yyyy H:mm:ss" })));
            put("1999-03-22 05.06.07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd HH.mm.ss" })));
            put("1999.22.3 05:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy.d.M HH:mm:ss" })));
            put("22.3.1999 05:06:", new HashSet<String>(Arrays.asList(new String[] //
            { "d.M.yyyy HH:mm:" })));
            put("22.03.1999 5:06:07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss" })));
            put("1999-03-22 5:06:07.PD", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd h:mm:ss.a" })));
            put("22.03.1999. 05.06.07", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy. HH.mm.ss" })));
            put("05:06:07 22-03-1999", new HashSet<String>(Arrays.asList(new String[] //
            { "HH:mm:ss dd-MM-yyyy" })));
            put("1999-03-22 5.06.07.PD CET", new HashSet<String>(Arrays.asList(new String[] //
            { "yyyy-MM-dd h.mm.ss.a z" })));
            put("22.03.1999. 05.06.07 CET", new HashSet<String>(Arrays.asList(new String[] //
            { "dd.MM.yyyy. HH.mm.ss z" })));
        }
    };

    @BeforeClass
    public static void loadTestData() throws IOException {

        InputStream dateInputStream = SystemDateTimePatternManager.class.getResourceAsStream("DateSampleTable.txt");
        DATE_SAMPLES = IOUtils.readLines(dateInputStream, "UTF-8");
        InputStream timeInputStream = SystemDateTimePatternManager.class.getResourceAsStream("TimeSampleTable.txt");
        TIME_SAMPLES = IOUtils.readLines(timeInputStream, "UTF-8");
    }

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    @Test
    public void testDatesWithMultipleFormats() throws IOException {

        for (String sample : EXPECTED_FORMATS.keySet()) {
            Set<String> patternSet = SystemDateTimePatternManager.datePatternReplace(sample);
            assertEquals("Unexpected Format Set on sample <" + sample + ">", EXPECTED_FORMATS.get(sample), patternSet);
        }
    }

    @Test
    @Ignore
    public void prepareDatesWithMultipleFormats() throws IOException {
        Set<String> datesWithMultipleFormats = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                Set<String> patternSet = SystemDateTimePatternManager.datePatternReplace(sample);

                if (patternSet.size() > 0) {
                    sb.append("put(\"").append(sample).append("\", new HashSet<String>(Arrays.asList(new String[] //\n\t{ ");
                    datesWithMultipleFormats.add(sample);
                    for (String p : patternSet) {
                        sb.append("\"").append(p).append("\",");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(" })));\n");
                }
            }
        }
        System.out.println(sb.toString());
    }

    @Test
    public void testAllSupportedDatesWithRegexes() throws Exception {

        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDateTimePatternManager.isDate(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);
                // System.out.println(SystemDateTimePatternManager.datePatternReplace(sample));

                setFinalStatic(SystemDateTimePatternManager.class.getDeclaredField("dateTimeFormatterCache"),
                        new HashMap<String, DateTimeFormatter>());

                assertTrue(sample + " is expected to be a valid date but actually not.",
                        SystemDateTimePatternManager.isDate(sample));
            }
        }
    }

    @Test
    public void testAllSupportedTimesWithRegexes() throws Exception {

        for (int i = 1; i < TIME_SAMPLES.size(); i++) {
            String line = TIME_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                // String expectedPattern = sampleLine[1];
                // String locale = sampleLine[2];
                // System.out.println(SystemDateTimePatternManager.isTime(sample) + "\t" + locale + "\t" + sample + "\t"
                // + expectedPattern);

                assertTrue(sample + " is expected to be a valid time but actually not.",
                        SystemDateTimePatternManager.isTime(sample));
            }
        }
    }

}
