package org.petctviewer.petcttools.reader;

import org.petctviewer.petcttools.reader.gui.Reader_Gui;

public class Dicom_Reader {

	public static void main(String[] args) {
		Reader_Gui gui=new Reader_Gui();
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);

	}

	//SK To DO
	//DICOM DIR
	//Renderer Selection
	//Read All Study ?
	//Compressed DICOM?
	// Presence du GR de BF ?

	
}
