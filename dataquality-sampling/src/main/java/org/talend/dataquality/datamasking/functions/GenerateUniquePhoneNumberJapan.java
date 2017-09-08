package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberJapan extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = 3058652596440148436L;

    public GenerateUniquePhoneNumberJapan() throws DQException {
        super();
    }

    @Override
    protected int getDigitsNumberToMask() {
        return 7;
    }

}
