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
package org.talend.dataquality.semantic.index;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link BaseDirectory} which supports accessing Lucene index inside a jar. The inner index files are extracted
 * to a temporary folder if not present.
 */
public class JARDirectory extends Directory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JARDirectory.class);

    private static final Object indexExtractionLock = new Object();

    private final JARDescriptor jarDescriptor;

    final String extractPath;

    final String indexDirectory;

    private FSDirectory fsDir;

    /**
     * Describes a opened JAR file.
     */
    public static class JARDescriptor {

        FileSystem fileSystem;

        String jarFileName;
    }

    /**
     * <p>
     * Creates a new {@link Directory directory} that looks up for Lucene indexes inside a JAR file.
     * </p>
     * <p>
     * <b>Important #1</b>: Intentionally left private. Use {@link ClassPathDirectory#open(URI)} to create an instance of this.
     * </p>
     * <p>
     * <b>Important #2</b>: URI's scheme "jar" stores content in a temporary directory, and is only cleaned upon
     * {@link #close()} call.
     * </p>
     *
     * @param extractPath the location for index content extraction
     * @param descriptor A {@link JARDescriptor descriptor} to the JAR file to open.
     * @param directory A path inside the opened JAR file.
     * @see ClassPathDirectory#open(URI)
     */
    public JARDirectory(String extractPath, JARDescriptor descriptor, String directory) {
        this.extractPath = extractPath;
        this.jarDescriptor = descriptor;
        this.indexDirectory = directory;
        try {
            extractIndex();
        } catch (IOException e) {
            LOGGER.error("Failed to extract index: " + e.getMessage(), e);
        }
    }

    private void extractIndex() throws IOException {
        final String unzipFolderName = extractPath + File.separator + indexDirectory + File.separator;
        final File destinationFolder = Paths.get(unzipFolderName).toFile();
        LOGGER.info("Extracting index to temporary directory: " + destinationFolder.getAbsolutePath());
        if (destinationFolder.exists()) {
            // File was already extracted, reuse it
            fsDir = FSDirectory.open(destinationFolder);
        } else {
            // File was not previously extracted to temporary directory, extract it and open it.
            // To prevent multiple concurrent extracts, lock on class
            synchronized (indexExtractionLock) {
                Path start = jarDescriptor.fileSystem.getPath(indexDirectory);
                if (!destinationFolder.exists()) {
                    destinationFolder.mkdirs();
                }
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Path destFilePath = Paths.get(destinationFolder.toString(), file.getFileName().toString());
                        Files.copy(file, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
                fsDir = FSDirectory.open(destinationFolder);
            }
        }
    }

    @Override
    public void close() throws IOException {
        fsDir.close();
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        return fsDir.createOutput(name, context);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        fsDir.deleteFile(name);
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return fsDir.fileExists(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return fsDir.fileLength(name);
    }

    @Override
    public String[] listAll() throws IOException {
        return fsDir.listAll();
    }

    @Override
    public IndexInput openInput(String arg0, IOContext arg1) throws IOException {
        return fsDir.openInput(arg0, arg1);
    }

    @Override
    public void sync(Collection<String> arg0) throws IOException {
        fsDir.sync(arg0);
    }

    @Override
    public void clearLock(String arg0) throws IOException {
        fsDir.clearLock(arg0);
    }

    @Override
    public LockFactory getLockFactory() {
        return fsDir.getLockFactory();
    }

    @Override
    public Lock makeLock(String arg0) {
        return fsDir.makeLock(arg0);
    }

    @Override
    public void setLockFactory(LockFactory arg0) throws IOException {
        fsDir.setLockFactory(arg0);
    }
}
