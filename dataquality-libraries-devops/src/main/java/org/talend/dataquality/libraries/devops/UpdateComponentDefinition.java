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
package org.talend.dataquality.libraries.devops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java application for updating DQ library versions used in studio components.
 * 
 * Usage:
 * 1. update the expect version in DEP_VERSION_MAP field.
 * 2. tell the application if need to USE_SNAPSHOT_VERSION.
 * 3. Run this class as Java application.
 * 
 * @author sizhaoliu
 */
public class UpdateComponentDefinition {

    private UpdateComponentDefinition() {
        // no need to implement
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateComponentDefinition.class);

    // the location of local git repo, supposing the data-quality repo is cloned in the same folder of tdq-studio-ee
    private static final String GIT_REPO_ROOT = "../.."; //$NON-NLS-1$

    private static final String TDQ_STUDIO_EE_ROOT = GIT_REPO_ROOT + "/tdq-studio-ee"; //$NON-NLS-1$

    private static final String MAIN_PLUGINS_FOLDER = "/main/plugins"; //$NON-NLS-1$

    private static final String COMPONENTS_FOLDER = "/components"; //$NON-NLS-1$

    private static final String DQ_LIB_VERSION = "6.2.2-SNAPSHOT"; //$NON-NLS-1$

    private static final String DAIKON_VERSION = "0.31.1"; //$NON-NLS-1$

    private static final String[] PROVIDERS = new String[] { //
            "/org.talend.designer.components.tdqprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqhadoopprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqsparkprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqsparkstprovider",// //$NON-NLS-1$
    };

    private static final Map<String, String> DEP_VERSION_MAP = new HashMap<String, String>();

    private static final long serialVersionUID = 1L;

    static {
        DEP_VERSION_MAP.put("daikon", DAIKON_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.common", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.record.linkage", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.sampling", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.standardization", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.email", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.survivorship", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.text.japanese", DQ_LIB_VERSION); //$NON-NLS-1$
        DEP_VERSION_MAP.put("org.talend.dataquality.statistics", DQ_LIB_VERSION); //$NON-NLS-1$
    }

    private static void handleComponentDefinition(File f) {
        File compoDefFile = new File(f.getAbsolutePath() + "/" + f.getName() + "_java.xml"); //$NON-NLS-1$ //$NON-NLS-2$

        if (compoDefFile.exists()) {
            try {
                FileInputStream file = new FileInputStream(compoDefFile);
                List<String> lines = IOUtils.readLines(file);

                boolean needUpdate = false;
                for (String line : lines) {
                    for (String depName : DEP_VERSION_MAP.keySet()) {
                        if (line.contains(depName)) {
                            needUpdate = true;
                            break;
                        }
                    }
                }

                if (needUpdate) {
                    LOGGER.info("Updating: " + compoDefFile.getName()); //$NON-NLS-1$
                    FileOutputStream fos = new FileOutputStream(compoDefFile);
                    for (String line : lines) {
                        for (String depName : DEP_VERSION_MAP.keySet()) {
                            if (line.contains(depName)) {
                                LOGGER.info(depName);
                                // MODULE field
                                line = line.replaceAll(depName + "-\\d\\d?.\\d\\d?.\\d\\d?(-SNAPSHOT)?(.jar)?\"", //$NON-NLS-1$
                                        depName + "-" + DEP_VERSION_MAP.get(depName) + "$2\""); //$NON-NLS-1$ //$NON-NLS-2$
                                // MVN field
                                line = line.replaceAll(depName + "/\\d\\d?.\\d\\d?.\\d\\d?(-SNAPSHOT)?(.jar)?\"", //$NON-NLS-1$
                                        depName + "/" + DEP_VERSION_MAP.get(depName) + "$2\""); //$NON-NLS-1$ //$NON-NLS-2$
                                // UrlPath field
                                line = line.replaceAll(depName + "_\\d\\d?.\\d\\d?.\\d\\d?(.SNAPSHOT)?.jar\"", depName //$NON-NLS-1$
                                        + "_" + DEP_VERSION_MAP.get(depName).replace('-', '.') + ".jar\""); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                        IOUtils.write(line + "\n", fos); //$NON-NLS-1$
                    }
                    fos.close();

                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    }

    public static void main(String[] args) {

        final String resourcePath = UpdateComponentDefinition.class.getResource(".").getFile(); //$NON-NLS-1$
        final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getPath() + File.separator;

        for (String provider : PROVIDERS) {
            String componentRootPath = projectRoot + TDQ_STUDIO_EE_ROOT + MAIN_PLUGINS_FOLDER + provider + COMPONENTS_FOLDER;
            LOGGER.info("\nProvider: " + provider); //$NON-NLS-1$
            File componentRoot = new File(componentRootPath);
            if (componentRoot.isDirectory()) {
                File[] files = componentRoot.listFiles();
                for (File f : files) {
                    if (f.isDirectory() && f.getName().startsWith("t")) { //$NON-NLS-1$
                        handleComponentDefinition(f);
                    }
                }
            }
        }

    }

}
