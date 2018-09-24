import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class Test_PyRadiomics extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Test_PyRadiomics dialog = new Test_PyRadiomics();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Test_PyRadiomics() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			{
				JPanel panel = new JPanel();
				contentPanel.add(panel);
				panel.setLayout(new GridLayout(0, 1, 0, 0));
				JButton btnInstallPyradiomics = new JButton("Install pyRadiomics");
				panel.add(btnInstallPyradiomics);
				{
					JButton btnUpgradePyradiomics = new JButton("Upgrade pyRadiomics");
					panel.add(btnUpgradePyradiomics);
					JButton btnTestPyradiomics = new JButton("Test pyRadiomics");
					panel.add(btnTestPyradiomics);
					btnTestPyradiomics.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							Radiomics.testPyRadiomics();
						}
					});
					btnUpgradePyradiomics.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							Radiomics.upgradePyRadiomics();
						}
					});
				}
				btnInstallPyradiomics.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Radiomics.installPyRadiomics();
					}
				});
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
