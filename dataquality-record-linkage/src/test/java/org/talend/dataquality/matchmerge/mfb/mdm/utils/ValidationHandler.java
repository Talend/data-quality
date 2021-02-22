/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import org.w3c.dom.Element;

/**
 * @see MetadataRepository#load(java.io.InputStream, ValidationHandler)
 */
public interface ValidationHandler {

    void fatal(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    void error(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    void warning(FieldMetadata field, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    void fatal(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    void error(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    void warning(TypeMetadata type, String message, Element element, Integer lineNumber, Integer columnNumber,
            ValidationError error);

    /**
     * Called by validation process to indicate implementation should no longer wait for other messages (example:
     * implementation can store messages and throw an exception that aggregates all messages in one exception when this
     * method is called).
     */
    void end();

    /**
     * @return The number errors this {@link ValidationHandler} received so far (i.e. the number of times the methods
     * error(...) were called).
     */
    int getErrorCount();
}