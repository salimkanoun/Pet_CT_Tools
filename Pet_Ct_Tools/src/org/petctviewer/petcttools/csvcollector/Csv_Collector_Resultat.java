
/**
Copyright (C) 2017 KANOUN Salim
This
 program is free software; you can redistribute it and/or modify
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

package org.petctviewer.petcttools.csvcollector;

public class Csv_Collector_Resultat {
	private String nom;
	private String id;
	private String date;
	private double mtv;
	private double tlg;
	private double suvMean;
	private double suvPeak;
	private double suvSD;
	private double suvMax;
	private double sul;
	private double qPeak;
	private int niftiRoiNumber, manualRoiNumber;
	private int timer;
	
	public Csv_Collector_Resultat(String nom, String id, String date, double mtv, double tlg, double suvMean, double suvSD, double suvPeak, double suvMax, double sul, double qPeak,int timer, int niftiRoiNumber, int manualRoiNumber) {
		this.nom=nom;
		this.id=id;
		this.date=date.substring(1);
		this.mtv=mtv;
		this.tlg=tlg;
		this.suvMean=suvMean;
		this.suvSD=suvSD;
		this.suvMax=suvMax;
		this.suvPeak=suvPeak;
		this.sul=sul;
		this.qPeak=qPeak;
		this.niftiRoiNumber=niftiRoiNumber;
		this.manualRoiNumber=manualRoiNumber;
		this.timer=timer;
		
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public String getId() {
		return this.id;
	}
	
	public double getMtv() {
		return this.mtv;
	}
	
	public double getSuvMean() {
		return this.suvMean;
	}
	
	public double getSuvPeak() {
		return this.suvPeak;
	}
	
	public double getSuvSD() {
		return this.suvSD;
	}
	
	public double getSuvMax() {
		return this.suvMax;
	}
	
	public double getTlg() {
		return this.tlg;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public Double getSul(){
		return this.sul;
	}
	
	public int getNiftiRoiNumber(){
		return this.niftiRoiNumber;
	}
	
	public int getManualRoiNumber(){
		return this.manualRoiNumber;
	}
	
	public double getqPeak(){
		return this.qPeak;
	}
	public int getTimer(){
		return this.timer;
	}


}
