/*******************************************************************************
 * Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge
 *
 * Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge
 * https://www.leibniz-hki.de/en/applied-systems-biology.html
 * HKI-Center for Systems Biology of Infection
 * Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)
 * Adolf-Reichwein-Straße 23, 07745 Jena, Germany
 *
 * The project code is licensed under BSD 2-Clause.
 * See the LICENSE file provided with the code for the full license.
 ******************************************************************************/
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
                    out += j == 0 ? String.valueOf(line[j]) : ";" + line[j];
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
