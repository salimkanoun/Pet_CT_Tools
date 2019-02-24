package org.petctviewer.petcttools.reader.dicomdir;

import org.dcm4che3.data.Attributes;

public class Instances_DicomDir{
	
	Attributes instanceAttributes;
	
	public Instances_DicomDir(Attributes instanceAttributes) {
		this.instanceAttributes=instanceAttributes;
		System.out.println(instanceAttributes);
		
		
	}
	
}
