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
package org.talend.utils.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * DOC mzhao bug 11128, try to load by current thread loader.
 */
public class TalendURLClassLoader extends URLClassLoader {

    private static Logger LOG = Logger.getLogger(TalendURLClassLoader.class);

    private Map<String, Class<?>> classesMap = new HashMap<String, Class<?>>();

    /**
     * DOC zhao mzhao 11128, try to load by current thread loader.
     * 
     * @param urls
     */
    public TalendURLClassLoader(URL[] urls) {
        this(urls, TalendURLClassLoader.class.getClassLoader());
    }

    /**
     * 
     * The classloader initiated by this contractor will load the class from urls class path , if not find it will use the given classloader to load it.
     * @param urls The url array used to load class.
     * @param classLoader when the class is not found from class path of urls, this classloader will be employeed to load it again.
     */
    public TalendURLClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    /**
     * ADD mzhao 11128, try to load by current thread loader.
     */
    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> cls = classesMap.get(className);
        if (cls == null) {
            try {
                cls = super.findClass(className);
            } catch (ClassNotFoundException cne) {
                LOG.info(cne);
                // MOD mzhao 11128, try to load by current thread loader.e.g: when a class has a super class that needs
                // to load by current loader other than url loader.
                cls = getClass().getClassLoader().loadClass(className);
            }
            classesMap.put(className, cls);
        }
        return cls;
    }
}
