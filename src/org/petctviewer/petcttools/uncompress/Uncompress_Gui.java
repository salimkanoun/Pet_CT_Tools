package org.petctviewer.petcttools.uncompress;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.ArrayUtils;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.tool.dcm2dcm.Dcm2Dcm;

import ij.plugin.PlugIn;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class Uncompress_Gui extends JFrame implements PlugIn {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private File originalFiles, destinationFile;
	private JFrame gui=this;
	private JLabel lbl_original_files, lbl_destinations_files, lblstatus;
	private int counterTranscoded;
	private int counterAllFiles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Uncompress_Gui frame = new Uncompress_Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Uncompress_Gui() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnSelectOriginalFile = new JButton("Select Original File");
		btnSelectOriginalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser originalFilesChooser=new JFileChooser();
				originalFilesChooser.setMultiSelectionEnabled(false);
				originalFilesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ok=originalFilesChooser.showOpenDialog(gui);
				if (ok==JFileChooser.APPROVE_OPTION ) {
					originalFiles=originalFilesChooser.getSelectedFile().getAbsoluteFile();
					lbl_original_files.setText(originalFilesChooser.getSelectedFile().toString());
					pack();
					
				}
				
			}
		});
		panel.add(btnSelectOriginalFile);
		
		lbl_original_files = new JLabel("N/A");
		panel.add(lbl_original_files);
		
		JButton btnSelectDestination = new JButton("Select Destination");
		btnSelectDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser destinationFilesChooser=new JFileChooser();
				destinationFilesChooser.setMultiSelectionEnabled(false);
				destinationFilesChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ok=destinationFilesChooser.showOpenDialog(gui);
				if (ok==JFileChooser.APPROVE_OPTION ) {
					destinationFile=destinationFilesChooser.getSelectedFile().getAbsoluteFile();
					lbl_destinations_files.setText(destinationFilesChooser.getSelectedFile().toString());
					pack();
					
				}
			}
		});
		panel.add(btnSelectDestination);
		
		lbl_destinations_files = new JLabel("N/A");
		panel.add(lbl_destinations_files);
		
		JPanel panel_title = new JPanel();
		contentPane.add(panel_title, BorderLayout.NORTH);
		
		JLabel lblDicomUncompressor = new JLabel("DICOM Uncompressor");
		lblDicomUncompressor.setHorizontalAlignment(SwingConstants.CENTER);
		panel_title.add(lblDicomUncompressor);
		
		JPanel panel_south = new JPanel();
		contentPane.add(panel_south, BorderLayout.SOUTH);
		
		JLabel lblPoweredByDcmche = new JLabel("Powered by DCM4CHE");
		
		lblstatus = new JLabel("Status : Idle");
		
		panel_south.add(lblPoweredByDcmche);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Uncompress all", "Uncompress only compressed", "List Compressed (CSV)"}));
		panel_south.add(comboBox);
		
		
		JButton btnStart = new JButton("Start");
		
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				counterTranscoded=0;
				counterAllFiles=0;
				Dcm2Dcm dcm2dcm=new Dcm2Dcm();
				
				if(comboBox.getSelectedIndex()==0) {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						
						
						protected Void doInBackground() throws Exception {
							
							mtranscode(originalFiles, destinationFile, dcm2dcm, false);
							return null;
							
						}
						
						@Override
						protected void done(){
							lblstatus.setText("Done");
							
						}
						
									
					};
					
					worker.execute();
					
					
					
				}else if(comboBox.getSelectedIndex()==1) {
					SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
						
						
						protected Void doInBackground() throws Exception {
							
							mtranscode(originalFiles, destinationFile, dcm2dcm, true);
							return null;
							
						}
						
						@Override
						protected void done(){
							lblstatus.setText("Done");
							
						}
						
									
					};
					
					worker.execute();
					
				}else if(comboBox.getSelectedIndex()==2) {
					listCompressed();
					
				}
				
			}
			
		});
		
		
		panel_south.add(btnStart);
		panel_south.add(lblstatus);
		
		pack();
	}
	

	@Override
	public void run(String arg0) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Uncompress_Gui frame = new Uncompress_Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	private void mtranscode(File src, File dest, Dcm2Dcm dcm, boolean onlycompressed) {
        if (src.isDirectory()) {
            dest.mkdir();
            for (File file : src.listFiles())
                mtranscode(file, new File(dest, file.getName()), dcm, onlycompressed);
            return;
        }
        if (dest.isDirectory()) dest = new File(dest, src.getName());
        counterAllFiles++;
        try {
        	if(onlycompressed) {
        		if (isCompressedDicom(src)){
        			dcm.transcode(src, dest);
        			 counterTranscoded++;
        		}
        	}else {
        		dcm.transcode(src, dest);
        		counterTranscoded++;
        	}
        	
            lblstatus.setText("Transcoded "+counterTranscoded+"/"+counterAllFiles+" Files");
        } catch (Exception e) {
            System.out.println(("failed"+src+e.getMessage()));
        }
    }
	
	private void listCompressed() {
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
			List<File> compressedDicoms=new ArrayList<File>();
			protected Void doInBackground() throws Exception {
				
				 if (originalFiles.isDirectory()) {
			           this.getTs(originalFiles);
			           StringBuilder csv=new StringBuilder();
			           csv.append("CompressedSeries, Parent Folder"+"\n");
			           for (int i=0; i<compressedDicoms.size(); i++) {
			        	   csv.append(compressedDicoms.get(i)+","+compressedDicoms.get(i).getParent()+"\n");
			           }
			           JFileChooser chooser=new JFileChooser();
			           chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			           int validation=chooser.showSaveDialog(gui);
			           if(validation==JFileChooser.APPROVE_OPTION) {
			        	   System.out.println(csv);
			           }
			           
			        }
				return null;
				
			}
			
			private void getTs(File originalFiles) throws IOException {
				
				File[] directories = originalFiles.listFiles(File::isDirectory);
				
				if(ArrayUtils.isEmpty(directories) ) {
					if(originalFiles.listFiles().length ==0) return;
					File[] files=originalFiles.listFiles();
					boolean compressed=isCompressedDicom(files[0]);
					if(compressed) {
						compressedDicoms.add(originalFiles);
					}
					
					
				}else {
					for (int i=0; i<directories.length; i++) {
						getTs(directories[i]);		
					}	
				}

			}
				
			
			
			@Override
			protected void done(){
				lblstatus.setText("Done");
				
			}
			
						
		};
		
		worker.execute();
	}
	
	private boolean isCompressedDicom(File file) {
		String ts=null;
		try {
			DicomInputStream dis=new DicomInputStream(file);
			dis.readFileMetaInformation();
			ts=dis.getTransferSyntax();
			dis.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(ts.contains("1.2.840.10008.1.2.4") || ts.contains("1.2.840.10008.1.2.5") || ts.contains("1.2.840.10008.1.2.6")) {
			return true;
		}else {
			return false;
		}
	}

}
