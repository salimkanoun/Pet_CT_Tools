package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.util.DicomTools;
import loci.plugins.BF;

public class Image_Reader {
	
	private ImagePlus image;
	
	public Image_Reader(File path) {
		
		File[] files = path.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".dcm") || !name.contains(".") );
		    }
		});
		
		readFiles(files);
	}
	
	public Image_Reader(ArrayList<File> files) {
		File[] fileArray=new File[files.size()];
		files.toArray(fileArray);
		readFiles(fileArray);
	}
	
	
	private void readFiles(File[] files) {
		
		ImageStack stack = null;
		Calibration calibration=null;
		
		for (File file: files) {
			System.out.println(file);
			ImagePlus slice=this.readFile(file);
			if(stack==null) {
				ImageProcessor ip=slice.getProcessor();
				stack=new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
				calibration=slice.getCalibration();
			}
			
			stack.addSlice(slice.getInfoProperty(),slice.getProcessor());
			
		}
		
		
		ImagePlus imp=new ImagePlus();
		imp.setStack(stack);
		imp.setCalibration(calibration);
		
		ImageStack stackSorted=this.sortStack(imp);
		imp.close();
		ImagePlus imp2=new ImagePlus();
		imp2.setStack(stackSorted);
		imp2.setCalibration(calibration);
		
		image=imp2;
		
	}
	
	public ImagePlus getImagePlus() {
		return image;
	}
	
	private void readFileBioFormat(File file) {
		ImagePlus[] imp=null;
		try {
			imp=BF.openImagePlus(file.getAbsolutePath().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		imp[0].show();

	}
	
	private ImagePlus readFile(File file) {
		Opener opener = new Opener();
		ImagePlus slice=opener.openImage(file.getAbsolutePath().toString());
		return slice;
	}
	
	/**
	 * Sort stack according to ImageNumber
	 * In uparseable ImageNumber or non labelled image in stack, return the orignal stack without change
	 * @param imp : Original Image plus
	 * @return ImageStack : New ordered stack image by ImageNumber
	 */
	private ImageStack sortStack(ImagePlus imp) {
		
		ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getStack().getColorModel());
		
		HashMap<Integer,Integer> sliceMap=new HashMap<Integer,Integer>();
		
		for(int i=1; i<=imp.getImageStackSize(); i++) {
			
			try {
				imp.setSlice(i);
				String imageNumber=DicomTools.getTag(imp, "0020,0013").trim();
				System.out.println(imageNumber);
				sliceMap.put(Integer.parseInt(imageNumber), i);	
			
			}catch(Exception e1){
				//If parse error of slice number return the original stack
				return imp.getStack();
			}
		}
		
		//Check that the number of parsed image number is matching the number of slice
		if(sliceMap.size() ==imp.getStackSize()) {
			for(int i=1; i<=imp.getStackSize(); i++){
				int sliceToadd=sliceMap.get(i);
				stack2.addSlice(imp.getStack().getSliceLabel(sliceToadd),imp.getStack().getProcessor(sliceToadd));	
			}
		//Else return original stack	
		}else {
			return imp.getStack();
		}
		
		
		return stack2;
	}
	
	
	

}
