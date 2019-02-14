package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ij.ImagePlus;
import ij.io.Opener;
import loci.formats.FormatException;
import loci.plugins.BF;

public class Read_Local_Dicom {

	public static void main(String[] args) {
		Read_Local_Dicom read = new Read_Local_Dicom();
		read.readFile(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		read.readFileBioFormat(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
	}
	
	public void readFile(File file) {
		Opener opener = new Opener();
		ImagePlus slice=opener.openImage(file.getAbsolutePath().toString());
		slice.show();
		System.out.println(slice.getInfoProperty());
		//opener.open("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm");
		//opener.openImage("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8", "Test");
	}
	
	public void readFileBioFormat(File file) {
		ImagePlus[] imp=null;
		try {
			imp=BF.openImagePlus(file.getAbsolutePath().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		imp[0].show();

	}

}
