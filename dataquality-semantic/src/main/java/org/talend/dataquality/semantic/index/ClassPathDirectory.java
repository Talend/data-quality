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
import java.nio.file.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.semantic.api.CategoryRegistryManager;

/**
 * A read-only directory that reads index from a JAR file. It supports several URI scheme:
 * <ul>
 * <li>jar</li>
 * <li>file</li>
 * <li>bundleresource</li>
 * </ul>
 */
public class ClassPathDirectory {

    private static final Logger LOGGER = Logger.getLogger(ClassPathDirectory.class);

    private static JARDirectoryProvider provider = new SingletonProvider();

    private ClassPathDirectory() {
    }

    /**
     * Set the location of index extraction for directories opened by jar URI.
     * <p/>
     * By default, if this method is not called, the index will be extracted to a sub-folder of java.io.tmpdir.
     * 
     * @deprecated use {@link CategoryRegistryManager.setLocalRegistryPath(folder)} instead
     */
    @Deprecated
    public static void setLocalIndexFolder(String folder) {
        CategoryRegistryManager.setLocalRegistryPath(folder);
    }

    /**
     * Allow external code to change behavior about extracted Lucene indexes (always extract a fresh copy or reuse
     * previous extract).
     * 
     * @param provider An implementation of {@link JARDirectoryProvider}. See {@link BasicProvider} or
     * {@link SingletonProvider} for examples.
     * @see BasicProvider
     * @see SingletonProvider
     */
    public static synchronized void setProvider(JARDirectoryProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider can not be null.");
        }
        ClassPathDirectory.provider = provider;
    }

    /**
     * <p>
     * Creates a new {@link Directory directory} that picks up the right implementation depending on URI's scheme.
     * </p>
     * 
     * @param uri A valid URI to a Lucene index
     * @return A {@link Directory} to the Lucene content in <code>uri</code>.
     */
    public static synchronized Directory open(URI uri) {
        if ("jar".equals(uri.getScheme())) {
            LOGGER.info("Opening '" + uri + "' ...");
            try {
                return provider.get(uri);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to open JAR '" + uri + "'.", e);
            }
        } else if ("file".equals(uri.getScheme())) {
            LOGGER.debug("Opening '" + uri + "' ...");
            try {
                return FSDirectory.open(new File(uri));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open path '" + uri + "'.", e);
            }
        } else if ("bundleresource".equals(uri.getScheme())) { // for OSGI environment
            LOGGER.info("Opening '" + uri + "' ...");
            try {
                final String path = PlatformPathUtil.getFilePathByPlatformURL(uri.toURL());
                return FSDirectory.open(new File(path));
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to open bundleresource '" + uri + "'.", e);
            }
        } else {
            LOGGER.info("Opening '" + uri + "' ...");
            throw new UnsupportedOperationException("Unsupported scheme '" + uri.getScheme() + "'.");
        }
    }

    public static void destroy() {
        provider.destroy();
    }

    /**
     * An interface to provide Lucene indexes based on provided location (as URI).
     */
    public interface JARDirectoryProvider {

        /**
         * Returns a {@link Directory lucene directory} for provided location (as URI).
         * 
         * @param uri An URI to a JAR file.
         * @return A {@link Directory lucene directory} ready to be used in Lucene code.
         * @throws Exception
         */
        Directory get(URI uri) throws IOException;

        /**
         * Destroys all cached resources by this provider.
         */
        void destroy();
    }

    /**
     * An implementation that extract only once content on disk for a given URI.
     */
    public static class SingletonProvider implements JARDirectoryProvider {

        private static final BasicProvider provider = new BasicProvider();

        private static final Map<URI, Directory> instances = new HashMap<>();

        @Override
        public synchronized Directory get(URI uri) throws IOException {
            Directory dir = instances.get(uri);
            if (dir == null) {
                instances.put(uri, provider.get(uri));
            } else {
                try {
                    dir.listAll();
                } catch (AlreadyClosedException e) {
                    LOGGER.debug("Directory has already been closed. Re-extracting ...", e);
                    instances.put(uri, provider.get(uri));
                }
            }
            return instances.get(uri);
        }

        @Override
        public void destroy() {
            provider.destroy();
        }
    }

    /**
     * An implementation that does not perform any reuse of previously extracted content.
     */
    public static class BasicProvider implements JARDirectoryProvider {

        private static final Map<URI, FileSystem> openedJars = new HashMap<>();

        /**
         * Holds all opened class path directory instances for clean up
         * TODO This is temporary until a more global resource management system is found/proposed
         *
         * @see #destroy()
         */
        private static final Set<JARDirectory> classPathDirectories = new HashSet<>();

        private static FileSystem openOrGet(String uri) throws IOException {
            FileSystem fs;
            final URI jarURI = URI.create(uri);
            synchronized (openedJars) {
                fs = openedJars.get(jarURI);
                if (fs == null) {
                    fs = FileSystems.newFileSystem(jarURI, Collections.<String, String> emptyMap());
                    openedJars.put(jarURI, fs);
                }
            }
            return fs;
        }

        @Override
        public Directory get(URI uri) throws IOException {
            String jarFile = StringUtils.substringBefore(uri.toString(), "!"); //$NON-NLS-1$

            String extractionRoot = CategoryRegistryManager.getLocalRegistryPath() + File.separator
                    + CategoryRegistryManager.SHARED_FOLDER_NAME + File.separator
                    + CategoryRegistryManager.PRODUCTION_FOLDER_NAME;
            JARDirectory.JARDescriptor openedJar = new JARDirectory.JARDescriptor();
            // Extract all nested JARs
            StringTokenizer tokenizer = new StringTokenizer(uri.toString(), "!"); //$NON-NLS-1$
            FileSystem fs = null;
            while (tokenizer.hasMoreTokens()) {
                final String current = tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    break;
                } else if (fs == null) {
                    fs = openOrGet(current);
                } else { // fs != null
                    final Path path = fs.getPath(current);
                    final String unzipFile = extractionRoot + File.separator + path.getFileName();
                    final Path destFile = Paths.get(unzipFile);
                    final File destinationFile = destFile.toFile();
                    if (!destinationFile.exists()) {
                        destinationFile.mkdirs();
                        Files.copy(path, destFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    // UUID ensures the path is unique, no need for openOrGet(...)
                    fs = FileSystems.newFileSystem(destFile, Thread.currentThread().getContextClassLoader());
                }
            }
            openedJar.fileSystem = fs;
            openedJar.jarFileName = jarFile;
            String directory = StringUtils.substringAfterLast(uri.toString(), "!"); //$NON-NLS-1$
            LOGGER.debug("Creating JARDirectory for '" + directory + "' ...");
            final JARDirectory jarDirectory = new JARDirectory(extractionRoot, openedJar, directory);
            classPathDirectories.add(jarDirectory);
            return jarDirectory;
        }

        /**
         * Destroy all resources that instances may have created on disk.
         */
        @Override
        public void destroy() {
            final Iterator<JARDirectory> iterator = classPathDirectories.iterator();
            while (iterator.hasNext()) {
                final JARDirectory jarDirectory = iterator.next();
                try {
                    jarDirectory.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close directory at " + jarDirectory.indexDirectory + " (location : "
                            + jarDirectory.extractPath + ").", e);
                } finally {
                    iterator.remove();
                }
            }
            for (Map.Entry<URI, FileSystem> entry : openedJars.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close " + entry.getValue() + ".", e);
                }
            }
        }
    }

}
