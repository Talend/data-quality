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
package org.talend.dataquality.datamasking.functions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by jgonzalez on 24 juin 2015. This class holds all the functions required by GenerateAccountNumberFormat and
 * GenerateAccountNumberFormat.
 *
 */
public abstract class GenerateAccountNumber extends Function<String> {

    private static final long serialVersionUID = -631455182627735683L;

    private static final BigInteger MOD97 = new BigInteger("97"); //$NON-NLS-1$

    private static final Map<Integer, Country> decryptionCode = new HashMap<Integer, Country>();
    static {
        decryptionCode.put(15, Country.CODE_15);
        decryptionCode.put(18, Country.CODE_18);
        decryptionCode.put(19, Country.CODE_19);
        decryptionCode.put(20, Country.CODE_20);
        decryptionCode.put(21, Country.CODE_21);
        decryptionCode.put(22, Country.CODE_22);
        decryptionCode.put(23, Country.CODE_23);
        decryptionCode.put(24, Country.CODE_24);
        decryptionCode.put(25, Country.CODE_25);
        decryptionCode.put(26, Country.CODE_26);
        decryptionCode.put(27, Country.CODE_27);
        decryptionCode.put(28, Country.CODE_28);
        decryptionCode.put(29, Country.CODE_29);
        decryptionCode.put(30, Country.CODE_30);
        decryptionCode.put(31, Country.CODE_31);
    }

    enum Country {
        CODE_15(Arrays.asList("NO")),
        CODE_16(Arrays.asList("BE", "BI")),
        CODE_18(Arrays.asList("DK", "FO", "FI", "GL", "NL")),
        CODE_19(Arrays.asList("MK", "SI")),
        CODE_20(Arrays.asList("AT", "BA", "EE", "KZ", "XK", "LT", "LU")),
        CODE_21(Arrays.asList("CR", "HR", "LV", "LI", "CH")),
        CODE_22(Arrays.asList("BH", "BG", "GE", "DE", "IE", "ME", "RS", "GB")),
        CODE_23(Arrays.asList("GI", "IL", "AE")),
        CODE_24(Arrays.asList("AD", "CZ", "MD", "PK", "RO", "SA", "SK", "ES", "SE", "DZ")),
        CODE_25(Arrays.asList("PT", "AO", "CV", "MZ")),
        CODE_26(Arrays.asList("IS", "TR", "IR")),
        CODE_27(Arrays.asList("FR", "GR", "IT", "MR", "MC", "SM", "CM", "MG", "BF")),
        CODE_28(Arrays.asList("AL", "AZ", "CY", "DO", "GT", "HU", "LB", "PL", "BJ", "CI", "ML", "SN")),
        CODE_29(Arrays.asList("BR", "PS", "QA", "UA")),
        CODE_30(Arrays.asList("JO", "KW", "MU")),
        CODE_31(Arrays.asList("MT"));

        private final List<String> countries;

        Country(List<String> list) {
            this.countries = list;
        }

        List<String> getCountries() {
            return countries;
        }

        boolean containCountryCode(String country) {
            return getCountries().contains(country);
        }

    }

    /**
     * This functions checks if the account number (ie, FR043568953359583693) is correct according to its size and
     * country.
     * 
     * @param accountNumber The account number given in input.
     * @return A boolean holding if the account number is correct or not.
     */
    private static boolean sizeOk(String accountNumber) {
        String str = accountNumber.substring(0, 2);
        Country code = decryptionCode.get(accountNumber.length());
        if (code == null)
            return false;
        else
            return code.containCountryCode(str);
    }

    /**
     * This function generates an american account number.
     * 
     * @param number The account number given in input.
     * @return An american account number, the nine first digits belonging to the original input and the others randomly
     * generated.
     */
    protected StringBuilder generateAmericanAccountNumber(String number) {
        StringBuilder sb = new StringBuilder(number.substring(0, 9)); // $NON-NLS-1$ //
        // //$NON-NLS-2$
        for (int i = 0; i < 10; ++i) {
            sb.append(String.valueOf(rnd.nextInt(10)));
        }
        return sb;
    }

    /**
     * This function checks if the account number is an American number account.
     * 
     * @param number The account number given in input.
     * @return A boolean holding if the account number is correct or not.
     */
    protected boolean isAmericanAccount(String number) {
        StringBuilder accountNumber = new StringBuilder(number.substring(0, 9));
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(accountNumber.charAt(i))) {
                return false;
            }
        }

        return (3
                * (Integer.parseInt(String.valueOf(accountNumber.charAt(0)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(3)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(6))))
                + 7 * (Integer.parseInt(String.valueOf(accountNumber.charAt(1)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(4)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(7))))
                + (Integer.parseInt(String.valueOf(accountNumber.charAt(2)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(5)))
                        + Integer.parseInt(String.valueOf(accountNumber.charAt(8)))))
                % 10 == 0;
    }

    /**
     * This function generates an account number. If the parameter is a correct iban number, this function will generate
     * an iban number, keeping the original country of where it's from (if the iban it's incorrect, the countru will be
     * France (see generateIban())). If the parameter is an correct american account number, this function will generate
     * a american number (see generateAmericanAccount).
     * 
     * @param number The account number given in input.
     * @return A correct account number.
     */
    public StringBuilder generateIban(String number) {
        if (!sizeOk(number)) {
            return generateIban();
        }

        StringBuilder sb = new StringBuilder(number.substring(0, 2));
        sb.append("00"); //$NON-NLS-1$
        for (int i = 0; i < number.length() - 4; ++i) {
            sb.append(rnd.nextInt(10));
        }

        return generateIbanLastPart(sb);
    }

    /**
     * This function is called when generating a French Iban number. The last two digits must be generated using an
     * algorithm.
     * 
     * @param sb The French Iban number missing its two last digits.
     * @return An int holding the two digits needed to complete the iban.
     */
    private static int generateRib(String sb) {
        Long b = Long.valueOf(sb.substring(0, 5));
        Long g = Long.valueOf(sb.substring(5, 10));

        StringBuilder csb = new StringBuilder(""); //$NON-NLS-1$
        for (int i = 10; i < 16; ++i) {
            csb.append(Character.getNumericValue(sb.charAt(i)));
        }
        Long d = Long.valueOf(csb.toString());

        csb = new StringBuilder(""); //$NON-NLS-1$

        for (int i = 16; i < 21; ++i) {
            csb.append(Character.getNumericValue(sb.charAt(i)));
        }
        Long c = Long.valueOf(csb.toString());

        return (int) (97 - ((89 * b + 15 * g + 76 * d + 3 * c) % 97));
    }

    /**
     * This function generates a correct French Iban number.
     * 
     * @return A string holding a correct French Iban number.
     */
    public StringBuilder generateIban() {
        StringBuilder sb = new StringBuilder("FR00"); //$NON-NLS-1$
        for (int i = 0; i < 10; ++i) {
            sb.append(String.valueOf(rnd.nextInt(10)));
        }
        for (int i = 0; i < 11; ++i) {
            int choice = rnd.nextInt(2);
            if (choice == 0) {
                sb.append(String.valueOf(rnd.nextInt(10)));
            } else {
                sb.append((char) (rnd.nextInt(26) + 'A'));
            }
        }

        int rib = generateRib(sb.substring(4));
        sb.append(rib / 10);
        sb.append(rib % 10);

        return generateIbanLastPart(sb);
    }

    private StringBuilder generateIbanLastPart(StringBuilder sb) {
        String sb2 = new String(sb);
        sb2 = sb2.substring(4) + sb2.substring(0, 4);

        StringBuilder numericAccountNumber = new StringBuilder();
        for (int i = 0; i < sb2.length(); i++) {
            numericAccountNumber.append(Character.getNumericValue(sb2.charAt(i)));
        }

        BigInteger ibanNumber = new BigInteger(numericAccountNumber.toString());
        int checkDigits = 98 - ibanNumber.mod(MOD97).intValue();

        sb.setCharAt(2, Character.forDigit(checkDigits / 10, 10));
        sb.setCharAt(3, Character.forDigit(checkDigits % 10, 10));

        return sb;
    }

}
