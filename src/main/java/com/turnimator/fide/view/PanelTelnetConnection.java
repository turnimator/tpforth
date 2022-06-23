/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.TelnetConnectionEvent;
import com.turnimator.fide.events.TelnetDisconnectEvent;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author atle
 */
public class PanelTelnetConnection extends Panel {
    private final ArrayList<TelnetConnectionEvent> connectionHandlerList = new ArrayList<>();
    private final ArrayList<TelnetDisconnectEvent> disconnectHandlerList = new ArrayList<>();
    JPanel buttonPanel = new JPanel();
    JPanel textPanel = new JPanel();
    
    JTextField urlTextField;
    MaskFormatter mask;
    
    JTextField portTextField ;
   
  
    private JButton buttonConnect;
    private JButton buttonDisconnect;

    
    public PanelTelnetConnection(){
        initComponents();
    }
    
    private void initComponents(){
        
        urlTextField = new JTextField();
        urlTextField.setText("192.168.4.1");
        
        portTextField = new JTextField();
        portTextField.setText("23");
        
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.add(urlTextField);
        textPanel.add(portTextField);
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonConnect = new JButton("Connect");
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(TelnetConnectionEvent ev:connectionHandlerList){
                    String text = urlTextField.getText();
                    Logger.getAnonymousLogger().log(Level.INFO, "urlTextField:"+text);
                    int port = Integer.parseInt(portTextField.getText());
                    ev.connect(text, port);
                }
            }
        });
        buttonDisconnect = new JButton("Disconnect");
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(TelnetDisconnectEvent ev:disconnectHandlerList){
                    ev.disconnect(urlTextField.getText());
                }
            }
        });
        buttonPanel.add(buttonConnect);
        buttonPanel.add(buttonDisconnect);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(textPanel);
        add(buttonPanel);
        
    } 
    
    public void addConnectionEventHandler(TelnetConnectionEvent ev){
        connectionHandlerList.add(ev);
    }
    
    public void addDisconnectEventHndler(TelnetDisconnectEvent ev){
        disconnectHandlerList.add(ev);
    }
}
