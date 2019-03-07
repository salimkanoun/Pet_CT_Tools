package org.petctviewer.petcttools.reader;

import org.petctviewer.petcttools.reader.gui.Reader_Gui;

import ij.plugin.PlugIn;

/**
 * Main class starting the program
 * @author salim
 *
 */
public class Dicom_Reader implements PlugIn{

	public static void main(String[] args) {
		Reader_Gui gui=new Reader_Gui();
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);

	}

	@Override
	public void run(String arg) {
		Reader_Gui gui=new Reader_Gui();
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
		
	}
	
	//NB : Lancement auto du viewer => Necessite class Run _pet_ct de Orthanc Tools

	//SK To DO
	//Compressed DICOM?
	//Presence du GR de BF?

	
}
