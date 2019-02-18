package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import ij.ImagePlus;

public class Read_Local_Dicom {
	
	ArrayList<File> folderList;
	HashMap<File, Series_Details> dicomMap;
	File folderToRead;

	public static void main(String[] args) {
		Read_Local_Dicom read = new Read_Local_Dicom();
		//read.readFile(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		//read.readFileBioFormat(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		//read.scanFolder(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001"));
		read.scanFolder(new File("/home/salim/Bureau/EsportatiHoros/"));
		//read.openAllFolders();
		
		Reader_Gui gui=new Reader_Gui(read);
		gui.setHashMap(read.dicomMap);
		gui.pack();
		gui.setVisible(true);
	}
	
	public void scanFolder(File folderToRead) {
		this.folderToRead=folderToRead;
		folderList=new ArrayList<File>();
		dicomMap=new HashMap<File, Series_Details>();
		recursiveScanFolder(folderToRead);
		
	}
	
	private void recursiveScanFolder(File folder) {

		String[] directories = folder.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
		});
		
		File[] files = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".dcm") ||name.toLowerCase().endsWith(".img") || !name.contains(".") );
		    }
		});
		

		if(directories.length==0 
				&& files.length>0) {
			
			Attributes meta2 = null;
			DicomInputStream dis = null;
			try {
				dis=new DicomInputStream(files[0]);
				meta2=dis.readDataset(-1, -1);
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				String patientName=meta2.getString(Tag.PatientName);
				String patientId=meta2.getString(Tag.PatientID);
				String ts=dis.getTransferSyntax();
				String studyDate=meta2.getString(Tag.StudyDate);
				String studyUID=meta2.getString(Tag.StudyInstanceUID);
				String studyDescription=meta2.getString(Tag.StudyDescription);
				String serieDescription=meta2.getString(Tag.SeriesDescription);
				String serieNumber=meta2.getString(Tag.SeriesNumber);
				String accessionNumber=meta2.getString(Tag.AccessionNumber);
				String numberOfImage=meta2.getString(Tag.NumberOfSlices);
				System.out.println(numberOfImage);
				String modality=meta2.getString(Tag.Modality);
				String sopClassUID=meta2.getString(Tag.SOPClassUID);
				
				Series_Details details=new Series_Details(ts, patientName, patientId, accessionNumber, studyUID, studyDescription,
						studyDate, serieDescription, serieNumber, modality, numberOfImage, sopClassUID, files[0].getParentFile());
				
				
				dicomMap.put(files[0].getParentFile(), 
						details);
				


			//Add found folder in the arrayList of folder that can be opened
			folderList.add(files[0].getParentFile());
			
		}else if(directories.length>0) {
			for(String directory:directories) {
				recursiveScanFolder(new File(folder.toString()+File.separator+directory));
			}
			
		}
		
		
	}
	
	public void openFolders(ArrayList<File> folders) {
		
		for(File folder: folders) {
			Image_Reader reader=new Image_Reader(folder);
			ImagePlus image=reader.getImagePlus();
			image.show();
			
		}
		
			
		
	}
	
	
	
	

}
