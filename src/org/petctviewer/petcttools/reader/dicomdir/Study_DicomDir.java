package org.petctviewer.petcttools.reader.dicomdir;

import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.media.DicomDirReader;
import org.petctviewer.petcttools.reader.Image_Reader;

public class Study_DicomDir {
	
	public Attributes studyAttributes;
	private DicomDirReader reader;
	public ArrayList<Series_DicomDir> series = new ArrayList<Series_DicomDir>();
	
	public Study_DicomDir(Attributes studyAttributes, DicomDirReader reader) {
		this.studyAttributes=studyAttributes;
		this.reader=reader;
		fillSeriesAttributes();
	}
	
	public void fillSeriesAttributes() {
		
		ArrayList<Attributes> series=Image_Reader.readLowerDirectoryDicomDir(reader,studyAttributes);
		for(Attributes serie:series) {
			addSeriesAttributes(serie);
		}
		
	}
	
	public void addSeriesAttributes(Attributes serieAttributes) {
			Series_DicomDir serie = new Series_DicomDir(serieAttributes, reader);
			series.add(serie);
		
	}
	
	public String getAccessionNumber() {
		return studyAttributes.getString(Tag.AccessionNumber);
	}
	
	public String getStudyUID() {
		return studyAttributes.getString(Tag.StudyInstanceUID);
	}
	
	public String getStudyDescription() {
		return studyAttributes.getString(Tag.StudyDescription);
	}
	
	public String getStudyDate() {
		return studyAttributes.getString(Tag.StudyDate);
	}


}
