package org.petctviewer.petcttools.reader.dicomdir;

import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.media.DicomDirReader;
import org.petctviewer.petcttools.reader.Image_Reader;

public class Series_DicomDir{
	
	Attributes serieAttributes;
	DicomDirReader reader;
	ArrayList<Instances_DicomDir> instances = new ArrayList<Instances_DicomDir>();
	
	public Series_DicomDir(Attributes serieAttributes, DicomDirReader reader) {
		this.reader=reader;
		this.serieAttributes=serieAttributes;
		fillInstancesAttributes();
	}
	
	public void fillInstancesAttributes() {
		
		ArrayList<Attributes> instances=Image_Reader.getLowerDirectory(reader,serieAttributes);
		for(Attributes instance:instances) {
			addInstancesAttributes(instance);
		}
		
	}
	
	
	public void addInstancesAttributes(Attributes instanceAttributes) {
		Instances_DicomDir instance = new Instances_DicomDir(instanceAttributes);
		instances.add(instance);
		
	}
	
	
}