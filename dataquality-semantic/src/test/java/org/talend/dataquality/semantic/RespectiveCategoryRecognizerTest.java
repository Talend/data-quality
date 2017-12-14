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
package org.talend.dataquality.semantic;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.DefaultCategoryRecognizer;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.snapshot.StandardDictionarySnapshotProvider;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class RespectiveCategoryRecognizerTest extends CategoryRegistryManagerAbstract {

    final String commontChar = "//##//"; //$NON-NLS-1$

    final String invalidChar = "invalid"; //$NON-NLS-1$

    final String validChar = "valid"; //$NON-NLS-1$

    // private static Logger log = Logger.getLogger(RespectiveCategoryRecognizerTest.class);

    private static CategoryRecognizer catRecognizer;

    private static Map<String, List<Pair<String, Boolean>>> EXPECTED_MATCHING_RES_FOR_CATS = new LinkedHashMap<String, List<Pair<String, Boolean>>>() {

        private static final long serialVersionUID = -7775617050399019496L;

        {
            // categories with regex patterns
            put(SemanticCategoryEnum.EMAIL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("first.name+last_name-@talend.com", true));
                    add(ImmutablePair.of("fïrst.name+lâst_name-@talend.com", true)); // accent is also allowed
                    add(ImmutablePair.of("sliu", false)); // must contains "@" symbol
                    add(ImmutablePair.of("sliu@.com.my", false)); // tld can not start with dot "."
                    add(ImmutablePair.of("sliu123@talend.a", false)); // last tld must contains at least two characters
                    add(ImmutablePair.of("sliu123@.talend.com", false)); // tld can not start with dot "."
                    add(ImmutablePair.of(".sliu@sliu.com", false)); // first character can not be dot "."
                    add(ImmutablePair.of("sliu()*@talend.com", false)); // only allow character, digit, underscore and dash
                    add(ImmutablePair.of("sliu@%*.com", false)); // tld is only allow character and digit
                    add(ImmutablePair.of("sliu.@talend.com", false)); // last character of local part can not be dot "."
                    add(ImmutablePair.of("sliu..2002@talend.com", false)); // double dots "." are not allowed in local part
                    add(ImmutablePair.of("sliu.2002@talend..com", false)); // double dots "." are not allowed in domain part
                    add(ImmutablePair.of("sliu@sliu@talend.com", false)); // double "@" are not allowed
                    add(ImmutablePair.of("sliu@talend.com.1a", false)); // tld which has two characters can not contains digit
                    add(ImmutablePair.of("marius.reidy@lmb.liebMr..com", false));
                    add(ImmutablePair.of("sidbpl@cebpl.caisse-epargne.fr", true)); // hyphen is allowed in domain
                    add(ImmutablePair.of("sidbpl@cebpl.caisse-epargne.FR", true));
                }
            });

            put(SemanticCategoryEnum.URL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("ftp://user:pass@talend.com", true));
                    add(ImmutablePair.of("http://www.centraldiecastingmfgcoinc.com/this/is/a/path?sort=desc#anchor", true));
                    add(ImmutablePair.of("https://info.talend.com/fr_di_di_dummies.html?type=tydl#bar", true));
                    add(ImmutablePair.of("HTTPS://info.talend.com/fr_di_di_dummies.html?type=tydl#bar", true));
                    add(ImmutablePair.of("hdfs://127.0.0.1/user/luis/sample.txt", false));

                }
            });

            put(SemanticCategoryEnum.US_PHONE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("+1 (650) 539 3200", true));
                    add(ImmutablePair.of("15417543010", true));// Dialed in the US
                    add(ImmutablePair.of("(541)754-3010", true)); // Domestic w/o space
                    add(ImmutablePair.of("(541) 754-3010", true)); // Domestic
                    add(ImmutablePair.of("+1-541-754-3010", true)); // International
                    add(ImmutablePair.of("1-541-754-3010", true));// Dialed in the US
                    add(ImmutablePair.of("1 541 754 3010", true));// Dialed in the US
                    add(ImmutablePair.of("1 541 754 3010 ext 23", true));// number with extension

                    add(ImmutablePair.of("1234567", false));
                    add(ImmutablePair.of("41-754-3010", false));
                    add(ImmutablePair.of("12341-754-3010", false));
                }
            });

            // categories with dictionary
            put(SemanticCategoryEnum.ANSWER.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("YES", true));
                    add(ImmutablePair.of("NO", true));
                    add(ImmutablePair.of("Oui", true));
                    add(ImmutablePair.of("Non", true));
                    add(ImmutablePair.of("ja", true));
                    add(ImmutablePair.of("nein", true));
                    add(ImmutablePair.of("Sí", true));
                    add(ImmutablePair.of("Si", true));

                }
            });

            put(SemanticCategoryEnum.ANIMAL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("CHÈVRE", true));
                    add(ImmutablePair.of("chèvre", true));
                    add(ImmutablePair.of("chèVRE", true));
                    add(ImmutablePair.of("CHEVRE", true));
                }
            });

            put(SemanticCategoryEnum.AIRPORT.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -7685972970239752864L;

                {
                    add(ImmutablePair.of("Tri–City Airport (Kansas)", true));
                    add(ImmutablePair.of("Tri–City Airport", true));
                    add(ImmutablePair.of("Tri–City", true));
                    add(ImmutablePair.of("Tri-City Airport", true));// dash symbol between tri and city
                    add(ImmutablePair.of("Tri City Airport", true));
                    add(ImmutablePair.of("Pointe–à–Pitre International Airport", true));
                    add(ImmutablePair.of("Pointe–à–Pitre International", true));
                    add(ImmutablePair.of("Pointe–à–Pitre", true));
                    add(ImmutablePair.of("Guadeloupe Pôle Caraïbes Airport", true));
                    add(ImmutablePair.of("Guadeloupe Pôle Caraïbes", true));
                    add(ImmutablePair.of("santa fe Municipal Airport", true));
                    add(ImmutablePair.of("santa fe Municipal", true));
                    add(ImmutablePair.of("santa fe", true));
                }
            });

            put(SemanticCategoryEnum.AIRPORT_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -8659177565927990323L;

                {
                    add(ImmutablePair.of("CDG", true));
                    add(ImmutablePair.of("cdg", true));
                }
            });

            put(SemanticCategoryEnum.BEVERAGE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -7049951170274620208L;

                {
                    add(ImmutablePair.of("5-hour Energy", true));
                    add(ImmutablePair.of("Destilerías y Crianza del Whisky S.A.", true));
                    add(ImmutablePair.of("Black & White", true));
                }
            });

            put(SemanticCategoryEnum.CITY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -6234013058013176603L;

                {
                    add(ImmutablePair.of("Paris", true));
                    add(ImmutablePair.of("Ville-Lumière", true));
                    add(ImmutablePair.of("巴黎", true)); // chinese
                    add(ImmutablePair.of("パリ", true)); // japanese
                    add(ImmutablePair.of("파리", true)); // korean
                    add(ImmutablePair.of("Mancos", true));
                    add(ImmutablePair.of("Kadoka", true));
                    add(ImmutablePair.of("Caraway", true));
                    add(ImmutablePair.of("Geraldine", true));

                    add(ImmutablePair.of("SURESNES", true));
                    add(ImmutablePair.of("敘雷訥", true));
                    add(ImmutablePair.of("シュレンヌ", true));
                    add(ImmutablePair.of("سورزن", true));
                }
            });

            put(SemanticCategoryEnum.CIVILITY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -7811408010567938892L;

                {
                    add(ImmutablePair.of("MADAM", true));// English
                    add(ImmutablePair.of("MRS", true));// British English
                    add(ImmutablePair.of("MRS.", true));// American English
                    add(ImmutablePair.of("MADAME", true));// French
                    add(ImmutablePair.of("MME", true));
                }
            });

            put(SemanticCategoryEnum.COMPANY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -8504533159225145444L;

                {
                    add(ImmutablePair.of("Napco Security Technologies, Inc.", true));
                    add(ImmutablePair.of("Napco Security Technologies Inc.", true));// fixed with MATCH_COMPLETE
                    add(ImmutablePair.of("Napco Security Technologies", false));// TODO check index
                    add(ImmutablePair.of("Napco Security", false));// TODO check index
                    add(ImmutablePair.of("Coins.ph (company)", false));
                    add(ImmutablePair.of("Solid Gold (pet food)", false));
                    add(ImmutablePair.of("Solid Gold ", true)); // fixed with MATCH_COMPLETE
                    add(ImmutablePair.of("Solid Gold", true));
                    add(ImmutablePair.of("DINA S.A.", true));
                    add(ImmutablePair.of("DINA SA", false));// TODO check index
                    add(ImmutablePair.of("DINA S", false));// TODO check index
                    add(ImmutablePair.of("DINA A", false));// TODO check index
                    add(ImmutablePair.of("DINA", false));// TODO check index

                    add(ImmutablePair.of("Talend", true));// TODO check index
                    add(ImmutablePair.of("talend", true));// TODO check index

                    add(ImmutablePair.of("yes", false));// TODO check index
                    add(ImmutablePair.of("no", false));// TODO check index
                }
            });

            put(SemanticCategoryEnum.CONTINENT.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 5896103578361443394L;

                {
                    add(ImmutablePair.of("NORTH AMERICA", true));
                    add(ImmutablePair.of("AMÉRICA DO NORTE", true));
                    add(ImmutablePair.of("AMERICA DO NORTE", true));
                    add(ImmutablePair.of("OCEANIA", true));
                    add(ImmutablePair.of("OCEANICA", true));
                    add(ImmutablePair.of("AUSTRALIA", true));
                    add(ImmutablePair.of("AFRO-EURASIA", true));
                    add(ImmutablePair.of("EURASIA", true));
                }
            });

            put(SemanticCategoryEnum.CONTINENT_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 3631731885389955273L;

                {
                    add(ImmutablePair.of("NAM", true));
                }
            });

            put(SemanticCategoryEnum.COUNTRY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 9140556505088456061L;

                {
                    add(ImmutablePair.of("Plurinational State of Bolivia", true));
                    add(ImmutablePair.of("Bolivia", true));
                    add(ImmutablePair.of("l'État Plurinational de Bolivie", true));
                    add(ImmutablePair.of("Bolivie", true));
                    add(ImmutablePair.of("Russia", true));
                    add(ImmutablePair.of("Russian", false));
                    add(ImmutablePair.of("Vietnam", true));
                    add(ImmutablePair.of("Socialist Republic of Vietnam", true));
                    add(ImmutablePair.of("Viet Nam", true));
                    add(ImmutablePair.of("Viêtnam", true));
                    add(ImmutablePair.of("Bonaire, Sint Eustatius and Saba", true));
                    add(ImmutablePair.of("Caribbean Netherlands", true));
                    add(ImmutablePair.of("Lebanon", true));
                }
            });

            put(SemanticCategoryEnum.COUNTRY_CODE_ISO2.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -251586469724079060L;

                {
                    add(ImmutablePair.of("FR", true));
                    add(ImmutablePair.of("FR.", true));
                    add(ImmutablePair.of(".FR", true));
                }
            });

            put(SemanticCategoryEnum.COUNTRY_CODE_ISO3.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -6924927822202895291L;

                {
                    add(ImmutablePair.of("FRA", true));
                }
            });

            put(SemanticCategoryEnum.CURRENCY_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 1309012917099581078L;

                {
                    add(ImmutablePair.of("USD", true));
                }
            });

            put(SemanticCategoryEnum.CURRENCY_NAME.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 6762016035486422951L;

                {
                    add(ImmutablePair.of("US Dollar", true));
                    add(ImmutablePair.of("dollar", true));
                    add(ImmutablePair.of("U.S. dollar", true));
                    add(ImmutablePair.of("American dollar", true));
                    add(ImmutablePair.of("United States dollar", true));
                }
            });

            put(SemanticCategoryEnum.FIRST_NAME.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2852426477419955576L;

                {
                    add(ImmutablePair.of("JEAN-BAPTISTE", true));
                    add(ImmutablePair.of("CÄCILIA", true));
                }
            });

            put(SemanticCategoryEnum.GENDER.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1462719325960443933L;

                {
                    add(ImmutablePair.of("FEMALE", true));
                    add(ImmutablePair.of("FEMELLE", true));
                    add(ImmutablePair.of("WOMAN", true));
                    add(ImmutablePair.of("FEMME", true));
                    add(ImmutablePair.of("F", true));
                }
            });

            put(SemanticCategoryEnum.HR_DEPARTMENT.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 8993591558276114541L;

                {
                    add(ImmutablePair.of("research and development team", true));
                    add(ImmutablePair.of("R&D", true));
                }
            });

            put(SemanticCategoryEnum.INDUSTRY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -8853186121873779457L;

                {
                    add(ImmutablePair.of("Oil, Gas & Consumable Fuels", true));
                    add(ImmutablePair.of("Oil Gas & Consumable Fuels", true));
                    add(ImmutablePair.of("Oil, Gas and Consumable Fuels", false));
                }
            });

            put(SemanticCategoryEnum.INDUSTRY_GROUP.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 5369075679823304405L;

                {
                    add(ImmutablePair.of("Pharmaceuticals, Biotechnology & Life Sciences", true));
                    add(ImmutablePair.of("Pharmaceuticals", false));
                }
            });

            put(SemanticCategoryEnum.JOB_TITLE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("CTO", true));
                    add(ImmutablePair.of("Chief Technical Officer", true));
                }
            });

            put(SemanticCategoryEnum.MONTH.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 6299947732628617617L;

                {
                    add(ImmutablePair.of("AUGUST", true));
                    add(ImmutablePair.of("AOÛT", true));
                    add(ImmutablePair.of("AOUT", true));
                }
            });

            put(SemanticCategoryEnum.MUSEUM.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2478741843516279631L;

                {
                    add(ImmutablePair.of("U.S. 23 Country Music Highway Museum", true));
                    add(ImmutablePair.of("Haldimand County Museum & Archives (Cayuga, Ontario)", true));
                    add(ImmutablePair.of("Haldimand County Museum & Archives", false));
                }
            });

            put(SemanticCategoryEnum.ORGANIZATION.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 1878776770941183595L;

                {
                    add(ImmutablePair.of("Asia-Oceania Federation of Organizations for Medical Physics", true));
                    add(ImmutablePair.of("Direction centrale du renseignement intérieur", true));
                    add(ImmutablePair.of("Citizens United (organization)", true));
                    add(ImmutablePair.of("Citizens United", false));
                }
            });

            put(SemanticCategoryEnum.SECTOR.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Information Technology", true));
                }
            });

            put(SemanticCategoryEnum.STREET_TYPE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("AVENUE", true));
                    add(ImmutablePair.of("AVE", true));
                    add(ImmutablePair.of("AV", true));
                    add(ImmutablePair.of("PÉRIPHÉRIQUE", true));
                    add(ImmutablePair.of("PERIPHERIQUE", true));
                }
            });

            put(SemanticCategoryEnum.MEASURE_UNIT.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Kilogram-force", true));
                    add(ImmutablePair.of("Kilogram", false));// TODO review this index
                    add(ImmutablePair.of("kg", false));// TODO review this index
                    add(ImmutablePair.of("metre", false));// TODO review this index
                    add(ImmutablePair.of("meter", false));// TODO review this index
                }
            });

            put(SemanticCategoryEnum.US_COUNTY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("District of Columbia County", true));
                    add(ImmutablePair.of("District of Columbia", true));
                    add(ImmutablePair.of("East Baton Rouge Parish", true));
                    add(ImmutablePair.of("East Baton Rouge", true));
                    add(ImmutablePair.of("Kenai Peninsula Borough", true));
                    add(ImmutablePair.of("Kenai Peninsula", true));
                    add(ImmutablePair.of("Adams", true));
                }
            });

            put(SemanticCategoryEnum.WEEKDAY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("TUESDAY", true));
                    add(ImmutablePair.of("MARDI", true));
                    add(ImmutablePair.of("TERÇA-FEIRA", true));
                }
            });

            put(SemanticCategoryEnum.FR_COMMUNE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Orange", true));
                    add(ImmutablePair.of("Suresnes", true));

                    add(ImmutablePair.of("ABERGEMENT-CLEMENCIAT", false));
                    add(ImmutablePair.of("Abergement-Clémenciat", false));
                    add(ImmutablePair.of("L'ABERGEMENT-CLEMENCIAT", true));
                    add(ImmutablePair.of("L'Abergement-Clémenciat", true));
                }
            });

            put(SemanticCategoryEnum.FR_DEPARTEMENT.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Hauts de seine", true));
                    add(ImmutablePair.of("Ile de france", false));
                    add(ImmutablePair.of("Île de France", false));
                }
            });

            put(SemanticCategoryEnum.FR_REGION.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Hauts de seine", false));
                    add(ImmutablePair.of("Ile de france", true));
                    add(ImmutablePair.of("Île de France", true));

                    add(ImmutablePair.of("Nord-Pas-de-Calais", false));
                    add(ImmutablePair.of("Picardie", false));
                    add(ImmutablePair.of("Hauts de France", true));
                }
            });

            put(SemanticCategoryEnum.FR_REGION_LEGACY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -2018495996354511195L;

                {
                    add(ImmutablePair.of("Hauts de seine", false));
                    add(ImmutablePair.of("Ile de france", true));
                    add(ImmutablePair.of("Île de France", true));

                    add(ImmutablePair.of("Nord-Pas-de-Calais", true));
                    add(ImmutablePair.of("Picardie", true));
                    add(ImmutablePair.of("Hauts de France", false));
                }
            });

            put(SemanticCategoryEnum.LANGUAGE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 2313423259030664084L;

                {
                    add(ImmutablePair.of("Chinese", true));
                    add(ImmutablePair.of("中文", true));
                    add(ImmutablePair.of("French", true));
                    add(ImmutablePair.of("français", true));
                    add(ImmutablePair.of("Interlingue Occidental", true));
                    add(ImmutablePair.of("ދިވެހިބަސް", true));
                    add(ImmutablePair.of("සිංහල", true));
                }
            });

            put(SemanticCategoryEnum.LANGUAGE_CODE_ISO2.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 4529110270243834044L;

                {
                    add(ImmutablePair.of("en", true));
                    add(ImmutablePair.of("fr", true));
                    add(ImmutablePair.of("zh", true));
                }
            });

            put(SemanticCategoryEnum.LANGUAGE_CODE_ISO3.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -7710318974840029621L;

                {
                    add(ImmutablePair.of("eng", true));
                    add(ImmutablePair.of("fre", true));// iso 639-2 "B" (bibliographic)
                    add(ImmutablePair.of("fra", true));// iso 639-2 "T" (terminology)
                    add(ImmutablePair.of("chi", true));// iso 639-2 "B" (bibliographic)
                    add(ImmutablePair.of("zho", true));// iso 639-2 "T" (terminology)
                }
            });

            put(SemanticCategoryEnum.CA_PROVINCE_TERRITORY.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 2753708096209228674L;

                {
                    add(ImmutablePair.of("Prince Edward Island", true));
                }
            });

            put(SemanticCategoryEnum.CA_PROVINCE_TERRITORY_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -5783790800327766690L;

                {
                    add(ImmutablePair.of("PE", true));
                }
            });

            put(SemanticCategoryEnum.MX_ESTADO.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -5783790800327766690L;

                {
                    add(ImmutablePair.of("Nuevo León", true));
                    add(ImmutablePair.of("Nuevo Leon", true));
                }
            });

            put(SemanticCategoryEnum.MX_ESTADO_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1216320964666546573L;

                {
                    add(ImmutablePair.of("NLE", true));
                    add(ImmutablePair.of("NL", false));// Not including: HASC(Hierarchical administrative subdivision
                                                       // codes)
                }
            });

            put(SemanticCategoryEnum.FR_PHONE.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = 7983289992158907116L;

                {
                    add(ImmutablePair.of("+33123456789", true));
                    add(ImmutablePair.of("+33 243134818", true));
                    add(ImmutablePair.of("0033523456789", true));
                    add(ImmutablePair.of("+33 6 23 45 67 89", true));
                    add(ImmutablePair.of("07.85.01.24.79", true));
                    add(ImmutablePair.of("08 25 01 20 11", true));
                    add(ImmutablePair.of("07 23 45 67 89", true));
                }
            });

            put(SemanticCategoryEnum.LAST_NAME.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -4060170781621972544L;

                {
                    add(ImmutablePair.of("CORREIA", true));
                    add(ImmutablePair.of("DESCHAMPS", false));
                    add(ImmutablePair.of("PIETROWSKI", false));
                    add(ImmutablePair.of("NULL", false));
                    add(ImmutablePair.of("TRUE", false));
                }
            });

            put(SemanticCategoryEnum.HDFS_URL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1241357834655337919L;

                {
                    add(ImmutablePair.of("hdfs://127.0.0.1/user/luis/sample.txt", true));
                    add(ImmutablePair.of("hdfs://toto.com/user/luis/sample.txt", true));
                    add(ImmutablePair.of("PIETROWSKI", false));
                }
            });

            put(SemanticCategoryEnum.FILE_URL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1241357834655337919L;

                {
                    add(ImmutablePair.of("file://localhost/c/WINDOWS/clock.avi", true));
                    add(ImmutablePair.of("file:///C:/WORKSPACE/reports.html", true));
                    add(ImmutablePair.of("PIETROWSKI", false));
                }
            });

            put(SemanticCategoryEnum.MAILTO_URL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1241357834655337919L;

                {
                    add(ImmutablePair.of("mailto:?to=&subject=mailto%20with%20examples&body=http://en.wikipedia.org/wiki/Mailto",
                            true));
                    add(ImmutablePair.of(
                            "mailto:p.dupond@example.com?subject=Sujet%20du%20courrier&cc=pierre@example.org&cc=jacques@example.net&body=Bonjour",
                            true));
                    add(ImmutablePair.of("PIETROWSKI", false));
                }
            });

            put(SemanticCategoryEnum.DATA_URL.getId(), new ArrayList<Pair<String, Boolean>>() {

                private static final long serialVersionUID = -1241357834655337919L;

                {
                    add(ImmutablePair.of("data:text/html;charset=US-ASCII,%3Ch1%3EHello!%3C%2Fh1%3E", true));
                    add(ImmutablePair.of("data:text/html;charset=,%3Ch1%3EHello!%3C%2Fh1%3E", true));
                    add(ImmutablePair.of("data:TEXT/html;charset=,%3Ch1%3EHello!%3C%2Fh1%3E", true));
                    add(ImmutablePair.of("PIETROWSKI", false));
                }
            });

            put(SemanticCategoryEnum.IBAN.getId(), new ArrayList<Pair<String, Boolean>>() {

                {
                    add(ImmutablePair.of("GB87 BARC 2065 8244 9716 55", true));
                    add(ImmutablePair.of("FR7612548029981234567890161", true));
                    add(ImmutablePair.of("BE43068999999501", true));
                    add(ImmutablePair.of("B443068999999501", false));
                    add(ImmutablePair.of("BE43068999999502", false));
                }
            });

            put(SemanticCategoryEnum.UK_POSTAL_CODE.getId(), new ArrayList<Pair<String, Boolean>>() {

                {
                    add(ImmutablePair.of("SL6 1AY", true));
                    add(ImmutablePair.of("SL61AY", true));
                    add(ImmutablePair.of("NT2ABC", false));

                    add(ImmutablePair.of("SW1X 9HB", true));
                    add(ImmutablePair.of("SW1X9HB", true));
                }
            });

        }
    };

    @Before
    public void init() throws URISyntaxException, IOException {
        DictionarySnapshot dictionarySnapshot = new StandardDictionarySnapshotProvider().get();
        catRecognizer = new DefaultCategoryRecognizer(dictionarySnapshot);
    }

    // init email list
    public void initEmailList() throws IOException {
        boolean startInvalid = false;
        InputStream dateStream = this.getClass().getResourceAsStream("emailList.txt"); //$NON-NLS-1$
        BufferedReader br = new BufferedReader(new InputStreamReader(dateStream, "UTF-8"));// for Hindi language Double-byte type
        String line;
        List<Pair<String, Boolean>> emailResultList = EXPECTED_MATCHING_RES_FOR_CATS.get(SemanticCategoryEnum.EMAIL.getId());
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith(commontChar)) {
                    String contronKey = line.replace(commontChar, StringUtils.EMPTY);
                    if (contronKey.equals(invalidChar)) {
                        startInvalid = true;
                    } else if (contronKey.equals(validChar)) {
                        startInvalid = false;
                    }
                    continue;
                }
                if (startInvalid) {
                    emailResultList.add(ImmutablePair.of(line, false));
                } else {
                    emailResultList.add(ImmutablePair.of(line, true));
                }
            }
        } finally {
            br.close();
        }
    }

    @Test
    public void test4EachCategory() throws IOException {
        initEmailList();
        catRecognizer.prepare();

        for (String cat : EXPECTED_MATCHING_RES_FOR_CATS.keySet()) {

            for (Pair<String, Boolean> pairDataRes : EXPECTED_MATCHING_RES_FOR_CATS.get(cat)) {
                // System.out.println(data + " => actual: " + catName);
                List<String> actualCatsList = Arrays.asList(catRecognizer.process(pairDataRes.getLeft()));
                assertEquals("Input data: \"" + pairDataRes.getLeft() + "\" belongs to the <" + cat + "> category.",
                        pairDataRes.getRight(), actualCatsList.contains(cat));
                // assertTrue(Arrays.asList(EXPECTED_CAT_ID_FOR_SINGLE_VALUES.get(data)).contains(catName));
            }
        }
    }
}
