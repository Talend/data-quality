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
            put("3/22/99", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy")));
            put("22/03/99", new HashSet<>(Arrays
                    .asList(//
                            "d/MM/yy", "dd/MM/yy")));
            put("22.03.99", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yy", "d.MM.yy")));
            put("99-03-22", new HashSet<>(Arrays
                    .asList(//
                            "yy-MM-dd")));
            put("99/03/22", new HashSet<>(Arrays
                    .asList(//
                            "yy/MM/dd")));
            put("99-3-22", new HashSet<>(Arrays
                    .asList(//
                            "yy-M-d")));
            put("Mar 22, 1999", new HashSet<>(Arrays
                    .asList(//
                            "MMM d, yyyy")));
            put("22 mars 1999", new HashSet<>(Arrays
                    .asList(//
                            "d MMMM yyyy", "d MMM yyyy", "dd MMMM yyyy")));
            put("22.03.1999", new HashSet<>(Arrays
                    .asList(//
                            "d.MM.yyyy", "dd.MM.yyyy")));
            put("22-Mar-1999", new HashSet<>(Arrays
                    .asList(//
                            "dd-MMM-yyyy", "d-MMM-yyyy")));
            put("22-mar-1999", new HashSet<>(Arrays
                    .asList(//
                            "dd-MMM-yyyy", "d-MMM-yyyy")));
            put("1999-03-22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd")));
            put("1999/03/22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy/MM/dd")));
            put("1999-3-22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d")));
            put("March 22, 1999", new HashSet<>(Arrays
                    .asList(//
                            "MMMM d, yyyy")));
            put("22. März 1999", new HashSet<>(Arrays
                    .asList(//
                            "d. MMMM yyyy")));
            put("22 March 1999", new HashSet<>(Arrays
                    .asList(//
                            "d MMMM yyyy", "dd MMMM yyyy")));
            put("22 marzo 1999", new HashSet<>(Arrays
                    .asList(//
                            "d MMMM yyyy", "dd MMMM yyyy")));
            put("1999年3月22日", new HashSet<>(Arrays
                    .asList(//
                            "yyyy'年'M'月'd'日'")));
            put("Monday, March 22, 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, MMMM d, yyyy")));
            put("lundi 22 mars 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEEE d MMMM yyyy")));
            put("Montag, 22. März 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, d. MMMM yyyy")));
            put("Monday, 22 March 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, d MMMM yyyy")));
            put("lunedì 22 marzo 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEEE d MMMM yyyy")));
            put("1999年3月22日 星期一", new HashSet<>(Arrays
                    .asList(//
                            "yyyy'年'M'月'd'日' EEEE")));
            put("3/22/99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy h:mm a")));
            put("22/03/99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yy HH:mm")));
            put("22.03.99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yy HH:mm")));
            put("22/03/99 5.06", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yy H.mm")));
            put("22/03/99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yy h:mm a")));
            put("99-03-22 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yy-MM-dd HH:mm")));
            put("99/03/22 5:06", new HashSet<>(Arrays
                    .asList(//
                            "yy/MM/dd H:mm")));
            put("99-3-22 上午5:06", new HashSet<>(Arrays
                    .asList(//
                            "yy-M-d ah:mm")));
            put("Mar 22, 1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MMM d, yyyy h:mm:ss a")));
            put("22 mars 1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "d MMM yyyy HH:mm:ss")));
            put("22.03.1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy HH:mm:ss")));
            put("22-Mar-1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd-MMM-yyyy HH:mm:ss")));
            put("22-mar-1999 5.06.07", new HashSet<>(Arrays
                    .asList(//
                            "d-MMM-yyyy H.mm.ss")));
            put("22-Mar-1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "d-MMM-yyyy h:mm:ss a")));
            put("1999-03-22 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss")));
            put("1999/03/22 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy/MM/dd H:mm:ss")));
            put("1999-3-22 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d H:mm:ss")));
            put("March 22, 1999 5:06:07 AM CET", new HashSet<>(Arrays
                    .asList(//
                            "MMMM d, yyyy h:mm:ss a z")));
            put("22 mars 1999 05:06:07 CET", new HashSet<>(Arrays
                    .asList(//
                            "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z")));
            put("22. März 1999 05:06:07 MEZ", new HashSet<>(Arrays
                    .asList(//
                            "d. MMMM yyyy HH:mm:ss z")));
            put("22 March 1999 05:06:07 CET", new HashSet<>(Arrays
                    .asList(//
                            "dd MMMM yyyy HH:mm:ss z", "d MMMM yyyy HH:mm:ss z")));
            put("22 marzo 1999 5.06.07 CET", new HashSet<>(Arrays
                    .asList(//
                            "d MMMM yyyy H.mm.ss z")));
            put("March 22, 1999 5:06:07 CET AM", new HashSet<>(Arrays
                    .asList(//
                            "MMMM d, yyyy h:mm:ss z a")));
            put("1999/03/22 5:06:07 CET", new HashSet<>(Arrays
                    .asList(//
                            "yyyy/MM/dd H:mm:ss z")));
            put("1999年3月22日 上午05时06分07秒", new HashSet<>(Arrays
                    .asList(//
                            "yyyy'年'M'月'd'日' ahh'时'mm'分'ss'秒'")));
            put("Monday, March 22, 1999 5:06:07 AM CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, MMMM d, yyyy h:mm:ss a z")));
            put("lundi 22 mars 1999 05 h 06 CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE d MMMM yyyy HH' h 'mm z")));
            put("Montag, 22. März 1999 05:06 Uhr MEZ", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, d. MMMM yyyy HH:mm' Uhr 'z")));
            put("Monday, 22 March 1999 05:06:07 o'clock CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, d MMMM yyyy HH:mm:ss 'o''clock' z")));
            put("lunedì 22 marzo 1999 5.06.07 CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE d MMMM yyyy H.mm.ss z")));
            put("Monday, March 22, 1999 5:06:07 o'clock AM CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE, MMMM d, yyyy h:mm:ss 'o''clock' a z")));
            put("lundi 22 mars 1999 5 h 06 CET", new HashSet<>(Arrays
                    .asList(//
                            "EEEE d MMMM yyyy H' h 'mm z")));
            put("1999年3月22日 5時06分07秒 CET", new HashSet<>(Arrays
                    .asList(//
                            "yyyy'年'M'月'd'日' H'時'mm'分'ss'秒' z")));
            put("1999年3月22日 星期一 上午05时06分07秒 CET", new HashSet<>(Arrays
                    .asList(//
                            "yyyy'年'M'月'd'日' EEEE ahh'时'mm'分'ss'秒' z")));
            put("22/03/99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yy HH:mm:ss")));
            put("22.03.99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yy HH:mm:ss")));
            put("22.03.1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy HH:mm")));
            put("99/03/22 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yy/MM/dd H:mm:ss")));
            put("1999/03/22 5:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy/MM/dd H:mm")));
            put("22/03/1999", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy")));
            put("22/03/1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy h:mm a")));
            put("22/03/1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy h:mm:ss a")));
            put("22/03/1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy HH:mm")));
            put("22/03/1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy HH:mm:ss")));
            put("22/03/1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy H:mm")));
            put("22/03/1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd/MM/yyyy H:mm:ss")));
            put("22/3/1999", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy")));
            put("22/3/1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy h:mm a")));
            put("22/3/1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy h:mm:ss a")));
            put("22/3/1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy HH:mm")));
            put("22/3/1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy HH:mm:ss")));
            put("22/3/1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy H:mm")));
            put("22/3/1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "d/M/yyyy H:mm:ss")));
            put("03/22/1999", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy")));
            put("03/22/1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy h:mm a")));
            put("03/22/1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy h:mm:ss a")));
            put("03/22/1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy HH:mm")));
            put("03/22/1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy HH:mm:ss")));
            put("03/22/1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy H:mm")));
            put("03/22/1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy H:mm:ss")));
            put("3/22/1999", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy")));
            put("3/22/1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy h:mm a")));
            put("3/22/1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy h:mm:ss a")));
            put("3/22/1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy HH:mm")));
            put("3/22/1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy HH:mm:ss")));
            put("3/22/1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy H:mm")));
            put("3/22/1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yyyy H:mm:ss")));
            put("03-22-99", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy")));
            put("03-22-99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy h:mm a")));
            put("03-22-99 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy h:mm:ss a")));
            put("03-22-99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy HH:mm")));
            put("03-22-99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy HH:mm:ss")));
            put("03-22-99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy H:mm")));
            put("03-22-99 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yy H:mm:ss")));
            put("3-22-99", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy")));
            put("3-22-99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy h:mm a")));
            put("3-22-99 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy h:mm:ss a")));
            put("3-22-99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy HH:mm")));
            put("3-22-99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy HH:mm:ss")));
            put("3-22-99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy H:mm")));
            put("3-22-99 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yy H:mm:ss")));
            put("03-22-1999", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy")));
            put("03-22-1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy h:mm a")));
            put("03-22-1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy h:mm:ss a")));
            put("03-22-1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy HH:mm")));
            put("03-22-1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy HH:mm:ss")));
            put("03-22-1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy H:mm")));
            put("03-22-1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM-dd-yyyy H:mm:ss")));
            put("3-22-1999", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy")));
            put("3-22-1999 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy h:mm a")));
            put("3-22-1999 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy h:mm:ss a")));
            put("3-22-1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy HH:mm")));
            put("3-22-1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy HH:mm:ss")));
            put("3-22-1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy H:mm")));
            put("3-22-1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M-d-yyyy H:mm:ss")));
            put("1999-03-22 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd h:mm a")));
            put("1999-03-22 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd h:mm:ss a")));
            put("1999-03-22 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm")));
            put("1999-03-22 5:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd H:mm")));
            put("1999-03-22 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd H:mm:ss")));
            put("1999-3-22 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d h:mm a")));
            put("1999-3-22 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d h:mm:ss a")));
            put("1999-3-22 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d HH:mm")));
            put("1999-3-22 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d HH:mm:ss")));
            put("1999-3-22 5:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-M-d H:mm")));
            put("03/22/99", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy")));
            put("03/22/99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy h:mm a")));
            put("03/22/99 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy h:mm:ss a")));
            put("03/22/99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy HH:mm")));
            put("03/22/99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy HH:mm:ss")));
            put("03/22/99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy H:mm")));
            put("03/22/99 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yy H:mm:ss")));
            put("3/22/99 5:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy h:mm:ss a")));
            put("3/22/99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy HH:mm")));
            put("3/22/99 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy HH:mm:ss")));
            put("3/22/99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy H:mm")));
            put("3/22/99 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "M/d/yy H:mm:ss")));
            put("Mar 22 1999", new HashSet<>(Arrays
                    .asList(//
                            "MMM d yyyy")));
            put("Mar.22.1999", new HashSet<>(Arrays
                    .asList(//
                            "MMM.dd.yyyy")));
            put("March 22 1999", new HashSet<>(Arrays
                    .asList(//
                            "MMMM d yyyy")));
            put("1999-03-22 05:06:07.0", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.S")));
            put("22/Mar/1999 5:06:07 +0100", new HashSet<>(Arrays
                    .asList(//
                            "d/MMM/yyyy H:mm:ss Z")));
            put("22-Mar-99 05.06.07.000000888 AM", new HashSet<>(Arrays
                    .asList(//
                            "dd-MMM-yy hh.mm.ss.nnnnnnnnn a")));
            put("Mon Mar 22 05:06:07 CET 1999", new HashSet<>(Arrays
                    .asList(//
                            "EEE MMM dd HH:mm:ss z yyyy")));
            put("22/Mar/99 5:06 AM", new HashSet<>(Arrays
                    .asList(//
                            "dd/MMM/yy h:mm a")));
            put("1999/3/22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy/M/d")));
            put("03/22/1999 05:06:07 AM", new HashSet<>(Arrays
                    .asList(//
                            "MM/dd/yyyy hh:mm:ss a")));
            put("19990322+0100", new HashSet<>(Arrays
                    .asList(//
                            "yyyyMMddZ")));
            put("19990322", new HashSet<>(Arrays
                    .asList(//
                            "yyyyMMdd")));
            put("1999-03-22 AD", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd G")));
            put("1999-03-22+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-ddXXX")));
            put("1999-03-22T05:06:07.000[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'")));
            put("1999-03-22T05:06:07,000[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss,SSS'['VV']'")));
            put("1999-03-22T05:06:07.000", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss.SSS")));
            put("1999-03-22T05:06:07,000", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss,SSS")));
            put("1999-03-22T05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss")));
            put("1999-03-22 05:06:07.000[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.SSS'['VV']'")));
            put("1999-03-22 05:06:07,000[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss,SSS'['VV']'")));
            put("1999-03-22 05:06:07.000", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.SSS")));
            put("1999-03-22 05:06:07,000", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss,SSS")));
            put("1999-03-22T05:06:07.000Z", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
            put("1999-03-22T05:06:07,000Z", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss,SSS'Z'")));
            put("1999-03-22 05:06:07.000Z", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.SSS'Z'")));
            put("1999-03-22 05:06:07,000Z", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss,SSS'Z'")));
            put("1999-03-22T05:06:07.000+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
            put("1999-03-22T05:06:07,000+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss,SSSXXX")));
            put("1999-03-22T05:06:07+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ssXXX")));
            put("1999-03-22 05:06:07.000+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.SSSXXX")));
            put("1999-03-22 05:06:07,000+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss,SSSXXX")));
            put("1999-03-22 05:06:07+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ssXXX")));
            put("1999-081+01:00", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-DDDXXX")));
            put("1999W132", new HashSet<>(Arrays
                    .asList(//
                            "YYYY'W'wc")));
            put("1999-W13-2", new HashSet<>(Arrays
                    .asList(//
                            "YYYY-'W'w-c")));
            put("1999-03-22T05:06:07.000+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'")));
            put("1999-03-22T05:06:07,000+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ss,SSSXXX'['VV']'")));
            put("1999-03-22T05:06:07+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'")));
            put("1999-03-22 05:06:07.000+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss.SSSXXX'['VV']'")));
            put("1999-03-22 05:06:07,000+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ss,SSSXXX'['VV']'")));
            put("1999-03-22 05:06:07+01:00[Europe/Paris]", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH:mm:ssXXX'['VV']'")));
            put("Mon, 22 Mar 1999 05:06:07 +0100", new HashSet<>(Arrays
                    .asList(//
                            "EEE, d MMM yyyy HH:mm:ss Z")));
            put("22 Mar 1999 05:06:07 +0100", new HashSet<>(Arrays
                    .asList(//
                            "d MMM yyyy HH:mm:ss Z")));
            put("22.3.99", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy")));
            put("22-03-99", new HashSet<>(Arrays
                    .asList(//
                            "dd-MM-yy")));
            put("22.3.1999", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy")));
            put("1999.03.22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd")));
            put("1999.03.22.", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd.")));
            put("99. 3. 22", new HashSet<>(Arrays
                    .asList(//
                            "yy. M. d")));
            put("99.3.22", new HashSet<>(Arrays
                    .asList(//
                            "yy.M.d")));
            put("99.22.3", new HashSet<>(Arrays
                    .asList(//
                            "yy.d.M")));
            put("22-3-99", new HashSet<>(Arrays
                    .asList(//
                            "d-M-yy")));
            put("22-03-1999", new HashSet<>(Arrays
                    .asList(//
                            "dd-MM-yyyy")));
            put("22.3.99.", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy.")));
            put("1999. 3. 22", new HashSet<>(Arrays
                    .asList(//
                            "yyyy. M. d")));
            put("1999.22.3", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.d.M")));
            put("22.03.1999.", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy.")));
            put("22.3.99 5.06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy H.mm")));
            put("22.3.99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy H:mm")));
            put("22-03-99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "dd-MM-yy HH:mm")));
            put("22/03/99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "d/MM/yy H:mm")));
            put("22.03.99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yy H:mm", "d.MM.yy H:mm")));
            put("22.3.1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy H:mm")));
            put("99/03/22 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yy/MM/dd HH:mm")));
            put("05:06 22/03/99", new HashSet<>(Arrays
                    .asList(//
                            "HH:mm dd/MM/yy")));
            put("1999.03.22 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd HH:mm")));
            put("1999.03.22. 5:06", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd. H:mm")));
            put("22.3.1999 05:06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy HH:mm")));
            put("99.3.22 05.06", new HashSet<>(Arrays
                    .asList(//
                            "yy.M.d HH.mm")));
            put("99.22.3 05:06", new HashSet<>(Arrays
                    .asList(//
                            "yy.d.M HH:mm")));
            put("22.3.99 05:06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy HH:mm")));
            put("22-3-99 5:06", new HashSet<>(Arrays
                    .asList(//
                            "d-M-yy H:mm")));
            put("22-03-1999 5:06", new HashSet<>(Arrays
                    .asList(//
                            "dd-MM-yyyy H:mm")));
            put("99-03-22 5.06.PD", new HashSet<>(Arrays
                    .asList(//
                            "yy-MM-dd h.mm.a")));
            put("22.3.99. 05.06", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yy. HH.mm")));
            put("05:06 22/03/1999", new HashSet<>(Arrays
                    .asList(//
                            "HH:mm dd/MM/yyyy")));
            put("22.3.1999 5.06.07", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy H.mm.ss")));
            put("22.3.1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy H:mm:ss")));
            put("22-03-1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd-MM-yyyy HH:mm:ss")));
            put("22.03.1999 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy H:mm:ss", "d.MM.yyyy H:mm:ss")));
            put("05:06:07 22/03/1999", new HashSet<>(Arrays
                    .asList(//
                            "HH:mm:ss dd/MM/yyyy")));
            put("1999.03.22 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd HH:mm:ss")));
            put("1999.03.22. 5:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.MM.dd. H:mm:ss")));
            put("22.3.1999 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy HH:mm:ss")));
            put("1999-03-22 05.06.07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd HH.mm.ss")));
            put("1999.22.3 05:06:07", new HashSet<>(Arrays
                    .asList(//
                            "yyyy.d.M HH:mm:ss")));
            put("22.3.1999 05:06:", new HashSet<>(Arrays
                    .asList(//
                            "d.M.yyyy HH:mm:")));
            put("1999-03-22 5:06:07.PD", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd h:mm:ss.a")));
            put("22.03.1999. 05.06.07", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy. HH.mm.ss")));
            put("05:06:07 22-03-1999", new HashSet<>(Arrays
                    .asList(//
                            "HH:mm:ss dd-MM-yyyy")));
            put("1999-03-22 5.06.07.PD CET", new HashSet<>(Arrays
                    .asList(//
                            "yyyy-MM-dd h.mm.ss.a z")));
            put("22.03.1999. 05.06.07 CET", new HashSet<>(Arrays
                    .asList(//
                            "dd.MM.yyyy. HH.mm.ss z")));
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
    public void testDatesWithMultipleFormats() {
        for (String sample : EXPECTED_FORMATS.keySet()) {
            Set<String> patternSet = SystemDateTimePatternManager.getDatePatterns(sample).keySet();
            assertEquals("Unexpected Format Set on sample <" + sample + ">", EXPECTED_FORMATS.get(sample), patternSet);
        }
    }

    @Test
    public void uniquenessOfDateFormats() throws Exception {
        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String date = sampleLine[0];
                String pattern = sampleLine[1];

                if (EXPECTED_FORMATS.containsKey(date)) {
                    // Multiple formats expected
                    continue;
                }

                Set<String> patternSet = SystemDateTimePatternManager.getDatePatterns(date).keySet();
                assertEquals("Unexpected number of patterns for date <" + date + ">", 1, patternSet.size());
                assertEquals("Unexpected pattern on date <" + date + ">", pattern,
                        patternSet.toArray(new String[] {})[0]);
            }
        }
    }

    @Test
    public void testAllSupportedDatesWithRegexes() throws Exception {

        for (int i = 1; i < DATE_SAMPLES.size(); i++) {
            String line = DATE_SAMPLES.get(i);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];

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
            System.out.println(line);
            if (!"".equals(line.trim())) {
                String[] sampleLine = line.trim().split("\t");
                String sample = sampleLine[0];
                assertTrue(sample + " is expected to be a valid time but actually not.",
                        SystemDateTimePatternManager.isTime(sample));
            }
        }
    }

}
