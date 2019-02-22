package org.talend.dataquality.semantic.extraction;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

public class ExtractFromCompoundTest {

    private DictionarySnapshot snapshot = new StandardDictionarySnapshotProvider().get();

    @Test
    public void basicMatch() {
        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName(SemanticCategoryEnum.PHONE.getId());
        ExtractFromCompound efc = new ExtractFromCompound(snapshot, category);
        TokenizedString input = new TokenizedString("+33 1 46 25 06 00");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartRegex(input, 0, 17));
        assertEquals(expected, efc.getMatches(input));
    }

    @Test
    public void matchHiddenInAField() {
        DQCategory category = CategoryRegistryManager.getInstance().getCategoryMetadataByName(SemanticCategoryEnum.PHONE.getId());
        ExtractFromCompound efc = new ExtractFromCompound(snapshot, category);
        TokenizedString input = new TokenizedString("France +33 1 46 25 06 00 Mr. Talend");
        List<MatchedPart> expected = Collections.singletonList(new MatchedPartRegex(input, 7, 24));
        assertEquals(expected, efc.getMatches(input));
    }

    @Test
    public void basicNoMatch() {
        DQCategory category = CategoryRegistryManager.getInstance()
                .getCategoryMetadataByName(SemanticCategoryEnum.COUNTRY.getId());
        ExtractFromCompound efc = new ExtractFromCompound(snapshot, category);
        TokenizedString input = new TokenizedString("+33 1 46 25 06 00");
        assertTrue(efc.getMatches(input).isEmpty());
    }

}
