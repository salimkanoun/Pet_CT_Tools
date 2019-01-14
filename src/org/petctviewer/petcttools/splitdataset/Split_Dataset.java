package org.petctviewer.petcttools.splitdataset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class Split_Dataset extends Split_Gui implements ActionListener {
	
	File inputPath;

	public static void main(String[] args) {
		new Split_Dataset();

	}
	
	public Split_Dataset() {
		super();
		this.setVisible(true);
		btnSeletectPath.addActionListener(this);
		btnStart.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnSeletectPath) {
			JFileChooser chooser=new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ok=chooser.showOpenDialog(null);
			if (ok==JFileChooser.APPROVE_OPTION ) {
				inputPath=chooser.getSelectedFile();
				lblNa.setText(inputPath.getAbsolutePath());
			}
			
		}else if(e.getSource()==btnStart) {
			String[] directories = inputPath.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return new File(current, name).isDirectory();
				  }
				});
			Arrays.sort(directories);
			int batchSize=(int)spinnerBatchSize.getValue();
			int numberOfBatch=directories.length/ batchSize;
			
			for(int i=0; i<=numberOfBatch; i++) {
				if((i*batchSize)==directories.length) {
					break;
				}
				File subdir=new File(inputPath+File.separator+"Batch"+i);
				subdir.mkdir();
				for(int j=0; j<batchSize; j++) {
					
					if((i*batchSize+j)==directories.length) {
						break;
					}
					
					try {

						Files.move(new File( inputPath+File.separator+directories[(i*batchSize+j)] ).toPath() ,new File(subdir+File.separator+directories[(i*batchSize+j)]).toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					}
					
				}
				
				
			}
		}
			
			
		
	}

}
