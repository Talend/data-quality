package org.talend.dataquality.semantic.api.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(CategoryRegistryManager.class);
        CategoryRegistryManager manager = mock(CategoryRegistryManager.class);
        when(manager.getLocalRegistryPath()).thenReturn("./target");

        mapper = Mockito.mock(ObjectMapper.class);

        PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(mapper);

        ObjectWriter writer = mock(ObjectWriter.class);
        when(mapper.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        CustomDictionaryHolder holder = Mockito.mock(CustomDictionaryHolder.class);
        Mockito.when(holder.getRegexClassifier()).thenReturn(new UserDefinedClassifier());
        Mockito.when(holder.getTenantID()).thenReturn("tenantId");

        access = new CustomRegexClassifierAccess(holder);
    }

    @Test
    public void getRegExs() throws IOException {

        when(mapper.readValue(any(File.class), (TypeReference) any())).thenReturn(createSubCategoryRegExs());

        List<ISubCategory> regexs = access.getRegExs();
        assertEquals(2, regexs.size());
    }

    @Test
    public void getRegExsEmptyFile() throws IOException {
        when(mapper.readValue(any(File.class), (TypeReference) any())).thenThrow(JsonMappingException.class);

        assertNull(access.getRegExs());
    }

    @Test
    public void getRegExsErrorFile() throws IOException {
        when(mapper.readValue(any(File.class), (TypeReference) any())).thenThrow(IOException.class);

        assertNull(access.getRegExs());
    }

    @Test
    public void deleteRegExWithoutRegExs() {
        access.deleteRegex("1");
        assertNull(access.getRegExs());
    }

    private List<ISubCategory> createSubCategoryRegExs() {
        List<ISubCategory> regeExs = new ArrayList<>();

        UserDefinedCategory category = new UserDefinedCategory("categoryName");
        UserDefinedCategory category2 = new UserDefinedCategory("categoryName2");

        regeExs.add(category);
        regeExs.add(category2);

        return regeExs;
    }
}
