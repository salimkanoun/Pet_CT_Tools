package org.petctviewer.petcttools.reader;

import org.petctviewer.petcttools.reader.gui.Reader_Gui;

import ij.Prefs;
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
		//Force reading with 16 bits
		Prefs.ignoreRescaleSlope=true;
		
	}
	
	//NB : Lancement auto du viewer => Necessite class Run _pet_ct de Orthanc Tools
	//SK To DO
	//IgnoreRescaleSlope A suivre
	//Compressed DICOM?

	
}
