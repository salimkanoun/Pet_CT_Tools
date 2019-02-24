package org.petctviewer.petcttools.reader.dicomdir;

import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.media.DicomDirReader;

public class Series_DicomDir{
	
	public Attributes serieAttributes;
	private DicomDirReader reader;
	public ArrayList<Instances_DicomDir> instances = new ArrayList<Instances_DicomDir>();
	
	public Series_DicomDir(Attributes serieAttributes, DicomDirReader reader) {
		this.reader=reader;
		this.serieAttributes=serieAttributes;
		//System.out.println(serieAttributes);
		fillInstancesAttributes();
	}
	
	public void fillInstancesAttributes() {
		
		ArrayList<Attributes> instances=Patient_DicomDir.readLowerDirectoryDicomDir(reader,serieAttributes);
		for(Attributes instance:instances) {
			addInstancesAttributes(instance);
		}
		
	}
	
	
	public void addInstancesAttributes(Attributes instanceAttributes) {
		Instances_DicomDir instance = new Instances_DicomDir(instanceAttributes);
		instances.add(instance);
		
	}
	
	public String getSerieDescription() {
		return serieAttributes.getString(Tag.SeriesDescription);
	}
	
	public String getSerieNumber() {
		return serieAttributes.getString(Tag.SeriesNumber);
	}
	
	public String getModality() {
		return serieAttributes.getString(Tag.Modality);
	}
	
	public String getSopClassUID() {
		return instances.get(0).instanceAttributes.getString(Tag.ReferencedSOPClassUIDInFile);
	}
	
	public int getNumberOfImages() {
		return instances.size();
	}
	
	
}