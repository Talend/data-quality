package org.talend.dataquality.semantic.api.internal;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Abstract class of custom index access
 */
public class AbstractCustomIndexAccess implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(AbstractCustomIndexAccess.class);

    private final Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);

    protected Directory directory;

    protected IndexWriter luceneWriter;

    protected DirectoryReader luceneReader;

    protected SearcherManager mgr;

    public AbstractCustomIndexAccess(Directory directory) {
        this.directory = directory;
    }

    protected DirectoryReader getReader() throws IOException {
        if (luceneReader == null) {
            luceneReader = DirectoryReader.open(directory);
        }
        return luceneReader;
    }

    protected void closeReader() {
        if (luceneReader != null) {
            try {
                luceneReader.close();
                luceneReader = null;
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public synchronized IndexWriter getWriter() throws IOException {
        if (luceneWriter == null) {
            final IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
            luceneWriter = new IndexWriter(directory, iwc);
        }
        return luceneWriter;
    }

    public void deleteAll() {
        LOGGER.debug("delete all content");
        try {
            getWriter().deleteAll();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (mgr != null) {
            mgr.close();
        }
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

    public void commitChanges() {
        try {
            if (getWriter() != null) {
                getWriter().commit();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void copyStagingContent(String srcPath) throws IOException {
        try (FSDirectory dir = FSDirectory.open(new File(srcPath));) {
            IndexWriter writer = getWriter();
            writer.deleteAll();
            writer.addIndexes(dir);
            writer.commit();
        }
    }
}
