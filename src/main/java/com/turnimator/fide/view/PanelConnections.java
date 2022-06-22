/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionEvent;
import com.turnimator.fide.events.SerialDisconnectEvent;
import com.turnimator.fide.events.TelnetConnectionEvent;
import com.turnimator.fide.events.TelnetDisconnectEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author atle
 */
public class PanelConnections extends JPanel {

    private final ArrayList<SerialConnectionEvent> serialConnectHandlerList = new ArrayList<>();
    private final ArrayList<SerialDisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();
    
      private final ArrayList<TelnetConnectionEvent> telnetConnectionHandlerList = new ArrayList<>();
    private final ArrayList<TelnetDisconnectEvent> telnetDisconnectHandlerList = new ArrayList<>();
    
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
        
       panelTelnetConnection = new PanelTelnetConnection();
       panelTelnetConnection.addConnectionEventHandler(new TelnetConnectionEvent() {
            @Override
            public void connect(String connectionString, int port) {
                for(TelnetConnectionEvent ev:telnetConnectionHandlerList){
                    ev.connect(connectionString, port);
                }
            }
       });
       
       panelTelnetConnection.addDisconnectEventHndler(new TelnetDisconnectEvent() {
            @Override
            public void disconnect(String source) {
                for(TelnetDisconnectEvent ev:telnetDisconnectHandlerList){
                    ev.disconnect(source);
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

    public void addTelnetConnectionHandler(TelnetConnectionEvent ev){
        telnetConnectionHandlerList.add(ev);
    }
    
    public void addTelnetDisconnectHandler(TelnetDisconnectEvent ev){
        telnetDisconnectHandlerList.add(ev);
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
