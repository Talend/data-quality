package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberGermany extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = -2638510638860677668L;

    public GenerateUniquePhoneNumberGermany() throws DQException {
        super();
    }

    @Override
    protected int getDigitsNumberToMask() {
        return 8;
    }
}
