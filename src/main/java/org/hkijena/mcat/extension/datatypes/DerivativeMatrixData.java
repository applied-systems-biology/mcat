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
package org.hkijena.mcat.extension.datatypes;

import org.hkijena.mcat.api.MCATData;
import org.hkijena.mcat.api.MCATDocumentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Contains a derivation matrix
 */
@MCATDocumentation(name = "Derivative matrix")
public class DerivativeMatrixData implements MCATData {

    private double[][] derivativeMatrix;

    public DerivativeMatrixData(double[][] derivativeMatrix) {
        super();
        this.derivativeMatrix = derivativeMatrix;
    }

    public double[][] getDerivativeMatrix() {
        return derivativeMatrix;
    }

    public void setDerivativeMatrix(double[][] derivativeMatrix) {
        this.derivativeMatrix = derivativeMatrix;
    }


    @Override
    public void saveTo(Path folder, Path fileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder.resolve(fileName).toString())));

            for (int i = 0; i < derivativeMatrix.length; i++) {
                String out = "";
                double[] line = derivativeMatrix[i];
                for (int j = 0; j < line.length; j++) {
                    out += j == 0 ? String.valueOf(line[j]) : ";" + String.valueOf(line[j]);
                }

                bw.write(out);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
