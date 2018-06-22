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
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.TokenizedResolutionMethod;

/**
 * A {@link IAttributeMatcher} implementation decorator that performs {@link String#substring(int, int)} operations on
 * values provided for match.
 */
public class SubstringAttributeMatcher implements IAttributeMatcher, ITokenization {

    private final IAttributeMatcher delegate;

    private final int beginIndex;

    private final int endIndex;

    private SubstringAttributeMatcher(IAttributeMatcher delegate, int beginIndex, int endIndex) {
        this.delegate = delegate;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    /**
     * <p>
     * Returns an {@link IAttributeMatcher} that will perform a substring operation on values provided to
     * {@link #getMatchingWeight(String, String)}.
     * </p>
     * <p>
     * If <code>endIndex</code> is greater than the provided string, it is replaced by the size of the string.
     * </p>
     * 
     * @param delegate An other {@link IAttributeMatcher} implementation.
     * @param beginIndex The begin index for the substring (inclusive).
     * @param endIndex The end index for the substring (exclusive).
     * @return A {@link IAttributeMatcher} that performs a substring on provided values.
     * @throws IndexOutOfBoundsException If <code>beginIndex</code> is lower than 0 or if <code>beginIndex</code> is
     * greater than <code>endIndex</code>.
     */
    public static IAttributeMatcher decorate(IAttributeMatcher delegate, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("Begin index must be greater than 0."); //$NON-NLS-1$
        }
        if (beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("Begin index is greater than end index."); //$NON-NLS-1$
        }
        return new SubstringAttributeMatcher(delegate, beginIndex, endIndex);
    }

    @Override
    public double getMatchingWeight(String str1, String str2) {
        String substring1 = getSubString(str1, beginIndex, endIndex);
        String substring2 = getSubString(str2, beginIndex, endIndex);
        return delegate.getMatchingWeight(substring1, substring2);
    }

    private static String getSubString(String string, int beginIndex, int endIndex) {
        String substring;
        int cpCount = string.codePointCount(0, string.length());
        int beginIndexOffset = string.offsetByCodePoints(0, beginIndex);
        if (endIndex <= cpCount) {
            substring = string.substring(beginIndexOffset, string.offsetByCodePoints(0, endIndex));
        } else {
            substring = string.substring(beginIndexOffset);
        }
        return substring;
    }

    @Override
    public AttributeMatcherType getMatchType() {
        return delegate.getMatchType();
    }

    @Override
    public void setNullOption(NullOption option) {
        delegate.setNullOption(option);
    }

    @Override
    public void setNullOption(String option) {
        delegate.setNullOption(option);
    }

    @Override
    public NullOption getNullOption() {
        return delegate.getNullOption();
    }

    @Override
    public String getAttributeName() {
        return delegate.getAttributeName();
    }

    @Override
    public void setAttributeName(String name) {
        delegate.setAttributeName(name);
    }

    @Override
    public float getThreshold() {
        return delegate.getThreshold();
    }

    @Override
    public double getWeight() {
        return delegate.getWeight();
    }

    @Override
    public void setTokenMethod(TokenizedResolutionMethod tokenMethod) {
        if (!AttributeMatcherType.CUSTOM.equals(delegate.getMatchType()))
            ((AbstractAttributeMatcher) delegate).setTokenMethod(tokenMethod);
    }

}
