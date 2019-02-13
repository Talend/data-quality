package org.talend.dataquality.semantic.extraction;

import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.List;

/**
 * Abstract Function for extracting parts of fields according to the semantic category {@link DQCategory}.
 *
 * @author afournier
 */
public abstract class ExtractFromSemanticType {

    protected DQCategory semancticCategory;

    protected DictionarySnapshot dicoSnapshot;

    protected ExtractFromSemanticType(DictionarySnapshot snapshot, DQCategory category) {
        semancticCategory = category;
        dicoSnapshot = snapshot;
    }

    public String getCategoryName() {
        return semancticCategory.getName();
    }

    public abstract List<MatchedPart> getMatches(TokenizedString tokenizedField);

}
