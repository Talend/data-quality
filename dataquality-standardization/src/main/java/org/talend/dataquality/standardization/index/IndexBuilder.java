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
package org.talend.dataquality.standardization.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.standardization.i18n.Messages;

import com.talend.csv.CSVReader;

/**
 * DOC scorreia class global comment. Detailed comment
 */
// TODO move the main method and related methods into the test project.
public class IndexBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SynonymIndexBuilder.class);

    private String directoryPath;

    private Directory index;

    /**
     * Getter for index.
     * 
     * @return the index
     */
    public Directory getIndex() {
        return this.index;
    }

    public IndexBuilder(String directoryPath) {
        this.directoryPath = directoryPath;

    }

    public IndexBuilder() {
        // needn't to do anything
    }

    public boolean initializeIndex(String csvFileToIndex, int[] columnsToBeIndexed) throws IOException {
        assert csvFileToIndex != null;
        if (!new File(csvFileToIndex).exists() || !new File(directoryPath).isDirectory()) {
            throw new IOException(Messages.getString("IndexBuilder.error", csvFileToIndex, directoryPath));//$NON-NLS-1$
        }
        index = new MMapDirectory(new File(directoryPath));
        // The same analyzer should be used for indexing and searching
        Analyzer analyzer = new StandardAnalyzer();
        // the boolean arg in the IndexWriter ctor means to
        // create a new index, overwriting any existing index
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // read the data (this will be the input data of a component called tFirstNameStandardize)
        CSVReader csvReader = createCSVReader(csvFileToIndex, ',');

        while (csvReader.readNext()) {
            String name = csvReader.get(columnsToBeIndexed[0]);
            String country = csvReader.get(columnsToBeIndexed[1]);
            String gender = csvReader.get(columnsToBeIndexed[2]);
            String count = csvReader.get(columnsToBeIndexed[3]);
            addDoc(w, name, country, gender, count);
        }
        csvReader.close();
        w.commit();
        w.close();

        return true;
    }

    private static void addDoc(IndexWriter w, String name, String country, String gender, String count) throws IOException {
        if (!"".equals(country) && !"".equals(gender)) {//$NON-NLS-1$ //$NON-NLS-2$
            Document doc = new Document();
            Field field = new Field("name", name, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES);//$NON-NLS-1$
            doc.add(field);
            doc.add(new Field("country", country, Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.YES));//$NON-NLS-1$
            doc.add(new Field("gender", gender, Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.YES));//$NON-NLS-1$
            doc.add(new Field("count", count, Field.Store.NO, Field.Index.NOT_ANALYZED, TermVector.NO));//$NON-NLS-1$
            w.addDocument(doc);
        }
    }

    public boolean initializeSynonymIndex(String csvFileToIndex, int[] columnsToBeIndexed) throws IOException {
        assert csvFileToIndex != null;
        if (!new File(csvFileToIndex).exists() || !new File(directoryPath).isDirectory()) {
            throw new IOException(Messages.getString("IndexBuilder.error", csvFileToIndex, directoryPath));//$NON-NLS-1$
        }

        index = FSDirectory.open(new File(directoryPath));

        // The same analyzer should be used for indexing and searching
        Analyzer analyzer = new StandardAnalyzer();
        // the boolean arg in the IndexWriter ctor means to
        // create a new index, overwriting any existing index
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // read the data (this will be the input data of a component called
        // tFirstNameStandardize)
        CSVReader csvReader = createCSVReader(csvFileToIndex, ';');

        while (csvReader.readNext()) {

            Document doc = new Document();
            String word = csvReader.get(columnsToBeIndexed[0]);
            doc.add(new Field("word", word, Field.Store.YES, Field.Index.NO, TermVector.NO));//$NON-NLS-1$
            doc.add(new Field("syn", word, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));//$NON-NLS-1$

            if (columnsToBeIndexed.length == 1) {
                w.addDocument(doc);
                continue;
            }

            String synonyms = csvReader.get(columnsToBeIndexed[1]);
            String[] split = StringUtils.split(synonyms, "|");//$NON-NLS-1$
            for (String str : split) {
                doc.add(new Field("syn", str, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES));//$NON-NLS-1$
            }
            w.addDocument(doc);
        }
        csvReader.close();
        w.commit();
        w.close();

        return true;
    }

    /**
     * DOC yyin Comment method "createCSVReader".
     * 
     * @param csvFileToIndex
     * @return
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private CSVReader createCSVReader(String csvFileToIndex, char seperator)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        CSVReader csvReader = new CSVReader(
                new java.io.BufferedReader(
                        new java.io.InputStreamReader(new java.io.FileInputStream(csvFileToIndex.toString()), "windows-1252")), //$NON-NLS-1$
                seperator);
        csvReader.setQuoteChar('\"');

        csvReader.readNext();// skip header
        return csvReader;
    }

    private static void createSynonymIndex(String indexPath, String sourceFile) {
        File dirFile = new File("data/indexes/" + indexPath);//$NON-NLS-1$
        if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
            dirFile.mkdirs();
        }

        IndexBuilder ib = new IndexBuilder("data/indexes/" + indexPath);//$NON-NLS-1$
        int[] columnsToIndex = { 0, 1 };
        try {
            ib.initializeSynonymIndex("data/indexes/" + sourceFile, columnsToIndex);//$NON-NLS-1$
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static void createSingleIndex(String indexPath, String sourceFile) {
        File dirFile = new File("data/indexes/" + indexPath);//$NON-NLS-1$
        if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
            dirFile.mkdirs();
        }

        IndexBuilder ib = new IndexBuilder("data/indexes/" + indexPath);//$NON-NLS-1$
        int[] columnsToIndex = { 0 };
        try {
            ib.initializeSynonymIndex("data/indexes/" + sourceFile, columnsToIndex);//$NON-NLS-1$
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws IOException {
        createSynonymIndex("Salutory", "SalutorySynonyms.csv");//$NON-NLS-1$ //$NON-NLS-2$
        createSynonymIndex("Address", "AddressSynonyms.csv");//$NON-NLS-1$ //$NON-NLS-2$
        createSingleIndex("Company", "CompanySynonyms.csv");//$NON-NLS-1$ //$NON-NLS-2$
        createSynonymIndex("StreetType", "StreetTypeSynonyms.csv");//$NON-NLS-1$ //$NON-NLS-2$
    }
}
