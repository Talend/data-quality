package org.talend.dataquality.datamasking.generic;

import com.idealista.fpe.algorithm.Cipher;
import com.idealista.fpe.component.functions.prf.PseudoRandomFunction;
import org.talend.dataquality.datamasking.SecretManager;

import java.util.List;
import java.util.Optional;

public class GenerateFromAlphabet {

    private static final long serialVersionUID = 4131439329223094305L;

    /**
     * The cipher used to encrypt data.
     * It is taken from <a href="https://github.com/idealista/format-preserving-encryption-java">
     * idealista library</a>
     * and corresponds to the <a href="https://nvlpubs.nist.gov/nistpubs/specialpublications/nist.sp.800-38g.pdf">
     * NIST-validated FF1 algorithm.</a>.
     *
     * The current implementation requires the input data to be encoded in an array of integers in a certain base.
     */
    private static Cipher cipher = new com.idealista.fpe.algorithm.ff1.Cipher();

    private Alphabet alphabet;

    private int minLength;

    public GenerateFromAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
        minLength = (int) Math.ceil(Math.log(100) / Math.log(alphabet.getRadix()));
    }

    /**
     * @param strs, the string input to encode
     * @param secretMng, the SecretManager instance providing the secrets to generate a unique string
     * @return the new encoded string
     */
    public Optional<StringBuilder> generateUniqueString(List<String> strs, SecretManager secretMng) {
        int[] data = transform(strs);

        if (data.length < minLength) {
            return Optional.empty();
        }

        byte[] tweak = new byte[] {};
        PseudoRandomFunction prf = secretMng.getPseudoRandomFunction();

        int[] result = cipher.encrypt(data, alphabet.getRadix(), tweak, prf);

        return Optional.ofNullable(transform(result));
    }

    /**
     * Transform the encrypted array of {@code int}s into the corresponding {@code String} representation.
     */
    public StringBuilder transform(int[] data) {

        StringBuilder sb = new StringBuilder();

        for (int numeral : data) {
            sb.append(alphabet.getCharactersMap().get(numeral));
        }

        return sb;
    }

    /**
     * Transform the {@code String} element into an array of {@code int}s for FF1 encryption.
     */
    public int[] transform(List<String> strs) {
        int[] data = new int[strs.size()];
        for (int i = 0; i < strs.size(); i++) {
            data[i] = alphabet.getRanksMap().get(strs.get(i));
        }
        return data;
    }
}
