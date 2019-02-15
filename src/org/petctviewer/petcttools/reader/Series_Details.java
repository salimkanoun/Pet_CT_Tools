package org.petctviewer.petcttools.reader;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Store details of a DICOM series
 * @author kanoun_s
 *
 */
public class Series_Details {
	
	String transferSyntax;
	boolean isCompressed;
	String patientName;
	String patientId;
	String studyUID;
	String studyDescription;
	Date studyDate;
	String serieDescription;
	String serieNumber;
	int numberOfImage;
	String modality;
	String sopClassUID;
	String imageType;
	File fileLocation;
	
	public Series_Details(String transferSyntax, String patientName, String patientId, String studyUID, String studyDescription,
			String studyDate, String serieDescription, String serieNumber, String modality, int numberOfImage, String sopClassUID, File fileLocation) {
		
		//Parse dicomDate String and put 01-01-1900 if unparseable
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date studyDateParsed=null;
		Date defaultDate = null;
		try {
			defaultDate = format.parse("19000101");
			studyDateParsed=format.parse(studyDate);
		} catch (ParseException e) {
			studyDateParsed=defaultDate;
		}
		
		this.transferSyntax= transferSyntax;
		this.patientName=patientName;
		this.patientId=patientId;
		this.studyUID=studyUID;
		this.studyDescription=studyDescription;
		this.studyDate=studyDateParsed;
		this.serieDescription=serieDescription;
		this.serieNumber=serieNumber;
		this.numberOfImage=numberOfImage;
		this.modality=modality;
		this.sopClassUID=sopClassUID;
		this.fileLocation=fileLocation;
		
		determineImageType();
		determineIsCompressed();
		
		
	}
	
	private void determineImageType() {
		if(sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.7")) {
			imageType="Secondary Captures" ;
			
		}else if(sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.1") ||
				sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.2") ||
				sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.3") ||
				sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.4")){
			imageType="Original" ;
			
		}else if( sopClassUID.startsWith("1.2.840.10008.5.1.4.1.1.8") ){
			imageType="Structured Report" ;
		}
		else {
			imageType="Unknown";
		}
	}
	
	private void determineIsCompressed() {
		if(transferSyntax.startsWith("1.2.840.10008.1.2.4")) {
			isCompressed=true;	
		}else {
			isCompressed=false;
		}
		
	}

}