package org.talend.dataquality.datamasking.functions;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * Created by jteuladedenantes on 22/09/16.
 */
public class GenerateUniquePhoneNumberFr extends AbstractGenerateUniquePhoneNumber {

    private static final long serialVersionUID = 6823172946239619086L;

    public GenerateUniquePhoneNumberFr() throws DQException {
        super();
    }

    @Override
    protected int getDigitsNumberToMask() {
        return 6;
    }

}
