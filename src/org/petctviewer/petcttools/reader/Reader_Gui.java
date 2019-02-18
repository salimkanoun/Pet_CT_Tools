package org.petctviewer.petcttools.reader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;

public class Reader_Gui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTable table_1;
	
	private DefaultTableModel modelSerie;
	
	
	public Reader_Gui() {
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Read", null, panel, null);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		table_1 = new JTable();
		scrollPane.setViewportView(table_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);
		
		table = new JTable();
		
		modelSerie=new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Modality", "Number of slice", "Serie number", "Serie Description", "Study Date", "Study Description", "Patient ID", "Patient Name"
			}
		);
		
		table.setModel(modelSerie);
		
		scrollPane_1.setViewportView(table);
		
	}
	
	public void updateSerieTable(HashMap<File, Series_Details> seriesMap) {
		
		for(File directory : seriesMap.keySet()) {
			Series_Details details=seriesMap.get(directory);
			modelSerie.addRow(new Object[] {details.modality,
								details.numberOfImage,
								details.serieNumber.toString(),
								details.serieDescription,
								details.studyDate,
								details.studyDescription,
								details.patientId,
								details.patientName});
			
		}
		
	}
	

}
