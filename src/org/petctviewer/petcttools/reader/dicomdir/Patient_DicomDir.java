package org.petctviewer.petcttools.reader.dicomdir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.media.DicomDirReader;
import org.petctviewer.petcttools.reader.Image_Reader;

public class Patient_DicomDir {
	
	DicomDirReader reader;
	Attributes patientAttributes;
	
	ArrayList<Study_DicomDir> studies = new ArrayList<Study_DicomDir>();
	
	HashMap<Integer, ArrayList<Attributes>> attributes;
	
	public Patient_DicomDir(Attributes patientAttributes, DicomDirReader reader) {
		this.patientAttributes=patientAttributes;
		this.reader=reader;
		fillStudiesAttributes();
	}
	
	public void fillStudiesAttributes() {
		
		ArrayList<Attributes> studies=Image_Reader.getLowerDirectory(reader,patientAttributes);
		for(Attributes study:studies) {
			addStudiesAttributes(study);
		}
		
	}
	
	public void addStudiesAttributes(Attributes studyAttributes) {
		Study_DicomDir study = new Study_DicomDir(studyAttributes,reader);
		System.out.println(studyAttributes);
		studies.add(study);
		
		
	}


}
