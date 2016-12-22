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
package org.talend.dataquality.semantic.classifier.custom;

import java.util.HashSet;
import java.util.Set;

import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.classifier.impl.AbstractSubCategoryClassifier;
import org.talend.dataquality.semantic.filter.ISemanticFilter;
import org.talend.dataquality.semantic.recognizer.MainCategory;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class UserDefinedClassifier extends AbstractSubCategoryClassifier {

    /**
     * Method "addSubCategory" adds a subcategory if it does not already exists.
     *
     * @param category the category to add
     * @return true when the category is added
     */
    public boolean addSubCategory(UserDefinedCategory category) {
        return potentialSubCategories.add(category);
    }

    /**
     * Method "insertOrUpdateSubCategory" inserts or update a category.
     *
     * @param category the category to insert or update
     * @return true when the category is correctly either inserted or updated
     */
    public boolean insertOrUpdateSubCategory(UserDefinedCategory category) {
        if (potentialSubCategories.contains(category)) {
            return updateSubCategory(category);
        } // else
        return addSubCategory(category);
    }

    /**
     * Method "updateSubCategory" updates a given category.
     *
     * @param category the category to update
     * @return true if the category exists and is updated.
     */
    public boolean updateSubCategory(UserDefinedCategory category) {
        if (removeSubCategory(category)) {
            return addSubCategory(category);
        }
        return false;
    }

    public boolean removeSubCategory(UserDefinedCategory category) {
        return potentialSubCategories.remove(category);
    }

    /**
     * classify data into Semantic Category IDs
     *
     * @see org.talend.dataquality.semantic.classifier.impl.AbstractSubCategoryClassifier#classify(java.lang.String)
     */
    @Override
    public Set<String> classify(String str) {
        MainCategory mainCategory = MainCategory.getMainCategory(str);
        return classify(str, mainCategory);
    }

    @Override
    public boolean validCategory(String str, String semanticType) {
        MainCategory mainCategory = MainCategory.getMainCategory(str);
        return validCategory(str, mainCategory, semanticType);
    }

    private boolean validCategory(String str, MainCategory mainCategory, String semanticType) {

        if (mainCategory == MainCategory.UNKNOWN || mainCategory == MainCategory.NULL || mainCategory == MainCategory.BLANK) {
            // FIXME return UNKNOWN instead of EMPTY as the category ID, require synchronization with dataprep team
            return semanticType.equals(SemanticCategoryEnum.UNKNOWN.getId());
        }

        // else
        for (ISubCategory classifier : potentialSubCategories) {
            if (!semanticType.equals(classifier.getName()))
                continue;
            // if the MainCategory is different, ignor it and continue;AlphaNumeric rule should contain pure Alpha and
            // Numeric.
            UserDefinedCategory userDefCat = (UserDefinedCategory) classifier;
            MainCategory classifierCategory = userDefCat.getMainCategory();

            if (mainCategory == MainCategory.Alpha) {
                if (classifierCategory != MainCategory.Alpha && classifierCategory != MainCategory.AlphaNumeric) {
                    break;
                }
            } else if (mainCategory == MainCategory.Numeric) {
                if (classifierCategory != MainCategory.Numeric && classifierCategory != MainCategory.AlphaNumeric) {
                    break;
                }
            } else if (classifierCategory != mainCategory) {
                break;
            }

            ISemanticFilter filter = classifier.getFilter();

            if (filter != null) {
                if (!filter.isQualified(str)) {
                    break;
                }
            }
            ISemanticValidator validator = classifier.getValidator();
            if (validator != null && validator.isValid(str)) {
                return true;
            }
            break;
        }
        return false;
    }

    /**
     * TODO check javadoc
     *
     * classify data into Semantic Category IDs
     * <p/>
     * Validate this input data to adapt which customized rules.
     * <p/>
     * Actually, the main category can be calculated based on the input string, but this method has better performance
     * in case the mainCategory is already calculated previously.
     *
     * @param str is input data
     * @param mainCategory: the MainCategory is computed by the input data
     * @return
     */
    public Set<String> classify(String str, MainCategory mainCategory) {
        Set<String> catSet = new HashSet<>();
        if (mainCategory == MainCategory.UNKNOWN || mainCategory == MainCategory.NULL || mainCategory == MainCategory.BLANK) {
            // FIXME return UNKNOWN instead of EMPTY as the category ID, require synchronization with dataprep team
            catSet.add(SemanticCategoryEnum.UNKNOWN.getId());
            return catSet;
        }

        // else
        for (ISubCategory classifier : potentialSubCategories) {
            // if the MainCategory is different, ignor it and continue;AlphaNumeric rule should contain pure Alpha and
            // Numeric.
            UserDefinedCategory userDefCat = (UserDefinedCategory) classifier;
            MainCategory classifierCategory = userDefCat.getMainCategory();

            if (mainCategory == MainCategory.Alpha) {
                if (classifierCategory != MainCategory.Alpha && classifierCategory != MainCategory.AlphaNumeric) {
                    continue;
                }
            } else if (mainCategory == MainCategory.Numeric) {
                if (classifierCategory != MainCategory.Numeric && classifierCategory != MainCategory.AlphaNumeric) {
                    continue;
                }
            } else if (classifierCategory != mainCategory) {
                continue;
            }

            ISemanticFilter filter = classifier.getFilter();

            if (filter != null) {
                if (!filter.isQualified(str)) {
                    continue;
                }
            }
            ISemanticValidator validator = classifier.getValidator();
            if (validator != null && validator.isValid(str)) {
                catSet.add(classifier.getName());
            }
        }
        return catSet;
    }
}
