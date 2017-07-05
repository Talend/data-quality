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
package org.talend.dataquality.standardization.main;

import java.io.IOException;
import java.util.Map;

import org.talend.dataquality.standardization.exception.DQException;

/**
 * DOC klliu class global comment.
 */
public interface HandleLucene {

    /**
     * Expect that by accepting parameter and returns the correct result, if not correspond with the result of
     * searchwords, do a fuzzy query, returns the result of the similar.
     * 
     * @param searchType
     * @param searchWords
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public Map<String, String[]> getSearchResult(String folderName, String inputName, Map<String, String> information2value,
            boolean fuzzyQuery) throws IOException;

    /**
     * 
     * DOC klliu Comment method "getReplaceSearchResult".
     * 
     * @param folderName
     * @param inputName
     * @param information2value
     * @param fuzzyQuery
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public String getReplaceSearchResult(String folderName, String inputName, Map<String, String> information2value,
            boolean fuzzyQuery) throws IOException;

    /**
     * 
     * DOC klliu Comment method "replaceName".
     * 
     * @param inputName
     * @param fuzzyQuery
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public String replaceName(String folderName, String inputName, boolean fuzzyQuery) throws IOException;

    /**
     * 
     * DOC klliu Comment method "replaceNameWithCountryGenderInfo".
     * 
     * @param inputName
     * @param inputCountry
     * @param inputGender
     * @param fuzzyQuery
     * @return
     * @throws Exception
     */
    public String replaceNameWithCountryGenderInfo(String folderName, String inputName, String inputCountry, String inputGender,
            boolean fuzzyQuery) throws DQException;

    /**
     * 
     * DOC klliu Comment method "replaceNameWithCountryInfo".
     * 
     * @param inputName
     * @param inputCountry
     * @param fuzzyQuery
     * @return
     * @throws Exception
     */
    public String replaceNameWithCountryInfo(String folderName, String inputName, String inputCountry, boolean fuzzyQuery)
            throws DQException;

    /**
     * 
     * DOC klliu Comment method "replaceNameWithGenderInfo".
     * 
     * @param inputName
     * @param inputGender
     * @param fuzzyQuery
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws Exception
     */
    public String replaceNameWithGenderInfo(String folderName, String inputName, String inputGender, boolean fuzzyQuery)
            throws DQException;

    /**
     * Input filename to be indexed once for all and indexfolder to store the files of indexing.
     * 
     * @param filename
     * @param indexfolder
     * @return
     */
    public boolean createIndex(String filename, String indexfolder);

}
