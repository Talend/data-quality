package org.talend.dataquality.datamasking.semantic;

import java.util.Date;
import java.util.Random;

import org.talend.dataquality.datamasking.functions.Function;

public abstract class AbstractDateFunction extends Function<Date> {

    private static final long serialVersionUID = 4614541779619899715L;

    protected abstract Date doGenerateMaskedField(Date date, Random r);
}
