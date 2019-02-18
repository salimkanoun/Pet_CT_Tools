package org.petctviewer.petcttools.reader;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Table_Series_Model extends DefaultTableModel{
	
	private String[] columnTitle = {"Serie Description", "Modality","Serie number", "Number of slice", "File", "Series Object" };
	private Class<?>[] columnClass = {String.class, String.class, String.class, Integer.class, File.class, Series_Details.class};
	
	ArrayList<Series_Details> series;
	public Table_Series_Model(ArrayList<Series_Details> series) {
		super(0,6);
		this.series=series;
		updateModel();
		
	}
	
	public String getColumnName(int column){
		return columnTitle[column];
		
	}
	
	public Class<?> getColumnClass(int column){
		return columnClass[column];
		
	}
	
	private void updateModel() {
		for(Series_Details serie: series) {
			this.addRow(new Object[]{serie.serieDescription, serie.modality, serie.serieNumber,
				serie.numberOfImage, serie.fileLocation,serie });
		}
	}

}
