/*
 * Copyright (C) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb.mdm.utils;

import java.util.List;
import java.util.Locale;

/**
 * Represents a "field" in a {@link ComplexTypeMetadata}. A field is an element in a MDM entity where user can specify
 * values.
 */
public interface FieldMetadata extends MetadataVisitable, MetadataExtensible {

    /**
     * @return The field's name that should be unique within the type
     */
    public String getName();

    /**
     * @return <code>true</code> if field is defined as key <b>in its declaring type</b>. Field may not be defined as a
     * key in its declaring type but might be used as a key in sub types.
     */
    public boolean isKey();

    /**
     * @return The {@link TypeMetadata} of the field's value.
     */
    TypeMetadata getType();

    /**
     * @return Returns the {@link TypeMetadata} that <b>contains</b> the field. Field might be contained by a type but
     * declared in another (in case of inheritance).
     * @see #getDeclaringType()
     */
    String getContainingType();

    /**
     * Changes containing type for this field. Use this method with extra caution.
     *
     * @param typeMetadata The new containing type for this field.
     * @see #getContainingType()
     */
    void setContainingType(ComplexTypeMetadata typeMetadata);

    /**
     * @return Returns the {@link TypeMetadata} that <b>declares</b> the field. Field might be contained by a type but
     * declared in another (in case of inheritance).
     * @see #getContainingType()
     */
    TypeMetadata getDeclaringType();

    /**
     * @return The {@link List} of users this field should be hidden to.
     */
    List<String> getHideUsers();

    /**
     * @return The {@link List} of users allowed to write to this field.
     */
    List<String> getWriteUsers();

    /**
     * @return The {@link List} of workflow roles (including writable, read-only and hidden roles. format: role
     * name#processId+processVersion#access rights).
     */
    List<String> getWorkflowAccessRights();

    /**
     * @return <code>true</code> if field contains multiple values (i.e. a sequence of values).
     */
    boolean isMany();

    /**
     * @return <code>true</code> if field <b>must</b> have a value (usually, this means a minOccurs='1' in the data
     * model).
     */
    boolean isMandatory();

    /**
     * "Adopt" this field in <code>metadata</code> type. This method performs all necessary operations so this field
     * behaves as is field was declared in <code>metadata</code>.
     *
     * @param metadata The new type.
     */
    void adopt(ComplexTypeMetadata metadata);

    /**
     * Copy the field and all depending information in <code>repository</code>.
     *
     * @return A copy of this field.
     */
    FieldMetadata copy();

    /**
     * Freezes all modifications that can be done to a field. Similar to {@link TypeMetadata#freeze()}.
     *
     * @return A frozen field metadata.
     * @see TypeMetadata#freeze()
     */
    FieldMetadata freeze();

    /**
     * Promotes this field to "key". After this method has been called, {@link #isKey()} must return <code>true</code>.
     */
    void promoteToKey();

    /**
     * Validates the field: performs assertions on content specific to MDM (this method should not raise XSD compliance
     * issues).
     *
     * @param handler A {@link ValidationHandler} to be used for error / warning reporting.
     * @see ValidationHandler#error(FieldMetadata, String, org.w3c.dom.Element, Integer, Integer, ValidationError)
     */
    void validate(ValidationHandler handler);

    /**
     * @return A XPath-like path from the top level entity type down to this field. Path <b>DOES NOT</b> include the MDM
     * entity type name. Returned values look like "address/address/street" or "id".
     * @since 5.4
     */
    String getPath();

    /**
     * @return The MDM entity type name. In fact, this method is a shortcut to a substring on {@link #getPath()} to get
     * only the MDM entity type name.
     * @since 5.4
     */
    String getEntityTypeName();

    void registerName(Locale locale, String name);

    String getName(Locale locale);

    void registerDescription(Locale locale, String description);

    String getDescription(Locale locale);

    String getVisibilityRule();

    /**
     * @return The Permission Operation Role List, the return list is a collection class that is used for storing No_Add right Role list,
     * the method is related with {@link #getWriteUsers()} and {@link #getNoRemoveRoles()}. now just support for ReferenceFieldMetadata type.
     * The Value is denoted as <b>existing Role information</b>, like Demo_Manager/Demo_User.<p>
     * <pre>
     *  <value>[Demo_User, Demo_Manager]</value>
     * </pre>
     * @since 7.1
     */
    List<String> getNoAddRoles();

    /**
     * @return The Permission Operation Role List, the return list is a collection class that is used for storing No_Remove right Role list,
     * the method is related with {@link #getWriteUsers()} and {@link #getNoAddRoles()}. now just support for ReferenceFieldMetadata type.
     * The Value is denoted as <b>existing Role information</b>, like Demo_Manager/Demo_User.<p>
     * <pre>
     *  <value>[Demo_User, Demo_Manager]</value>
     * </pre>
     * @since 7.1
     */
    List<String> getNoRemoveRoles();
}