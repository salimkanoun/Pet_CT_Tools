package org.petctviewer.petcttools.uncompress;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.dcm4che3.tool.dcm2dcm.Dcm2Dcm;

import ij.plugin.PlugIn;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class Uncompress_Gui extends JFrame implements PlugIn {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private File originalFiles, destinationFile;
	private JFrame gui=this;
	private JLabel lbl_original_files, lbl_destinations_files;

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
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		JLabel lblPoweredByDcmche = new JLabel("Powered by DCM4CHE");
		panel_1.add(lblPoweredByDcmche);
		
		JButton btnUncompress = new JButton("Uncompress");
		btnUncompress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Dcm2Dcm decompressor=new Dcm2Dcm();
				mtranscode(originalFiles,destinationFile,decompressor);

			}
		});
		panel_1.add(btnUncompress);
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
        	dcm.transcodeWithTranscoder(src, dest);

            System.out.println( ("transcoded")+src+ dest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

}
