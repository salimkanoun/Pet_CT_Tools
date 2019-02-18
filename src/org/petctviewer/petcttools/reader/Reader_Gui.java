package org.petctviewer.petcttools.reader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;

public class Reader_Gui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tableSeries;
	private JTable tableStudy;
	
	private Table_Study_Model modelStudy;
	
	
	public Reader_Gui() {
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Read", null, panel, null);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		tableStudy = new JTable();
		modelStudy=new Table_Study_Model();
		tableStudy.setModel(modelStudy);
		scrollPane.setViewportView(tableStudy);
		
		tableStudy.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
	        public void valueChanged(ListSelectionEvent event) {
				
				ArrayList<Series_Details> series=(ArrayList<Series_Details>) tableStudy.getValueAt(tableStudy.getSelectedRow(), 6);
				
				tableSeries.setModel(new Table_Series_Model(series));
				
	        }


	    });
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1);
		
		tableSeries = new JTable();
		
		scrollPane_1.setViewportView(tableSeries);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
	
	public void setHashMap(HashMap<File, Series_Details> seriesMap) {
		
		HashMap<String, ArrayList<Series_Details>> studyMap=new HashMap<String, ArrayList<Series_Details>>();
		
		for(File directory : seriesMap.keySet()) {
			
			if(!studyMap.containsKey(seriesMap.get(directory).studyUID)) {
				studyMap.put(seriesMap.get(directory).studyUID, new ArrayList<Series_Details>());
				
			}
			
			studyMap.get(seriesMap.get(directory).studyUID).add(seriesMap.get(directory));
			
			
			
		}
		
		updateSerieTable(studyMap);
		
	}
	
	private void updateSerieTable(HashMap<String, ArrayList<Series_Details>> studyMap) {
		
		
		for(String studyUID : studyMap.keySet()) {
			
			ArrayList<Series_Details> details=studyMap.get(studyUID);
			
			modelStudy.addRow(new Object[] {details.get(0).patientName,
					details.get(0).patientId,
					details.get(0).studyDate,
					details.get(0).studyDescription,
					details.get(0).accessionNumber,
					details.get(0).fileLocation,
					details});
			
			
			
			
			
			
		}
		
		
	}
	

}
