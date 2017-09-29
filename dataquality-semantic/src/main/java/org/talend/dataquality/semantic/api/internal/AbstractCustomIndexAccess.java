package org.talend.dataquality.semantic.api.internal;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class AbstractCustomIndexAccess implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(AbstractCustomIndexAccess.class);

    private Directory directory;

    protected IndexWriter luceneWriter;

    protected DirectoryReader luceneReader;

    protected IndexSearcher luceneSearcher;

    public AbstractCustomIndexAccess(Directory directory) {
        this.directory = directory;
    }

    protected void createReader() throws IOException {
        if (luceneReader == null) {
            luceneReader = DirectoryReader.open(directory);
        }
    }

    protected IndexWriter getWriter() throws IOException {
        if (luceneWriter == null) {
            final Analyzer analyzer = new StandardAnalyzer();
            final IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
            luceneWriter = new IndexWriter(directory, iwc);
        }
        return luceneWriter;
    }

    protected void createSearcher() throws IOException {
        if (luceneSearcher == null) {
            luceneSearcher = new IndexSearcher(luceneReader);
        }
    }

    protected void deleteAll() {
        LOGGER.debug("delete all content");
        try {
            getWriter().deleteAll();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (luceneWriter != null) {
            try {
                luceneWriter.commit();
                luceneWriter.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (luceneReader != null) {
            try {
                luceneReader.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (directory != null) {
            directory.close();
        }
    }

    public void commitChangesAndCloseWriter() {
        if (luceneWriter != null) {
            try {
                luceneWriter.commit();
                luceneWriter.close();
                luceneWriter = null;
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
