/**
Copyright (C) 2017 KANOUN Salim
This
 program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public v.3 License as published by
the Free Software Foundation;
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.petctviewer.petcttools.csvcollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ij.plugin.PlugIn;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;

public class Csv_Collector implements PlugIn{
	
	private File repertoire;
	
	private ArrayList <Csv_Collector_Resultat> resultats;
	private JLabel lblLoadedDirectory, lblStatusIdle;
	private JButton btnGenerate;
	private int nombreSucces, nombreTotal;
	
	
	public static void main(String[] args) {
		Csv_Collector start=new Csv_Collector();
		start.gui();
    
	}
	
	@Override
	public  void run (String string) {
		Csv_Collector start=new Csv_Collector();
		start.gui();
	}
	
	public void lectureCSV(File listOfFiles) {
	    // On scanne les fichiers (non repertoire) et qui ont une extension CSV
	      if (listOfFiles.isFile() && listOfFiles.getName().endsWith(".csv")) {
	    	  //On defini le format du CSV
	    	  CSVFormat csvFileFormat = CSVFormat.DEFAULT;
	    	  CSVParser csvParser = null;
	    	  List<CSVRecord> csvRecord=null;
	    	  int ligne=0;
	    	  int ligneNiftiRoiNumber=0;
	    	  int ligneManualRoiNumber=0;
	    	  int niftiNumber=0;
	    	  int manualNumber=0;
			try {
				BufferedReader br = new BufferedReader(new FileReader(listOfFiles));
				System.out.println(listOfFiles);
				csvParser = CSVParser.parse(br,  csvFileFormat);
				  //On met les records dans une list
		    	  csvRecord=csvParser.getRecords();
		    	  // On balaie le fichier ligne par ligne
		    	  for (int j=0 ; j<csvRecord.size(); j++) {
		    		  //On regarde la colonne 1 et on cherche " sum"
		    		  String labelNum=csvRecord.get(j).get(1);
		    		  if (labelNum.equals(" sum")) {
		    			  // on trouve sum et on recupere son numero de ligne
		    			  ligne=j;
		    			  break;
		    		  }
		    	  }
		    	for (int i=(ligne+1); i<csvRecord.size(); i++) {
		    		
		    		  String roiNomber=csvRecord.get(i).get(0);
		    		  if (roiNomber.contains("Number of Nifti ROIs =")) {
		    			  ligneNiftiRoiNumber=i;
		    		  }
		    		  else if (roiNomber.contains("Number of ROIs =")) {
		    			  ligneManualRoiNumber=i;
		    		  }
		    	}
		    		 
		    	  
			} catch (IOException e) {System.out.println(e + "File Reader "+ listOfFiles);}
	    		try {
		    	  //On recupere les valeurs qui nous interessent
		    	  String name=csvRecord.get(ligne+1).get(0);
		    	  String id=csvRecord.get(ligne+1).get(13);
		    	  String date=csvRecord.get(ligne+1).get(1);
		    	  double mtv=Double.parseDouble(csvRecord.get(ligne).get(3));
		    	  double tlg=Double.parseDouble(csvRecord.get(ligne).get(4));
		    	  double suvMean=Double.parseDouble(csvRecord.get(ligne).get(5));
		    	  double suvSD=Double.parseDouble(csvRecord.get(ligne).get(6));
		    	  double suvPeak=Double.parseDouble(csvRecord.get(ligne).get(7));
		    	  double suvMax=Double.parseDouble(csvRecord.get(ligne).get(11));
		    	  double qPeak=Double.parseDouble(csvRecord.get(ligne).get(9));
		    	  double sul=Double.parseDouble(csvRecord.get(ligne+1).get(3));
		    	  int timer=0;
		    	  if (!csvRecord.get(ligne+1).get(5).equals("")) timer=Integer.parseInt(csvRecord.get(ligne+1).get(5));
		    	  //On recupere le nombre de ROI nifti et manual
		    	  if (ligneNiftiRoiNumber!=0) {
		    		  String niftiNumberString=csvRecord.get(ligneNiftiRoiNumber).get(0);
		    		  niftiNumberString=niftiNumberString.substring(23);
		    		  niftiNumber=Integer.parseInt(niftiNumberString);
		    	  } 
		    	  if (ligneManualRoiNumber!=0) {
		    		  String RoiNumberString=csvRecord.get(ligneManualRoiNumber).get(0);
		    		  RoiNumberString=RoiNumberString.substring(17);
		    		  manualNumber=Integer.parseInt(RoiNumberString);
		    		  
		    	  } 
		    	  //On cree un objet pour chaque patient et qu'on recuperera via un getteur
		    	  Csv_Collector_Resultat resu=new Csv_Collector_Resultat(name, id, date, mtv, tlg, suvMean,suvSD,suvPeak,suvMax,sul,qPeak, timer, niftiNumber, manualNumber);
		    	  //On ajoute cet objet dans la list des objet patient
		    	  resultats.add(resu);
		    	  nombreSucces++;
	    	 }catch (Exception e) {System.out.println(e+ "File Parser"+listOfFiles+ ligne);}
	    		 
	    	 
	    	  }
	      
	      else if (listOfFiles.isDirectory()) {
	        System.out.println("Subdirectory Not analyzed" + listOfFiles.getName());
	      }
	      
	      lblStatusIdle.setText("Success: "+nombreSucces+"/"+nombreTotal);

	}
	
private StringBuilder collecterPatient() {
	
	StringBuilder sb=new StringBuilder();
	//On met la colonne de titre
	sb.append("Name"+","+"ID"+","+"Date"+","+"Suv Max"+","+"TMTV"+","+"TLG"+","+"SUV Mean"+","+"SUV SD"+","+"SUV Peak"+","+"SUV qPeak"+","+"SUL Factor"+","+"Timer"+","+"Manual Roi Number"+","+"Automatic Roi number"+"\n");
	//On boucle les objet et on ajoute les lignes
	for (int i=0; i<resultats.size(); i++) {
		sb.append(resultats.get(i).getNom()+","+resultats.get(i).getId()+","+resultats.get(i).getDate()+","+String.valueOf(resultats.get(i).getSuvMax())+","+String.valueOf(resultats.get(i).getMtv())+","+String.valueOf(resultats.get(i).getTlg())+","+String.valueOf(resultats.get(i).getSuvMean())+","+String.valueOf(resultats.get(i).getSuvSD())+","+String.valueOf(resultats.get(i).getSuvPeak())+","+String.valueOf(resultats.get(i).getqPeak())+","+String.valueOf(resultats.get(i).getSul())+","+String.valueOf(resultats.get(i).getTimer())+","+String.valueOf(resultats.get(i).getManualRoiNumber())+","+String.valueOf(resultats.get(i).getNiftiRoiNumber())+"\n");
	}
	return sb;
}

/**
 * @wbp.parser.entryPoint
 */
private void gui() {
	
	JFrame window=new JFrame();
	window.setTitle("CSV Collector");
	window.setLocationRelativeTo(null);
	window.setVisible(true);
	window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	window.getContentPane().setLayout(new BorderLayout(0, 0));
	
	JPanel Main_panel = new JPanel();
	window.getContentPane().add(Main_panel, BorderLayout.CENTER);
	Main_panel.setLayout(new BorderLayout(0, 0));
	
	JPanel Panel_button = new JPanel();
	Panel_button.setBorder(new LineBorder(new Color(0, 0, 0)));
	Main_panel.add(Panel_button);
	Panel_button.setLayout(new GridLayout(0, 2, 0, 0));
	
	JLabel lblThisDirectoryWill = new JLabel("CSV directory to scan");
	lblThisDirectoryWill.setToolTipText("Will scan the root folder (not it's subdirectories)");
	lblThisDirectoryWill.setBorder(new LineBorder(Color.LIGHT_GRAY));
	Panel_button.add(lblThisDirectoryWill);
	
	lblLoadedDirectory = new JLabel("N/A");
	lblLoadedDirectory.setBorder(new LineBorder(Color.LIGHT_GRAY));
	Panel_button.add(lblLoadedDirectory);
	
	JLabel lblCollectedData = new JLabel("Collected Data");
	lblCollectedData.setBorder(new LineBorder(Color.LIGHT_GRAY));
	Panel_button.add(lblCollectedData);
	
	JPanel Collected_data_panel = new JPanel();
	Collected_data_panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
	Panel_button.add(Collected_data_panel);
	Collected_data_panel.setLayout(new GridLayout(0, 2, 5, 0));
	
	JLabel lblPatientName = new JLabel("Patient Name");
	Collected_data_panel.add(lblPatientName);
	
	JLabel lblPatientId = new JLabel("Patient ID");
	Collected_data_panel.add(lblPatientId);
	
	JLabel lblDate = new JLabel("Date");
	Collected_data_panel.add(lblDate);
	
	JLabel lblSuvmax = new JLabel("SUVMax");
	Collected_data_panel.add(lblSuvmax);
	
	JLabel lblSuvmean = new JLabel("SUVMean");
	Collected_data_panel.add(lblSuvmean);
	
	JLabel lblSuvsd = new JLabel("SUVSD");
	Collected_data_panel.add(lblSuvsd);
	
	JLabel lblSuvpeak = new JLabel("SUVPeak");
	Collected_data_panel.add(lblSuvpeak);
	
	JLabel lblSuvqPeak = new JLabel("SUVqPeak");
	Collected_data_panel.add(lblSuvqPeak);
	
	JLabel lblTmt = new JLabel("TMTV");
	Collected_data_panel.add(lblTmt);
	
	JLabel lblTlg = new JLabel("TLG");
	Collected_data_panel.add(lblTlg);
	
	JLabel lblSulFactor = new JLabel("SUL Factor");
	Collected_data_panel.add(lblSulFactor);
	
	JLabel timer = new JLabel("Timer");
	Collected_data_panel.add(timer);
	
	JLabel numberManual = new JLabel("Number Manual ROI");
	Collected_data_panel.add(numberManual);
	
	JLabel numberAuto = new JLabel("Number Automatic ROI");
	Collected_data_panel.add(numberAuto);
	
	JPanel Button_Panel = new JPanel();
	window.getContentPane().add(Button_Panel, BorderLayout.SOUTH);
	
	JButton btnLoadDirectory = new JButton("Load Directory");
	Button_Panel.add(btnLoadDirectory);
	btnLoadDirectory.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fc=new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ouvrir=fc.showOpenDialog(null);
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
				repertoire=new File(fc.getSelectedFile().getAbsolutePath());
				lblLoadedDirectory.setText(fc.getSelectedFile().toString());
				btnGenerate.setEnabled(true);
				btnLoadDirectory.setBackground(Color.green);
				
			}
		}
	});
	
	btnGenerate = new JButton("Generate collected CSV");
	btnGenerate.setEnabled(false);
	btnGenerate.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fc=new JFileChooser();
			fc.setSelectedFile(new File("csvCollection.csv"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
			fc.setFileFilter(filter);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ouvrir=fc.showSaveDialog(null);
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
			//Confirmation Dialog if File already Exist
			if (fc.getSelectedFile().exists()) {

			    int returnVal = JOptionPane.showConfirmDialog(fc,
			            "Overwrite existing file " + fc.getSelectedFile() + "?", "Overwrite warning", JOptionPane.OK_CANCEL_OPTION,
			            JOptionPane.WARNING_MESSAGE);
			    if (returnVal == JOptionPane.CANCEL_OPTION) {
			        return;
			    }
			}
			File f=fc.getSelectedFile().getAbsoluteFile();	
				
					
						SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								resultats=new ArrayList<Csv_Collector_Resultat>();
								File[] listOfFiles = repertoire.listFiles();
								nombreSucces = 0;
								nombreTotal=listOfFiles.length;
							    for (int i = 0; i < listOfFiles.length; i++) {
							    	lectureCSV(listOfFiles[i]);
							    	
							    }
									StringBuilder sbfinal=collecterPatient();
									PrintWriter pw = new PrintWriter(f);
									pw.write(sbfinal.toString());
									pw.close();
								
								return null;
							}
							
							@Override
							protected void done(){
								lblStatusIdle.setText("Done "+nombreSucces+"/"+nombreTotal);
								lblStatusIdle.setForeground(Color.BLUE);
							}
						};
						worker.execute();
			}
			
		}
	});
	Button_Panel.add(btnGenerate);
	
	lblStatusIdle = new JLabel("Status : Idle              ");

	Button_Panel.add(lblStatusIdle);
	window.pack();
	window.setSize(window.getPreferredSize());
}
	

}


