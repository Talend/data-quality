package org.talend.dataquality.jp.tokenization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.TokenizerBase;

public abstract class TextTokenizerBase {

    protected static TokenizerBase tokenizer;

    /**
     *
     * @param text
     * @return List of TokenBase
     */
    public List<TokenBase> tokenize(String text) {
        return tokenizer.tokenize(text).stream().filter(token -> !" ".equals(token.getSurface())) //$NON-NLS-1$
                .collect(Collectors.toList());
    }

    private Stream<String> getTokenSurface(String text) {
        return this.tokenize(text).stream().map(token -> token.getSurface());
    }

    /**
     *
     * @param text
     * @return List of tokens
     */
    public List<String> getListTokens(String text) {
        return getTokenSurface(text).collect(Collectors.toList());
    }

    /**
     *
     * @param text
     * @param delimiter
     * @return tokenized string with delimiter
     */
    public String getTokenizedString(String text, String delimiter) {
        Stream<String> tokens = getTokenSurface(text);
        return tokens.collect(Collectors.joining(delimiter));
    }

    /**
     *
     * @param text
     * @return tokenized string with space as delimiter
     */
    public String getTokenizedString(String text) {
        return getTokenizedString(text, " "); //$NON-NLS-1$
    }
}
