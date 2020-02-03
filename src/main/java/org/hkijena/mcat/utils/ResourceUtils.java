/*
 * Copyright by Ruman Gerst
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * This code is licensed under BSD 2-Clause
 * See the LICENSE file provided with this code for the full license.
 */

package org.hkijena.mcat.utils;

import java.io.InputStream;
import java.net.URL;

/**
 * Functions for resource access
 */
public class ResourceUtils {

    /**
     * Returns the resource path for this software
     * @return
     */
    public static String getResourceBasePath() {
        return "/org/hkijena/mcat";
    }

    /**
     * Returns the path of a resource within the getResourceBasePath()
     * @param internalResourcePath
     * @return
     */
    public static String getResourcePath(String internalResourcePath) {
        if(internalResourcePath.startsWith("/"))
            internalResourcePath = internalResourcePath.substring(1);
        return getResourceBasePath() + "/" + internalResourcePath;
    }

    /**
     * Returns a resource URL
     * @param internalResourcePath
     * @return
     */
    public static URL getPluginResource(String internalResourcePath) {
        return ResourceUtils.class.getResource(getResourcePath(internalResourcePath));
    }

    /**
     * Returns a resource stream
     * @param internalResourcePath
     * @return
     */
    public static InputStream getPluginResourceAsStream(String internalResourcePath) {
        return ResourceUtils.class.getResourceAsStream(getResourcePath(internalResourcePath));
    }

}
