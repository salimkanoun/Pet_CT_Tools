

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
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;

import ij.IJ;
import ij.Macro;
import ij.WindowManager;
import ij.plugin.PlugIn;
import sc.fiji.io.Nrrd_Writer;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class Radiomics_Frame extends JFrame implements PlugIn {

	private File imageFile;
	private File maskFile;
	private JLabel lblStatusIdle; 
	private JTextField roiNumberString;
	private List<Integer> label;
	private Preferences jPrefer = null;

	/////Option Variables ////
	private boolean optionSet=false;
	//Discretization
	private boolean fixedbin;
	private boolean useFixedBinPerRoi;
	private Double binWidth;
	private boolean resegmentLimit;
	private double min;
	private double max;
	private int fixedBinPerRoi;
	//Normalize
	private boolean normalize;
	private double normalizeScale, removeOutliners;
	//Resampling
	private boolean resample;
	private double[] pixelSpacing=new double[3];
	private String interpolator;
	private int padDistance;
	//Validate
	private boolean validateMask;
	private int minRoiDimension, minRoiSize;
	private double geometryTolerance;
	private boolean correctMask;
	//Others
	private boolean isForce2DExtraction;
	private boolean isDistance;
	private int Dimension2D;
	private String matrixWeighting;
	private String distanceNeighbour;
	private boolean preCroping;
	//Specific settings
	private int voxelArrayShift;
	private boolean symetricalGLCM;
	private double alfa;
	//Log and Wavelet Params
	private String logSigma, waveletString;
	private int waveletStart,waveletLevel;
	//Gradient param
	private boolean useGradientSpacing;
	//ImageType
	private HashMap<String, Boolean> imageType = new HashMap<String, Boolean>();
	
	private File fileSettingsOption;
	private HashMap<String, Boolean> featureSelection=new HashMap<String, Boolean>();
	
	private OptionsRadiomics option;
	
	//Main Frame buttons
	private JButton btnImagejStack, btnSetImage, btnSetMask, btnImagejMaskStack ;
	
	/**
	 * Create the frame.
	 */
	public Radiomics_Frame() {
		//Set Jprefer for storing settings
		jPrefer = Preferences.userNodeForPackage(this.getClass());
		jPrefer = jPrefer.node("pyRadiomicsGUI");
		//Charge donnees si existe
		loadSettingsFromRegistery();
		//builing GUI
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("PyRadiomics");
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel Button_panel = new JPanel();
		Button_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(Button_panel, BorderLayout.SOUTH);
		
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						calculate();
						return null;
					}
					@Override
					protected void done(){
						lblStatusIdle.setText("<html><font color='green'>Done.</font></html>");
					}
					
				};
				worker.execute();
			}
		});
		Button_panel.add(btnCalculate);
		
		JButton btnSetSettings = new JButton("Set settings");
		btnSetSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				option=new OptionsRadiomics();
				option.setLocationRelativeTo(null);
				if (optionSet){
					setOptions();
				}
				option.setModal(true);
				option.setVisible(true);
				if (option.ok) {
					getOptions();
				}
				
			}
		});
		Button_panel.add(btnSetSettings);
		
		lblStatusIdle = new JLabel("Status : Idle");
		Button_panel.add(lblStatusIdle);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.BLACK));
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		
		JLabel lblImage = new JLabel("Image");
		panel.add(lblImage);
		
		btnSetImage = new JButton("Image File");
		panel.add(btnSetImage);
		btnSetImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//Set current directory if already define one
				if (maskFile !=null) fileChooser.setSelectedFile(maskFile);
				if (imageFile !=null) fileChooser.setSelectedFile(imageFile);
				int ouvrir=fileChooser.showOpenDialog(null);
				if(ouvrir==JFileChooser.APPROVE_OPTION) {
				imageFile=fileChooser.getSelectedFile();
				disableImageButton(true);
				}
			}
		});
		
		btnSetMask = new JButton("Mask File");
		btnSetMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (imageFile !=null) fileChooser.setSelectedFile(imageFile);
				if (maskFile !=null)fileChooser.setSelectedFile(maskFile);
				int ouvrir=fileChooser.showOpenDialog(null);
				if(ouvrir==JFileChooser.APPROVE_OPTION) {
				maskFile=fileChooser.getSelectedFile();
				disableMaskButton(true);
				}
			}
		});
		
		btnImagejStack = new JButton("ImageJ Image Stack");
		btnImagejStack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Export stack in NRRD in temporary file and store it's file location
				Nrrd_Writer writer = new Nrrd_Writer();
				try {
					Path folder = Files.createTempDirectory("pyRadiomics_");
					File image=new File(folder.toAbsolutePath()+File.separator+"image.nrrd");
					writer.save(WindowManager.getCurrentImage(), image.getAbsolutePath().toString());
					image.deleteOnExit();
					folder.toFile().deleteOnExit();
					imageFile=image;
					disableImageButton(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		panel.add(btnImagejStack);
		
		JLabel lblMask = new JLabel("Mask");
		panel.add(lblMask);
		panel.add(btnSetMask);
		
		btnImagejMaskStack = new JButton("ImageJ Mask Stack");
		btnImagejMaskStack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Export stack in NRRD in temporary file and store it's file location
				Nrrd_Writer writer = new Nrrd_Writer();
				try {
					Path folder = Files.createTempDirectory("pyRadiomics_");
					File mask=new File(folder.toAbsolutePath()+File.separator+"image.nrrd");
					writer.save(WindowManager.getCurrentImage(), mask.getAbsolutePath().toString());
					maskFile=mask;
					mask.deleteOnExit();
					folder.toFile().deleteOnExit();
					disableMaskButton(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnImagejMaskStack);
		
		JLabel lblRoiNumber = new JLabel("Roi number");
		panel.add(lblRoiNumber);
		
		roiNumberString = new JTextField("1");
		panel.add(roiNumberString);
		roiNumberString.setToolTipText("ex: 1;5;10-15");
		roiNumberString.setColumns(3);
		
		JPanel Title_Panel = new JPanel();
		Title_Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(Title_Panel, BorderLayout.NORTH);
		
		JLabel lblPyradiomicsCalculation = new JLabel("PyRadiomics Calculation");
		Title_Panel.add(lblPyradiomicsCalculation);
		
		this.setLocationRelativeTo(null);
		try {
			this.setIconImage(ImageIO.read(getClass().getResource("/logo/logo_pyRadiomics.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pack();
		this.setSize(600,200);
		
	}
	
	/**
	 * Parse label string to calculate
	 */
	private void parseRoiNumberString(){
		label = new ArrayList<Integer>();
		String input=roiNumberString.getText();
		input+=";";
		ArrayList<String> groups=new ArrayList<String>();
		int index = input.indexOf(";");
		while (index >= 0) {
		    groups.add(input.substring(0, index));
		    input=input.substring(index+1);
		    index = input.indexOf(";");
		}
		for (int i=0; i<groups.size();i++){
			String group=groups.get(i);
			if (group.indexOf("-")!=(-1)){
				int start=Integer.parseInt(group.substring(0, group.indexOf("-")));
				int stop=Integer.parseInt(group.substring(group.indexOf("-")+1));
				 for(int j=start;j<=stop;j++) {
					 label.add(j);
					 }
			}
			else {
				label.add(Integer.parseInt(group));
			}
		}
	}
	
	private void calculate(){
		try {
			//Choisi destination du CSV
			StringBuilder csv=new StringBuilder();
			//building Radiomics object with awaiting results
			Radiomics radiomics=new Radiomics(/*imageType*/);
			//radiomics.addColumnheader(csv);
			parseRoiNumberString();
			File settings=null;
			JsonObject json=null;
			if (label.size()!=0){
					for (int i=0; i<label.size();i++){
						//Display info
						lblStatusIdle.setText("Calculating ROI "+(i+1)+"/"+label.size());
						
						//Generate file settings if not defined in options
						if ( fileSettingsOption==null && optionSet && imageFile!=null && maskFile!=null)  {
							settings=radiomics.writeYaml(useFixedBinPerRoi, fixedBinPerRoi, binWidth,validateMask, this.minRoiDimension, this.minRoiSize, this.geometryTolerance, this.correctMask, label.get(i),normalize, this.normalizeScale, this.removeOutliners, resample, pixelSpacing, interpolator, padDistance, isForce2DExtraction, Dimension2D, isDistance, distanceNeighbour, resegmentLimit, min, max, preCroping, logSigma, waveletStart, waveletLevel, waveletString, useGradientSpacing,  voxelArrayShift, symetricalGLCM, matrixWeighting, alfa, imageType, featureSelection);
							json=radiomics.sendPyRadiomics(imageFile.getAbsolutePath().toString(), maskFile.getAbsolutePath().toString(), settings);
						}
						
						//Use defined YAML file settings if defined in options
						else if (fileSettingsOption!=null && imageFile!=null && maskFile!=null){
							json=radiomics.sendPyRadiomics(imageFile.getAbsolutePath().toString(), maskFile.getAbsolutePath().toString(), fileSettingsOption);
						}
						
						//Settings by default if not set
						else if(!optionSet && imageFile!=null && maskFile!=null) {
							settings=radiomics.writeDefaultYaml(label.get(i));
							json=radiomics.sendPyRadiomics(imageFile.getAbsolutePath().toString(), maskFile.getAbsolutePath().toString(), settings);
						}
						
						else if (imageFile==null || maskFile==null) {
							JOptionPane.showMessageDialog(null,"Missing Image or Mask File");
						}
					//build CSV
					if (json.size()!=0) {
						if (i==0) radiomics.jsonToCsv(csv, json, label.get(i), true);
						else radiomics.jsonToCsv(csv, json, label.get(i), false);
					}
					
					}
				//Select CSV destination
				JFileChooser fileChooser=new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setSelectedFile(new File("radiomics.csv"));
				int ouvrir=fileChooser.showSaveDialog(null);
				if(ouvrir==JFileChooser.APPROVE_OPTION) {
				radiomics.writeCSV(csv, fileChooser.getSelectedFile().getAbsolutePath().toString());
				//Empty the list of ROI for the next calls
				label.clear();
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void saveSettingsInRegistery() {
		//Settings have been set once
		jPrefer.putBoolean("optionSet", true);
		//Add settings
		jPrefer.putBoolean("fixedbin", fixedbin);
		jPrefer.putBoolean("resegmentPerRoi", useFixedBinPerRoi);
		jPrefer.putDouble("binWidth", binWidth);
		jPrefer.putBoolean("resegmentLimit", resegmentLimit);
		jPrefer.putDouble("min", min);
		jPrefer.putDouble("max", max);
		jPrefer.putInt("fixedBinPerRoi",fixedBinPerRoi);
		jPrefer.putBoolean("normalize", normalize);
		jPrefer.putDouble("normalizeScale", normalizeScale);
		jPrefer.putDouble("removeOutliners", removeOutliners);
		jPrefer.putBoolean("resample",resample);
		jPrefer.putDouble("pixelSpacing0", pixelSpacing[0]);
		jPrefer.putDouble("pixelSpacing1", pixelSpacing[1]);
		jPrefer.putDouble("pixelSpacing2", pixelSpacing[2]);
		jPrefer.put("interpolator", interpolator);
		jPrefer.putInt("padDistance", padDistance);
		jPrefer.putBoolean("validateMask", validateMask);
		jPrefer.putInt("minRoiDimension", minRoiDimension);
		jPrefer.putInt("minRoiSize", minRoiSize);
		jPrefer.putDouble("geometryTolerance", geometryTolerance);
		jPrefer.putBoolean("correctMask", correctMask);
		jPrefer.putBoolean("isForce2DExtraction", isForce2DExtraction);
		jPrefer.putBoolean("isDistance", isDistance);
		jPrefer.putInt("Dimension2D", Dimension2D);
		jPrefer.put("matrixWeighting", matrixWeighting);
		jPrefer.put("distanceNeighbour", distanceNeighbour);
		jPrefer.putBoolean("preCroping", preCroping);
		jPrefer.putInt("voxelArrayShift", voxelArrayShift);
		jPrefer.putBoolean("symetricalGLCM", symetricalGLCM);
		jPrefer.putDouble("alfa", alfa);
		jPrefer.put("logSigma", logSigma);
		jPrefer.put("waveletString", waveletString);
		jPrefer.putInt("waveletStart", waveletStart);
		jPrefer.putInt("waveletLevel", waveletLevel);
		jPrefer.putBoolean("useGradientSpacing", useGradientSpacing );
		jPrefer.putBoolean("typeOriginal", imageType.get("typeOriginal"));
		jPrefer.putBoolean("typeLoG", imageType.get("typeLoG"));
		jPrefer.putBoolean("typeWavelet", imageType.get("typeWavelet"));
		jPrefer.putBoolean("typeSquare", imageType.get("typeSquare"));
		jPrefer.putBoolean("typeSquareRoot", imageType.get("typeSquareRoot"));
		jPrefer.putBoolean("typeLogarithm", imageType.get("typeLogarithm"));
		jPrefer.putBoolean("typeExponential", imageType.get("typeExponential"));
		jPrefer.putBoolean("typeGradient", imageType.get("typeGradient"));
		
		String[] features=new String[featureSelection.size()];
		featureSelection.keySet().toArray(features);
		for (int i=0; i<features.length; i++){
			jPrefer.putBoolean(features[i], (boolean) featureSelection.get(features[i]));
		}
		
		
	}
	
	private void loadSettingsFromRegistery() {
		
		optionSet=jPrefer.getBoolean("optionSet", false);
		if (optionSet) {
			fixedbin=jPrefer.getBoolean("fixedbin", false);
			useFixedBinPerRoi=jPrefer.getBoolean("resegmentPerRoi", false);
			binWidth=jPrefer.getDouble("binWidth", 0);
			resegmentLimit=jPrefer.getBoolean("resegmentLimit", false);
			min=jPrefer.getDouble("min", 0);
			max=jPrefer.getDouble("max", 0);
			fixedBinPerRoi=jPrefer.getInt("fixedBinPerRoi",0);
			normalize=jPrefer.getBoolean("normalize", false);
			normalizeScale=jPrefer.getDouble("normalizeScale", 0);
			removeOutliners=jPrefer.getDouble("removeOutliners", 0);
			resample=jPrefer.getBoolean("resample",false);
			pixelSpacing[0]=jPrefer.getDouble("pixelSpacing0", 0);
			pixelSpacing[1]=jPrefer.getDouble("pixelSpacing1", 0);
			pixelSpacing[2]=jPrefer.getDouble("pixelSpacing2", 0);
			interpolator=jPrefer.get("interpolator", "");
			padDistance=jPrefer.getInt("padDistance", 0);
			validateMask=jPrefer.getBoolean("validateMask", false);
			minRoiDimension=jPrefer.getInt("minRoiDimension", 0);
			minRoiSize=jPrefer.getInt("minRoiSize", 0);
			geometryTolerance=jPrefer.getDouble("geometryTolerance", 0);
			correctMask=jPrefer.getBoolean("correctMask", false);
			isForce2DExtraction=jPrefer.getBoolean("isForce2DExtraction", false);
			isDistance=jPrefer.getBoolean("isDistance", false);
			Dimension2D=jPrefer.getInt("Dimension2D", 0);
			matrixWeighting=jPrefer.get("matrixWeighting", "");
			distanceNeighbour=jPrefer.get("distanceNeighbour", "");
			preCroping=jPrefer.getBoolean("preCroping", false);
			voxelArrayShift=jPrefer.getInt("voxelArrayShift", 0);
			symetricalGLCM=jPrefer.getBoolean("symetricalGLCM", false);
			alfa=jPrefer.getDouble("alfa", 0);
			logSigma=jPrefer.get("logSigma", "");
			waveletString=jPrefer.get("waveletString", "");
			waveletStart=jPrefer.getInt("waveletStart", 0);
			waveletLevel=jPrefer.getInt("waveletLevel", 0);
			useGradientSpacing=jPrefer.getBoolean("useGradientSpacing", true);
			
			//Poplation imageType
			imageType.put("typeOriginal", jPrefer.getBoolean("typeOriginal", false));
			imageType.put("typeLoG", jPrefer.getBoolean("typeLoG", false));
			imageType.put("typeWavelet", jPrefer.getBoolean("typeWavelet", false));
			imageType.put("typeSquare", jPrefer.getBoolean("typeSquare", false));
			imageType.put("typeSquareRoot", jPrefer.getBoolean("typeSquareRoot", false));
			imageType.put("typeLogarithm", jPrefer.getBoolean("typeLogarithm", false));
			imageType.put("typeExponential", jPrefer.getBoolean("typeExponential", false));
			imageType.put("typeGradient", jPrefer.getBoolean("typeGradient", false));
			
			
			//population feature list
			featureSelection.put("Additional Info", jPrefer.getBoolean("Additional Info", true));
			featureSelection.put("First Order", jPrefer.getBoolean("First Order", true));
			featureSelection.put("Shape", jPrefer.getBoolean("Shape", true));
			featureSelection.put("GLCM", jPrefer.getBoolean("GLCM", true));
			featureSelection.put("GLRLM", jPrefer.getBoolean("GLRLM", true));
			featureSelection.put("GLSZM", jPrefer.getBoolean("GLSZM", true));
			featureSelection.put("NGTDM", jPrefer.getBoolean("NGTDM", true));
			featureSelection.put("GLDM", jPrefer.getBoolean("GLDM", true));
		}
			
		
	}
	/**
	 * Load option data from panel option in this main class
	 */
	private void getOptions() {
		fixedbin=option.chckbxEnableFixedBin.isSelected();
		binWidth=option.getfixedBinWidth();
		resegmentLimit=option.chckbxResegmentation.isSelected();
		min=option.getResegmentMin();
		max=option.getResegmentMax();
		useFixedBinPerRoi=option.chckbxEnableFixedBin.isSelected();
		fixedBinPerRoi=(int) option.spinnerBinFixed.getValue();
		//Get the Normalize settings
		normalize=option.checkBoxNormalize.isSelected();
		normalizeScale=option.getNormalizeScale();
		removeOutliners=option.getRemoveOutliners();
		//Get ImageResampling 
		resample=option.chckbxResampleImage.isSelected();
		pixelSpacing=option.getResamplePixelSpacing();
		interpolator=(String) option.comboBox_Interpolator.getSelectedItem();
		padDistance=(int) option.spinner_padDistance.getValue();
		// Get Validate Mask
		validateMask=option.chckbxValidateMask.isSelected();
		minRoiDimension=(int) option.spinner_minRoiDimension.getValue();
		minRoiSize=(int) option.spinner_minRoiSize.getValue();
		geometryTolerance=option.getGeometryTolerance();
		correctMask=option.chckbxCorrectMask.isSelected();
		//Get others
		isForce2DExtraction=option.chckbxForce2DExtraction.isSelected();
		Dimension2D=(int) option.spinner_2D_Dimension.getValue();
		matrixWeighting=option.getWeighting();
		isDistance=option.chckbxUseDistancesToNeighbour.isSelected();
		distanceNeighbour=option.txt_setDistances.getText();
		preCroping=option.chckbxPrecropping.isSelected();
		//Get Specific Settings 
		voxelArrayShift=(int) option.spinner_VoxelArrayShift.getValue();
		symetricalGLCM=option.chckbxSymetricalGlcm.isSelected();
		alfa=option.getAlfaGLDM();
		//Get file settings
		fileSettingsOption=option.getSettingFile();
		//Get the feature Selection
		featureSelection=option.getSelectedFeatures();
		//Get Log And Wavelet Parameters
		logSigma=option.txtLoGSigma.getText();
		waveletStart=(int) option.spinner_startLevelWavelet.getValue();
		waveletLevel=(int) option.spinner_Wavelet_Level.getValue();
		waveletString=option.txtWavelet.getText();
		useGradientSpacing=option.chckbxGradientSpacing.isSelected();
		//get ImageType
		imageType=option.getImageType();
		
		//Save all in the registery
		this.optionSet=true;
		saveSettingsInRegistery();
		
	}
	
	private void setOptions() {
		option.chckbxEnableFixedBin.setSelected(fixedbin);
		option.setfixedBinWidth(binWidth);
		option.chckbxResegmentation.setSelected(resegmentLimit);
		option.setResegment(min, max);
		option.chckbxEnableFixedBin.setSelected(useFixedBinPerRoi);
		option.spinnerBinFixed.setValue(fixedBinPerRoi);
		option.checkBoxNormalize.setSelected(normalize);
		option.setNormalizeScale(normalizeScale);
		option.setRemoveOutliners(removeOutliners);
		option.chckbxResampleImage.setSelected(resample);
		option.setResamplePixelSpacing(pixelSpacing);
		option.setInterpolator(interpolator);
		option.spinner_padDistance.setValue((int) padDistance);
		option.chckbxValidateMask.setSelected(validateMask);
		option.spinner_minRoiDimension.setValue(minRoiDimension); 
		option.spinner_minRoiSize.setValue(minRoiSize);
		option.textField_GeometryTolerance.setText(String.valueOf(geometryTolerance));
		option.chckbxCorrectMask.setSelected(correctMask);
		option.chckbxForce2DExtraction.setSelected(isForce2DExtraction);
		option.spinner_2D_Dimension.setValue(Dimension2D);
		option.setWeighting(matrixWeighting);
		option.chckbxUseDistancesToNeighbour.setSelected(isDistance);
		option.txt_setDistances.setText(distanceNeighbour);
		option.chckbxPrecropping.setSelected(preCroping);
		option.spinner_VoxelArrayShift.setValue(voxelArrayShift);
		option.chckbxSymetricalGlcm.setSelected(symetricalGLCM);
		option.setAlfaGLDM(alfa);
		option.setSelectedFeatures(featureSelection);
		option.txtLoGSigma.setText(logSigma);
		option.spinner_startLevelWavelet.setValue(waveletStart);
		option.spinner_Wavelet_Level.setValue(waveletLevel);
		option.txtWavelet.setText(waveletString);
		option.chckbxGradientSpacing.setSelected(useGradientSpacing);
		option.setImageType(imageType);
		
		
	}
	
	private void disableImageButton(boolean file){
		// if set by file disable the ImageJ button
		if (file){
			btnImagejStack.setEnabled(false);
			btnSetImage.setBackground(Color.GREEN);
			btnSetImage.setOpaque(true);
			
		}
		else{
			btnImagejStack.setBackground(Color.GREEN);
			btnImagejStack.setOpaque(true);
			btnSetImage.setEnabled(false);
		}
	}
	
	private void disableMaskButton(boolean file){
		if (file){
			btnImagejMaskStack.setEnabled(false);
			btnSetMask.setBackground(Color.GREEN);
			btnSetMask.setOpaque(true);
			
		}
		else{
			btnImagejMaskStack.setBackground(Color.GREEN);
			btnImagejMaskStack.setOpaque(true);
			btnSetMask.setEnabled(false);
		}
		
	}

	/**
	 * ImageJ Run methods
	 */
	@Override
	public void run(String arg0) {
		String argumentString = Macro.getOptions();
		Radiomics_Frame frame = new Radiomics_Frame();
		frame.setVisible(true);
		//If argument parse them to load mask and image file
		if (!StringUtils.isEmpty(argumentString)) {
			argumentString=argumentString.trim();
			int separator=argumentString.indexOf(";");
			frame.imageFile= new File(argumentString.substring(0, separator));
			frame.maskFile= new File(argumentString.substring(separator+1));
			frame.disableImageButton(true);
			frame.disableMaskButton(true);
			IJ.log(argumentString+"_"+frame.imageFile.getAbsolutePath().toString()+"=="+frame.maskFile.getAbsolutePath().toString()+"fin");
		}
		//Example of running Macro
		//run("pyRadiomics", "/home/salim/Nifti/Repertoire Espace/_float.nii;/home/salim/Nifti/Repertoire Espace/_mask.nii");
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Radiomics_Frame frame = new Radiomics_Frame();
					//Disable ImageJ button for Stack reading in case of standalone run
					frame.btnImagejStack.setVisible(false);
					frame.btnImagejMaskStack.setVisible(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
