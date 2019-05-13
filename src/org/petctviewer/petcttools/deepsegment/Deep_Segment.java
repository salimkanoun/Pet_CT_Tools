package org.petctviewer.petcttools.deepsegment;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.Resizer;
import ij.process.ImageProcessor;

public class Deep_Segment {
	
	public static void main(String[] arg) {
		Deep_Segment segment=new Deep_Segment();
		
		
		try {
			//segment.keras();
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
		

		SavedModelBundle savedModelBundle = SavedModelBundle.load("C:\\Users\\kanoun_s\\ownCloud2\\DeepLearning-Thomas\\0513_savemodel\\saved_model_v3", "serve");

		Graph graph = savedModelBundle.graph();
		
		Iterator<Operation> it = graph.operations();

		while (it.hasNext()) {
			Operation op=it.next();
			System.out.println(op.numOutputs());
			System.out.println(op.toString());
			System.out.println(op.type());
			
			System.out.println(op.name());
		}
		
		System.out.println(graph.toString());
		
		
		
		
		Opener opener = new Opener();
		opener.setSilentMode(true);
		ImagePlus image=opener.openImage("C:\\Users\\kanoun_s\\Pictures\\ctNrrd.nrrd");	
		IJ.run(image, "32-bit", "");
		IJ.run(image, "Size...", "width=240 height=240 average interpolation=Bicubic");
		
		//new TensorFlowInferenceInterface();
		
		Resizer resize=new Resizer();
		ImagePlus imp2=resize.zScale(image, 240, ImageProcessor.BICUBIC);
		imp2.show();
		FloatBuffer fb = FloatBuffer.allocate(imp2.getHeight()*imp2.getWidth()*imp2.getStackSize()); 
		
		for(int i=1; i<=imp2.getStackSize(); i++) {
			
			
		  fb.put((float[])imp2.getStack().getPixels(i));
		  System.out.println(i);
			
	
			
		}
		
		fb.position(0);
		System.out.println(fb.toString());
		
		Tensor<Float> tensorShort = Tensor.create(new long[] {240L,240L,240L}, fb);
		
		System.out.println(tensorShort.shape());
		
		/*
		ArrayList<float[][]> futurTensor=new ArrayList<float[][]>();
		
		for(int i=1; i<=image.getStackSize(); i++) {
			
			float[] imagearray=(float[]) image.getStack().getPixels(i);
			
			float[][] imagearray2= pixelArrayTo2D(image.getHeight(),image.getWidth(), imagearray);
			futurTensor.add(imagearray2);
			
			System.out.println(imagearray2.length);
			System.out.println(imagearray2[0].length);
			
		}
		
		
		System.out.println(futurTensor.size());
		//Tensor<float> tensorShort = Tensor.create(new long[] {240L,240L,240L}, data);
		*/
		
		
		
		
		
		//printOperations(graph);
		//DoubleBuffer buffer=new DoubleBuffer();
		
		//Tensor<Double> input= Tensor.create(new long[] {240L,240L,240L}, buffer);
		
		//Tensor input =new Tensor(TensorFlow., new TensorShape(2,5)); 
		


		Tensor result=savedModelBundle.session().runner().feed("input", tensorShort).fetch("SALIM_OUTPUT").run().get(0);
		
		System.out.println(result.dataType());
		System.out.println(result.numDimensions());
		System.out.println(result.numElements());
		System.out.println(result.toString());
		
		

		// load the model
		/*String simpleMlp = new ClassPathResource("model.h5").getFile().getPath();
		
		ComputationGraph model = KerasModelImport.
				importKerasModelAndWeights(simpleMlp);
		
		MultiLayerNetwork model2 = KerasModelImport.
                importKerasSequentialModelAndWeights(simpleMlp);
		
		// make a random sample
		INDArray features =Nd4j.create(new double[240][240][240]);*/
		
		/*for (int i=0; i<inputs; i++) 
			
		    features.putScalar(new int[] {i}, Math.random() < 0.5 ? 0 : 1);
		*/
		// get the prediction
		//INDArray prediction = model.output(features);
		
	}
	
	public static float[][] pixelArrayTo2D(int x, int y , float[] imagearray){
		
		float array2d[][] = new float[x][y];


		for(int i=0; i<x;i++)
		   for(int j=0;j<y;j++)
		       array2d[i][j] = imagearray[(j*x) + i];
		
		
		return array2d; 
	}
	
	public void keras() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
		
		String simpleMlp = new ClassPathResource("saved_model.h5").getFile().getPath();
		
		ComputationGraph model = KerasModelImport.
				importKerasModelAndWeights(simpleMlp);
		
		/*MultiLayerNetwork model2 = KerasModelImport.
                importKerasSequentialModelAndWeights(simpleMlp);*/
		
		// make a random sample
		INDArray features =Nd4j.create(new double[240][240][240]);
		
		/*for (int i=0; i<inputs; i++) 
			
		    features.putScalar(new int[] {i}, Math.random() < 0.5 ? 0 : 1);
		*/
		// get the prediction
		//INDArray prediction = model.output(features);
	}
}
