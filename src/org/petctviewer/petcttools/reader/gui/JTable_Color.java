package org.petctviewer.petcttools.reader.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class JTable_Color extends JTable {
	
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		
        Component returnComp = super.prepareRenderer(renderer, row, column);
        Color alternateColor = new Color(204, 204, 204);
        Color whiteColor = Color.WHITE;
        if (!returnComp.getBackground().equals(getSelectionBackground())){
            Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
            returnComp .setBackground(bg);
            bg = null;
        }
        return returnComp;
        }

}
