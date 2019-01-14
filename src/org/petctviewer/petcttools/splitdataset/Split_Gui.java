package org.petctviewer.petcttools.splitdataset;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Split_Gui extends JFrame {

	private JPanel contentPane;
	protected JButton btnSeletectPath, btnStart;
	protected JLabel lblNa;
	protected JSpinner spinnerBatchSize;

	/**
	 * Create the frame.
	 */
	public Split_Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel_center = new JPanel();
		contentPane.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_center.add(panel_1);
		
		btnSeletectPath = new JButton("Seletect Path");
		
		panel_1.add(btnSeletectPath);
		
		lblNa = new JLabel("N/A");
		panel_1.add(lblNa);
		
		JPanel panel = new JPanel();
		panel_center.add(panel);
		
		JLabel lblSplitEach = new JLabel("Split each");
		panel.add(lblSplitEach);
		
		spinnerBatchSize = new JSpinner(new SpinnerNumberModel(10,1,Integer.MAX_VALUE,1));
		panel.add(spinnerBatchSize);
		
		JLabel lblFolders = new JLabel("folders");
		panel.add(lblFolders);
		
		JPanel panel_south = new JPanel();
		contentPane.add(panel_south, BorderLayout.SOUTH);
		
		btnStart = new JButton("Start");
		panel_south.add(btnStart);
	}

}
