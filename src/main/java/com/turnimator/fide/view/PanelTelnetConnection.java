/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.TelnetConnectionEvent;
import java.awt.Panel;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author atle
 */
public class PanelTelnetConnection extends Panel {
    private final ArrayList<TelnetConnectionEvent> connectionHandlerList = new ArrayList<>();
    
    JPanel panel = new JPanel();
    
    private JButton jButtonConnect;
    private JButton jButtonDisconnect;
    private JButton jButtonRescan;
    
    public PanelTelnetConnection(){
        initComponents();
    }
    
    private void initComponents(){
        
    } 
}
