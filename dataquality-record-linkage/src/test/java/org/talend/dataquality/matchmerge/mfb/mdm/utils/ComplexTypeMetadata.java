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

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Represents a "complex" type (i.e. a MDM entity type).
 */
public interface ComplexTypeMetadata extends TypeMetadata {

    /**
     * Return the type name in the <code>locale</code>. If no type name was declared for <code>locale</code>, returns same
     * as {@link TypeMetadata#getName()}.
     * @param locale A non null {@link java.util.Locale locale}.
     * @return The type name for <code>locale</code>.
     */
    String getName(Locale locale);

    /**
     * Return the type Description in the <code>locale</code>. If no type Description was declared for
     * <code>locale</code>, returns same as {@link TypeMetadata#getName()}.
     *
     * @param locale A non null {@link java.util.Locale locale}.
     * @return The type Description for <code>locale</code>.
     */
    String getDescription(Locale locale);

    /**
     * @return The top level {@link org.talend.mdm.commmon.metadata.ComplexTypeMetadata entity}. If <code>this</code> is
     * already the top entity, returns <code>this</code>.
     */
    ComplexTypeMetadata getEntity();

    /**
     * @return The {@link org.talend.mdm.commmon.metadata.FieldMetadata field} that contains this type or <code>null</code> if
     * there's no container. When returning <code>null</code>, {@link #getEntity()} is expected to return <code>this</code>.
     */
    FieldMetadata getContainer();

    /**
     * Changes (or set) the container field for this type.
     * @param field The new container field or <code>null</code> to unset container field.
     */
    void setContainer(FieldMetadata field);

    /**
     * @return A {@link List} of {@link FieldMetadata} that represents key information for the complex type. This method
     *         might return an empty list if no key field is defined for this type.
     * @see #registerKey(FieldMetadata)
     */
    Collection<FieldMetadata> getKeyFields();

    /**
     * @param keyField Register <code>keyField</code> as a key for this type.
     * @throws IllegalArgumentException If <code>keyField</code> is <code>null</code>.
     */
    void registerKey(FieldMetadata keyField);

    /**
     * Retrieves a {@link FieldMetadata} based on name. Name might either be a field name or a '/'-separated path (like
     * 'Family/FamilyId', any '[n]' (index) in the path should be ignored.
     * @param fieldName A field name. Field name is case sensitive and implementations supports path syntax.
     * @return The {@link FieldMetadata} for the given <code>fieldName</code>.
     * @throws IllegalArgumentException If:<ul>
     *                                  <li>field is not declared in type nor inherited types</li>
     *                                  <li><code>fieldName</code> is null or empty string</li>
     *                                  </ul>
     *                                  Field name lookup is case sensitive.
     * @see #hasField(String)
     */
    FieldMetadata getField(String fieldName);

    /**
     * Returns a <b>READ ONLY</b> collection of fields. For adding super type see {@link ComplexTypeMetadata#addField(FieldMetadata)}.
     *
     * @return A collection of super types.
     */
    Collection<FieldMetadata> getFields();

    /**
     * Adds a new field to this type. Please note that if {@link org.talend.mdm.commmon.metadata.FieldMetadata#isKey()} returns
     * <code>true</code>, there's no need to call {@link #registerKey(FieldMetadata)}.
     *
     * @param fieldMetadata A new field to add to this type.
     * @throws IllegalArgumentException If <code>fieldMetadata</code> is <code>null</code> or is type is frozen.
     * @see TypeMetadata#freeze()
     */
    void addField(FieldMetadata fieldMetadata);

    /**
     * @return The {@link List} of users allowed to write to this type.
     */
    List<String> getWriteUsers();

    /**
     * @return The {@link List} of users this type should be hidden to.
     */
    List<String> getHideUsers();

    /**
     * @return The {@link List} of users that can't create an instance of this type.
     */
    List<String> getDenyCreate();

    /**
     * @param type Type of delete (physical delete, logical delete aka. 'send-to-trash delete').
     * @return The {@link List} of users that can't delete an instance of this type.
     */
    List<String> getDenyDelete(DeleteType type);

    /**
     * @return The {@link List} of workflow roles (including writable, read-only and hidden roles. format: role
     * name#processId+processVersion#access rights).
     */
    List<String> getWorkflowAccessRights();

    /**
     * @return Schematron validation rules for this type ready for immediate use (no need to un-escape XML characters).
     *         Returns an empty string if no schematron rule was specified for this type.
     */
    String getSchematron();

    /**
     * @return Returns the fields to be used for primary key info, or empty list if undefined.
     */
    List<FieldMetadata> getPrimaryKeyInfo();

    /**
     * Tests if a {@link FieldMetadata} exists for given name. Name might either be a field name or a '/'-separated path
     * (like 'Family/FamilyId').
     * @param fieldName A field name. Field name is case sensitive and implementations supports path syntax.
     * @return <code>true</code> if type (or inherited types) has a field named <code>fieldName</code>,
     *         <code>false</code> otherwise.
     * @see #getField(String)
     */
    boolean hasField(String fieldName);

    /**
     * @return A collection of {@link ComplexTypeMetadata} that inherits from this type. If this type is not extended
     *         by any other type, this method returns empty {@link Collection}.
     */
    Collection<ComplexTypeMetadata> getSubTypes();

    Collection<ComplexTypeMetadata> getDirectSubTypes();

    /**
     * Registers a {@link ComplexTypeMetadata} type as a sub type of this type.
     */
    void registerSubType(ComplexTypeMetadata type);

    List<FieldMetadata> getLookupFields();

    void declareUsage(ComplexTypeMetadata usage);

    Collection<ComplexTypeMetadata> getUsages();

    void setSubTypes(List<ComplexTypeMetadata> subTypes);

    void registerName(Locale locale, String label);

    void registerDescription(Locale locale, String description);

    enum DeleteType {
        /**
         * Logical delete (a.k.a. send to trash)
         */
        LOGICAL,
        /**
         * Physical delete (permanent delete).
         */
        PHYSICAL
    }

    /**
     * @return Returns the category definitions, or empty list if undefined.
     */
    List<Category> getCategories();
}