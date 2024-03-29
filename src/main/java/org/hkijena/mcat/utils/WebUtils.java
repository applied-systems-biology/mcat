/*
 * Copyright by Zoltán Cseresnyés, Ruman Gerst
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Institute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 */

package org.hkijena.mcat.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WebUtils {
    private static boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ")
                    || hv.contains(" 302 ")) return true;
        }
        return false;
    }

    public static void download(URL url, Path outputFile) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setGroupingUsed(false);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
        df.setRoundingMode(RoundingMode.CEILING);
        try {
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            // Handle redirection
            Map<String, List<String>> header = http.getHeaderFields();
            while (isRedirected(header)) {
                String link = header.get("Location").get(0);
                url = new URL(link);
                http = (HttpURLConnection) url.openConnection();
                header = http.getHeaderFields();
            }

            long contentLength = http.getContentLengthLong();
            long lastMessageTime = System.currentTimeMillis();

            // Download the file
            try (InputStream input = http.getInputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                long total = 0;
                try (OutputStream output = new FileOutputStream(outputFile.toFile())) {
                    while ((n = input.read(buffer)) != -1) {
                        total += n;
                        output.write(buffer, 0, n);
                        long currentMessageTime = System.currentTimeMillis();
                        if (currentMessageTime - lastMessageTime > 1000) {
                            lastMessageTime = currentMessageTime;
                            String message;
                            if (contentLength <= 0) {
                                message = "Downloaded " + df.format(total / 1024.0 / 1024.0) + " MB";
                            } else {
                                message = "Downloaded " + df.format(total / 1024.0 / 1024.0) + " MB / " + df.format(contentLength / 1024.0 / 1024.0) + " MB";
                            }
                            System.out.println(message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
