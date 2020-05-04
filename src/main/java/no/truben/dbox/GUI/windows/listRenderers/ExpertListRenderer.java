package no.truben.dbox.GUI.windows.listRenderers;

/**
 *
 * @author Truben
 *
 **/

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;

public class ExpertListRenderer extends JLabel implements ListCellRenderer {

    JLabel section = new JLabel();
    JLabel property = new JLabel();
    JLabel propertyvalue = new JLabel();

    public ExpertListRenderer() {
        this.setLayout(new GridLayout(1,3));
        this.add(section);
        this.add(property);
        this.add(propertyvalue);
    }


    public Component getListCellRendererComponent(
            JList list,
            Object value,           // value to display
            int index,              // cell index
            boolean isSelected,     // is the cell selected
            boolean cellHasFocus)   // the list and the cell have the focus
    {
        section.setFont(list.getFont());
        property.setFont(list.getFont());
        propertyvalue.setFont(list.getFont());

        String text = value.toString();
        

        this.setText(" ");

        int first  = text.indexOf(" => ");
        int second = text.indexOf(" = ");
        
        if(first <= 0)
            return null;
                
        section.setText("[" + text.substring(0, first) + "]");


        if(section.getText().toLowerCase().equals("[autoexec]")) {
            property.setText(text.substring(first+4));
            propertyvalue.setText("");
        }
        else {
            property.setText(text.substring(first+4,second) + " =");
            propertyvalue.setText(text.substring(second+3));
        }

        section.setVisible(true);
        property.setVisible(true);
        propertyvalue.setVisible(true);

        // Hide text
        this.setForeground(this.getBackground());


        if (isSelected) {
            section.setBackground(list.getSelectionBackground());
            section.setForeground(list.getSelectionForeground());
            property.setBackground(list.getSelectionBackground());
            property.setForeground(list.getSelectionForeground());
            propertyvalue.setBackground(list.getSelectionBackground());
            propertyvalue.setForeground(list.getSelectionForeground());
            section.setOpaque(true);
            property.setOpaque(true);
            propertyvalue.setOpaque(true);
        } else {
            section.setBackground(list.getBackground());
            section.setForeground(list.getForeground());
            property.setBackground(list.getBackground());
            property.setForeground(list.getForeground());
            propertyvalue.setBackground(list.getBackground());
            propertyvalue.setForeground(list.getForeground());

            section.setOpaque(false);
            property.setOpaque(false);
            propertyvalue.setOpaque(false);

        }

        return this;
    }

}

