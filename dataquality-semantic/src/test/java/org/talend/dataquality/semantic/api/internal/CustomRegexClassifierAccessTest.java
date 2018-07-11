package org.talend.dataquality.semantic.api.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.api.CustomDictionaryHolder;
import org.talend.dataquality.semantic.classifier.ISubCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedCategory;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CategoryRegistryManager.class, CustomRegexClassifierAccess.class })
public class CustomRegexClassifierAccessTest {

    private CustomRegexClassifierAccess access;

    private ObjectMapper mapperMock;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(CategoryRegistryManager.class);
        CategoryRegistryManager manager = mock(CategoryRegistryManager.class);
        when(manager.getLocalRegistryPath()).thenReturn("./target");

        mapperMock = Mockito.mock(ObjectMapper.class);

        ObjectWriter writer = mock(ObjectWriter.class);
        when(mapperMock.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        CustomDictionaryHolder holder = Mockito.mock(CustomDictionaryHolder.class);
        Mockito.when(holder.getRegexClassifier()).thenReturn(new UserDefinedClassifier());
        Mockito.when(holder.getTenantID()).thenReturn("tenantId");

        access = new CustomRegexClassifierAccess(holder);
    }

    @Test
    public void getRegExs() {
        createSubCategoryRegExs();
        List<ISubCategory> regexs = access.getRegExs();
        assertEquals(2, regexs.size());
    }

    @Test
    public void getRegExsEmptyFile() throws IOException {
        Whitebox.setInternalState(access, "mapper", mapperMock);
        when(mapperMock.readValue(any(File.class), any(TypeReference.class))).thenThrow(JsonMappingException.class);

        assertNull(access.getRegExs());
    }

    @Test
    public void getRegExsErrorFile() throws IOException {
        Whitebox.setInternalState(access, "mapper", mapperMock);
        when(mapperMock.readValue(any(File.class), any(TypeReference.class))).thenThrow(IOException.class);

        assertNull(access.getRegExs());
    }

    @Test
    public void deleteRegExWithoutRegExs() {
        createSubCategoryRegExs();
        access.deleteRegex("1");

        assertEquals(1, access.getRegExs().size());
    }

    private void createSubCategoryRegExs() {
        UserDefinedCategory category = new UserDefinedCategory("categoryName");
        category.setId("1");
        UserDefinedCategory category2 = new UserDefinedCategory("categoryName2");
        category2.setId("2");

        access.insertOrUpdateRegex(category);
        access.insertOrUpdateRegex(category2);
    }
}
