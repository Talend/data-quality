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
package org.talend.dataquality.sampling;

import org.talend.dataquality.sampling.exception.DQException;

/**
 * created by zhao interface for sample data source. This interface provide a "stream" stype data operation.
 *
 */
public interface SamplingDataSource<DataSource> {

    /**
     * 
     * DOC zhao set data source.
     * 
     * @param ds
     */
    public void setDataSource(DataSource ds);

    /**
     * DOC zhao has next or not in the data source.
     * 
     * @return true if there are records still to be read, false otherwise
     * @throws Exception throws when unexpected exception occurs.
     */
    public boolean hasNext() throws DQException;

    /**
     * 
     * DOC zhao get one record from data source.
     * 
     * @return Array of field data of one record.
     */
    public Object[] getRecord() throws DQException;

    /**
     * 
     * DOC zhao Finalize the data extraction from data source , some operation need to be done here such as closing csv
     * stream.
     * 
     * @return true if success, false otherwise.
     * @throws TalendException When unexpected exception occurs
     */
    public boolean finalizeDataSampling() throws DQException;

}
