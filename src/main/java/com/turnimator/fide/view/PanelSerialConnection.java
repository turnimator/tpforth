/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionEvent;
import com.turnimator.fide.events.SerialDisconnectEvent;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 *
 * @author atle
 */
public class PanelSerialConnection extends Panel {

    private final ArrayList<SerialConnectionEvent> serialConnectHandlerList = new ArrayList<>();
    private final ArrayList<SerialDisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();
    
    private JComboBox<String> commList = new JComboBox<>();
    private JComboBox<String> jComboBoxBitRate;
    private JButton jButtonConnect;
    private JButton jButtonDisconnect;
    private JButton jButtonRescan;

    public PanelSerialConnection() {
        initComponents();
    }

    private void initComponents() {
        jButtonConnect = new JButton("Connect");
        jButtonConnect.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (SerialConnectionEvent ev : serialConnectHandlerList) {
                    String s = (String) jComboBoxBitRate.getSelectedItem();
                    Integer i = Integer.parseInt(s);
                    ev.connect(ConnectionType.Serial, (String) commList.getSelectedItem(), i);
                }
            }
        });

        jComboBoxBitRate = new JComboBox<>();
        jComboBoxBitRate.addItem("115200");
        jComboBoxBitRate.addItem("9600");

        jButtonDisconnect = new JButton("Disconnect");
        jButtonDisconnect.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(SerialDisconnectEvent ev: serialDisconnectHandlerList){
                    ev.disconnect((String) jComboBoxBitRate.getSelectedItem());
                }
            }
        });
        jButtonRescan = new JButton("Rescan");
        jButtonRescan.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (RescanEvent ev:rescanHandlerList){
                    ev.rescan();
                }
            }
        });
        setLayout(new GridLayout(3, 4));

        add(commList);
        add(jComboBoxBitRate);
        add(jButtonConnect);
        add(jButtonDisconnect);
        add(jButtonRescan);
    }

    public void addRescanEventHandler(RescanEvent ev){
        rescanHandlerList.add(ev);
    }
    
    public void addPort(String port) {
        commList.addItem(port);
    }

    public void addConnectionEventHandler(SerialConnectionEvent ev) {
        serialConnectHandlerList.add(ev);
    }
    
    public void addDisconnectEventHandler(SerialDisconnectEvent ev){
        serialDisconnectHandlerList.add(ev);
    }

    void clearPortList() {
        commList.removeAllItems();
    }
}
