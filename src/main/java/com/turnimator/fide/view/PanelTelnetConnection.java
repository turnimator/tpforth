/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.TelnetConnectionRequestEvent;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import sun.jvm.hotspot.gc.shared.GCCause;

/**
 *
 * @author atle
 */
public class PanelTelnetConnection extends Panel {
    private final ArrayList<TelnetConnectionRequestEvent> connectionHandlerList = new ArrayList<>();
    private final ArrayList<ConnectionCloseEvent> disconnectHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> _rescanHandlerList = new ArrayList<>();
    JPanel buttonPanel = new JPanel();
    JPanel textPanel = new JPanel();
    private ConnectionType _connectionType = ConnectionType.Telnet;
    JTextField _urlTextField;
    MaskFormatter mask;
    
    JTextField portTextField ;
    JComboBox<String> _portListBox;
  
    private JButton _connectButton;
    private JButton _disconnectButton;
    private JButton _rescanButton;
    
    public PanelTelnetConnection(){
        initComponents();
    }
    
    private void initComponents(){
        
        _urlTextField = new JTextField();
        _urlTextField.setText("192.168.4.1");
        
        portTextField = new JTextField();
        //portTextField.setText("23");
        
        _portListBox = new JComboBox<>();
        _portListBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED){
                    portTextField.setText((String) e.getItem());
                }
            }
        });
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.add(_urlTextField);
        textPanel.add(portTextField);
        textPanel.add(_portListBox);
        
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        _connectButton = new JButton("Connect");
        _connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(TelnetConnectionRequestEvent ev:connectionHandlerList){
                    String text = _urlTextField.getText();
                    Logger.getAnonymousLogger().log(Level.INFO, "urlTextField:"+text);
                    int port = Integer.parseInt(portTextField.getText());
                    ev.connect(text, portTextField.getText());
                }
            }
        });
        _disconnectButton = new JButton("Disconnect");
        _disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ConnectionCloseEvent ev:disconnectHandlerList){
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Needs a String connectionIdBuilder()");
                    ev.close("BUG");
                }
            }
        });
        _rescanButton = new JButton(new AbstractAction("Scan") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(RescanEvent ev:_rescanHandlerList){
                    ev.rescan(_urlTextField.getText());
                }
            }
        });
        buttonPanel.add(_connectButton);
        buttonPanel.add(_disconnectButton);
        buttonPanel.add(_rescanButton);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(textPanel);
        add(buttonPanel);
        
    } 
    
    public void addConnectionEventHandler(TelnetConnectionRequestEvent ev){
        connectionHandlerList.add(ev);
    }
    
    public void addRescanEventHandler(RescanEvent ev){
        _rescanHandlerList.add(ev);
    }
    
    public void setPortList(List<String> ports){
        for(String s: ports){
            _portListBox.addItem(s);
        }
    }

    void addPort(String host, String s) {
        _portListBox.addItem(s);
    }

    void clearPortList() {
        _portListBox.removeAllItems();
    }
    
}
