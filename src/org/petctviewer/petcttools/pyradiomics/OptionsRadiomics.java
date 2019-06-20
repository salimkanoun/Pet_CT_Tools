
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

package org.petctviewer.petcttools.pyradiomics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JSpinner;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class OptionsRadiomics extends JDialog {
	
	protected JTextField 
	txtResegmentationMin,
	txtResegmentationMax, 
	txtFixedBinWidth, 
	txtNormalizeScale, 
	txtRemoveOutliners, 
	txtXPixelSpacing, 
	txtYPixelSpacing, 
	txtZPixelSpacing, 
	textField_GeometryTolerance, 
	txt_setDistances,
	txt_setAlfa,
	txtLoGSigma,
	txtWavelet ;
	
	protected JCheckBox 
	chckbxFixedBinWidth, 
	chckbxEnableFixedBin, 
	chckbxResegmentation, 
	chckbxAdditionalInfo,
	chckbxFirstOrder, 
	chckbxShape,
	chckbxGlcm, 
	chckbxGlrlm,
	chckbxGlszm,
	chckbxNgtdm,
	chckbxGtdm,
	checkBoxNormalize, 
	chckbxCorrectMask,
	chckbxResampleImage,
	chckbxValidateMask,
	chckbxForce2DExtraction,
	chckbxPrecropping,
	chckbxSymetricalGlcm,
	chckbxUseDistancesToNeighbour,
	chckbxGradient,
	chckbxGradientSpacing;
	
	protected JSpinner 
	spinner_2D_Dimension, 
	spinnerBinFixed, 
	spinner_padDistance, 
	spinner_minRoiDimension, 
	spinner_minRoiSize, 
	spinner_VoxelArrayShift,
	spinner_startLevelWavelet,
	spinner_Wavelet_Level;
	
	protected List<JCheckBox> imageType = new ArrayList<JCheckBox>();
	protected JComboBox<String> comboBox_Interpolator, comboBox_Weighting;
	
	private File settingsFile=null;
	protected boolean ok=false;
	
	private OptionsRadiomics gui; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OptionsRadiomics dialog = new OptionsRadiomics();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public OptionsRadiomics() {
		this.setTitle("pyRadiomics Options");
		getContentPane().setLayout(new BorderLayout());
		
		JPanel buttonPane = new JPanel();
		buttonPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_4 = new JPanel();
		buttonPane.add(panel_4);
		
		JLabel lblDocumentationOnHttpwwwradiomicsio = new JLabel("Documentation : http://www.radiomics.io/  - Require pyRadiomics 2.2.0");
		panel_4.add(lblDocumentationOnHttpwwwradiomicsio);
		lblDocumentationOnHttpwwwradiomicsio.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPane.add(panel_5);
		
		JButton btnTestPyradiomics = new JButton("pyRadiomics");
		panel_5.add(btnTestPyradiomics);

		JButton btnSetSettingsFile = new JButton("Set Settings File");
		panel_5.add(btnSetSettingsFile);
		
		JButton okButton = new JButton("OK");
		panel_5.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ok=true;
				dispose();
			}
		});
		
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);
		
				JButton cancelButton = new JButton("Cancel");
				panel_5.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
		btnSetSettingsFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Warning override message
				JOptionPane.showMessageDialog(null,
				    "Setting a Yaml file will overide your personnal settings",
				    "YAML File",
				    JOptionPane.WARNING_MESSAGE);
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("YAML Files", "yaml");
				fileChooser.setFileFilter(filter);
				int ouvrir=fileChooser.showOpenDialog(null);
				if(ouvrir==JFileChooser.APPROVE_OPTION) {
				settingsFile=fileChooser.getSelectedFile();
				btnSetSettingsFile.setBackground(Color.green);
				}
			}
		});
		
		btnTestPyradiomics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Test_PyRadiomics testGui=new Test_PyRadiomics();
				testGui.setModal(true);
				testGui.pack();
				testGui.setLocationRelativeTo(gui);
				testGui.setVisible(true);
			}
		});
		

		JPanel Settings_Panel = new JPanel();
		getContentPane().add(Settings_Panel, BorderLayout.CENTER);
		Settings_Panel.setLayout(new GridLayout(0, 2, 0, 0));
			
			{
			//////////////////////////////////////PANEL IMAGE DISCRETIZATION /////////////////////////////////////////////	
			JPanel Discretization_Panel = new JPanel();
			Discretization_Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(Discretization_Panel);
			Discretization_Panel.setLayout(new BorderLayout(0, 0));
			
			JPanel Discretization_Title = new JPanel();
			Discretization_Title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			Discretization_Panel.add(Discretization_Title, BorderLayout.NORTH);
			
			JLabel lblDiscretization = new JLabel("Discretization / Resegmentation");
			Discretization_Title.add(lblDiscretization);
				
			JPanel Discretization_Center = new JPanel();
			Discretization_Panel.add(Discretization_Center, BorderLayout.CENTER);
			Discretization_Center.setLayout(new GridLayout(0, 1, 0, 0));
		
			JPanel panel_3 = new JPanel();
			Discretization_Center.add(panel_3);
			chckbxFixedBinWidth = new JCheckBox("Fixed bin width");
			chckbxFixedBinWidth.setSelected(true);
			panel_3.add(chckbxFixedBinWidth);
				
			txtFixedBinWidth = new JTextField("25");
			panel_3.add(txtFixedBinWidth);
			txtFixedBinWidth.setColumns(5);
				
			JPanel panel_2 = new JPanel();
			Discretization_Center.add(panel_2);
			
			chckbxResegmentation = new JCheckBox("Resgmentation (Value limits)");
			chckbxResegmentation.addChangeListener(new ChangeListener()  {
				public void stateChanged (ChangeEvent e) {
					if (!chckbxEnableFixedBin.isSelected()){
						if (chckbxResegmentation.isSelected()) {
						txtResegmentationMin.setEnabled(true); 
						txtResegmentationMax.setEnabled(true);
					}
						else if (!chckbxResegmentation.isSelected()) {
						txtResegmentationMin.setEnabled(false); 
						txtResegmentationMax.setEnabled(false);	
					}
					}
					
				}
			});
			panel_2.add(chckbxResegmentation);
			
			JLabel lblMin = new JLabel("min");
			panel_2.add(lblMin);

			txtResegmentationMin = new JTextField();
			txtResegmentationMin.setEnabled(false);
			panel_2.add(txtResegmentationMin);
			txtResegmentationMin.setText("0");
			txtResegmentationMin.setColumns(5);
			
			JLabel lblMax = new JLabel("max");
			panel_2.add(lblMax);
	
			txtResegmentationMax = new JTextField();
			txtResegmentationMax.setEnabled(false);
			panel_2.add(txtResegmentationMax);
			txtResegmentationMax.setText("0");
			txtResegmentationMax.setColumns(5);
	
			JPanel panel_1 = new JPanel();
			Discretization_Center.add(panel_1);
			chckbxEnableFixedBin = new JCheckBox("Fixed bins per ROI");
			panel_1.add(chckbxEnableFixedBin);
		
			spinnerBinFixed = new JSpinner();
			spinnerBinFixed.setModel(new SpinnerNumberModel(0, null, 1000, 1));
			spinnerBinFixed.setEnabled(false);
			panel_1.add(spinnerBinFixed);
					
			chckbxEnableFixedBin.addChangeListener(new ChangeListener() {
				public void stateChanged (ChangeEvent e) {
					if (chckbxEnableFixedBin.isSelected()) {
						chckbxFixedBinWidth.setSelected(false);
						chckbxResegmentation.setSelected(false);
						txtFixedBinWidth.setEnabled(false);
						txtResegmentationMin.setEnabled(false);
						txtResegmentationMax.setEnabled(false);
						spinnerBinFixed.setEnabled(true);
						
					}
					else{
						chckbxFixedBinWidth.setSelected(true);
						txtFixedBinWidth.setEnabled(true);
						spinnerBinFixed.setEnabled(false);
					}
				}
			});
				
			chckbxFixedBinWidth.addChangeListener(new ChangeListener() {
				public void stateChanged (ChangeEvent e) {
				//Disable fixed bin number
				if (chckbxFixedBinWidth.isSelected()) {
					chckbxEnableFixedBin.setSelected(false);
					txtFixedBinWidth.setEnabled(true);
				}
				else{
					txtFixedBinWidth.setEnabled(false);
					chckbxEnableFixedBin.setSelected(true);
					
				}
				}
			});
			
			}
		
			
			{
			//////////////////////////////////////PANEL IMAGE NORMALIZATION /////////////////////////////////////////////
			JPanel Image_Normalization_panel = new JPanel();
			Image_Normalization_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(Image_Normalization_panel);
			Image_Normalization_panel.setLayout(new BorderLayout(0, 0));
			
			JPanel panel_NormalizationTitle = new JPanel();
			panel_NormalizationTitle.setBorder(new LineBorder(Color.LIGHT_GRAY));
			Image_Normalization_panel.add(panel_NormalizationTitle, BorderLayout.NORTH);
			
			JLabel lblImageNormalization = new JLabel("Image Normalization");
			panel_NormalizationTitle.add(lblImageNormalization);

			JPanel panel_Normalization_Settings = new JPanel();
			Image_Normalization_panel.add(panel_Normalization_Settings, BorderLayout.CENTER);
			panel_Normalization_Settings.setLayout(new BorderLayout(0, 0));
			
			checkBoxNormalize = new JCheckBox("Normalize");
			checkBoxNormalize.setToolTipText("Enable normalizing of the image before any resampling.");
			checkBoxNormalize.addChangeListener(new ChangeListener() {
				public void stateChanged (ChangeEvent e) {
					if (checkBoxNormalize.isSelected()) enableNormalize(true);
					else enableNormalize(false);				
				}
			});
			panel_Normalization_Settings.add(checkBoxNormalize, BorderLayout.WEST);

			JPanel panel_Nomalization = new JPanel();
			panel_Normalization_Settings.add(panel_Nomalization, BorderLayout.CENTER);
			panel_Nomalization.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			JPanel panel_NormalizationElements = new JPanel();
			panel_Nomalization.add(panel_NormalizationElements);
			panel_NormalizationElements.setLayout(new GridLayout(0, 2, 0, 0));
			
			JLabel lblNormalizeScale = new JLabel("Normalize Scale :");
			txtNormalizeScale = new JTextField();
			txtNormalizeScale.setToolTipText("Determines the scale after normalizing the image");
			txtNormalizeScale.setEnabled(false);
			txtNormalizeScale.setText("1.0");
			txtNormalizeScale.setColumns(5);
			JLabel lblRemoveOutliners = new JLabel("Remove Outliers :");
			txtRemoveOutliners = new JTextField();
			txtRemoveOutliners.setToolTipText("Defines the outliers to remove from the image");
			txtRemoveOutliners.setEnabled(false);
			txtRemoveOutliners.setText("0.0");
			txtRemoveOutliners.setColumns(5);
			
			panel_NormalizationElements.add(lblNormalizeScale);
			panel_NormalizationElements.add(txtNormalizeScale);
			panel_NormalizationElements.add(lblRemoveOutliners);
			panel_NormalizationElements.add(txtRemoveOutliners);
			}

			{
			//////////////////////////////////////PANEL IMAGE REASEMBLING /////////////////////////////////////////////
			JPanel panel_ImageResampling = new JPanel();
			panel_ImageResampling.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(panel_ImageResampling);
			panel_ImageResampling.setLayout(new BorderLayout(0, 0));
				
			JPanel panel_Resampling_Title = new JPanel();
			panel_Resampling_Title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			panel_ImageResampling.add(panel_Resampling_Title, BorderLayout.NORTH);
					
			JLabel lblImageResampling = new JLabel("Image Resampling");
			panel_Resampling_Title.add(lblImageResampling);
				
			JPanel panel_Resempling_Settings = new JPanel();
			panel_ImageResampling.add(panel_Resempling_Settings, BorderLayout.CENTER);
			panel_Resempling_Settings.setLayout(new BorderLayout(0, 0));
					
			JPanel panel_Resampling = new JPanel();
			panel_Resempling_Settings.add(panel_Resampling, BorderLayout.CENTER);
			panel_Resampling.setLayout(new GridLayout(0, 1, 0, 0));
						
			JPanel panel_pixelSpacing = new JPanel();
			panel_Resampling.add(panel_pixelSpacing);
							
			JLabel lblPixelSpacing = new JLabel("Pixel Spacing");
			panel_pixelSpacing.add(lblPixelSpacing);		
							
			txtXPixelSpacing = new JTextField();
			txtXPixelSpacing.setEnabled(false);
			txtXPixelSpacing.setText("0.0");
			txtXPixelSpacing.setColumns(3);			
			txtYPixelSpacing = new JTextField();
			txtYPixelSpacing.setEnabled(false);
			txtYPixelSpacing.setText("0.0");
			txtYPixelSpacing.setColumns(3);				
			txtZPixelSpacing = new JTextField();
			txtZPixelSpacing.setEnabled(false);
			txtZPixelSpacing.setText("0.0");
			txtZPixelSpacing.setColumns(3);
			
			JLabel lblX = new JLabel("X");
			panel_pixelSpacing.add(lblX);
			
			panel_pixelSpacing.add(txtXPixelSpacing);
			
			JLabel lblY = new JLabel("Y");
			panel_pixelSpacing.add(lblY);
			panel_pixelSpacing.add(txtYPixelSpacing);
			
			JLabel lblZ = new JLabel("Z");
			panel_pixelSpacing.add(lblZ);
			panel_pixelSpacing.add(txtZPixelSpacing);
	
			JPanel panel_Interpolator = new JPanel();
			panel_Resampling.add(panel_Interpolator);
			
			JLabel lblInterpolator = new JLabel("Interpolator");
			panel_Interpolator.add(lblInterpolator);
			
			comboBox_Interpolator = new JComboBox<String>();
			comboBox_Interpolator.setEnabled(false);
			panel_Interpolator.add(comboBox_Interpolator);
			comboBox_Interpolator.setModel(new DefaultComboBoxModel<String>(new String[] {"sitkNearestNeighbor", "sitkLinear", "sitkBSpline", "sitkGaussian", "sitkLabelGaussian", "sitkHammingWindowedSinc", "sitkCosineWindowedSinc", "sitkWelchWindowedSinc", "sitkLanczosWindowedSinc", "sitkBlackmanWindowedSinc"}));
			comboBox_Interpolator.setSelectedIndex(2);

			JLabel lbl_PadDistance = new JLabel("Pad Distance");
			panel_Interpolator.add(lbl_PadDistance);

			spinner_padDistance = new JSpinner();
			spinner_padDistance.setToolTipText("Set the number of voxels pad cropped tumor volume with during resampling");
			spinner_padDistance.setEnabled(false);
			spinner_padDistance.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
			panel_Interpolator.add(spinner_padDistance);

			chckbxResampleImage = new JCheckBox("Resample Image");
			chckbxResampleImage.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if (chckbxResampleImage.isSelected()) enableResample(true);
					else enableResample(false);
				}
			});
			panel_Resempling_Settings.add(chckbxResampleImage, BorderLayout.WEST);

			}

			{
			//////////////////////////////////////PANEL MASK VALIDATION /////////////////////////////////////////////
			JPanel panel_MaskValidation = new JPanel();
			panel_MaskValidation.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(panel_MaskValidation);
			panel_MaskValidation.setLayout(new BorderLayout(0, 0));
			
			JPanel panel_MaskValidation_Title = new JPanel();
			panel_MaskValidation_Title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			panel_MaskValidation.add(panel_MaskValidation_Title, BorderLayout.NORTH);
			
			JLabel lblMaskValidation = new JLabel("Mask Validation");
			panel_MaskValidation_Title.add(lblMaskValidation);

			chckbxValidateMask = new JCheckBox("Validate Mask");
			chckbxValidateMask.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if (chckbxValidateMask.isSelected()) enableMaskValidate(true);
					else enableMaskValidate(false);
				}
			});
				
			panel_MaskValidation.add(chckbxValidateMask, BorderLayout.WEST);
		
		
			JPanel panel_MaskValidation_Settings = new JPanel();
			panel_MaskValidation.add(panel_MaskValidation_Settings, BorderLayout.CENTER);
			
			JPanel panel_ValidationElements = new JPanel();
			panel_MaskValidation_Settings.add(panel_ValidationElements);
			panel_ValidationElements.setLayout(new GridLayout(0, 2, 0, 0));
			
			JLabel lblMinimumRoiDimension = new JLabel("Minimum ROI dimensions");
			spinner_minRoiDimension = new JSpinner();
			spinner_minRoiDimension.setEnabled(false);
			spinner_minRoiDimension.setToolTipText("Number of needed dimensions in the max (1 to 3)");
			spinner_minRoiDimension.setModel(new SpinnerNumberModel(1, 1, 3, 1));
			JLabel lblMinimumRoiSize = new JLabel("Minimum ROI Size");
			spinner_minRoiSize = new JSpinner();
			spinner_minRoiSize.setEnabled(false);
			spinner_minRoiSize.setToolTipText("minimum of Voxels needed in a ROI");
			JLabel lblGeometryTolerance = new JLabel("Geometry Tolerance");
			textField_GeometryTolerance = new JTextField();
			textField_GeometryTolerance.setToolTipText("Determines the tolarance used by SimpleITK to compare origin, direction and spacing between image and mask");
			textField_GeometryTolerance.setEnabled(false);
			textField_GeometryTolerance.setText("0.0");
			textField_GeometryTolerance.setColumns(10);
			chckbxCorrectMask = new JCheckBox("Correct Mask");
			chckbxCorrectMask.setToolTipText("PyRadiomics will attempt to resample the mask to the image geometry");
			chckbxCorrectMask.setEnabled(false);
			
			panel_ValidationElements.add(lblMinimumRoiDimension);
			panel_ValidationElements.add(spinner_minRoiDimension);
			panel_ValidationElements.add(lblMinimumRoiSize);
			panel_ValidationElements.add(spinner_minRoiSize);
			panel_ValidationElements.add(lblGeometryTolerance);
			panel_ValidationElements.add(textField_GeometryTolerance);
			panel_ValidationElements.add(chckbxCorrectMask);
			}
			
						
			{
			//////////////////////////////////////PANEL OTHER SETTINGS /////////////////////////////////////////////
			JPanel Other_Panel = new JPanel();
			Other_Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(Other_Panel);
			Other_Panel.setLayout(new BorderLayout(0, 0));
			
			JPanel Other_Title = new JPanel();
			Other_Title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			Other_Panel.add(Other_Title, BorderLayout.NORTH);
			
			JLabel lblOthers = new JLabel("Other");
			Other_Title.add(lblOthers);

			JPanel panel_OrtherSettings = new JPanel();
			Other_Panel.add(panel_OrtherSettings, BorderLayout.CENTER);
			panel_OrtherSettings.setLayout(new GridLayout(0, 1, 0, 0));
			
			JPanel panel_2DExtraction = new JPanel();
			panel_OrtherSettings.add(panel_2DExtraction);
			
			chckbxForce2DExtraction = new JCheckBox("Force 2D Extraction");
				chckbxForce2DExtraction.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent arg0) {
						if (chckbxForce2DExtraction.isSelected()) spinner_2D_Dimension.setEnabled(true);
						if (!chckbxForce2DExtraction.isSelected()) spinner_2D_Dimension.setEnabled(false);
					}
				});
			panel_2DExtraction.add(chckbxForce2DExtraction);	
					
			JLabel lblDimension2D = new JLabel("Dimension :");
			panel_2DExtraction.add(lblDimension2D);
		
			spinner_2D_Dimension = new JSpinner();
			spinner_2D_Dimension.setToolTipText("Specifies the \u2018slice\u2019 dimension for a by-slice feature extraction");
			spinner_2D_Dimension.setEnabled(false);
			spinner_2D_Dimension.setModel(new SpinnerNumberModel(0, 0, 2, 1));
			panel_2DExtraction.add(spinner_2D_Dimension);
					
			JPanel panel_Weighting = new JPanel();
			panel_OrtherSettings.add(panel_Weighting);
			
			JLabel lblTextureMatrixWeighting = new JLabel("Texture matrix weighting");
			panel_Weighting.add(lblTextureMatrixWeighting);
			
			comboBox_Weighting = new JComboBox<String>();
			comboBox_Weighting.setModel(new DefaultComboBoxModel<String>(new String[] {"manhattan", "euclidean", "infinity", "no_weighting", "None"}));
			comboBox_Weighting.setSelectedIndex(4);
			panel_Weighting.add(comboBox_Weighting);
								
			JLabel label = new JLabel("");
			panel_Weighting.add(label);
				
			JPanel panel_SetDistances = new JPanel();
			panel_OrtherSettings.add(panel_SetDistances);
			
			chckbxUseDistancesToNeighbour = new JCheckBox("Set Distances to neighbour");
			chckbxUseDistancesToNeighbour.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						if (chckbxUseDistancesToNeighbour.isSelected()) {
							txt_setDistances.setEnabled(true);
						}
						else {
							txt_setDistances.setEnabled(false);
						}
					}
				});
			panel_SetDistances.add(chckbxUseDistancesToNeighbour);
			
			txt_setDistances = new JTextField();
			txt_setDistances.setToolTipText(" List of integers specifies the distances between the center voxel and the neighbor");
			txt_setDistances.setEnabled(false);
			txt_setDistances.setText("0");
			panel_SetDistances.add(txt_setDistances);
			txt_setDistances.setColumns(10);

			JPanel panel_PreCropping = new JPanel();
			panel_OrtherSettings.add(panel_PreCropping);
			
			chckbxPrecropping = new JCheckBox("Pre-Cropping");
			panel_PreCropping.add(chckbxPrecropping);
			chckbxPrecropping.setHorizontalAlignment(SwingConstants.CENTER);
			}	
			
			{
				///////////////////////////////////////PANEL SPECIFIC SETTINGS /////////////////////////////////////////////
			JPanel panel_specificSettings = new JPanel();
			panel_specificSettings.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(panel_specificSettings);
			panel_specificSettings.setLayout(new BorderLayout(0, 0));
				
			JPanel panel_title = new JPanel();
			panel_title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			panel_specificSettings.add(panel_title, BorderLayout.NORTH);
				
			JLabel lblSpecificSettings = new JLabel("Specific Settings");
			panel_title.add(lblSpecificSettings);
						
			JPanel panel_SpecificSettings_Options = new JPanel();
			panel_specificSettings.add(panel_SpecificSettings_Options, BorderLayout.CENTER);
			panel_SpecificSettings_Options.setLayout(new GridLayout(0, 1, 0, 0));
			
			JPanel panel_SpecificSettingsPanel = new JPanel();
			panel_SpecificSettings_Options.add(panel_SpecificSettingsPanel);
			panel_SpecificSettingsPanel.setLayout(new GridLayout(0, 1, 0, 0));
							
			JPanel panel_specificFirstOrder = new JPanel();
			panel_SpecificSettingsPanel.add(panel_specificFirstOrder);				
			JLabel lblFirstOrder = new JLabel("First Order : ");
			panel_specificFirstOrder.add(lblFirstOrder);
			JLabel lblVoxelArrayShift = new JLabel("Voxel Array Shift");
			panel_specificFirstOrder.add(lblVoxelArrayShift);
			spinner_VoxelArrayShift = new JSpinner();
			spinner_VoxelArrayShift.setToolTipText("This amount is added to the gray level intensity in features Energy, Total Energy and RMS, this is to prevent negative values.");
			panel_specificFirstOrder.add(spinner_VoxelArrayShift);
			
			JPanel panel_SpecificSettingsGLCM = new JPanel();
			panel_SpecificSettings_Options.add(panel_SpecificSettingsGLCM);			
			JLabel lblGlcm = new JLabel("GLCM : ");
			panel_SpecificSettingsGLCM.add(lblGlcm);
			chckbxSymetricalGlcm = new JCheckBox("Symetrical GLCM");
			chckbxSymetricalGlcm.setToolTipText("indicates whether co-occurrences should be assessed in two directions per angle");
			panel_SpecificSettingsGLCM.add(chckbxSymetricalGlcm);
			chckbxSymetricalGlcm.setSelected(true);
			
			JPanel panel_SpecificSettingsGLDM = new JPanel();
			panel_SpecificSettings_Options.add(panel_SpecificSettingsGLDM);
			JLabel lblGldm = new JLabel("GLDM : ");
			panel_SpecificSettingsGLDM.add(lblGldm);
			
			JLabel lblAlfa = new JLabel("Alfa");
			panel_SpecificSettingsGLDM.add(lblAlfa);
				
			txt_setAlfa = new JTextField();
			txt_setAlfa.setToolTipText("\u03B1  cutoff value for dependence");
			txt_setAlfa.setText("0.0");
			panel_SpecificSettingsGLDM.add(txt_setAlfa);
			txt_setAlfa.setColumns(10);
				
			}
			
			{
			///////////////////////////////////////PANEL IMAGE TYPE /////////////////////////////////////////////
			JPanel ImageType_panel = new JPanel();
			ImageType_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(ImageType_panel);
			ImageType_panel.setLayout(new BorderLayout(0, 0));
			JPanel ImageType_Title = new JPanel();
			ImageType_Title.setBorder(new LineBorder(Color.LIGHT_GRAY));
			ImageType_panel.add(ImageType_Title, BorderLayout.NORTH);
				
			JLabel lblImageType = new JLabel("Image Type");
			ImageType_Title.add(lblImageType);
				
			JPanel ImageType_CheckBox = new JPanel();
			ImageType_panel.add(ImageType_CheckBox, BorderLayout.CENTER);
			
			ImageType_CheckBox.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			JPanel panel_2 = new JPanel();
			ImageType_CheckBox.add(panel_2);
			panel_2.setLayout(new GridLayout(0, 2, 0, 0));
			
			JCheckBox chckbxOriginal = new JCheckBox("Original");
			panel_2.add(chckbxOriginal);
			chckbxOriginal.setSelected(true);
			imageType.add(chckbxOriginal);
			JCheckBox chckbxLog = new JCheckBox("LoG");
			chckbxLog.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				if (chckbxLog.isSelected()) txtLoGSigma.setEnabled(true);
				else if (!chckbxLog.isSelected()) txtLoGSigma.setEnabled(false);
				}
			});
			panel_2.add(chckbxLog);
			imageType.add(chckbxLog);
			JCheckBox chckbxWavelet = new JCheckBox("Wavelet");
			chckbxWavelet.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if (chckbxWavelet.isSelected()) {
						spinner_startLevelWavelet.setEnabled(true);
						spinner_Wavelet_Level.setEnabled(true);
						txtWavelet.setEnabled(true);
					}
					else if (!chckbxWavelet.isSelected()) {
						spinner_startLevelWavelet.setEnabled(false);
						spinner_Wavelet_Level.setEnabled(false);
						txtWavelet.setEnabled(false);
					}
				}
			});
			panel_2.add(chckbxWavelet);
			imageType.add(chckbxWavelet);
			JCheckBox chckbxSquare = new JCheckBox("Square");
			panel_2.add(chckbxSquare);
			imageType.add(chckbxSquare);
			JCheckBox chckbxSquareroot = new JCheckBox("SquareRoot");
			panel_2.add(chckbxSquareroot);
			imageType.add(chckbxSquareroot);
			JCheckBox chckbxLogarithm = new JCheckBox("Logarithm");
			panel_2.add(chckbxLogarithm);
			imageType.add(chckbxLogarithm);
			JCheckBox chckbxExponential = new JCheckBox("Exponential");
			panel_2.add(chckbxExponential);
			imageType.add(chckbxExponential);
			
			chckbxGradient = new JCheckBox("Gradient");
			chckbxGradient.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					if(chckbxGradient.isSelected()) chckbxGradientSpacing.setEnabled(true);
					else chckbxGradientSpacing.setEnabled(false);
				}
			});
			panel_2.add(chckbxGradient);
			imageType.add(chckbxGradient);
			
			JPanel panel = new JPanel();
			ImageType_CheckBox.add(panel);
			panel.setLayout(new BorderLayout(0, 0));
			
			JPanel panel_Wavelet_Options = new JPanel();
			panel.add(panel_Wavelet_Options, BorderLayout.CENTER);
			panel_Wavelet_Options.setLayout(new GridLayout(0, 2, 0, 0));
			
			JLabel lblWavelet = new JLabel("Wavelet");
			panel_Wavelet_Options.add(lblWavelet);
			
			Component horizontalStrut = Box.createHorizontalStrut(20);
			panel_Wavelet_Options.add(horizontalStrut);
			
			JLabel lblStartLevel = new JLabel("Start Level");
			panel_Wavelet_Options.add(lblStartLevel);
			
			spinner_startLevelWavelet = new JSpinner();
			spinner_startLevelWavelet.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
			spinner_startLevelWavelet.setEnabled(false);
			panel_Wavelet_Options.add(spinner_startLevelWavelet);
			
			JLabel lblLevel = new JLabel("Level");
			panel_Wavelet_Options.add(lblLevel);
			
			spinner_Wavelet_Level = new JSpinner();
			spinner_Wavelet_Level.setModel(new SpinnerNumberModel(new Integer(1), new Integer(0), null, new Integer(1)));
			spinner_Wavelet_Level.setEnabled(false);
			panel_Wavelet_Options.add(spinner_Wavelet_Level);
			
			JLabel lblWavelet_1 = new JLabel("Wavelet");
			panel_Wavelet_Options.add(lblWavelet_1);
			
			txtWavelet = new JTextField();
			txtWavelet.setEnabled(false);
			txtWavelet.setText("coif1");
			panel_Wavelet_Options.add(txtWavelet);
			txtWavelet.setColumns(10);
			
			JPanel panel_Log_Options = new JPanel();
			panel.add(panel_Log_Options, BorderLayout.NORTH);
			
			JLabel lblLog = new JLabel("LoG");
			panel_Log_Options.add(lblLog);
			
			JLabel lblSigma = new JLabel("Sigma");
			panel_Log_Options.add(lblSigma);
			
			txtLoGSigma = new JTextField();
			txtLoGSigma.setToolTipText("List of floats or integers, must be greater than 0");
			txtLoGSigma.setEnabled(false);
			panel_Log_Options.add(txtLoGSigma);
			txtLoGSigma.setColumns(10);
			
			JPanel panel_Gradient_Options = new JPanel();
			panel.add(panel_Gradient_Options, BorderLayout.SOUTH);
			
			chckbxGradientSpacing = new JCheckBox("Gradient use spacing");
			chckbxGradientSpacing.setEnabled(false);
			panel_Gradient_Options.add(chckbxGradientSpacing);
			}
			
			{
			///////////////////////////////////////PANEL FEATURES /////////////////////////////////////////////
			JPanel Features_Panel = new JPanel();
			Features_Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			Settings_Panel.add(Features_Panel);
			Features_Panel.setLayout(new BorderLayout(0, 0));
			
			JPanel Enabled_Feature_Panel = new JPanel();
			Enabled_Feature_Panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
			Features_Panel.add(Enabled_Feature_Panel, BorderLayout.NORTH);
			
			JLabel lblEnabledFeatures = new JLabel("Enabled Features");
			Enabled_Feature_Panel.add(lblEnabledFeatures);
			
			JPanel Features_Options = new JPanel();
			Features_Panel.add(Features_Options, BorderLayout.CENTER);
			Features_Options.setLayout(new GridLayout(0, 3, 0, 0));
				
				
			chckbxFirstOrder = new JCheckBox("First Order");
			chckbxFirstOrder.setSelected(true);
			chckbxShape = new JCheckBox("Shape");
			chckbxShape.setSelected(true);
			chckbxGlcm = new JCheckBox("GLCM");
			chckbxGlcm.setSelected(true);
			chckbxGlrlm = new JCheckBox("GLRLM");
			chckbxGlrlm.setSelected(true);
			chckbxGlszm = new JCheckBox("GLSZM");
			chckbxGlszm.setSelected(true);
			chckbxNgtdm = new JCheckBox("NGTDM");
			chckbxNgtdm.setSelected(true);
			chckbxGtdm = new JCheckBox("GTDM");
			chckbxGtdm.setSelected(true);
			
			chckbxAdditionalInfo = new JCheckBox("Additional Info");
			chckbxAdditionalInfo.setSelected(true);
			Features_Options.add(chckbxAdditionalInfo);
			
			
			Features_Options.add(chckbxFirstOrder);
			Features_Options.add(chckbxShape);
			Features_Options.add(chckbxGlcm);
			Features_Options.add(chckbxGlrlm);
			Features_Options.add(chckbxGlszm);
			Features_Options.add(chckbxNgtdm);
			Features_Options.add(chckbxGtdm);
			
			}
		
			{
			this.pack();
			try {
				this.setIconImage(ImageIO.read(getClass().getResource("/logo/logo_pyRadiomics.png")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			this.setSize(this.getPreferredSize());
			}
			this.gui=this;
		
		
	}
	
	private void enableNormalize(boolean enable) {
		if (enable) {
			txtNormalizeScale.setEnabled(true);
			txtRemoveOutliners.setEnabled(true);
		}
		else {
			txtNormalizeScale.setEnabled(false);
			txtRemoveOutliners.setEnabled(false);
		}
	}
	
	private void enableResample(boolean enable) {
		if (enable) {
			txtXPixelSpacing.setEnabled(true);
			txtYPixelSpacing.setEnabled(true);
			txtZPixelSpacing.setEnabled(true);
			spinner_padDistance.setEnabled(true);
			comboBox_Interpolator.setEnabled(true);
		}
		else {
			txtXPixelSpacing.setEnabled(false);
			txtYPixelSpacing.setEnabled(false);
			txtZPixelSpacing.setEnabled(false);
			spinner_padDistance.setEnabled(false);
			comboBox_Interpolator.setEnabled(false);
		}
	}
	
	private void enableMaskValidate(boolean validate) {
		if (validate) {
			textField_GeometryTolerance.setEnabled(true);
			spinner_minRoiDimension.setEnabled(true);
			spinner_minRoiSize.setEnabled(true);
			chckbxCorrectMask.setEnabled(true);
		}
		else {
			textField_GeometryTolerance.setEnabled(false);
			spinner_minRoiDimension.setEnabled(false);
			spinner_minRoiSize.setEnabled(false);
			chckbxCorrectMask.setEnabled(false);
		}
	}
	
	public Double getfixedBinWidth(){
		return Double.parseDouble(txtFixedBinWidth.getText());
	}
	
	public void setfixedBinWidth(double binWidth) {
		txtFixedBinWidth.setText(String.valueOf(binWidth)); 
	}
	
	public double getResegmentMin(){
		 return Double.parseDouble(txtResegmentationMin.getText());
	}
	
	public double getResegmentMax(){
		return Double.parseDouble(txtResegmentationMax.getText());
	}
	
	public void setResegment(double min, double max) {
		txtResegmentationMin.setText(String.valueOf(min));
		txtResegmentationMax.setText(String.valueOf(max));
	}
	
	public double getNormalizeScale() {
		return Double.parseDouble(txtNormalizeScale.getText());
	}
	
	public void setNormalizeScale(double normalizeScale) {
		txtNormalizeScale.setText(String.valueOf(normalizeScale));
	}
	
	public double getRemoveOutliners() {
		return Double.parseDouble(txtRemoveOutliners.getText());
	}
	
	public void setRemoveOutliners(double outliners) {
		txtRemoveOutliners.setText(String.valueOf(outliners));
	}
	
	public double[] getResamplePixelSpacing() {
		double[] pixelSpacing=new double[3];
		pixelSpacing[0] = Double.parseDouble(txtXPixelSpacing.getText());
		pixelSpacing[1] = Double.parseDouble(txtYPixelSpacing.getText());
		pixelSpacing[2] = Double.parseDouble(txtZPixelSpacing.getText());
		return pixelSpacing;
	}
	
	public void setResamplePixelSpacing(double[] pixelSpacing) {
		txtXPixelSpacing.setText( String.valueOf(pixelSpacing[0]) );
		txtYPixelSpacing.setText( String.valueOf(pixelSpacing[1]) );
		txtZPixelSpacing.setText( String.valueOf(pixelSpacing[2]) );
	}
	
	public void setInterpolator(String interpolator) {
		comboBox_Interpolator.setSelectedItem(interpolator);
	}
	
	public double getGeometryTolerance() {
		return Double.parseDouble(textField_GeometryTolerance.getText());
	}
	
	public String getWeighting() {
		return (String) comboBox_Weighting.getSelectedItem();
	}
	
	public void setWeighting(String weighting) {
		comboBox_Weighting.setSelectedItem(weighting); 
	}
	
	
	public double getAlfaGLDM() {
		return Double.parseDouble(txt_setAlfa.getText());
	}
	
	public void setAlfaGLDM(double alpha) {
		txt_setAlfa.setText(String.valueOf(alpha));
	}
	
	public File getSettingFile(){
		return settingsFile;
	}
	
	public HashMap<String, Boolean> getSelectedFeatures(){
		HashMap<String, Boolean> features=new HashMap<String, Boolean>();
		features.put("Additional Info", chckbxAdditionalInfo.isSelected());
		features.put("First Order", chckbxFirstOrder.isSelected());
		features.put("Shape", chckbxShape.isSelected());
		features.put("GLCM", chckbxGlcm.isSelected());
		features.put("GLRLM", chckbxGlrlm.isSelected());
		features.put("GLSZM", chckbxGlszm.isSelected());
		features.put("NGTDM", chckbxNgtdm.isSelected());
		features.put("GLDM", chckbxGtdm.isSelected());
		return features;
	}
	
	public HashMap<String, Boolean> getImageType() {
		HashMap<String, Boolean> imageType=new HashMap<String, Boolean>();
		imageType.put("typeOriginal", this.imageType.get(0).isSelected());
		imageType.put("typeLoG", this.imageType.get(1).isSelected());
		imageType.put("typeWavelet", this.imageType.get(2).isSelected());
		imageType.put("typeSquare", this.imageType.get(3).isSelected());
		imageType.put("typeSquareRoot", this.imageType.get(4).isSelected());
		imageType.put("typeLogarithm", this.imageType.get(5).isSelected());
		imageType.put("typeExponential", this.imageType.get(6).isSelected());
		imageType.put("typeGradient", this.imageType.get(7).isSelected());
		return  imageType;
	}
	
	public void setSelectedFeatures(HashMap<String, Boolean> features){
		if (!features.get("Additional Info")) chckbxAdditionalInfo.setSelected(false);
		if (!features.get("First Order")) chckbxFirstOrder.setSelected(false);
		if (!features.get("Shape")) chckbxShape.setSelected(false);
		if (!features.get("GLCM")) chckbxGlcm.setSelected(false);
		if (!features.get("GLRLM")) chckbxGlrlm.setSelected(false);
		if (!features.get("GLSZM")) chckbxGlszm.setSelected(false);
		if (!features.get("NGTDM")) chckbxNgtdm.setSelected(false);
		if (!features.get("GLDM")) chckbxGtdm.setSelected(false);
		
	}
	
	public void setImageType(HashMap<String, Boolean> imageType) {
		if (imageType.get("typeOriginal")) this.imageType.get(0).setSelected(true);
		if (imageType.get("typeLoG")) this.imageType.get(1).setSelected(true);
		if (imageType.get("typeWavelet")) this.imageType.get(2).setSelected(true);
		if (imageType.get("typeSquare")) this.imageType.get(3).setSelected(true);
		if (imageType.get("typeSquareRoot")) this.imageType.get(4).setSelected(true);
		if (imageType.get("typeLogarithm")) this.imageType.get(5).setSelected(true);
		if (imageType.get("typeExponential")) this.imageType.get(6).setSelected(true);
		if (imageType.get("typeGradient")) this.imageType.get(7).setSelected(true);
	}

}
