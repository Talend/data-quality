package org.talend.dataquality.semantic.api.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQDocument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomDocumentIndexAccess.class, SearcherManager.class, IndexWriter.class, DirectoryReader.class })
public class CustomDocumentIndexAccessTest {

    private CustomDocumentIndexAccess access;

    private SearcherManager mgr;

    private IndexWriter luceneWriter;

    private DirectoryReader luceneReader;

    @Before
    public void setUp() throws Exception {

        mgr = PowerMockito.mock(SearcherManager.class);
        PowerMockito.whenNew(SearcherManager.class).withAnyArguments().thenReturn(mgr);

        ScoreDoc[] scores = { new ScoreDoc(1, 1.0f) };
        TopDocs docs = new TopDocs(1, scores, 1.0f);
        IndexSearcher searcher = Mockito.mock(IndexSearcher.class);
        when(mgr.acquire()).thenReturn(searcher);
        when(searcher.search(any(Query.class), eq(1))).thenReturn(docs);

        luceneWriter = PowerMockito.mock(IndexWriter.class);
        PowerMockito.whenNew(IndexWriter.class).withAnyArguments().thenReturn(luceneWriter);

        PowerMockito.mockStatic(DirectoryReader.class);

        luceneReader = PowerMockito.mock(DirectoryReader.class);
        PowerMockito.when(DirectoryReader.open(any(Directory.class))).thenReturn(luceneReader);

        access = new CustomDocumentIndexAccess(FSDirectory.open(new File("/target")));

    }

    @Test
    public void insertOrUpdateDocument() throws IOException {
        List<DQDocument> documents = new ArrayList<>();

        DQCategory category = new DQCategory();
        category.setId("1");
        category.setName("DQ_CATEGORY");

        DQDocument doc1 = new DQDocument();
        doc1.setId("1");
        doc1.setCategory(category);
        doc1.setValues(new HashSet<String>(Arrays.asList("AAAA", "BBBB")));
        documents.add(doc1);

        DQDocument doc2 = new DQDocument();
        doc2.setId("2");
        doc2.setCategory(category);
        doc2.setValues(new HashSet<String>(Arrays.asList("CCCC", "DDDD")));
        documents.add(doc2);

        access.insertOrUpdateDocument(documents);

        verify(mgr, times(2)).maybeRefreshBlocking();
        verify(luceneWriter, times(2)).updateDocument(any(Term.class), any(List.class));
        verify(mgr, times(2)).release(any(IndexSearcher.class));

    }

    @Test
    public void close() throws IOException {
        access.getWriter();
        access.getReader();
        access.close();

        verify(mgr, times(1)).close();
        verify(luceneWriter, times(1)).close();
        verify(luceneReader, times(1)).close();
    }

    @Test
    public void copyStagingContent() throws IOException {
        access.copyStagingContent(".");

        verify(luceneWriter, times(1)).deleteAll();
        verify(luceneWriter, times(1)).addIndexes(any(FSDirectory.class));
        verify(luceneWriter, times(2)).commit(); //internal lucene class call
    }
}
