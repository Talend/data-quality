// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.survivorship.services;

import java.util.ArrayList;
import java.util.List;

import org.talend.survivorship.model.Attribute;
import org.talend.survivorship.model.DataSet;
import org.talend.survivorship.model.Record;

/**
 * Time Service to determine the latest, earliest value of a given column by
 * predefined date format.
 * <p>
 * TODO complete Time Service
 */
public class CompletenessService extends AbstractService {

	List<Integer> mostCompleteRecNumList;

	/**
	 * StringService constructor.
	 * 
	 * @param dataset
	 */
	public CompletenessService(DataSet dataset) {
		super(dataset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.survivorship.services.AbstractService#init()
	 */
	@Override
	public void init() {
		if (mostCompleteRecNumList != null) {
			mostCompleteRecNumList.clear();
		}
	}

	/**
	 * DOC sizhaoliu Comment method "getMostCompleteRecords".
	 * 
	 * @return
	 */
	public List<Integer> getMostCompleteRecNumList() {
		if (mostCompleteRecNumList == null) {
			mostCompleteRecNumList = new ArrayList<Integer>();

			int max = 0;
			for (Record r : dataset.getRecordList()) {
				int count = 0;
				for (Attribute a : r.getAttributes()) {
					Object obj = a.getValue();
                    if (obj != null && !"".equals(obj)) { //$NON-NLS-1$
						count++;
					}
				}
				if (count > max) {
					mostCompleteRecNumList.clear();
					mostCompleteRecNumList.add(r.getId());
					max = count;
				} else if (count == max) {
					mostCompleteRecNumList.add(r.getId());
				} else {
					// Do nothing
				}
			}
		}
		return mostCompleteRecNumList;
	}

}
