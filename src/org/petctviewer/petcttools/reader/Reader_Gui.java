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
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Reader_Gui extends JFrame {

	private JTable tableSeries;
	private JTable tableStudy;
	
	private Table_Study_Model modelStudy;
	
	
	public Reader_Gui() {
		super("Read Local Dicoms");
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Read", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		modelStudy=new Table_Study_Model();
		
		JPanel panel_north = new JPanel();
		panel.add(panel_north, BorderLayout.NORTH);
		
		JList list = new JList();
		panel_north.add(list);
		
		JLabel lblPathNa = new JLabel("Path : N/A");
		panel_north.add(lblPathNa);
		
		JButton btnScanFolder = new JButton("Scan Folder");
		panel_north.add(btnScanFolder);
		
		JPanel panel_center = new JPanel();
		panel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane_study = new JScrollPane();
		panel_center.add(scrollPane_study);
		
		tableStudy = new JTable();
		tableStudy.setModel(modelStudy);
		scrollPane_study.setViewportView(tableStudy);
		
		tableStudy.getColumnModel().getColumn(5).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(5).setMaxWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMaxWidth(0);
		
		JScrollPane scrollPane_serie = new JScrollPane();
		panel_center.add(scrollPane_serie);
		
		tableSeries = new JTable();
		
		scrollPane_serie.setViewportView(tableSeries);
		
		JPanel panel_east = new JPanel();
		panel.add(panel_east, BorderLayout.EAST);
		
		JButton btnRead = new JButton("Read");
		panel_east.add(btnRead);
		
		JPanel panel_setup = new JPanel();
		tabbedPane.addTab("Setup", null, panel_setup, null);
		panel_setup.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_center_setup = new JPanel();
		panel_setup.add(panel_center_setup);
		
		JButton btnNewButton = new JButton("Select Folder");
		panel_center_setup.add(btnNewButton);
		
		tableStudy.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
	        public void valueChanged(ListSelectionEvent event) {
				
				@SuppressWarnings("unchecked")
				ArrayList<Series_Details> series=(ArrayList<Series_Details>) tableStudy.getValueAt(tableStudy.getSelectedRow(), 6);
				
				tableSeries.setModel(new Table_Series_Model(series));
				
				tableSeries.getColumnModel().getColumn(4).setMinWidth(0);
				tableSeries.getColumnModel().getColumn(4).setMaxWidth(0);
				tableSeries.getColumnModel().getColumn(5).setMinWidth(0);
				tableSeries.getColumnModel().getColumn(5).setMaxWidth(0);
				
	        }


	    });
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
