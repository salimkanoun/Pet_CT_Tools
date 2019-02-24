package org.petctviewer.petcttools.reader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Store details of a DICOM series
 * @author kanoun_s
 *
 */
public class Series_Details {
	
	public String transferSyntax;
	public boolean isCompressed;
	public String patientName;
	public String patientId;
	public String accessionNumber;
	public String studyUID;
	public String studyDescription;
	public Date studyDate;
	public String serieDescription;
	public String serieNumber="N/A";
	public int numberOfImage;
	public String modality;
	public String sopClassUID;
	public String imageType;
	public File fileLocation;
	public boolean isDicomDir=false;
	public ArrayList<File> fileLocationList;
	
	/**
	 * 
	 * @param transferSyntax
	 * @param patientName
	 * @param patientId
	 * @param accessionNumber
	 * @param studyUID
	 * @param studyDescription
	 * @param studyDate
	 * @param serieDescription
	 * @param serieNumber
	 * @param modality
	 * @param numberOfImage
	 * @param sopClassUID
	 * @param fileLocation
	 */
	public Series_Details(String transferSyntax, String patientName, String patientId, String accessionNumber, String studyUID, String studyDescription,
			String studyDate, String serieDescription, String serieNumber, String modality, String numberOfImage, String sopClassUID, File fileLocation) {
		
		//Parse dicomDate String and put 01-01-1900 if unparseable
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date studyDateParsed=null;
		Date defaultDate = null;
		try {
			defaultDate = format.parse("19000101");
			studyDateParsed=format.parse(studyDate);
		} catch (Exception e) {
			studyDateParsed=defaultDate;
		}
		
		try {
			this.numberOfImage=Integer.parseInt(numberOfImage);
		}catch (Exception e) {
			this.numberOfImage=0;
		}
		
		this.transferSyntax= transferSyntax;
		
		this.patientName=patientName;
		this.patientId=patientId;
		this.studyUID=studyUID;
		this.studyDescription=studyDescription;
		this.studyDate=studyDateParsed;
		this.serieDescription=serieDescription;
		if(serieNumber!=null) {
			this.serieNumber=serieNumber;
		}
		
		
		this.modality=modality;
		this.sopClassUID=sopClassUID;
		this.fileLocation=fileLocation;
		this.accessionNumber=accessionNumber;
		
		determineImageType();
		determineIsCompressed();
		
		
	}
	
	/***
	 * Flag this Series as DICOMDIR and store Instance location in this object as arrayList of File
	 * @param rootPath
	 * @param fileLocationList
	 */
	public void setDicomDir(String rootPath, ArrayList<String> fileLocationList) {
		this.isDicomDir=true;
		this.fileLocationList=new ArrayList<File>();
		
		for(String file : fileLocationList) {
			this.fileLocationList.add(new File(rootPath+File.separator+file));
		}
		
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
