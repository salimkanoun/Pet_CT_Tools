/**
Copyright (C) 2017 KANOUN Salim

This program is free software; you can redistribute it and/or modify
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

package org.petctviewer.petcttools.ctsegmentation;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class CT_Segmentation_GUI extends JFrame {

	private JPanel contentPane;
	private CT_Segmentation segmentation;
	private ButtonGroup group ;
	private JButton btnGenerateMaskedImage, btnLoad, btnBatchSegmentation, btnSelectImagejStack;
	private JCheckBox chckbxFillMaskHole ;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CT_Segmentation_GUI frame = new CT_Segmentation_GUI();
					frame.pack();
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
	public CT_Segmentation_GUI() {
		makeGui();
	}
	
	public CT_Segmentation_GUI(CT_Segmentation segmentation) {
		makeGui();
		this.segmentation=segmentation;
	}
	
	public JButton getGenerateMaskButton() {
		return btnGenerateMaskedImage;
	}
	
	public JButton getLoadButton() {
		return btnLoad;
	}
	
	protected void makeGui() {
		setTitle("CT Segmentation");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel Main_Panel = new JPanel();
		contentPane.add(Main_Panel, BorderLayout.CENTER);
		Main_Panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(Color.LIGHT_GRAY));
		Main_Panel.add(panel_2, BorderLayout.NORTH);
		
		JPanel Grid_Panel = new JPanel();
		panel_2.add(Grid_Panel);
		Grid_Panel.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel lblNa = new JLabel("N/A");
		
		btnSelectImagejStack = new JButton("Select ImageJ Stack");
		btnSelectImagejStack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				segmentation.clearBatch();
				ImagePlus imageInput=WindowManager.getCurrentImage();
				segmentation.setImageInput(imageInput);
				imageInput.close();
				lblNa.setText("Stack Selected: "+imageInput.getImageStackSize()+" Slices");
				btnBatchSegmentation.setEnabled(false);
				btnLoad.setEnabled(true);
			}
		});
		
		Grid_Panel.add(btnSelectImagejStack);	
		Grid_Panel.add(lblNa);
		
		JLabel lblStatusIdle = new JLabel("Status : Idle");
		
		JButton btnCalculateSegmentation = new JButton("Calculate Segmentation");
		btnCalculateSegmentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (segmentation.inputImageList.size()==0) {
					segmentation.runSegmentation(lblStatusIdle, false, null);
				}
				else {
					//JFILE CHOOSER POUR RESULTAT ET LANCER LA PROCEDURE
					JFileChooser batchSegmentation=new JFileChooser();
					batchSegmentation.setMultiSelectionEnabled(false);
					batchSegmentation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int ok=batchSegmentation.showOpenDialog(null);
					if (ok==JFileChooser.APPROVE_OPTION ) {
						segmentation.runSegmentation(lblStatusIdle, true, batchSegmentation.getSelectedFile());
						IJ.log(batchSegmentation.getSelectedFile().getPath());
						}
					
					
					
				}
				
			}
		});
		Grid_Panel.add(btnCalculateSegmentation);
		
	
		Grid_Panel.add(lblStatusIdle);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		Grid_Panel.add(horizontalStrut);
		
		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);
		panel_3.setLayout(new GridLayout(0, 2, 0, 0));
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel_3.add(horizontalStrut_1);
		
		btnBatchSegmentation = new JButton("Batch Segmentation");
		btnBatchSegmentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSelectImagejStack.setEnabled(false);
				if(segmentation.inputImageList.size()< 6) {
					ImagePlus imp=WindowManager.getCurrentImage();
					segmentation.addImageInputBatch(imp);
					imp.close();
					lblNa.setText(segmentation.inputImageList.size() +" stack Selected");
				}
				else {
					lblNa.setText("Max stack number reached (5)");
				}
				
				
			}
		});
		panel_3.add(btnBatchSegmentation);
		
		JLabel lblLoadSavedSegmentation = new JLabel("Segmentation:");
		panel_3.add(lblLoadSavedSegmentation);
		
		btnLoad = new JButton("Load");
		btnLoad.setEnabled(false);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnLoad.getText().equals("Load")) {
					JFileChooser chooser=new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setFileFilter(new FileNameExtensionFilter("nrrd file", "nrrd"));
					int ok=chooser.showOpenDialog(null);
					if (ok==JFileChooser.APPROVE_OPTION ) {
						ImagePlus imp = IJ.openImage(chooser.getSelectedFile().getAbsolutePath());
						segmentation.setSavedSegmentation(imp);
					}
					
					
				}
				else if (btnLoad.getText().equals("Save")) {
					JFileChooser chooser=new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setFileFilter(new FileNameExtensionFilter("nrrd file", "nrrd"));
					chooser.setSelectedFile(new File(".nrrd"));
					int ok=chooser.showSaveDialog(null);
					if (ok==JFileChooser.APPROVE_OPTION ) {
						IJ.run(segmentation.getRawSegmentation(), "Nrrd ... ", "nrrd=["+chooser.getSelectedFile().getAbsolutePath()+"]");
						}
					
				}
				
				
			}
		});
		panel_3.add(btnLoad);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 3, 0, 0));
		
		JRadioButton rdbtnBone = new JRadioButton("Bone");
		rdbtnBone.setActionCommand("Bone");
		rdbtnBone.setSelected(true);
		panel_1.add(rdbtnBone);
		
		JRadioButton rdbtnSoftTissue = new JRadioButton("Soft Tissue");
		rdbtnSoftTissue.setActionCommand("Soft Tissue");
		panel_1.add(rdbtnSoftTissue);
		
		JRadioButton rdbtnGrease = new JRadioButton("Fat Tissue");
		rdbtnGrease.setActionCommand("Fat Tissue");
		panel_1.add(rdbtnGrease);
		
		JRadioButton rdbtnLung = new JRadioButton("Lung");
		rdbtnLung.setActionCommand("Lung");
		panel_1.add(rdbtnLung);
		
		JRadioButton rdbtnOutside = new JRadioButton("Outside");
		rdbtnOutside.setActionCommand("Outside");
		panel_1.add(rdbtnOutside);
		
		btnGenerateMaskedImage = new JButton("Generate Masked Image");
		btnGenerateMaskedImage.setEnabled(false);
		btnGenerateMaskedImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedButton =group.getSelection().getActionCommand();
				
				Integer i=null;
				if (selectedButton.equals("Bone")) i=0;
				else if (selectedButton.equals("Soft Tissue")) i=1;
				else if (selectedButton.equals("Fat Tissue")) i=2;
				else if (selectedButton.equals("Outside")) i=3;
				else if (selectedButton.equals("Lung")) i=4;
				
				segmentation.makeMaskedImage(i, lblStatusIdle, chckbxFillMaskHole.isSelected());
			}
		});
		panel.add(btnGenerateMaskedImage, BorderLayout.EAST);
		
		JLabel lblPowredByWeka = new JLabel("Powered by Weka Trainable Segmentation");
		lblPowredByWeka.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(lblPowredByWeka, BorderLayout.SOUTH);
		lblPowredByWeka.setHorizontalAlignment(SwingConstants.CENTER);
		
		group = new ButtonGroup();
		group.add(rdbtnBone);
		group.add(rdbtnSoftTissue);
		group.add(rdbtnGrease);
		group.add(rdbtnLung);
		group.add(rdbtnOutside);
		
		chckbxFillMaskHole = new JCheckBox("Fill Mask Holes");
		panel_1.add(chckbxFillMaskHole);
	}

}
