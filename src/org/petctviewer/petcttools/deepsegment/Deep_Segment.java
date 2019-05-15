package org.petctviewer.petcttools.deepsegment;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;

import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.SavedModelBundle.Loader;
import org.tensorflow.Session.Runner;
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
		} 
	}

	
	public void test() throws IOException {
		


		SavedModelBundle savedModelBundle = SavedModelBundle.load("C:\\Users\\kanoun_s\\ownCloud2\\DeepLearning-Thomas\\RESULTS\\saved_model", "serve");

	
		Graph graph = savedModelBundle.graph();
		
		
		Iterator<Operation> it = graph.operations();

		while (it.hasNext()) {
			Operation op=it.next();
			System.out.println(op.numOutputs());
			System.out.println(op.type());
			System.out.println(op.name());
		}
		
		
		Opener opener = new Opener();
		opener.setSilentMode(true);
		ImagePlus image=opener.openImage("C:\\Users\\kanoun_s\\Pictures\\ctNrrd.nrrd");	
		IJ.run(image, "32-bit", "");
		IJ.run(image, "Size...", "width=240 height=240 average interpolation=Bicubic");
		

		
		Resizer resize=new Resizer();
		ImagePlus imp2=resize.zScale(image, 240, ImageProcessor.BICUBIC);

		FloatBuffer fb = FloatBuffer.allocate(imp2.getHeight()*imp2.getWidth()*imp2.getStackSize()); 
		
		for(int i=1; i<=imp2.getStackSize(); i++) {
			
			
		  fb.put((float[])imp2.getStack().getPixels(i));
		  System.out.println(i);
			
	
			
		}
		
		fb.position(0);
		System.out.println(fb.toString());
		

		
		Tensor<Float> tensorShort = Tensor.create(new long[] {1, 240, 240, 240, 1}, fb);

		
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
		

		Runner runner=savedModelBundle.session().runner().feed("serving_default_SALIM_INPUT:0", tensorShort).
				fetch("StatefulPartitionedCall:0");
		
		List<Tensor<?>> tensors=runner.run();
		

		System.out.println("ici");

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
	/*
	public void keras() throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
		
		String simpleMlp = new ClassPathResource("saved_model.h5").getFile().getPath();
		
		ComputationGraph model = KerasModelImport.
				importKerasModelAndWeights(simpleMlp);
		
		/*MultiLayerNetwork model2 = KerasModelImport.
                importKerasSequentialModelAndWeights(simpleMlp);*/
		
		// make a random sample
		//INDArray features =Nd4j.create(new double[240][240][240]);
		
		/*for (int i=0; i<inputs; i++) 
			
		    features.putScalar(new int[] {i}, Math.random() < 0.5 ? 0 : 1);
		*/
		// get the prediction
		//INDArray prediction = model.output(features);
	//}*/
}
