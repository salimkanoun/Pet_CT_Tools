package org.petctviewer.petcttools.reader.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class JTable_Color extends JTable {
	/*
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		
        Component returnComp = super.prepareRenderer(renderer, row, column);
        Color alternateColor = new Color(220, 220, 220);
        Color whiteColor = Color.WHITE;
        if (!returnComp.getBackground().equals(getSelectionBackground())){
            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
            returnComp .setBackground(bg);
            bg = null;
        }
        return returnComp;
        }
	*/

}
