package org.petctviewer.petcttools.reader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class Reader_Gui extends JFrame {

	private JTable tableSeries;
	private JTable tableStudy;
	
	private Read_Local_Dicom dicomReader;
	
	private Table_Study_Model modelStudy;
	private JTable table;
	
	
	public Reader_Gui(Read_Local_Dicom dicomReader) {
		super("Read Local Dicoms");
		this.dicomReader=dicomReader;
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Read", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		modelStudy=new Table_Study_Model();
		
		JPanel panel_north = new JPanel();
		panel.add(panel_north, BorderLayout.NORTH);
		
		JLabel lblSelector = new JLabel("Selector");
		panel_north.add(lblSelector);
		
		JComboBox<Integer> comboBox_position_read = new JComboBox<Integer>();
		comboBox_position_read.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1, 2,3,4,5,6,7,8,9,10,11,12}));
		comboBox_position_read.setSelectedIndex(0);
		panel_north.add(comboBox_position_read);
		
		JLabel lblPathNa = new JLabel("Path : N/A");
		panel_north.add(lblPathNa);
		
		JButton btnScanFolder = new JButton("Scan Folder");
		panel_north.add(btnScanFolder);
		
		JPanel panel_center = new JPanel();
		panel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane_study = new JScrollPane();
		panel_center.add(scrollPane_study);
		
		tableStudy = new JTable(){			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		
	        Component returnComp = super.prepareRenderer(renderer, row, column);
	        Color alternateColor = new Color(204, 204, 204);
	        Color whiteColor = Color.WHITE;
	        if (!returnComp.getBackground().equals(getSelectionBackground())){
	            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
	            returnComp .setBackground(bg);
	            bg = null;
	        }
	        return returnComp;
	        }
		};
		
		tableStudy.setModel(modelStudy);
		scrollPane_study.setViewportView(tableStudy);
		
		tableStudy.getColumnModel().getColumn(5).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(5).setMaxWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMaxWidth(0);
		
		tableStudy.setAutoCreateRowSorter(true);
		
		JScrollPane scrollPane_serie = new JScrollPane();
		panel_center.add(scrollPane_serie);
		
		tableSeries = new JTable(){			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				
		        Component returnComp = super.prepareRenderer(renderer, row, column);
		        Color alternateColor = new Color(204, 204, 204);
		        Color whiteColor = Color.WHITE;
		        if (!returnComp.getBackground().equals(getSelectionBackground())){
		            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
		            returnComp .setBackground(bg);
		            bg = null;
		        }
		        return returnComp;
		        }
			};
			
		tableSeries.setAutoCreateRowSorter(true);
		
		scrollPane_serie.setViewportView(tableSeries);
		
		JPanel panel_east = new JPanel();
		panel.add(panel_east, BorderLayout.EAST);
		
		JButton btnRead = new JButton("Read");
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//SK SWING WORKER ICI
				if(tableSeries.getSelectedRowCount()!=0) {
					ArrayList<File> folders=new ArrayList<File>();
					int[] rows=tableSeries.getSelectedRows();
					for(int row : rows) {
						folders.add((File) tableSeries.getValueAt(row, 4));
					}
					dicomReader.openFolders(folders);
					
				}
				
				
			}
		});
		panel_east.add(btnRead);
		
		JPanel panel_setup = new JPanel();
		tabbedPane.addTab("Setup", null, panel_setup, null);
		panel_setup.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_center_setup = new JPanel();
		panel_setup.add(panel_center_setup);
		
		table = new JTable();
		table.setEnabled(false);
		table.setModel(new DefaultTableModel(
			new String[][] {
				{"1", null},
				{"2", null},
				{"3", null},
				{"4", null},
				{"5", null},
				{"6", null},
				{"7", null},
				{"8", null},
				{"9", null},
				{"10", null},
				{"11", null},
				{"12", null},
			},
			new String[] {
				"Position", "Path"
			}
		));
		panel_center_setup.add(table);
		
		JPanel panel_north_stup = new JPanel();
		panel_setup.add(panel_north_stup, BorderLayout.NORTH);
		
		JComboBox<Integer> comboBox_position_setup = new JComboBox<Integer>();
		comboBox_position_setup.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1, 2,3,4,5,6,7,8,9,10,11,12}));
		comboBox_position_setup.setSelectedIndex(0);
		comboBox_position_setup.setMaximumRowCount(12);
		panel_north_stup.add(comboBox_position_setup);
		
		JButton btnNewButton = new JButton("Select Folder");
		panel_north_stup.add(btnNewButton);
		
		tableStudy.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
	        public void valueChanged(ListSelectionEvent event) {
				
				
				if(tableStudy.getSelectedRowCount()!=0) {
					@SuppressWarnings("unchecked")
					ArrayList<Series_Details> series=(ArrayList<Series_Details>) tableStudy.getValueAt(tableStudy.getSelectedRow(), 6);
					
					tableSeries.setModel(new Table_Series_Model(series));
					
					tableSeries.getColumnModel().getColumn(4).setMinWidth(0);
					tableSeries.getColumnModel().getColumn(4).setMaxWidth(0);
					tableSeries.getColumnModel().getColumn(5).setMinWidth(0);
					tableSeries.getColumnModel().getColumn(5).setMaxWidth(0);
					
				}
				
				
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
