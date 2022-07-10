/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionRequestEvent;
import com.turnimator.fide.events.DisconnectEvent;
import com.turnimator.fide.events.TelnetConnectionRequestEvent;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author atle
 */
public class PanelConnections extends JPanel {

    private final ArrayList<SerialConnectionRequestEvent> serialConnectHandlerList = new ArrayList<>();
    private final ArrayList<DisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();
    
    private final ArrayList<TelnetConnectionRequestEvent> telnetConnectionHandlerList = new ArrayList<>();
    
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    private PanelSerialConnection panelSerialConnection;
    private PanelTelnetConnection panelTelnetConnection;
    
    private void initComponents() {
        panelSerialConnection = new PanelSerialConnection();
        panelSerialConnection.addRescanEventHandler(new RescanEvent() {
            @Override
            public void rescan(String host) {
                for(RescanEvent ev:rescanHandlerList){
                    ev.rescan(host);
                }
            }
        });
        tabbedPane.add("Serial", panelSerialConnection);
        
       panelTelnetConnection = new PanelTelnetConnection();
       panelTelnetConnection.addConnectionEventHandler(new TelnetConnectionRequestEvent() {
            @Override
            public void connect(String host, String port) {
                for(TelnetConnectionRequestEvent ev:telnetConnectionHandlerList){
                    ev.connect(host, port);
                }
            }
       });
       panelTelnetConnection.addRescanEventHandler(new RescanEvent() {
            @Override
            public void rescan(String host) {
                for (RescanEvent ev:rescanHandlerList){
                    ev.rescan(host);
                }
            }
       });
       tabbedPane.add("Telnet", panelTelnetConnection);
       
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

    public void addTelnetConnectionHandler(TelnetConnectionRequestEvent ev){
        telnetConnectionHandlerList.add(ev);
    }
    
    
    public void addSerialConnectionEventHandler(SerialConnectionRequestEvent ev) {
        panelSerialConnection.addConnectionEventHandler(ev);
    }
    
    public void addDisconnectEventHandler(DisconnectEvent ev){
        panelSerialConnection.addDisconnectEventHandler(ev);
    }

    void clearSerialPortsList() {
        panelSerialConnection.clearPortList();
    }
}
