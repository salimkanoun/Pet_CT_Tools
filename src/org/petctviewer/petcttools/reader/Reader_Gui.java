package org.petctviewer.petcttools.reader;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;

import ij.ImagePlus;

@SuppressWarnings("serial")
public class Reader_Gui extends JFrame {

	private JTable tableSeries;
	private JTable tableStudy;
	
	private Table_Study_Model modelStudy;
	private JTable table_path_setup;
	
	private Reader_Gui gui=this;
	
	Preferences jPrefer = Preferences.userNodeForPackage(this.getClass());
	
	
	public Reader_Gui() {
		super("Read Local Dicoms");
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.NORTH);
		
		JPanel panel_read = new JPanel();
		tabbedPane.addTab("Read", null, panel_read, null);
		panel_read.setLayout(new BorderLayout(0, 0));
		modelStudy=new Table_Study_Model();
		
		JPanel panel_north = new JPanel();
		panel_read.add(panel_north, BorderLayout.NORTH);
		
		JLabel lblSelector = new JLabel("Selector");
		panel_north.add(lblSelector);
		
		JComboBox<Integer> comboBox_position_read = new JComboBox<Integer>();
		comboBox_position_read.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1, 2,3,4,5,6,7,8,9,10,11,12}));
		comboBox_position_read.setSelectedIndex(getLastRead());
		panel_north.add(comboBox_position_read);
		
		JLabel lblPathNa = new JLabel("Path : N/A");

		JButton btnScanFolder = new JButton("Scan Folder");
		btnScanFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storeLastRead(comboBox_position_read.getSelectedIndex());
				String path=(String) table_path_setup.getValueAt( (int) comboBox_position_read.getSelectedItem()-1,1);
				lblPathNa.setText("Path : "+path);
				emptyStudySerieTable();
				btnScanFolder.setEnabled(false);
				
				SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						Read_Local_Dicom reader= new Read_Local_Dicom();
						reader.scanFolder(new File(path), btnScanFolder);
						gui.setHashMap(reader.dicomMap);
						gui.pack();
						return null;
					}
					
					@Override
				  	protected void done() {
						btnScanFolder.setEnabled(true);
						btnScanFolder.setText("Scan Folder");
					}

					
				};
				
				worker.execute();
				
				
				
				
			}
		});
		panel_north.add(btnScanFolder);
		panel_north.add(lblPathNa);
		
		JPanel panel_center = new JPanel();
		panel_read.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPane_study = new JScrollPane();
		panel_center.add(scrollPane_study);
		
		tableStudy = new JTable_Color();
		
		tableStudy.setModel(modelStudy);
		scrollPane_study.setViewportView(tableStudy);
		
		tableStudy.getColumnModel().getColumn(5).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(5).setMaxWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMinWidth(0);
		tableStudy.getColumnModel().getColumn(6).setMaxWidth(0);
		
		tableStudy.setAutoCreateRowSorter(true);
		
		JScrollPane scrollPane_serie = new JScrollPane();
		panel_center.add(scrollPane_serie);
		
		tableSeries = new JTable_Color();
			
		tableSeries.setAutoCreateRowSorter(true);
		
		scrollPane_serie.setViewportView(tableSeries);
		
		JPanel panel_east = new JPanel();
		panel_read.add(panel_east, BorderLayout.EAST);
		
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
					openFolders(folders);
					
				}
				
				
			}
		});
		panel_east.add(btnRead);
		
		JPanel panel_setup = new JPanel();
		tabbedPane.addTab("Setup", null, panel_setup, null);
		panel_setup.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_center_setup = new JPanel();
		panel_setup.add(panel_center_setup);
		panel_center_setup.setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane_setup = new JScrollPane();
		panel_center_setup.add(scrollPane_setup);
		
		table_path_setup = new JTable_Color();
		table_path_setup.setEnabled(false);
		table_path_setup.setModel(new DefaultTableModel(
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
		
		table_path_setup.getColumnModel().getColumn(0).setMinWidth(100);
		table_path_setup.getColumnModel().getColumn(0).setMaxWidth(100);
		scrollPane_setup.setViewportView(table_path_setup);
		loadPreference();
		
		JPanel panel_north_stup = new JPanel();
		panel_setup.add(panel_north_stup, BorderLayout.NORTH);
		
		JLabel lblModifyPosition = new JLabel("Modify Position : ");
		panel_north_stup.add(lblModifyPosition);
		
		JComboBox<Integer> comboBox_position_setup = new JComboBox<Integer>();
		comboBox_position_setup.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {1, 2,3,4,5,6,7,8,9,10,11,12}));
		comboBox_position_setup.setSelectedIndex(0);
		comboBox_position_setup.setMaximumRowCount(12);
		panel_north_stup.add(comboBox_position_setup);
		
		JButton btnNewButton = new JButton("Select Folder");
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(!StringUtils.isEmpty((String) table_path_setup.getValueAt(comboBox_position_setup.getSelectedIndex(), 1))) {
					fc.setSelectedFile(new File((String) table_path_setup.getValueAt(comboBox_position_setup.getSelectedIndex(), 1)+File.separator+"child"));
				}
				int choose=fc.showOpenDialog(gui);
				//If choice validated update the table with directory location and store the path in the registery
				if(choose==JFileChooser.APPROVE_OPTION) {
					storePreference((int) comboBox_position_setup.getSelectedIndex(),fc.getSelectedFile().toString()+File.separator);
					loadPreference();
				}
			}
		});
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
	
	/**
	 * Build a map of study sorted by studyUID, will be used to fill the study/serie table
	 * @param seriesMap
	 */
	private void setHashMap(HashMap<File, Series_Details> seriesMap) {
		
		HashMap<String, ArrayList<Series_Details>> studyMap=new HashMap<String, ArrayList<Series_Details>>();
		
		for(File directory : seriesMap.keySet()) {
			
			if(!studyMap.containsKey(seriesMap.get(directory).studyUID)) {
				studyMap.put(seriesMap.get(directory).studyUID, new ArrayList<Series_Details>());
				
			}
			
			studyMap.get(seriesMap.get(directory).studyUID).add(seriesMap.get(directory));
			
			
			
		}
		
		updateSerieTable(studyMap);
		
	}
	
	/**
	 * Update the study tabel with the scann results
	 * @param studyMap
	 */
	private void updateSerieTable(HashMap<String, ArrayList<Series_Details>> studyMap) {
		//Empty the model before filling it
		emptyStudySerieTable();
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
	
	private void emptyStudySerieTable() {
		modelStudy.setRowCount(0);
		((DefaultTableModel) tableSeries.getModel()).setRowCount(0);
	}
	
	/**
	 * Open DICOMs contained in array list of folders (1 folder = 1 serie)
	 * @param folders
	 */
	private void openFolders(ArrayList<File> folders) {
		
		for(File folder: folders) {
			Image_Reader reader=new Image_Reader(folder);
			ImagePlus image=reader.getImagePlus();
			image.show();
		}
			
	}
		
	private void storePreference(int position, String path) {
		jPrefer.put("path"+position, path);
	}
	
	private void storeLastRead(int position) {
		jPrefer.putInt("lastRead", position);
	}
	
	private int getLastRead() {
		 return jPrefer.getInt("lastRead", 0);
	}
	
	private void loadPreference() {
		for (int i=0 ; i<12 ; i++) {
			String path=jPrefer.get("path"+i, null);
			table_path_setup.setValueAt(path, i, 1);
		}
		
	}
		
			
		
	
}
