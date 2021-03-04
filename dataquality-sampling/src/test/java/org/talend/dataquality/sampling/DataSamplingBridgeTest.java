// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
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

import org.junit.Test;

public class DataSamplingBridgeTest {

    @Test
    public void testGetRecord() throws Exception {
        // // 1. Create a data source from csv file.
        // List<String> columnNames = new ArrayList<String>();
        // SamplingDataSource<DelimitedFileConnection> fileDatasource = new FileSamplingDataSource(columnNames);
        // // fileDatasource.setDataSource(new File(getClass().getClassLoader()
        //        //                        .getResource("org/talend/dataquality/sampling/simple_data.csv").getFile())); //$NON-NLS-1$
        // DelimitedFileConnection createDelimitedFileConnection =
        // ConnectionFactory.eINSTANCE.createDelimitedFileConnection();
        // fileDatasource.setDataSource(createDelimitedFileConnection);
        // // 2. create a sampling bridge instance.
        // DataSamplingBridge samplingBridge = new DataSamplingBridge(fileDatasource);
        // // 2.1 set sampling option , by default top n if not set.
        // samplingBridge.setSamplingOption(SamplingOption.Reservoir);
        // // 2.2 set sampling size , by default 1000 if not set.
        // samplingBridge.setSampleSize(4);
        // // 2.3 prepare the data sampling data source.
        // samplingBridge.prepareData(DataSamplingBridge.RANDOM_SEED);
        // // 2.4 get data
        // while (samplingBridge.hasNext()) {
        // System.out.println(getString(samplingBridge.getRecord()));
        // }
        //
        // // 3. Finalize the sampling.
        // samplingBridge.finalizeDataSampling();
    }

    public String getString(Object[] data) {
        StringBuffer sb = new StringBuffer();
        for (Object o : data) {
            sb.append(o == null ? "" : o.toString()).append(",");
        }
        return sb.toString();
    }
}
