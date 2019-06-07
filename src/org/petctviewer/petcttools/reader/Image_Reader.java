package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.util.DicomTools;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class Image_Reader {
	
	private ImagePlus image;
	boolean compressed;
	
	public Image_Reader(File path, boolean compressed) {
		this.compressed=compressed;
		File[] files = path.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".dcm") || !name.contains(".") );
		    }
		});
		
		
		readFiles(files);
	}
	
	public Image_Reader(ArrayList<File> files, boolean compressed) {
		this.compressed=compressed;
		File[] fileArray=new File[files.size()];
		files.toArray(fileArray);
		readFiles(fileArray);
	}
	
	
	private void readFiles(File[] files) {
		
		ImageStack stack = null;
		Calibration calibration=null;
		
		int i=0;
		for (File file: files) {
			ImagePlus slice=this.readFile(file, compressed);
			
			if(stack==null) {
				ImageProcessor ip=slice.getProcessor();
				stack=new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
				calibration=slice.getCalibration();
			}
			if(slice.getStackSize()==1) {
				stack.addSlice(slice.getInfoProperty(),slice.getProcessor());
				i++;
				IJ.showStatus("Reading");
				IJ.showProgress((double) i/files.length);
			//Case of MultiFrame Image	
			}else if(slice.getStackSize()>=1) {
				stack=slice.getStack();
				for(int j=1; j<slice.getStackSize(); j++) {
					stack.setSliceLabel(slice.getInfoProperty(), j);
				}
				break;
			}
			
			
		}
		
		
		ImagePlus imp=new ImagePlus();
		imp.setStack(stack);

		ImageStack stackSorted=Image_Reader.sortStackByImageNumber(imp);
		ImagePlus imp2=new ImagePlus();
		imp2.setStack(stackSorted);
		imp2.setCalibration(calibration);
		imp2.setProperty("Info", imp2.getStack().getSliceLabel(1));
		imp2.setTitle(DicomTools.getTag(imp2, "0010,0010")+"-"+DicomTools.getTag(imp2, "0008,0022")+"-"+DicomTools.getTag(imp2, "0008,103E"));
		
		FileInfo fi=new FileInfo();
		fi.fileFormat = FileInfo.UNKNOWN;
		fi.fileName = "";
		fi.directory = files[0].getParent();
		
		imp2.setFileInfo(fi);
		
		image=imp2;
		
	}
	
	public ImagePlus getImagePlus() {
		return image;
	}
	
	private ImagePlus readFile(File file, boolean compressed) {
		ImagePlus slice=null;
		if(!compressed){
			Opener opener = new Opener();
			opener.setSilentMode(true);
			slice=opener.openImage(file.getAbsolutePath().toString());		
		}else{
			try {
				ImporterOptions option=new ImporterOptions();
				option.setOpenAllSeries(false);
				option.setQuiet(true);
				option.setShowMetadata(false);
				option.setStackOrder(ImporterOptions.ORDER_XYZTC);
				option.setId(file.getAbsolutePath().toString());
				ImagePlus[] images=BF.openImagePlus(option);
				slice=images[0];
				String info = slice.getInfoProperty();
				String newMeta=simpleModifyBF(info);
				slice.setProperty("Info", newMeta);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		return slice;
	}
	
	/**
	 * Sort stack according to ImageNumber
	 * In uparseable ImageNumber or non labelled image in stack, return the orignal stack without change
	 * @param imp : Original Image plus
	 * @return ImageStack : New ordered stack image by ImageNumber
	 */
	public static ImageStack sortStackByImageNumber(ImagePlus imp) {
		
		ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getStack().getColorModel());
		
		HashMap<Integer,Integer> sliceMap=new HashMap<Integer,Integer>();
		
		for(int i=1; i<=imp.getImageStackSize(); i++) {
			
			try {
				imp.setSlice(i);
				String imageNumber=DicomTools.getTag(imp, "0020,0013").trim();
				sliceMap.put(Integer.parseInt(imageNumber), i);	
			
			}catch(Exception e1){
				//If parse error of slice number return the original stack
				return imp.getStack();
			}
		}
		//Sort the index image number
		List<Integer> employeeByKey = new ArrayList<>(sliceMap.keySet());
		Collections.sort(employeeByKey);
		try {
			
			//Check that the number of parsed image number is matching the number of slice
			if(sliceMap.size() ==imp.getStackSize()) {
				
				for(int sliceNb : employeeByKey) {
					int sliceToadd=sliceMap.get(sliceNb);
					stack2.addSlice(imp.getStack().getSliceLabel(sliceToadd),imp.getStack().getProcessor(sliceToadd));	
					
				}
				
			//Else return original stack	
			}else {
				return imp.getStack();
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}
		
		return stack2;
	}
	
	/**
	 * Sort stack according to ImageNumber
	 * In uparseable ImageNumber or non labelled image in stack, return the orignal stack without change
	 * @param imp : Original Image plus
	 * @return ImageStack : New ordered stack image by ImageNumber
	 */
	public static ImageStack sortStackByImagePosition(ImagePlus imp) {
		
		ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getStack().getColorModel());
		
		HashMap<Double,Integer> sliceMap=new HashMap<Double,Integer>();
		
		for(int i=1; i<=imp.getImageStackSize(); i++) {
			
			try {
				imp.setSlice(i);
				String imagePosition=DicomTools.getTag(imp, "0020,0032").trim();
				String[] postionsValues=StringUtils.split(imagePosition, "\\");
				sliceMap.put(Double.parseDouble(postionsValues[2]), i);
			
			}catch(Exception e1){
				System.out.println("error");
				IJ.handleException(e1);
				//If parse error of slice number return the original stack
				return imp.getStack();
			}
		}
		//Sort the index image number
		List<Double> sliceLocation = new ArrayList<>(sliceMap.keySet());
		Collections.sort(sliceLocation, Collections.reverseOrder());
		try {
			
			//Check that the number of parsed image number is matching the number of slice
			if(sliceMap.size() ==imp.getStackSize()) {
				
				for(double sliceNb : sliceLocation) {
					int sliceToadd=sliceMap.get(sliceNb);
					stack2.addSlice(imp.getStack().getSliceLabel(sliceToadd),imp.getStack().getProcessor(sliceToadd));	
					
				}
				
			//Else return original stack	
			}else {
				return imp.getStack();
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}
		
		return stack2;
	}
	
	public static ImagePlus getImpByImagePosition(ImagePlus imp) {
		ImageStack stack=Image_Reader.sortStackByImagePosition(imp);
		ImagePlus imp2=new ImagePlus();
		imp2.setStack(stack);
		imp2.setCalibration(imp.getCalibration());
		imp2.setProperty("Info", imp.getStack().getSliceLabel(1));
		imp2.setTitle(DicomTools.getTag(imp, "0010,0010")+"-"+DicomTools.getTag(imp, "0008,0022")+"-"+DicomTools.getTag(imp, "0008,103E"));
		return imp2;
	}
	
	private String simpleModifyBF(String info) {
		String newInfo=info.replaceAll(" #1 =", ":");
		return newInfo;
	}
	
	private String processDicomMetaBioFormat(String info){
		Scanner scanner = new Scanner(info);
		
		HashMap<String,Boolean> issequenceTag=new HashMap<String,Boolean>();
		HashMap<String,String> rawUniqueTag=new HashMap<String,String>();
		HashMap<String,ArrayList<String>> rawMultipleTag=new HashMap<String,ArrayList<String>>();
		
		while (scanner.hasNextLine()) {
		  
		  String line = scanner.nextLine();
		  String tagRead=line.substring(0,9);
			if(issequenceTag.containsKey(tagRead)){
				issequenceTag.put(tagRead,true);
				if(rawMultipleTag.get(tagRead)==null){
					rawMultipleTag.put(tagRead, new ArrayList<String>());
				}
				rawMultipleTag.get(tagRead).add(line);
				
			}else{
				issequenceTag.put(tagRead,false);
				rawUniqueTag.put(tagRead, line);
			}
		}
		
		scanner.close();
		
		ArrayList<String> uniqueTag=new ArrayList<String>();
		
		Set<String> tags=issequenceTag.keySet();
		for(String tag:tags){
			if(!issequenceTag.get(tag)){
				String rawTag=rawUniqueTag.get(tag);
				String newtag=rawTag.replace(" #1 =", ":");
				uniqueTag.add(newtag);
			}
		}
		
		Collections.sort(uniqueTag);
		StringBuilder tagsFinal=new StringBuilder();
		for(String finalTag:uniqueTag){
			tagsFinal.append(finalTag+"\n");
			
		}
		
		Set<String> tagsMultiple=rawMultipleTag.keySet();
		for(String multipleTag:tagsMultiple){
			System.out.println(multipleTag);
			
		}
		
		return tagsFinal.toString();
	}
	
}
