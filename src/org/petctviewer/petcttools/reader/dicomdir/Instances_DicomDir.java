package org.petctviewer.petcttools.reader.dicomdir;

import org.dcm4che3.data.Attributes;

/**
 * Stores Attributes of Instance Entry of DICOMDIR
 * @author salim
 *
 */
public class Instances_DicomDir{
	
	Attributes instanceAttributes;
	
	public Instances_DicomDir(Attributes instanceAttributes) {
		this.instanceAttributes=instanceAttributes;
	}
	
}
