package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.media.DicomDirReader;
import org.petctviewer.petcttools.reader.dicomdir.Patient_DicomDir;
import org.petctviewer.petcttools.reader.dicomdir.Series_DicomDir;
import org.petctviewer.petcttools.reader.dicomdir.Study_DicomDir;

public class Read_Local_Dicom {
	
	public HashMap<String, ArrayList<Series_Details>> dicomMap;
	private JButton buttonScann;
	
	public void scanFolder(File folderToRead, JButton buttonScann) {
		this.buttonScann=buttonScann;
		dicomMap=new HashMap<String,  ArrayList<Series_Details>>();;
		recursiveScanFolder(folderToRead);
		
	}
	
	private void recursiveScanFolder(File folder) {
		
		File[] fileDicomDir = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().equals("dicomdir") );
		    }
		});
		
		if(fileDicomDir.length==1) {
			readDicomDir(fileDicomDir[0]);
			return;
			
		}

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
				dis.setAllocateLimit(-1);
				meta2=dis.readDataset(-1, -1);
				dis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("met"+meta2);
				String patientName=meta2.getString(Tag.PatientName);
				String patientId=meta2.getString(Tag.PatientID);
				String ts=dis.getTransferSyntax();
				String studyDate=meta2.getString(Tag.StudyDate);
				String studyUID=meta2.getString(Tag.StudyInstanceUID);
				String studyDescription=meta2.getString(Tag.StudyDescription);
				String serieDescription=meta2.getString(Tag.SeriesDescription);
				String serieNumber=meta2.getString(Tag.SeriesNumber);
				String accessionNumber=meta2.getString(Tag.AccessionNumber);
				String numberOfImage=String.valueOf(files.length);
				String modality=meta2.getString(Tag.Modality);
				String sopClassUID=meta2.getString(Tag.SOPClassUID);
				
				Series_Details seriesDetails=new Series_Details(ts, patientName, patientId, accessionNumber, studyUID, studyDescription,
						studyDate, serieDescription, serieNumber, modality, numberOfImage, sopClassUID, files[0].getParentFile());
				
				
				addToDicomMap(studyUID, seriesDetails);
				//dicomMap.put(studyUID, seriesDetails);
				buttonScann.setText("Scanned "+dicomMap.size()+" Series");
				
			
		}else if(directories.length>0) {
			for(String directory:directories) {
				recursiveScanFolder(new File(folder.toString()+File.separator+directory));
			}
			
		}
		
		
	}
	
	private void addToDicomMap(String studyUID, Series_Details seriesDetails) {
		if(!dicomMap.containsKey(studyUID)) {
			dicomMap.put(studyUID, new ArrayList<Series_Details>());
		}
		
		dicomMap.get(studyUID).add(seriesDetails);
		
	}
	
	
	public void readDicomDir(File dicomDir) {

		ArrayList<Patient_DicomDir> patients=new ArrayList<Patient_DicomDir>();
		Attributes globalMetadata=null;
		
		try {
			
			DicomDirReader dicomDirReader = new DicomDirReader(dicomDir);
			globalMetadata=dicomDirReader.getFileMetaInformation();		
			Attributes patientAttributes=dicomDirReader.readFirstRootDirectoryRecord();
			Patient_DicomDir patient=new Patient_DicomDir(patientAttributes,dicomDirReader);
			
			patients.add(patient);
			
			while(dicomDirReader.readNextDirectoryRecord(patientAttributes)!=null) {
				
				patientAttributes=dicomDirReader.readNextDirectoryRecord(patientAttributes);
				patient=new Patient_DicomDir(patientAttributes, dicomDirReader);
				patients.add(patient);
				
			}
	
			dicomDirReader.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Processing result to creat and File Series_Details object that will be sent to GUI
		for(Patient_DicomDir patient:patients) {
			
			String transfertSyntax=globalMetadata.getString(Tag.TransferSyntaxUID);
			
			String patientName=patient.getPatientName();
			String patientId=patient.getPatientId();
			
			for(Study_DicomDir study:patient.studies) {
				
				String accessionNumber=study.getAccessionNumber();
				String studyDescription=study.getStudyDescription();
				String studyDate=study.getStudyDate();
				String studyUID=study.getStudyUID();

				for(Series_DicomDir serie:study.series) {
					
					String modality=serie.getModality();
					int numberOfImage=serie.getNumberOfImages();
					String serieDescription=serie.getSerieDescription();
					String serieNumber=serie.getSerieNumber();
					String sopClassUID=serie.getSopClassUID();
					ArrayList<String> fileLocationList=serie.getFileList();
					
					Series_Details seriesDetails=new Series_Details(transfertSyntax, patientName, patientId,
							accessionNumber, studyUID, studyDescription,
							studyDate, serieDescription, serieNumber, modality, String.valueOf(numberOfImage), 
							sopClassUID, null);
					seriesDetails.setDicomDir(dicomDir.getParent(),fileLocationList);
					
					addToDicomMap(studyUID, seriesDetails);
					
					
				}
				
			}
			
		}

	}	

}
