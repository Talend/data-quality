package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberUk extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = -1614421877363195905L;

    public GenerateUniquePhoneNumberUk() throws DQException {
        super();
    }

    @Override
    protected int getDigitsNumberToMask() {
        return 7;
    }
}
