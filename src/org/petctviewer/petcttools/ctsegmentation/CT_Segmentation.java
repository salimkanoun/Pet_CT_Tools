/**
Copyright (C) 2017 KANOUN Salim

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.petcttools.ctsegmentation;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Concatenator;
import ij.plugin.ImageCalculator;
import ij.plugin.PlugIn;
import ij.plugin.SubstackMaker;
import ij.plugin.filter.ImageMath;
import ij.util.DicomTools;
import trainableSegmentation.WekaSegmentation;

public class CT_Segmentation implements PlugIn {

	private WekaSegmentation weka;
	protected List<ImagePlus> inputImageList=new ArrayList<ImagePlus>();
	private ImagePlus inputImage=null;
	private ImagePlus resultatFinal;
	private CT_Segmentation_GUI gui;
	
	@Override
	public void run(String arg0) {
		weka= new WekaSegmentation(true);
		InputStream classifier = ClassLoader.getSystemResourceAsStream("classifier/classifier5classes3d.model");
		weka.loadClassifier(classifier);
		gui= new CT_Segmentation_GUI(this);
		gui.pack();
		gui.setVisible(true);
	}
	
	public void setImageInput(ImagePlus imp) {
		//inputImage.add(imp);
		inputImage=imp;
		
	}
	
	public void addImageInputBatch(ImagePlus imp) {
		inputImageList.add(imp);
	}
	
	public void clearBatch() {
		inputImageList.clear();
	}
	
	public void setSavedSegmentation(ImagePlus imp) {
		resultatFinal=imp;
		gui.getGenerateMaskButton().setEnabled(true);
	}
	
	public ImagePlus getRawSegmentation() {
		return resultatFinal;
	}
	
	public void makeMaskedImage(int label, JLabel status, boolean fillHoles) {
		status.setText("Calculating Masked CT");
		status.setForeground(Color.red);
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			String rescaleIntercept = DicomTools.getTag(inputImage, "0028,1052").trim();
			String rescaleSloap = DicomTools.getTag(inputImage, "0028,1053").trim();
			
			
			@Override
			protected Void doInBackground() throws Exception {
		
			ImagePlus binaryMask=resultatFinal.duplicate();
			IJ.run(binaryMask, "Macro...", "code=[if(v=="+label+") v=1 ; else v=0 ;] stack");
			
			if (fillHoles) {
				IJ.run(binaryMask, "Macro...", "code=[if(v==1) v=0 ; else v=255 ;] stack");
				IJ.run(binaryMask, "Fill Holes", "stack");
				IJ.run(binaryMask, "Macro...", "code=[if(v==255) v=0 ; else v=1 ;] stack");
			}
			
			ImageCalculator ic=new ImageCalculator();
			ImagePlus maskedCT = ic.run("Multiply create stack", inputImage, binaryMask);
			for (int i=1 ; i<=maskedCT.getImageStackSize() ; i++) {
				ImageMath.applyMacro(maskedCT.getImageStack().getProcessor(i), ( "if(v==0) v=(-1000)-("+rescaleIntercept+")/("+rescaleSloap+");" ), false);
			}
			
			maskedCT.show();
			
			return null;
			}

			@Override
			protected void done(){
				status.setText("Done");
				status.setForeground(Color.green);
				
			}
		};
		
		worker.execute();
		
		

	
	}
	
	public void runSegmentation(JLabel status, boolean batch, File destinationBatch) {
		
		
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			
			private void calculate(int batchnumber) {
				int numberOfSlice=inputImage.getImageStackSize();
				Double batch10 = numberOfSlice / (double) 10.0;
				ArrayList <ImagePlus> resultat = new ArrayList<ImagePlus>();
				status.setForeground(Color.red);
				
				gui.getLoadButton().setEnabled(false);
				
				for (int i=0 ; i<=batch10.intValue(); i++) {
					//Create Substack of 10 Slice to avoir Run out Memory
					SubstackMaker substackMaker= new SubstackMaker();
					ImagePlus image = null;
					
					//User progress feedback
					if (!batch) status.setText("Calculating slices "+ ((i*10)+1)+"-"+((i*10)+10) );
					else status.setText("Stack number "+ (batchnumber +1) + " Calculating slices "+ ((i*10)+1)+"-"+((i*10)+10));
					
					if (i<batch10.intValue()) {
						image=substackMaker.makeSubstack(inputImage, ((i*10)+1)+"-"+((i*10)+10));
						//Calculate Segmentation
						ImagePlus resultatTemp = weka.applyClassifier(image);
						//Put result to Final Stack
						resultat.add(resultatTemp);
					}
					
					//if last batch
					else {
						if (numberOfSlice - (i*10) != 0) {
							image=substackMaker.makeSubstack(inputImage, ((i*10)+1)+"-"+(numberOfSlice));
							//Calculate Segmentation
							ImagePlus resultatTemp = weka.applyClassifier(image);
							//Put result to Final Stack
							resultat.add(resultatTemp);
						}
					}
					
					//FreeMemory
					System.gc();
				}
				
				//Merge result to Final Stack
				if (resultat.size()>1) {
					Concatenator concatenator =new Concatenator() ;
					ImagePlus[] resultatTableau=new ImagePlus[resultat.size()];
					resultat.toArray(resultatTableau);
					resultatFinal=concatenator.concatenate(resultatTableau, false);
				}
				else resultatFinal=resultat.get(0);
				
				if (batch) {
					String name = DicomTools.getTag(inputImage, "0010,0010").trim();
					name=name.replace("^", "_").toLowerCase();
					//String id = DicomTools.getTag(inputImage, "0010,0020").trim();
					DateFormat format=new SimpleDateFormat("yyyyMMdd");
					String date = DicomTools.getTag(inputImage, "0008,0020").trim();
					Date studyDate = null;
					try {
						studyDate = format.parse("19000101");
						studyDate = format.parse(date);

					} catch (ParseException e) {
						e.printStackTrace();
					}
					System.out.println(studyDate);
					DateFormat datetoString=new SimpleDateFormat("MMM_d_YYYY",  Locale.ENGLISH);
					
					File file=new File(destinationBatch.getAbsolutePath()+File.separator+"msk_"+name+"_"+datetoString.format(studyDate).toLowerCase()+"_ct.nrrd");
					IJ.log(file.getAbsolutePath());
					IJ.run(resultatFinal, "Nrrd ... ", "nrrd=["+file.getAbsolutePath()+"]");
				}
			}
		
			@Override
			protected Void doInBackground() throws Exception {
				if (batch) {
					for (int i=0 ; i<inputImageList.size(); i++) {
						inputImage=inputImageList.get(i);
						calculate(i);
						
					}
					
				}
				else {
					calculate(0);
				}
				
				
					
				return null;
			}

			@Override
			protected void done(){
				//resultatFinal.show();
				status.setText("Segmentation done");
				status.setForeground(Color.green);
				//Activate the button to generate the final Image
				if (!batch) {
					gui.getGenerateMaskButton().setEnabled(true);
					gui.getLoadButton().setEnabled(true);
					gui.getLoadButton().setText("Save");
				}
				
				
			}
		};
		
		worker.execute();
		
	}

}
