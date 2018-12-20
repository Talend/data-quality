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
package org.talend.dataquality.standardization.phone;

import java.util.List;
import java.util.Locale;

/**
 * As per {@link #PhoneNumberHandlerBase} ,but use the default region code and default Local language in this class.
 */
public class PhoneNumberHandler {

    private String defaultRegion = Locale.getDefault().getCountry();

    private Locale defaultLocale = Locale.getDefault();

    /**
     * 
     * As per {@link #isValidPhoneNumber(Object, String)} but explicitly the region code is default.
     * 
     * @param data the data that we want to validate
     * @return
     */
    public boolean isValidPhoneNumber(Object data) {
        return PhoneNumberHandlerBase.isValidPhoneNumber(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #isPossiblePhoneNumber(Object, String)} but explicitly the region code is default.
     * 
     * @param data the data that we want to validate
     * @return
     */
    public boolean isPossiblePhoneNumber(Object data) {
        return PhoneNumberHandlerBase.isPossiblePhoneNumber(data, defaultRegion);

    }

    /**
     * 
     * As per {@link #formatE164(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return
     */
    public String formatE164(Object data) {
        return PhoneNumberHandlerBase.formatE164(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #formatInternational(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return return a formated number like as "+1 242-365-1234".
     */
    public String formatInternational(Object data) {
        return PhoneNumberHandlerBase.formatInternational(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #formatNational(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return the formatted phone number like as "(242) 365-1234"
     */
    public String formatNational(Object data) {
        return PhoneNumberHandlerBase.formatNational(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #formatRFC396(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return the formatted phone number like as "tel:+1-242-365-1234"
     */
    public String formatRFC396(Object data) {
        return PhoneNumberHandlerBase.formatRFC396(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #getPhoneNumberType(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return
     */
    public PhoneNumberTypeEnum getPhoneNumberType(Object data) {
        return PhoneNumberHandlerBase.getPhoneNumberType(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #getTimeZonesForNumber(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return
     */
    public List<String> getTimeZonesForNumber(Object data) {
        return PhoneNumberHandlerBase.getTimeZonesForNumber(data, defaultRegion);
    }

    /**
     * 
     * As per {@link #getGeocoderDescriptionForNumber(Object, Locale)} but explicitly the Locale is default.
     * 
     * @param data
     * @return
     */
    public String getGeocoderDescriptionForNumber(Object data) {
        return PhoneNumberHandlerBase.getGeocoderDescriptionForNumber(data, defaultRegion, defaultLocale);
    }

    /**
     * 
     * As per {@link #getCarrierNameForNumber(Object, String)} but explicitly the region code is default.
     * 
     * @param data
     * @return
     */
    public String getCarrierNameForNumber(Object data) {
        return PhoneNumberHandlerBase.getCarrierNameForNumber(data, defaultRegion, defaultLocale);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public void setDefaultRegion(String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

}
