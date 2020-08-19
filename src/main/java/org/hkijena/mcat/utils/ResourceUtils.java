/*******************************************************************************
 * Copyright by Bianca Hoffmann, Ruman Gerst, Zoltán Cseresnyés and Marc Thilo Figge
 *
 * Research Group Applied Systems Biology
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Beutenbergstr. 11a, 07745 Jena, Germany
 *
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 *
 *******************************************************************************/

package org.hkijena.mcat.utils;

import java.io.InputStream;
import java.net.URL;

/**
 * Functions for resource access
 */
public class ResourceUtils {

    /**
     * Returns the resource path for this software
     *
     * @return
     */
    public static String getResourceBasePath() {
        return "/org/hkijena/mcat";
    }

    /**
     * Returns the path of a resource within the getResourceBasePath()
     *
     * @param internalResourcePath
     * @return
     */
    public static String getResourcePath(String internalResourcePath) {
        if (internalResourcePath.startsWith("/"))
            internalResourcePath = internalResourcePath.substring(1);
        return getResourceBasePath() + "/" + internalResourcePath;
    }

    /**
     * Returns a resource URL
     *
     * @param internalResourcePath
     * @return
     */
    public static URL getPluginResource(String internalResourcePath) {
        return ResourceUtils.class.getResource(getResourcePath(internalResourcePath));
    }

    /**
     * Returns a resource stream
     *
     * @param internalResourcePath
     * @return
     */
    public static InputStream getPluginResourceAsStream(String internalResourcePath) {
        return ResourceUtils.class.getResourceAsStream(getResourcePath(internalResourcePath));
    }

}
