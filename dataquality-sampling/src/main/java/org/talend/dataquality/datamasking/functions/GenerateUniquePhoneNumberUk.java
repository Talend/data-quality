package org.talend.dataquality.datamasking.functions;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberUk extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = -1614421877363195905L;

    @Override
    protected int getDigitsNumberToMask() {
        return 7;
    }
}
