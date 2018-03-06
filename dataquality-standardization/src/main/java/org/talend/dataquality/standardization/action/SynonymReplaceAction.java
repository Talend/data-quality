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
package org.talend.dataquality.standardization.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class SynonymReplaceAction implements ITalendStrConversionAction {

    private static final Logger LOG = LoggerFactory.getLogger(SynonymReplaceAction.class);

    private Map<String, SynonymIndexSearcher> synonymSearcherMap;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.standardization.action.ITalendStrConversionAction#run(java.lang.String, int,
     * java.lang.String)
     */
    @Override
    public String run(String str, int modifCount, String extraParameter, final Random random) {
        if (str.length() > 0) {
            SynonymIndexSearcher searcher = getSynonymSearcherMap().get(extraParameter);
            if (searcher == null) {
                searcher = new SynonymIndexSearcher(extraParameter);
                synonymSearcherMap.put(extraParameter, searcher);
            }
            try {
                TopDocs docs = searcher.searchDocumentBySynonym(str);
                if (docs.totalHits > 0) {
                    Document doc = searcher.getDocument(docs.scoreDocs[0].doc);
                    List<String> replaceList = new ArrayList<String>();
                    String word = doc.getValues(SynonymIndexSearcher.F_WORD)[0];
                    if (!word.equalsIgnoreCase(str)) {
                        replaceList.add(word);
                    }
                    String[] synonyms = doc.getValues(SynonymIndexSearcher.F_SYN);
                    for (String syn : synonyms) {
                        if (!syn.equalsIgnoreCase(str)) {
                            replaceList.add(syn);
                        }
                    }
                    if (replaceList.isEmpty()) {
                        return str;
                    } else {
                        return replaceList.get(random.nextInt(replaceList.size()));
                    }
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return StringUtils.EMPTY;
    }

    private Map<String, SynonymIndexSearcher> getSynonymSearcherMap() {
        if (synonymSearcherMap == null) {
            synonymSearcherMap = new HashMap<String, SynonymIndexSearcher>();
        }
        return synonymSearcherMap;
    }

    public void closeSynonymIndice() {
        if (synonymSearcherMap != null && !synonymSearcherMap.isEmpty()) {
            for (SynonymIndexSearcher searcher : synonymSearcherMap.values()) {
                searcher.close();
            }
            synonymSearcherMap.clear();
        }
    }

}
