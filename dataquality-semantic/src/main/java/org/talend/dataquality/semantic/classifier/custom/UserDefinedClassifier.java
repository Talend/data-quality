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
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.ISubCategoryClassifier;
import org.talend.dataquality.semantic.classifier.impl.AbstractSubCategoryClassifier;
import org.talend.dataquality.semantic.filter.ISemanticFilter;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.MainCategory;
import org.talend.dataquality.semantic.validator.AbstractRegexSemanticValidator;
import org.talend.dataquality.semantic.validator.ISemanticValidator;

/**
 * created by talend on 2015-07-28 Detailled comment.
 */
public class UserDefinedClassifier extends AbstractSubCategoryClassifier {

    private static final long serialVersionUID = 6641017802505586690L;

    /**
     * Method "addSubCategory" adds a subcategory if it does not already exists.
     *
     * @param category the category to add
     * @return true when the category is added
     */
    public boolean addSubCategory(ISubCategory category) {
        return potentialSubCategories.add(category);
    }

    /**
     * Method "insertOrUpdateSubCategory" inserts or update a category.
     *
     * @param category the category to insert or update
     * @return true when the category is correctly either inserted or updated
     */
    public boolean insertOrUpdateSubCategory(ISubCategory category) {
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
    public boolean updateSubCategory(ISubCategory category) {
        if (removeSubCategory(category)) {
            return addSubCategory(category);
        }
        return false;
    }

    public boolean removeSubCategory(ISubCategory category) {
        return potentialSubCategories.remove(category);
    }

    /**
     * classify data into Semantic Category IDs
     *
     * @see ISubCategoryClassifier#classify(String, List, List)
     * @param data
     * @param sharedCategories
     * @param tenantCategories
     */
    @Override
    public Set<String> classify(String data) {
        MainCategory mainCategory = MainCategory.getMainCategory(data);
        return classify(data, mainCategory);
    }

    @Override
    public boolean validCategories(String str, DQCategory semanticType, Set<DQCategory> children) {
        MainCategory mainCategory = MainCategory.getMainCategory(str);
        if (mainCategory == MainCategory.NULL || mainCategory == MainCategory.BLANK) {
            return false;
        }
        if (CollectionUtils.isEmpty(children)) {
            return validCategories(str, mainCategory, semanticType);
        }
        return validChildrenCategories(str, mainCategory, children);

    }

    /**
     * if there are children, we valid a COMPOUND category, so we have to valid the string with the children categories
     * list
     * 
     * @param str, the string to valid
     * @param mainCategory
     * @param children, the children categories list
     * @return
     */
    private boolean validChildrenCategories(String str, MainCategory mainCategory, Set<DQCategory> children) {
        int cpt = 0;
        final Set<String> childrenId = new HashSet<>();
        for (DQCategory child : children) {
            childrenId.add(child.getId());
        }
        for (ISubCategory classifier : potentialSubCategories) {
            if (childrenId.contains(classifier.getId())) {
                cpt++;
                if (isValid(str, mainCategory, (UserDefinedCategory) classifier, true)) {
                    return true;
                }
                if (cpt == children.size()) {
                    return false;
                }
            }
        }
        return false;

    }

    private boolean validCategories(String str, MainCategory mainCategory, DQCategory semanticType) {
        for (ISubCategory classifier : potentialSubCategories) {
            if (semanticType.getId().equals(classifier.getId())) {
                return isValid(str, mainCategory, (UserDefinedCategory) classifier, true);
            }

        }
        return false;
    }

    /**
     * <p>
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
        if (mainCategory != MainCategory.NULL && mainCategory != MainCategory.BLANK) {
            for (ISubCategory classifier : potentialSubCategories) {
                if (isValid(str, mainCategory, (UserDefinedCategory) classifier, false)) {
                    catSet.add(classifier.getId());
                }
            }
        }
        return catSet;

    }

    private boolean isValid(String str, MainCategory mainCategory, UserDefinedCategory classifier, boolean caseSensitive) {
        MainCategory classifierCategory = classifier.getMainCategory();
        if (classifierCategory == null)
            return false;

        // if the MainCategory is different, ignor it and continue;AlphaNumeric rule should contain pure Alpha and
        // Numeric.
        if (mainCategory == MainCategory.Alpha || mainCategory == MainCategory.Numeric) {
            if (!classifierCategory.equals(mainCategory) && !classifierCategory.equals(MainCategory.AlphaNumeric)) {
                return false;
            }
        } else if (!classifierCategory.equals(mainCategory)) {
            return false;
        }
        if (invalidFilter(str, classifier.getFilter())) {
            return false;
        }
        return validValidator(str, classifier.getValidator(), caseSensitive);

    }

    private boolean invalidFilter(String str, ISemanticFilter filter) {
        return filter != null && !filter.isQualified(str);
    }

    private boolean validValidator(String str, ISemanticValidator validator, boolean caseSensitive) {
        return validator != null && validator.isValid(str, caseSensitive);
    }

    /**
     * 
     * Method "getPatternStringByCategoryId" get pattern string by the id of category
     * 
     * @param categoryId the id of category
     * @return null if categoryId is null or the categoryid is not exist else string of pattern
     */
    public String getPatternStringByCategoryId(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        for (ISubCategory category : getClassifiers()) {
            if (categoryId.equals(category.getId())) {
                return ((UserDefinedCategory) category).getValidator().getPatternString();
            }
        }
        return null;
    }

    public boolean isGenerexCompliant(String categoryId) {
        if (categoryId == null) {
            return false;
        }
        for (ISubCategory category : getClassifiers()) {
            if (categoryId.equals(category.getId())) {
                if (category.getValidator() instanceof UserDefinedRegexValidator) {
                    AbstractRegexSemanticValidator validator = (AbstractRegexSemanticValidator) category.getValidator();
                    return validator.getGenerexCompliant();
                }
            }
        }
        return false;
    }
}
