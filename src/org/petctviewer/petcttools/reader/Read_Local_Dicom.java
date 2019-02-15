package org.petctviewer.petcttools.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.BulkDataDescriptor;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;

import com.google.gson.stream.JsonWriter;

import ij.ImagePlus;

public class Read_Local_Dicom {
	
	ArrayList<File> folderList;

	public static void main(String[] args) {
		Read_Local_Dicom read = new Read_Local_Dicom();
		//read.readFile(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		//read.readFileBioFormat(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0\\1.2.840.113704.1.111.5352.1350646167.8\\CT_001_0a63112d11044b85a7d247852479b063.dcm"));
		read.recursiveScanFolder(new File("G:\\GAINED_Complet_CopieExportFinal\\Batch00\\11011101021001\\PET0"));
		read.openAllFolders();
	}
	

	
	public void recursiveScanFolder(File folder) {
		
		folderList=new ArrayList<File>();

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
			
			try {
				

				DicomInputStream dis=new DicomInputStream(files[0]);
				 
			   String blkFilePrefix = "blk";
			   IncludeBulkData includeBulkData = IncludeBulkData.URI;
				
				dis.setIncludeBulkData(includeBulkData);
		        //dis.setBulkDataDescriptor(bulkDataDescriptor);
		       // dis.setBulkDataDirectory(blkDirectory);
		        //dis.setBulkDataFilePrefix(blkFilePrefix);
		        //dis.setBulkDataFileSuffix(blkFileSuffix);
		       // dis.setConcatenateBulkDataFiles(catBlkFiles);
				

				 //JsonWriter jsonWriter = new JSONWriter(jsonGen);
				//DisplayTag.loadDicomObject(File f);
				dis.readDataset(-1, -1);
				
				//SK CHERCHER DICOM OUTPUT STREAM ?

				 //dis.setDicomInputHandler(jsonWriter);
				//Attributes meta2=dis.readItem();
				//dis.readHeader();
				//dis.get
				
				Attributes meta=dis.getFileMetaInformation();
				
				String patientName=meta.getString(Tag.MediaStorageSOPClassUID);
				
				System.out.println(patientName);
				
				dis.close();
				System.out.println(meta);

				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//DicomFormat dcm = new DicomFormat();
			//Add found folder in the arrayList of folder that can be opened
			folderList.add(files[0].getParentFile());
			
		}else if(directories.length>0) {
			for(String directory:directories) {
				recursiveScanFolder(new File(folder.toString()+File.separator+directory));
			}
			
		}
		
	}
	
	private void openAllFolders() {
		
		for (File folder : folderList) {
			Image_Reader reader=new Image_Reader(folder);
			ImagePlus image=reader.getImagePlus();
			image.show();
			
		}
	}
	
	
	
	

}
