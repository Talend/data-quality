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
package org.talend.dataquality.semantic.broadcast;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;

/**
 * Created by jteuladedenantes on 20/10/16.
 * <p>
 * This class uses the singleton pattern to avoid creating the lucene index for each row. The lucene index will be created for
 * each worker.
 */
public class BroadcastIndexObject implements Serializable {

    private static final long serialVersionUID = 7350930198992853600L;

    private static final Logger LOGGER = Logger.getLogger(BroadcastIndexObject.class);

    // The serializable object
    private List<BroadcastDocumentObject> documentList;

    // The lucene index created from the serializable object
    private Directory ramDirectory;

    /**
     * Constructor
     * 
     * @param inputDirectory
     */
    public BroadcastIndexObject(Directory inputDirectory) {
        try {
            documentList = BroadcastUtils.readDocumentsFromIndex(inputDirectory);
        } catch (IOException e) {
            LOGGER.error("Unable to read synonym index.", e);
        }
    }

    /**
     * The singleton method which creates the lucene index if necessary.
     * 
     * @return the lucene index
     */
    public synchronized Directory get() {
        if (ramDirectory == null) {
            try {
                ramDirectory = BroadcastUtils.createRamDirectoryFromDocuments(documentList);
            } catch (IOException e) {
                LOGGER.error("Unable to rebuild the broadcast dictionary.", e);
            }
        }
        return ramDirectory;
    }

}
