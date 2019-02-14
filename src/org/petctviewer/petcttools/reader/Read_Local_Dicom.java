package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;

import ij.ImagePlus;
import ij.io.Opener;
import loci.plugins.BF;

public class Read_Local_Dicom {

	public static void main(String[] args) {
		Read_Local_Dicom read = new Read_Local_Dicom();
		//read.readFile(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		//read.readFileBioFormat(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		read.recursiveScanFolder(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0"));
	}
	
	public void readFile(File file) {
		Opener opener = new Opener();
		ImagePlus slice=opener.openImage(file.getAbsolutePath().toString());
		slice.show();
		System.out.println(slice.getInfoProperty());
		//opener.open("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm");
		//opener.openImage("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8", "Test");
	}
	
	public void recursiveScanFolder(File folder) {
		
		String[] directories = folder.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
		});
		
		File[] files = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return (name.toLowerCase().endsWith(".dcm") || !name.contains(".") );
		    }
		});
		

		if(directories.length==0 
				&& files.length>0) {
			/*for(File file : files) {
				System.out.println(file);
			}*/
			
			System.out.println(files[0]);
			
		}else if(directories.length>0) {
			for(String directory:directories) {
				recursiveScanFolder(new File(folder.toString()+File.separator+directory));
			}
			
		}
		
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
