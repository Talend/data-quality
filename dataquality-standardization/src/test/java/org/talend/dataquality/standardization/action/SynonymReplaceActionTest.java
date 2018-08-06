package org.talend.dataquality.standardization.action;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SynonymIndexSearcher.class })
public class SynonymReplaceActionTest {

    @InjectMocks
    private SynonymReplaceAction action;

    @Before
    public void setUp() throws Exception {
        SearcherManager mgr = new SearcherManager(createLuceneDirectory(), null);
        PowerMockito.whenNew(SearcherManager.class).withAnyArguments().thenReturn(mgr);
    }

    private Directory createLuceneDirectory() throws IOException {
        RAMDirectory testDir = new RAMDirectory();

        IndexWriter writer = new IndexWriter(testDir,
                new IndexWriterConfig(Version.LATEST, new StandardAnalyzer(CharArraySet.EMPTY_SET)));

        Document doc = new Document();

        doc.add(new StringField(SynonymIndexSearcher.F_WORD, "Germany", Field.Store.YES));
        String[] synonyms = new String[] { "Germany", "Allemagne", "Deutschland", "德国" };
        for (String syn : synonyms) {
            doc.add(new StringField(SynonymIndexSearcher.F_SYN, syn, Field.Store.YES));
            doc.add(new StringField(SynonymIndexSearcher.F_SYNTERM, syn.toLowerCase(), Field.Store.NO));
        }

        writer.addDocument(doc);
        writer.commit();
        writer.close();
        return testDir;
    }

    @Test
    public void runWithoutResult() {
        Assert.assertEquals(StringUtils.EMPTY, action.run("string", 1, "the_index_path", new Random(123L)));
    }

    @Test
    public void runWithResult() {
        Assert.assertEquals("Allemagne", action.run("Germany", 1, "the_index_path", new Random(1L)));
        Assert.assertEquals("Deutschland", action.run("Germany", 1, "the_index_path", new Random(2L)));
        Assert.assertEquals("德国", action.run("Germany", 1, "the_index_path", new Random(3L)));
        Assert.assertEquals("德国", action.run("Germany", 1, "the_index_path", new Random(4L)));
    }

}
