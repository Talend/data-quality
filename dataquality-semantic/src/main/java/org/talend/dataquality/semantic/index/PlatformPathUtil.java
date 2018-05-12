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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
public class PlatformPathUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformPathUtil.class);

    private PlatformPathUtil() {
    }

    /**
     * @deprecated
     * @throws FileNotFoundException
     */
    @Deprecated
    public static InputStream getInputStreamByPlatformURL(String bundleName, String filePath) throws FileNotFoundException {
        try {
            URL url = new URL("platform:/plugin/" + bundleName + "/" + filePath); //$NON-NLS-1$ //$NON-NLS-2$
            return url.openConnection().getInputStream();
        } catch (MalformedURLException e) {
            // if the platform protocol is unknown, try to create a FileInputStream with local path
            return new FileInputStream(filePath);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static String getFilePathByPlatformURL(String bundleName, String filePath) {
        try {
            URL url = new URL("platform:/plugin/" + bundleName + filePath); //$NON-NLS-1$
            return getFilePathByPlatformURL(url);
        } catch (MalformedURLException e) {
            // if the platform protocol is unknown, return the input local path
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return filePath;
    }

    public static String getFilePathByPlatformURL(URL url) throws IOException {
        String filePath = url.getPath();
        try {
            url.openConnection().getInputStream();
        } catch (FileNotFoundException e) {
            // try to get the absolute path from exception message
            final String msg = e.getMessage();
            LOGGER.warn(msg, e);
            if (filePath.endsWith("/")) { //$NON-NLS-1$
                filePath = filePath.substring(0, filePath.length() - 1);
            }
            final String osFilePath = filePath.replace("/", File.separator); //$NON-NLS-1$
            final int idx = msg.indexOf(osFilePath);
            if (idx > 0) {
                return msg.substring(0, idx + filePath.length());
            }
        }
        return filePath;
    }
}
