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
 * Java application for updating libphonenumber jars used in studio components.
 * 
 * Usage:
 * 1. update the expect version in DEP_VERSION_MAP field.
 * 2. Run this class as Java application.
 * 
 * @author msjian
 */
public class UpdateComponent4libphonenumber {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateComponent4libphonenumber.class);

    // the location of local git repo, supposing the data-quality repo is cloned in the same folder of tdq-studio-ee
    private static final String GIT_REPO_ROOT = "../.."; //$NON-NLS-1$

    private static final String TDQ_STUDIO_EE_ROOT = GIT_REPO_ROOT + "/tdq-studio-ee"; //$NON-NLS-1$

    private static final String MAIN_PLUGINS_FOLDER = "/main/plugins"; //$NON-NLS-1$

    private static final String COMPONENTS_FOLDER = "/components"; //$NON-NLS-1$

    private static final String[] PROVIDERS = new String[] { //
            "/org.talend.designer.components.tdqprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqhadoopprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqsparkprovider", // //$NON-NLS-1$
            "/org.talend.designer.components.tdqsparkstprovider",// //$NON-NLS-1$
    };

    private static final Map<String, String> DEP_VERSION_MAP = new HashMap<String, String>();

    private static final long serialVersionUID = 1L;

    // when update, change the new version here.
    private static final String LIBPHONENUMBER_VERSION = "8.10.7"; //$NON-NLS-1$

    static {
        // DEP_VERSION_MAP.put("carrier", "1.85"); //$NON-NLS-1$ //$NON-NLS-2$
        // DEP_VERSION_MAP.put("geocoder", "2.95"); //$NON-NLS-1$ //$NON-NLS-2$
        DEP_VERSION_MAP.put("libphonenumber", LIBPHONENUMBER_VERSION); //$NON-NLS-1$
        // DEP_VERSION_MAP.put("prefixmapper", "2.113"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private UpdateComponent4libphonenumber() {
        // no need to implement
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
                                line = line.replaceAll(depName + "-\\d\\d?.\\d\\d?.\\d\\d?(.jar)?\"", //$NON-NLS-1$
                                        depName + "-" + DEP_VERSION_MAP.get(depName) + ".jar\""); //$NON-NLS-1$ //$NON-NLS-2$
                                // MVN field
                                line = line.replaceAll(depName + "-\\d\\d?.\\d\\d?.\\d\\d?", //$NON-NLS-1$
                                        depName + "-" + DEP_VERSION_MAP.get(depName)); //$NON-NLS-1$
                                // UrlPath field
                                line = line.replaceAll(depName + "-\\d\\d?.\\d\\d?.\\d\\d?.jar\"", depName //$NON-NLS-1$
                                        + "-" + DEP_VERSION_MAP.get(depName) + ".jar\""); //$NON-NLS-1$ //$NON-NLS-2$
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

        final String resourcePath = UpdateComponent4libphonenumber.class.getResource(".").getFile(); //$NON-NLS-1$
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
