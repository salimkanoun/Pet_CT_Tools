package org.petctviewer.petcttools.deepsegment;

import java.io.IOException;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;

public class Deep_Segment {
	
	public static void main(String[] arg) {
		Deep_Segment segment=new Deep_Segment();
		try {
			segment.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedKerasConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void test() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
		// load the model
		String simpleMlp = new ClassPathResource("model.h5").getFile().getPath();
		
		ComputationGraph model = KerasModelImport.
				importKerasModelAndWeights(simpleMlp);
		
		MultiLayerNetwork model2 = KerasModelImport.
                importKerasSequentialModelAndWeights(simpleMlp);
		
		// make a random sample
		INDArray features =Nd4j.create(new double[240][240][240]);
		
		/*for (int i=0; i<inputs; i++) 
			
		    features.putScalar(new int[] {i}, Math.random() < 0.5 ? 0 : 1);
		*/
		// get the prediction
		//INDArray prediction = model.output(features);
		
	}
}
