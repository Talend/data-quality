package org.talend.dataquality.datamasking.functions.email;

public class MaskEmailLocalPartRandomly extends MaskEmailRandomly {

    private static final long serialVersionUID = 7679998342343191644L;

    /**
     * Replace local part email by the given replacement
     * 
     * @param address
     * @return
     */
    @Override
    protected String maskEmail(String address) {
        int splitAddress = address.indexOf('@');
        return parameters[chooseAppropriateDomainIndex(address.substring(0, splitAddress))] + address.substring(splitAddress);
    }
}
