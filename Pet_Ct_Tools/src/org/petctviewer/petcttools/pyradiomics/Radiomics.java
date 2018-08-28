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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Radiomics {
	String[] resultsTitle;
	/**
	 * constructor with awaiting labels
	 */
	public Radiomics() {
	}
	
	
	/**
	 * Send pyRadiomics Requests and return result as JSONObject
	 * @param image
	 * @param mask
	 * @param settingsYaml
	 * @return
	 * @throws IOException
	 */
	public JsonObject sendPyRadiomics(String image, String mask, File settingsYaml) throws IOException {
		String json;
		
		ProcessBuilder pb = new ProcessBuilder("pyradiomics", image , mask ,"--format", "json", "--param", settingsYaml.getAbsolutePath().toString());
		pb.redirectErrorStream(true); 
        Process process = pb.start();  
        //OutputStream stdin = process.getOutputStream(); 
        //InputStream stderr = process.getErrorStream(); 
        InputStream stdout = process.getInputStream();  
		 
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));

		 StringBuilder builder = new StringBuilder();
		 String line = null;
		 JsonObject resultsJson=null;
		 while ( (line = reader.readLine()) != null) {
			 	//If JSON Object parse it
			 	if (line.startsWith("{")){
			 		json=line;
			 		json=json.replace("NaN", "\"NaN\"");
			 		resultsJson=readJson(json);
			 	}
			 	//if not add to string builder and log it
			 	else{
			 		builder.append(line);
			 		builder.append(System.getProperty("line.separator"));
			 	}
			 	
		 }
		 process.destroy();
		 JOptionPane.showMessageDialog(null, builder.toString(),"info", JOptionPane.INFORMATION_MESSAGE);
		 System.out.println(resultsJson.toString());
		 
		 return resultsJson;
	}
	/**
	 * Test if pyRadiomics is reachable in the OS.
	 */
	public static void testPyRadiomics()  {
		
		ProcessBuilder pb = new ProcessBuilder("pyradiomics", "--version");
		pb.environment();
		pb.redirectErrorStream(true); 
		BufferedReader reader = null;
        Process process = null;
        
		try {
			process = pb.start();
			InputStream stdout = process.getInputStream();
			reader = new BufferedReader (new InputStreamReader(stdout));
		} catch (IOException e) {
			e.printStackTrace(); 
			
		}  
  
		StringBuilder builder = new StringBuilder();
		String line = null;
		 try {
			while ( (line = reader.readLine()) != null) {
				 		builder.append(line);
				 		builder.append(System.getProperty("line.separator"));
				 	
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		process.destroy();
		
		if (builder.toString().startsWith("pyradiomics")) {
			JOptionPane.showMessageDialog(null,
				    "pyRadiomics OK "+builder.toString());
		} else {
			JOptionPane.showMessageDialog(null,
				    "pyRadiomics is not found, install it on your system. \n Visit pyRadiomics.io or petctviewer.org \n error"+ builder.toString(),
				    "Not Found",
				    JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * Parse JsonString to JsonObject
	 * @param json
	 * @return
	 */
	private JsonObject readJson(String json) {
		JsonObject resultsJson=null; 
		
		JsonParser parser = new JsonParser();
				
		resultsJson= parser.parse(json).getAsJsonObject();
		
		 return resultsJson;
	}

	/**
	 * Add column Title to CSV that will be generated
	 * @param csv
	 */
	private void addColumnheader(StringBuilder csv, String[] resultsTitle){
		//Add Roi Column Title
		csv.append("ROI number,");
		for (int i=0; i<resultsTitle.length; i++) {
			csv.append(resultsTitle[i]+",");
		}
		csv.append("\n");
	}
	/**
	 * Write JSON response into CSV format
	 * @param csv
	 * @param resultsJson
	 * @param roiNumber
	 */
	public void jsonToCsv(StringBuilder csv, JsonObject resultsJson, int roiNumber, boolean maketitle) {
		
		if (maketitle) {
			resultsTitle=new String[resultsJson.size()];
			resultsJson.keySet().toArray(resultsTitle);
			Arrays.sort(resultsTitle);
			addColumnheader(csv , resultsTitle);
		}
		// SK EXCEL HAS LIMITATION OF NUMBER OF CHARACTER IN A CELL IF TOO MUCH RADIOMICS PARAMETER RESULTS SPLITED IN 2 LINES
		//CONSIDER MOVING TO ANOTHER FILE FORMAT
		
		//Add ROI number results
		csv.append(roiNumber+",");
		//Write results one by one
		for (int i=0; i<resultsTitle.length; i++) {
			if (resultsJson.has(resultsTitle[i])){
				String value=resultsJson.get(resultsTitle[i]).toString();
				//replace comma by semicolon to avoid breaking CSV structure
				value=value.replaceAll(",", "|");
				//Append comma to separate results in CSV
				csv.append(value+",");
			}
			else csv.append(",");
			
		}
		
		csv.append("\n");
	}
	
	/**
	 * Write the Finale CSV file
	 * @param csv
	 * @param path
	 */
	protected void writeCSV(StringBuilder csv, String path) {
		File f = new File(path);

		// On ecrit les CSV
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(f);
			pw.write(csv.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}
	
	/**
	 * Write the config file in a temp file for pyradiomics processing for each request
	 * @throws IOException 
	 */
	protected File writeYaml(boolean discretize, double binWidth, boolean validate,  int minimumROIDimensions, int minimumROISize, double geometryTolerance, boolean correctMask, int label, boolean normalize, double normalizeScale, double removeOutliers, boolean resample, double[] pixelSpacing, String interpolator, int padDistance, boolean force2D, int force2DDimensions, boolean distance, String distancesValues, boolean resegment, double min, double max, boolean preCrop, String sigma, int startLevelWavelet, int levelWavelet, String stringWavelet, boolean useGradientSpacing, int voxelArrayShift, boolean symmetricalGLCM, String weightingNorm, double gldmAlfa, HashMap<String, Boolean> imageType, HashMap<String,Boolean> features) throws IOException{
	
	//Prepare resegment string
	String resegmentRange=null;
	if (resegment) resegmentRange="["+min+","+max+"]";
	
	//Prepare feature string
	StringBuilder featuresString=new StringBuilder();
	if (features.size()!=0){
		if (features.get("First Order")== true) featuresString.append("  firstorder:\n");
		if (features.get("Shape")== true) featuresString.append("  shape:\n");
		if (features.get("GLCM")==true) featuresString.append("  glcm:\n");
		if (features.get("GLRLM")==true) featuresString.append("  glrlm:\n");
		if (features.get("GLSZM")==true) featuresString.append("  glszm:\n");
		if (features.get("NGTDM")==true) featuresString.append("  ngtdm:\n");
	}
	
 		
	String settingsYaml= "\nsetting:\n";
	
	if (validate) {
		if (minimumROIDimensions!=0) settingsYaml+="  minimumROIDimensions: "+String.valueOf(minimumROIDimensions)+"\n";
		if (minimumROISize!=0)	settingsYaml+="  minimumROISize: "+String.valueOf(minimumROISize)+"\n";
		if (geometryTolerance!=0) settingsYaml+="  geometryTolerance: "+geometryTolerance+"\n";
		settingsYaml+= "  correctMask: "+String.valueOf(correctMask)+"\n";
	}
	
	if (features.get("Additional Info")== true) settingsYaml +="  additionalInfo:\n";
			
	settingsYaml += "  additionalInfo: true"+"\n"
			+ "  label: "+ label+"\n";
	
	if (discretize) settingsYaml += "  binWidth: "+binWidth+"\n";
	
	if (normalize) {
		settingsYaml+= "  normalize: true"+"\n";
		if (normalizeScale!=0)	settingsYaml += "  normalizeScale: "+normalizeScale+"\n";
		if (removeOutliers!=0)	settingsYaml += "  removeOutliers: "+removeOutliers+"\n";
	}
	
	if (resample) {
		if ((pixelSpacing[0]+pixelSpacing[1]+pixelSpacing[2])!=0)settingsYaml += "  resampledPixelSpacing: "+"[" + String.valueOf(pixelSpacing[0])+ ","+String.valueOf(pixelSpacing[1])+","+String.valueOf(pixelSpacing[2])+"]"+"\n";
		else settingsYaml +="  resampledPixelSpacing: None"+"\n";
		settingsYaml += "  interpolator: "+interpolator+"\n";
		settingsYaml += "  padDistance: "+String.valueOf(padDistance)+"\n";
	}
	
	if (distance) settingsYaml +="  distances: "+distancesValues+"\n";
	if (force2D) {
		settingsYaml +="  force2D: true"+"\n"
		+ "  force2Ddimension: "+String.valueOf(force2DDimensions)+"\n";
	}
	
	if (resegment) settingsYaml +="  resegmentRange: "+resegmentRange+"\n";
	
	if (imageType.get("typeLoG")|| imageType.get("typeWavelet")||imageType.get("typeSquare")||imageType.get("typeSquareRoot")||imageType.get("typeLogarithm")||imageType.get("typeExponential") ) settingsYaml +="  preCrop: "+String.valueOf(preCrop)+"\n";
			
	if (voxelArrayShift!=0) settingsYaml +="  voxelArrayShift: "+voxelArrayShift+"\n";
	
	settingsYaml +=	"  symmetricalGLCM: "+String.valueOf(symmetricalGLCM)+"\n";
	
	if (!weightingNorm.equals("None"))	settingsYaml += "  weightingNorm: "+weightingNorm+"\n";
	
	if (gldmAlfa!=0)	settingsYaml +="  gldm_a: "+String.valueOf(gldmAlfa)+"\n";
	
	settingsYaml += "\n\n"
			+ "imageType:\n";
	//LOG ET WAVELET A TESTER
	if (imageType.get("typeOriginal")) settingsYaml += "  Original: {}"+"\n";
	if (imageType.get("typeLoG")) settingsYaml += "  LoG: {'sigma' : ["+sigma+"]}" + "\n";
	if (imageType.get("typeWavelet")) settingsYaml += "  Wavelet: {'start_level' : "+String.valueOf(startLevelWavelet)+", 'level' : "+String.valueOf(levelWavelet)+", 'wavelet' : \""+stringWavelet+"\" }"+"\n";
	if (imageType.get("typeSquare")) settingsYaml += "  Square: {}"+"\n";
	if (imageType.get("typeSquareRoot")) settingsYaml += "  SquareRoot: {}"+"\n";
	if (imageType.get("typeLogarithm")) settingsYaml += "  Logarithm: {}"+"\n";
	if (imageType.get("typeExponential")) settingsYaml += "  Exponential: {}"+"\n";
	if (imageType.get("typeGradient")) settingsYaml += "  Gradient: {'gradientUseSpacing' : "+String.valueOf(useGradientSpacing)+"}"+"\n";
	if (featuresString.length()!=0) settingsYaml+="featureClass:\n"+ featuresString+"\n";
	
	System.out.println(settingsYaml);
	
	return writeYaml(settingsYaml);
	}
	
	protected File writeYaml(int label, boolean resegment, double min, double max) throws IOException{
	//Prepare resegment string
	String resegmentRange=null;
	if (resegment) resegmentRange="["+min+","+max+"]"; 
		
		String settingsYaml= "\nsetting:\n"
			+ "  label: "+ label+"\n"
			+ "  #interpolator: 'sitkBSpline' \n"
			+ "  resampledPixelSpacing: \n"
			+ "  weightingNorm: \n\n"
			+ "  resegmentRange: "+resegmentRange+"\n"
			+ "imageType:\n"
			+ "  Original: {} \n\n"
			+ "featureClass:\n"
			+ "  firstorder: []\n";
	
	return writeYaml(settingsYaml);
	}
	
	/**
	 * Make YAML with label only, and use all default options
	 * @param label
	 * @return
	 * @throws IOException
	 */
	protected File writeYaml(int label) throws IOException{

			String settingsYaml= "\nsetting:\n"
				+ "  label: "+ label+"\n"
				+ "imageType:\n"
				+ "  Original: {} \n\n";
		System.out.println(settingsYaml);
		return writeYaml(settingsYaml);
		}
	
	
	/**
	 * Write Yaml config file to a temporary file (will be deleted on exit of JVM)
	 * @param settingsYaml
	 * @return
	 * @throws IOException
	 */
	private File writeYaml(String settingsYaml) throws IOException{
		PrintWriter pw = null;
		File temp = File.createTempFile("settings", ".yaml");
		temp.deleteOnExit();
		try {
			pw = new PrintWriter(temp);
			pw.write(settingsYaml);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
		return temp;
	}

}
