package org.talend.dataquality.datamasking.functions;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberFr extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = 6823172946239619086L;

    @Override
    protected int getDigitsNumberToMask() {
        return 6;
    }
}
