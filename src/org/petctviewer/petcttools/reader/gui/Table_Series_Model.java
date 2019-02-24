package org.petctviewer.petcttools.reader.gui;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import org.petctviewer.petcttools.reader.Series_Details;

@SuppressWarnings("serial")
public class Table_Series_Model extends DefaultTableModel{
	
	private String[] columnTitle = {"Serie Description", "Modality","Serie number", "Slices", "Serie Directory", "Series Object" };
	private Class<?>[] columnClass = {String.class, String.class, String.class, Integer.class, Object.class, Series_Details.class};
	
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
	
	public boolean isCellEditable(int row,int column){
		return false;	
	}
	
	
	
	private void updateModel() {
		for(Series_Details serie: series) {
			if(!serie.isDicomDir) {
				this.addRow(new Object[]{serie.serieDescription, serie.modality, serie.serieNumber,
						serie.numberOfImage, serie.fileLocation,serie });
			}else {
				this.addRow(new Object[]{serie.serieDescription, serie.modality, serie.serieNumber,
						serie.numberOfImage, serie.fileLocationList,serie });
			}
			
		}
	}

}
