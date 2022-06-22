/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionEvent;
import com.turnimator.fide.events.SerialDisconnectEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author atle
 */
public class PanelConnections extends JPanel {

    private ArrayList<SerialConnectionEvent> serialConnectHandlerList = new ArrayList<>();
    private ArrayList<SerialDisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();
    
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    private PanelSerialConnection panelSerialConnection;
    private PanelTelnetConnection panelTelnetConnection;
    
    private void initComponents() {
        panelSerialConnection = new PanelSerialConnection();
        panelSerialConnection.addRescanEventHandler(new RescanEvent() {
            @Override
            public void rescan() {
                for(RescanEvent ev:rescanHandlerList){
                    ev.rescan();
                }
            }
        });
        tabbedPane.add("Serial", panelSerialConnection);
        tabbedPane.add("Telnet", new PanelTelnetConnection());
        add(tabbedPane);
    }

    public PanelConnections() {
        initComponents();
    }

    public void addRescanHandler(RescanEvent ev){
        rescanHandlerList.add(ev);
    }
    
    public void addSerialPortToList(String s) {
        panelSerialConnection.addPort(s);
    }

    public void addSerialConnectionEventHandler(SerialConnectionEvent ev) {
        panelSerialConnection.addConnectionEventHandler(ev);
    }
    
    public void addSerialDisconnectEventHandler(SerialDisconnectEvent ev){
        panelSerialConnection.addDisconnectEventHandler(ev);
    }

    void clearSerialPortsList() {
        panelSerialConnection.clearPortList();
    }
}
