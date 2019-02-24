package org.petctviewer.petcttools.reader.dicomdir;

import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.media.DicomDirReader;
import org.petctviewer.petcttools.reader.Image_Reader;

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
		
		ArrayList<Attributes> studies=Image_Reader.readLowerDirectoryDicomDir(reader,patientAttributes);
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


}
