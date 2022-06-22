/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.TelnetConnectionEvent;
import java.awt.Panel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author atle
 */
public class PanelTelnetConnection extends Panel {
    private final ArrayList<TelnetConnectionEvent> connectionHandlerList = new ArrayList<>();
    
    JPanel buttonPanel = new JPanel();
    JPanel textPanel = new JPanel();
    
    JFormattedTextField urlTextField;
    MaskFormatter mask;
    
    JTextField portTextField ;
   
  
    private JButton buttonConnect;
    private JButton buttonDisconnect;
    private JButton buttonRescan;
    
    public PanelTelnetConnection(){
        initComponents();
    }
    
    private void initComponents(){
        try {
            mask = new MaskFormatter("###.###.###.###");
        } catch (ParseException ex) {
            Logger.getLogger(PanelTelnetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        urlTextField = new JFormattedTextField(mask);
        
        portTextField = new JTextField();
        portTextField.setText("23");
        
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.add(urlTextField);
        textPanel.add(portTextField);
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonConnect = new JButton("Connect");
        buttonDisconnect = new JButton("Disconnect");
        buttonPanel.add(buttonConnect);
        buttonPanel.add(buttonDisconnect);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(textPanel);
        add(buttonPanel);
        
    } 
}
