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
package org.talend.dataquality.semantic.broadcast;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.model.DQCategory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BroadcastMetadataObjectTest {

    private static final Map<String, String[]> TEST_INDEX_CONTENT = new LinkedHashMap<String, String[]>() {

        private static final long serialVersionUID = 1L;

        {
            put("AIRPORT", new String[] { "CDG" });
            put("COMPANY", new String[] { "Talend SA" });
            put("STREET_TYPE", new String[] { "BOULEVARD", "BD" });
        }
    };

    @Test
    public void testConstructor() throws Exception {
        // GIVEN
        final DQCategory catPhone = CategoryRegistryManager.getInstance()
                .getCategoryMetadataById(SemanticCategoryEnum.PHONE.getTechnicalId());
        final DQCategory catFRPhone = CategoryRegistryManager.getInstance()
                .getCategoryMetadataById(SemanticCategoryEnum.FR_PHONE.getTechnicalId());
        final Map<String, DQCategory> dqCatMap = new HashMap<String, DQCategory>() {

            private static final long serialVersionUID = 1L;

            {
                put(catPhone.getId(), catPhone);
                put(catFRPhone.getId(), catFRPhone);
            }
        };

        // WHEN
        BroadcastMetadataObject bmo = new BroadcastMetadataObject(dqCatMap);
        ObjectMapper mapper = new ObjectMapper();
        String bmoStringToSend = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bmo);

        // THEN
        // the json payload must conform
        assertEquals(//
                IOUtils.toString(this.getClass().getResourceAsStream("broadcastMetadataObject.json")).replaceAll("\\s*|\t|\r|\n",
                        ""), //
                bmoStringToSend.replaceAll("\\s*|\t|\r|\n", ""));

        // and be able reconstruct object from payload without exception
        mapper.readValue(bmoStringToSend, BroadcastMetadataObject.class);
    }
}
