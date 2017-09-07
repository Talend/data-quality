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

    // the location of local git repo, supposing the data-quality repo is cloned in the same folder of tdq-studio-ee
    private static final String GIT_REPO_ROOT = "../..";

    private static final String TDQ_STUDIO_EE_ROOT = GIT_REPO_ROOT + "/tdq-studio-ee";

    private static final String MAIN_PLUGINS_FOLDER = "/main/plugins";

    private static final String COMPONENTS_FOLDER = "/components";

    private static final String[] PROVIDERS = new String[] { //
            "/org.talend.designer.components.tdqprovider", //
            "/org.talend.designer.components.tdqhadoopprovider", //
            "/org.talend.designer.components.tdqsparkprovider", //
            "/org.talend.designer.components.tdqsparkstprovider",//
    };

    private static final Map<String, String> DEP_VERSION_MAP = new HashMap<String, String>() {

        private static final long serialVersionUID = 1L;

        {
            put("org.talend.dataquality.common", "1.7.1");
            put("org.talend.dataquality.record.linkage", "3.4.1");
            put("org.talend.dataquality.sampling", "2.5.1");
            put("org.talend.dataquality.standardization", "3.4.1");
            put("org.talend.dataquality.email", "1.2.1");
            put("org.talend.dataquality.survivorship", "2.1.1");
        }
    };

    private static final boolean USE_SNAPSHOT_VERSION = true;

    private static void handleComponentDefinition(File f) {
        File compoDefFile = new File(f.getAbsolutePath() + "/" + f.getName() + "_java.xml");

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
                    System.out.println("Updating: " + compoDefFile.getName());
                    FileOutputStream fos = new FileOutputStream(compoDefFile);
                    for (String line : lines) {
                        for (String depName : DEP_VERSION_MAP.keySet()) {
                            if (line.contains(depName)) {
                                System.out.println(depName);
                                // MODULE field
                                line = line.replaceAll(depName + "-\\d.\\d.\\d(-SNAPSHOT)?(.jar)?\"", depName + "-"
                                        + DEP_VERSION_MAP.get(depName) + (USE_SNAPSHOT_VERSION ? "-SNAPSHOT" : "") + "$2\"");
                                // MVN field
                                line = line.replaceAll(depName + "/\\d.\\d.\\d(-SNAPSHOT)?(.jar)?\"", depName + "/"
                                        + DEP_VERSION_MAP.get(depName) + (USE_SNAPSHOT_VERSION ? "-SNAPSHOT" : "") + "$2\"");
                                // UrlPath field
                                line = line.replaceAll(depName + "_\\d.\\d.\\d(.SNAPSHOT)?.jar\"", depName + "_"
                                        + DEP_VERSION_MAP.get(depName) + (USE_SNAPSHOT_VERSION ? ".SNAPSHOT" : "") + ".jar\"");
                            }
                        }
                        IOUtils.write(line + "\n", fos);
                    }
                    fos.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {

        final String resourcePath = UpdateComponentDefinition.class.getResource(".").getFile();
        final String projectRoot = new File(resourcePath).getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getPath() + File.separator;

        for (String provider : PROVIDERS) {
            String componentRootPath = projectRoot + TDQ_STUDIO_EE_ROOT + MAIN_PLUGINS_FOLDER + provider + COMPONENTS_FOLDER;
            System.out.println("\nProvider: " + provider);
            File componentRoot = new File(componentRootPath);
            if (componentRoot.isDirectory()) {
                File[] files = componentRoot.listFiles();
                for (File f : files) {
                    if (f.isDirectory() && f.getName().startsWith("t")) {
                        handleComponentDefinition(f);
                    }
                }
            }
        }

    }

}
