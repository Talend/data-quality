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

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java application for updating libphonenumber jars used in studio components.
 * <p>
 * Usage:
 * 1. update the expect version in DEP_VERSION_MAP field.
 * 2. Run this class as Java application.
 */
public class UpdateComponent4libphonenumber {

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

    private static final Map<String, String> DEP_VERSION_MAP = new HashMap<>();

    // when update, change the new version here.
    private static final String LIBPHONENUMBER_VERSION = "8.12.3"; //$NON-NLS-1$

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
        Path filePath = Paths.get(f.getAbsolutePath() + "/" + f.getName() + "_java.xml"); //$NON-NLS-1$ //$NON-NLS-2$
        if (Files.exists(filePath)) {
            try {
                List<String> lines = Files.readAllLines(filePath);

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
                    System.out.println("Updating: " + f.getName()); // NOSONAR
                    DataOutputStream writer = new DataOutputStream(Files.newOutputStream(filePath));
                    for (String line : lines) {
                        for (String depName : DEP_VERSION_MAP.keySet()) {
                            if (line.contains(depName)) {
                                System.out.println(depName); // NOSONAR
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
                        writer.write((line + "\n").getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
                    }
                    writer.close();

                }
            } catch (IOException e) {
                e.printStackTrace(); // NOSONAR
            }

        }
    }

    public static void main(String[] args) {

        final String resourcePath = UpdateComponent4libphonenumber.class.getResource(".").getFile(); //$NON-NLS-1$
        final String projectRoot = new File(resourcePath)
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .getPath() + File.separator;

        for (String provider : PROVIDERS) {
            String componentRootPath =
                    projectRoot + TDQ_STUDIO_EE_ROOT + MAIN_PLUGINS_FOLDER + provider + COMPONENTS_FOLDER;
            System.out.println("\nProvider: " + provider); // NOSONAR
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
