package org.petctviewer.petcttools.uncompress;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.dcm4che3.data.UID;
import org.dcm4che3.tool.dcm2dcm.Dcm2Dcm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ij.plugin.PlugIn;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;

public class Uncompress_Gui extends JFrame implements PlugIn {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private File originalFiles, destinationFile;
	private JFrame gui=this;
	private JLabel lbl_original_files, lbl_destinations_files, lblstatus;
	private int counter;

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
		
		
		JButton btnUncompress = new JButton("Uncompress");
		btnUncompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				counter=0;
				Dcm2Dcm dcm2dcm=new Dcm2Dcm();
				dcm2dcm.setTransferSyntax(UID.ImplicitVRLittleEndian);
				
				
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){
					
					
					protected Void doInBackground() throws Exception {
						
						mtranscode(originalFiles, destinationFile, dcm2dcm);
						return null;
						
					}
					
					@Override
					protected void done(){
						lblstatus.setText("Done");
						
					}
					
								
				};
				
				worker.execute();
				
				
			}
		});
		panel_south.add(lblPoweredByDcmche);
		panel_south.add(btnUncompress);
		panel_south.add(lblstatus);
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
	
	private void mtranscode(File src, File dest, Dcm2Dcm dcm) {
        if (src.isDirectory()) {
            dest.mkdir();
            for (File file : src.listFiles())
                mtranscode(file, new File(dest, file.getName()), dcm);
            return;
        }
        if (dest.isDirectory())
            dest = new File(dest, src.getName());
        try {
            dcm.transcode(src, dest);
            counter++;
            System.out.println(("transcoded"+src+dest));
            lblstatus.setText("Transcoded "+counter+" Files");
        } catch (Exception e) {
            System.out.println(("failed"+src+e.getMessage()));
            e.printStackTrace(System.out);
        }
    }

}
