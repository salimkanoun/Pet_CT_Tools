package org.petctviewer.petcttools.reader.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.petctviewer.petcttools.reader.Image_Reader;
import org.petctviewer.petcttools.reader.Read_Local_Dicom;
import org.petctviewer.petcttools.reader.Series_Details;

import ij.ImagePlus;

@SuppressWarnings("serial")
public class Reader_Gui extends JFrame {

	private JTable tableSeries;
	private JTable tableStudy;
	private JButton btnScanFolder;
	
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
		
		comboBox_position_read.setSelectedIndex(-1);
		comboBox_position_read.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange()==ItemEvent.SELECTED) {
					String path=(String) table_path_setup.getValueAt( comboBox_position_read.getSelectedIndex(),1);
					lblPathNa.setText("Path : "+path);
				}
			}
			
		});
		
		btnScanFolder = new JButton("Scan Folder");
		btnScanFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storeLastRead(comboBox_position_read.getSelectedIndex());
				String path=(String) table_path_setup.getValueAt(comboBox_position_read.getSelectedIndex() ,1);
				emptyStudySerieTable();
				btnScanFolder.setEnabled(false);
				
				SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						Read_Local_Dicom reader= new Read_Local_Dicom();
						reader.scanFolder(new File(path), btnScanFolder);
						updateSerieTable(reader.dicomMap);
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
		addPopupMenu(tableStudy);
		
		JScrollPane scrollPane_serie = new JScrollPane();
		panel_center.add(scrollPane_serie);
		
		tableSeries = new JTable_Color();
			
		tableSeries.setAutoCreateRowSorter(true);
		addPopupMenu(tableSeries);
		
		scrollPane_serie.setViewportView(tableSeries);
		
		JPanel panel_east = new JPanel();
		panel_read.add(panel_east, BorderLayout.EAST);
		
		JButton btnRead = new JButton("Read");
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SwingWorker<Void,Void> worker=new SwingWorker<Void,Void>() {
					
					boolean ct, pet=false;

					@Override
					protected Void doInBackground() throws Exception {
						if(tableSeries.getSelectedRowCount()!=0) {
							int[] rows=tableSeries.getSelectedRows();
							ArrayList<ImagePlus> imagesPlus=new ArrayList<ImagePlus>();
							
							if(tableSeries.getValueAt(rows[0], 4) instanceof File) {
								ArrayList<File> folders=new ArrayList<File>();
								
								for(int row : rows) {
									folders.add((File) tableSeries.getValueAt(row, 4));
									ctOrPet(tableSeries.getValueAt(row, 1).toString());
									
								}
								
								for(File folder: folders) {
									Image_Reader reader=new Image_Reader(folder);
									ImagePlus image=reader.getImagePlus();
									imagesPlus.add(image);
									image.show();
								}
								
							}else if(tableSeries.getValueAt(rows[0], 4) instanceof ArrayList) {
								
								for(int row : rows) {
									@SuppressWarnings("unchecked")
									ArrayList<File> fileList=(ArrayList<File>) tableSeries.getValueAt(row, 4);
									ctOrPet(tableSeries.getValueAt(row, 1).toString());
									Image_Reader reader=new Image_Reader(fileList);
									ImagePlus image=reader.getImagePlus();
									imagesPlus.add(image);
									image.show();
								}
							}
							
							if(ct && pet) {
								Class<?> Run_Pet_Ct = null;
								try {
									Run_Pet_Ct = Class.forName("Run_Pet_Ct");
								} catch (ClassNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								try {
									Constructor<?> cs=Run_Pet_Ct.getDeclaredConstructor(ArrayList.class);
									cs.newInstance(imagesPlus);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
							}
						}
						return null;
					}
					
					private void ctOrPet(String modality) {
						
						if (modality.equals("CT")) {
							ct=true;
						};
						if (modality.equals("PT")) {
							pet=true;
						};
						
					}
					@Override
					protected void done() {
						// TODO Auto-generated method stub
					}
					
				};
				worker.execute();

				
				
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
					
		
					tableSeries.getColumnModel().getColumn(1).setMaxWidth(100);

					tableSeries.getColumnModel().getColumn(3).setMaxWidth(100);
					
				}
	        }
	    });
		
		comboBox_position_read.setSelectedIndex(getLastRead());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
			
			if(details.get(0).isDicomDir) {
				modelStudy.addRow(new Object[] {details.get(0).patientName,
						details.get(0).patientId,
						details.get(0).studyDate,
						details.get(0).studyDescription,
						details.get(0).accessionNumber,
						null,
						details});
				
			}else {
				modelStudy.addRow(new Object[] {details.get(0).patientName,
						details.get(0).patientId,
						details.get(0).studyDate,
						details.get(0).studyDescription,
						details.get(0).accessionNumber,
						details.get(0).fileLocation.getParentFile(),
						details});
				
			}

		}

	}
	
	private void emptyStudySerieTable() {
		modelStudy.setRowCount(0);
		((DefaultTableModel) tableSeries.getModel()).setRowCount(0);
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
	
	
	private void addPopupMenu(JTable table) {
		
		JPopupMenu popMenuDelete = new JPopupMenu();
		
		JMenuItem menuItemModifySeries = new JMenuItem("Delete Files");
		
		menuItemModifySeries.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Object directory=table.getValueAt(table.getSelectedRow(), table.getColumnCount()-2);
				
				if(directory instanceof File) {
					int response=JOptionPane.showConfirmDialog(gui, "File Will Be Erased, This can't be undone !", "Definitive Erase", JOptionPane.WARNING_MESSAGE);
					if(response==JOptionPane.YES_OPTION) {
						try {
							FileUtils.deleteDirectory((File) directory);
							btnScanFolder.doClick();
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
				}else {
					JOptionPane.showMessageDialog(gui, "DICOMDIR structure can't be deleted safly", "DICOMDIR", JOptionPane.ERROR_MESSAGE);
					
				}
				
			}
			
		});
	
		popMenuDelete.add(menuItemModifySeries);

		popMenuDelete.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popMenuDelete, new Point(0, 0), table));
                        if (rowAtPoint > -1) {
                        	table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}
	
		});
		
		table.setComponentPopupMenu(popMenuDelete);
		
	}
	
	

		
			
		
	
}
