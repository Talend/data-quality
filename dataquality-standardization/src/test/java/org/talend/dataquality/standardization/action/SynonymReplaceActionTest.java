package org.talend.dataquality.standardization.action;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.dataquality.standardization.utils.LuceneIndex;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SynonymIndexSearcher.class, SearcherManager.class, IndexReader.class })
public class SynonymReplaceActionTest {

    private SynonymReplaceAction action;

    private SearcherManager mgr;

    private IndexSearcher searcher;

    @Before
    public void setUp() throws Exception {

        mgr = PowerMockito.mock(SearcherManager.class);
        PowerMockito.whenNew(SearcherManager.class).withAnyArguments().thenReturn(mgr);

        searcher = Mockito.mock(IndexSearcher.class);
        PowerMockito.when(mgr.acquire()).thenReturn(searcher);

        MockitoAnnotations.initMocks(this);

        action = new SynonymReplaceAction();

        LuceneIndex.createLuceneIndex("target/.talend");
    }

    @Test
    public void runWithoutResult() throws IOException {
        ScoreDoc[] scores = new ScoreDoc[1];
        TopDocs docs = new TopDocs(0, scores, 0.0f);
        when(searcher.search(any(Query.class), anyInt())).thenReturn(docs);

        Assert.assertEquals(StringUtils.EMPTY, action.run("string", 1, "target/.talend", new Random()));
    }

    @Test
    public void runWithResult() throws IOException {
        ScoreDoc[] scores = { new ScoreDoc(1, 1.0f) };
        TopDocs docs = new TopDocs(1, scores, 1.0f);
        when(searcher.search(any(Query.class), anyInt())).thenReturn(docs);

        IndexReader reader = PowerMockito.mock(IndexReader.class);
        when(searcher.getIndexReader()).thenReturn(reader);

        Document document = new Document();
        Field idTermField = new StringField("catid", "1", Field.Store.YES);
        document.add(idTermField);
        document.add(new StringField("word", "string", Field.Store.NO));
        document.add(new StringField("syn", "replacingSyn", Field.Store.NO));

        when(reader.document(1)).thenReturn(document);

        Assert.assertEquals("replacingSyn", action.run("string", 1, "target/.talend", new Random()));
    }

}
