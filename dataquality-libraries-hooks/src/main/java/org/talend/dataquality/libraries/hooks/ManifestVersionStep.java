package org.talend.dataquality.libraries.hooks;

import com.google.common.collect.Maps;
import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import com.itemis.maven.plugins.cdi.annotations.RollbackOnError;
import com.itemis.maven.plugins.cdi.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ProcessingStep(id = "manifestDQ", description = "Upgrade the version defined in the MANIFEST files.")
public class ManifestVersionStep implements CDIMojoProcessingStep {

    private static final String BUNDLE_VERSION_STRING = "Bundle-Version: ";

    @Inject
    private Logger log;

    private Map<String, String> cachedManifest;
    @Inject
    @Named("reactorProjects")
    protected List<MavenProject> reactorProjects;

    @Override
    public void execute(ExecutionContext context) {
        this.cachedManifest = Maps.newHashMap();
        for (MavenProject project : this.reactorProjects) {
            this.log.debug("\tPreparing module '" + project.getArtifactId() + "'.");
            Path currentModuleRoot = project.getBasedir().toPath();

            try (Stream<Path> paths = Files.find(currentModuleRoot, Integer.MAX_VALUE,
                    (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith("MANIFEST.MF"))) {

                paths.forEach(path -> {
                    try {
                        updateManifestVersion(path, project.getVersion());
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                });
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void updateManifestVersion(Path manifestPath, String targetVersion) throws IOException {
        File manifestFile = manifestPath.toFile();
        if (manifestFile.exists()) {
            this.log.info("Updating: " + manifestFile.getAbsolutePath() + " with Version " + targetVersion);
            FileInputStream fis = new FileInputStream(manifestFile);
            List<String> lines = IOUtils.readLines(fis);
            FileOutputStream fos = new FileOutputStream(manifestFile);

            for (String line : lines) {
                String lineToWrite = line;
                if (line.startsWith(BUNDLE_VERSION_STRING)) {
                    cachedManifest.put(manifestPath.toAbsolutePath().toString(), line);
                    lineToWrite = BUNDLE_VERSION_STRING + targetVersion.replace("-", ".");
                }
                IOUtils.write(lineToWrite + "\n", fos);
            }
            fos.flush();
            fos.close();
            fis.close();
        }
    }

    @RollbackOnError
    public void rollback(ExecutionContext context, Throwable t) {
        this.log.info(
                "Rollback of all pom changes necessary for setting of the development version as well as reverting any made SCM commits.");

        for (MavenProject project : this.reactorProjects) {
            this.log.debug("\tPreparing module '" + project.getArtifactId() + "'.");
            Path currentModuleRoot = project.getBasedir().toPath();

            try (Stream<Path> paths = Files.find(currentModuleRoot, Integer.MAX_VALUE,
                    (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith("MANIFEST.MF"))) {

                paths.forEach(path -> {
                    try {
                        rollbackManifest(path);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                });
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void rollbackManifest(Path manifestPath) throws IOException {
        File manifestFile = manifestPath.toFile();
        if (manifestFile.exists()) {
            this.log.info("Rollback : " + manifestFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(manifestFile);
            List<String> lines = IOUtils.readLines(fis);
            FileOutputStream fos = new FileOutputStream(manifestFile);

            for (String line : lines) {
                String lineToWrite = line;
                if (line.startsWith(BUNDLE_VERSION_STRING)) {
                    lineToWrite = cachedManifest.get(manifestPath.toAbsolutePath().toString());
                }
                IOUtils.write(lineToWrite + "\n", fos);
            }
            fos.flush();
            fos.close();
            fis.close();
        }
    }
}
