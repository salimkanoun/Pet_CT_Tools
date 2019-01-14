package org.petctviewer.petcttools.listfolder;

import java.io.File;
import java.io.FilenameFilter;

public class List_Folder {

	public static void main(String[] args) {

		List_Folder listFolders=new List_Folder();
		String results=listFolders.listFolder(new File("F:\\GAINED_Complet_CopieExportFinal"));
		System.out.println(results);
	}
	
	
	private String listFolder(File inputPath) {
		
		StringBuilder sb=new StringBuilder();
		
		String[] directories=listDirectories(inputPath);
		
		for(String directory : directories) {
			
			File directoryBatch=new File(inputPath+File.separator+directory);
			
			System.out.println(directoryBatch.getAbsolutePath());
			
			String[] patients=listDirectories(directoryBatch);
			
			for(int i=0; i<patients.length ; i++){
				
				sb.append(directory+","+patients[i]+"\n");
			}
			
			
		}
		
		return sb.toString();
	}
	
	private String[] listDirectories(File inputPath) {
		String[] directories = inputPath.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
		
		return directories;
	}

}
