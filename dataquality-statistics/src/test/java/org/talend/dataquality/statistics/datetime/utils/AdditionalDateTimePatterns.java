package org.talend.dataquality.statistics.datetime.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AdditionalDateTimePatterns {

    private AdditionalDateTimePatterns() {

    }

    /**
     * Add important date pattern here which need to be combined
     * with SHORT and MEDIUM style time of the primary locales.
     */
    static final List<LocaledPattern> OTHER_COMMON_PATTERNS_NEED_COMBINATION = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        // NOTE: do not use patterns containing only one "y" for year part.
        {
            add(new LocaledPattern("dd/MM/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("d/M/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM/dd/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M/d/yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM-dd-yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M-d-yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM-dd-yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M-d-yyyy", Locale.US, "OTHER", false));
            add(new LocaledPattern("yyyy-MM-dd", Locale.US, "OTHER", false));
            add(new LocaledPattern("yyyy-M-d", Locale.US, "OTHER", false));
            add(new LocaledPattern("MM/dd/yy", Locale.US, "OTHER", false));
            add(new LocaledPattern("M/d/yy", Locale.US, "OTHER", false));
        }
    };

    /**
     * Other singular patterns which do not need to be combined
     * with SHORT and MEDIUM style time of the primary locales.
     */
    static final List<LocaledPattern> OTHER_COMMON_PATTERNS = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        // NOTE: do not use patterns containing only one "y" for year part.
        {
            add(new LocaledPattern("MMM d yyyy", Locale.US, "OTHER", false));// Jan 18 2012
            add(new LocaledPattern("MMM.dd.yyyy", Locale.US, "OTHER", false));// Jan.02.2010
            add(new LocaledPattern("MMMM d yyyy", Locale.US, "OTHER", false));// January 18 2012
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.S", Locale.US, "OTHER", true));// 2013-2-14 13:40:51.1
            add(new LocaledPattern("d/MMM/yyyy H:mm:ss Z", Locale.US, "OTHER", true));// 14/Feb/2013 13:40:51 +0100
            add(new LocaledPattern("dd-MMM-yy hh.mm.ss.nnnnnnnnn a", Locale.UK, "OTHER", true)); // default format of
            // java.util.Date
            add(new LocaledPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.US, "OTHER", true));
            add(new LocaledPattern("dd/MMM/yy h:mm a", Locale.US, "OTHER", true)); // data time pattern from jira
            add(new LocaledPattern("yyyy/M/d", Locale.US, "OTHER", false)); // TDQ-13539
            add(new LocaledPattern("MM/dd/yyyy hh:mm:ss a", Locale.US, "OTHER", true)); // TDQ-11557

        }
    };

    /**
     * ISO and RFC datetime patterns
     */
    static final List<LocaledPattern> ISO_RFC_DATETIME_PATTERNS = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        // NOTE: do not use patterns containing only one "y" for year part.
        {
            // 1. BASIC_ISO_DATE
            add(new LocaledPattern("yyyyMMddZ", Locale.US, "BASIC_ISO_DATE", false));
            add(new LocaledPattern("yyyyMMdd", Locale.US, "BASIC_ISO_DATE", false));
            // 2. ISO_DATE
            add(new LocaledPattern("yyyy-MM-dd G", Locale.US, "ISO_DATE", false));// 2017-05-27 AD
            add(new LocaledPattern("yyyy-MM-ddXXX", Locale.US, "ISO_DATE", false));
            add(new LocaledPattern("yyyy-MM-dd", Locale.US, "ISO_DATE", false));
            // 3. ISO_DATE_TIME
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']'", Locale.US, "ISO_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss,SSS'['VV']'", Locale.US, "ISO_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US, "ISO_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss,SSS", Locale.US, "ISO_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US, "ISO_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.SSS'['VV']'", Locale.US, "ISO_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss,SSS'['VV']'", Locale.US, "ISO_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US, "ISO_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss,SSS", Locale.US, "ISO_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss", Locale.US, "ISO_DATE_TIME", true));
            // 4. ISO_INSTANT
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US, "ISO_INSTANT", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss,SSS'Z'", Locale.US, "ISO_INSTANT", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US, "ISO_INSTANT", true)); // TDQ-18441
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.SSS'Z'", Locale.US, "ISO_INSTANT", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss,SSS'Z'", Locale.US, "ISO_INSTANT", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss'Z'", Locale.US, "ISO_INSTANT", true)); // TDQ-18441
            // 5. ISO_LOCAL_DATE (removed because they are duplicated with existing patterns)
            // 6. ISO_LOCAL_DATE_TIME (removed because they are duplicated with existing patterns)
            // 7. ISO_OFFSET_DATE
            add(new LocaledPattern("yyyy-MM-ddXXX", Locale.US, "ISO_OFFSET_DATE", false));
            // 8. ISO_OFFSET_DATE_TIME
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-18441
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssX", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-18441
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss,SSSXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ssZ", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-18441
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ssX", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-18441
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ssXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.SSSXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss,SSSXXX", Locale.US, "ISO_OFFSET_DATE_TIME", true)); // TDQ-16796

            // 9. ISO_ORDINAL_DATE
            add(new LocaledPattern("yyyy-DDDXXX", Locale.US, "ISO", false));
            // 10. ISO_WEEK_BASED_DATE
            add(new LocaledPattern("YYYY'W'wc", Locale.US, "ISO", false)); // compact form
            add(new LocaledPattern("YYYY-'W'w-c", Locale.US, "ISO", false)); // extended form
            // 11. ISO_ZONED_DATE_TIME
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ss,SSSXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss.SSSXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ss,SSSXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true)); // TDQ-16796
            add(new LocaledPattern("yyyy-MM-dd HH:mm:ssXXX'['VV']'", Locale.US, "ISO_ZONED_DATE_TIME", true));
            // 12. RFC_1123_DATE_TIME
            add(new LocaledPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US, "RFC1123_WITH_DAY", true));
            add(new LocaledPattern("d MMM yyyy HH:mm:ss Z", Locale.US, "RFC1123", true));
        }
    };

    /**
     * Additional time patterns with offset time zone
     */
    static final List<LocaledPattern> OFFSET_TIME_ZONE_PATTERNS = new ArrayList<LocaledPattern>() {

        private static final long serialVersionUID = 1L;
        {
            add(new LocaledPattern("H:mmX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mmX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mmX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mmX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H:mm:ssX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mm.ssX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mm:ssX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mm.ssX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H:mmZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mmZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mmZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mmZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("H:mm:ssZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mm.ssZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mm:ssZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mm.ssZ", Locale.US, "OTHER", true));
            add(new LocaledPattern("H:mmXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mmXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mmXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mmXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H:mm:ssXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("H.mm.ssXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH:mm:ssXXX", Locale.US, "OTHER", true));
            add(new LocaledPattern("HH.mm.ssXXX", Locale.US, "OTHER", true));
        }
    };
}
