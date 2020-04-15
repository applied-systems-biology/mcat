package org.hkijena.mcat.api.dataproviders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.hkijena.mcat.api.datatypes.DerivativeMatrixData;

/**
 * Loads a {@link DerivativeMatrixData} from a file
 */
public class DerivativeMatrixFromFileProvider extends FileDataProvider<DerivativeMatrixData> {

    public DerivativeMatrixFromFileProvider() {
        super();
    }

    public DerivativeMatrixFromFileProvider(FileDataProvider<?> other) {
        super(other);
    }

    @Override
    public DerivativeMatrixData get() {
    	double[][] derivativeMatrix = null;
    	
    	try {
    		String filePath = getFilePath().toString();
        	long lines = Files.lines(getFilePath()).count();
        	
        	BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        	
        	String line = br.readLine();
        	double[] arr = Stream.of(line.split(";"))
                    .mapToDouble (Double::parseDouble)
                    .toArray();
        	
        	derivativeMatrix = new double[Math.toIntExact(lines)][arr.length];
        	derivativeMatrix[0] = arr;
        	
        	int counter = 1;
        	while((line = br.readLine()) != null) {
        		arr = Stream.of(line.split(";"))
                        .mapToDouble (Double::parseDouble)
                        .toArray();
        		derivativeMatrix[counter++] = arr;
        	}
        	
        	br.close();
        	
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        return new DerivativeMatrixData(derivativeMatrix);
    }

    @Override
    public String getName() {
        return "Derivative matrix (*.csv)";
    }

}
