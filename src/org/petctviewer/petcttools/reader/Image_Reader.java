package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.media.DicomDirReader;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.util.DicomTools;
import loci.plugins.BF;

public class Image_Reader {
	
	private File path;
	private ImagePlus image;
	
	public Image_Reader(File path) {
		
		this.path=path;
		readPath();
		
	}
	
	private void readPath() {
		
		ImageStack stack = null;
		Calibration calibration=null;
		
		File[] files = path.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".dcm") || !name.contains(".") );
		    }
		});
		
		for (File file: files) {
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
	
	public static void readDicomDir(File dicomDir) {

		dicomDir=new File("/home/salim/DICOMDIR/dicom/DICOMDIR");
		
		try {
			DicomDirReader dicomDirReader = new DicomDirReader(dicomDir);
			
			Attributes dcmDirAttributes=dicomDirReader.getFileMetaInformation();
			//Attributes dcmDirAttributes2=dicomDirReader.getFileSetInformation();
			
			System.out.println(dcmDirAttributes);
			//System.out.println(dcmDirAttributes2);
			Attributes dcmDirAttributes3=dicomDirReader.readFirstRootDirectoryRecord();
			Attributes last=readLowerDirectoryDicomDir(dicomDirReader,dcmDirAttributes3);
			
			System.out.println("nextDiretctory");
			Attributes dcmDirAttributes5=dicomDirReader.readNextDirectoryRecord(last);

			Attributes previous2=dcmDirAttributes5;
			while(dicomDirReader.readLowerDirectoryRecord(previous2)!=null) {
				previous2=dicomDirReader.readLowerDirectoryRecord(previous2);
				System.out.println(previous2);
				
			}
			System.out.println(dcmDirAttributes5);
						
			dicomDirReader.close();
			//System.out.println(dcmDirAttributes);
			//System.out.println(dcmDirAttributes2);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static Attributes readLowerDirectoryDicomDir(DicomDirReader dicomDirReader, Attributes current) throws IOException {
		while(dicomDirReader.readLowerDirectoryRecord(current)!=null) {
			System.out.println("lower");
			current=dicomDirReader.readLowerDirectoryRecord(current);
			System.out.println(current);
			
		}
		return current;
	}
	
	public static void main(String[] args) {
		Image_Reader.readDicomDir(null);
		
		
	}

}
