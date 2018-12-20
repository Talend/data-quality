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

/**
 * Type of phone numbers.
 */
public enum PhoneNumberTypeEnum {
    FIXED_LINE("Fix_Line"), //$NON-NLS-1$
    MOBILE("Mobile"), //$NON-NLS-1$
    // In some regions (e.g. the USA), it is impossible to distinguish between fixed-line and
    // mobile numbers by looking at the phone number itself.
    FIXED_LINE_OR_MOBILE("Fixed_Line_Or_Mobile"), //$NON-NLS-1$
    // Freephone lines
    TOLL_FREE("Toll_Free"), //$NON-NLS-1$
    PREMIUM_RATE("Premium_Rate"), //$NON-NLS-1$
    // The cost of this call is shared between the caller and the recipient, and is hence typically
    // less than PREMIUM_RATE calls. See // http://en.wikipedia.org/wiki/Shared_Cost_Service for
    // more information.
    SHARED_COST("Shared_Cost"), //$NON-NLS-1$
    // Voice over IP numbers. This includes TSoIP (Telephony Service over IP).
    VOIP("Voip"), //$NON-NLS-1$
    // A personal number is associated with a particular person, and may be routed to either a
    // MOBILE or FIXED_LINE number. Some more information can be found here:
    // http://en.wikipedia.org/wiki/Personal_Numbers
    PERSONAL_NUMBER("Personal_Number"), //$NON-NLS-1$
    PAGER("Pager"), //$NON-NLS-1$
    // Used for "Universal Access Numbers" or "Company Numbers". They may be further routed to
    // specific offices, but allow one number to be used for a company.
    UAN("Uan"), //$NON-NLS-1$
    // Used for "Voice Mail Access Numbers".
    VOICEMAIL("Voicemail"), //$NON-NLS-1$
    // A phone number is of type UNKNOWN when it does not fit any of the known patterns for a
    // specific region.
    UNKNOWN("Unknown"); //$NON-NLS-1$

    private String name;

    PhoneNumberTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
