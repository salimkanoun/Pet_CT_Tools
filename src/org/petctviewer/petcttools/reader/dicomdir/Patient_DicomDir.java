package org.petctviewer.petcttools.reader.dicomdir;

import java.io.IOException;
import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.media.DicomDirReader;

/**
 * Stores Attributes of Patient Entry of DICOMDIR
 * Fetch the Study Level to get child studies
 * @author salim
 *
 */
public class Patient_DicomDir {
	
	private DicomDirReader reader;
	public Attributes patientAttributes;
	public ArrayList<Study_DicomDir> studies = new ArrayList<Study_DicomDir>();

	public Patient_DicomDir(Attributes patientAttributes, DicomDirReader reader) {
		this.patientAttributes=patientAttributes;
		this.reader=reader;
		fillStudiesAttributes();
	}
	
	public void fillStudiesAttributes() {
		ArrayList<Attributes> studies=Patient_DicomDir.readLowerDirectoryDicomDir(reader,patientAttributes);
		for(Attributes study:studies) {
			addStudiesAttributes(study);
		}
	}
	
	public void addStudiesAttributes(Attributes studyAttributes) {
		Study_DicomDir study = new Study_DicomDir(studyAttributes,reader);
		studies.add(study);
	}
	
	public String getPatientName() {
		return patientAttributes.getString(Tag.PatientName);
	}
	
	public String getPatientId() {
		return patientAttributes.getString(Tag.PatientID);
	}

	public static ArrayList<Attributes> readLowerDirectoryDicomDir(DicomDirReader dicomDirReader,Attributes current) {
		ArrayList<Attributes> lowerResults=new ArrayList<Attributes>();
		try {
			Attributes temp = dicomDirReader.readLowerDirectoryRecord(current);
			lowerResults.add(temp);
			
			while(dicomDirReader.readNextDirectoryRecord(temp)!= null) {
				temp=dicomDirReader.readNextDirectoryRecord(temp);
				lowerResults.add(temp);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lowerResults;
	}


}
