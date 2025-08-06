/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionRequestEvent;
import com.turnimator.fide.events.DisconnectEvent;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 *
 * @author atle
 */
public class PanelSerialConnection extends Panel {

    private final ArrayList<SerialConnectionRequestEvent> serialConnectHandlerList = new ArrayList<>();
    private final ArrayList<DisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();

    private final JComboBox<String> _commList = new JComboBox<>();
    private JComboBox<String> jComboBoxBitRate;
    private JButton _buttonConnect;
    private JButton _disconnectButton;
    private JButton jButtonRescan;

    public PanelSerialConnection() {
        initComponents();
    }

    private void initComponents() {
        _buttonConnect = new JButton("Connect");
        _buttonConnect.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (SerialConnectionRequestEvent ev : serialConnectHandlerList) {
                    String s = (String) jComboBoxBitRate.getSelectedItem();
                    Integer i = Integer.parseInt(s);
                    ev.connect((String) _commList.getSelectedItem(), i);
                    _disconnectButton.setEnabled(true);
                }
            }
        });
        _buttonConnect.setEnabled(false);

        _commList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread.yield();
                _buttonConnect.setEnabled(true);
            }
        });
        jComboBoxBitRate = new JComboBox<>();
        jComboBoxBitRate.addItem("115200");
        jComboBoxBitRate.addItem("9600");

        _disconnectButton = new JButton("Disconnect");
        _disconnectButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (DisconnectEvent ev : serialDisconnectHandlerList) {
                    ev.disconnect((String) _commList.getSelectedItem());
                }
            }
        });
        _disconnectButton.setEnabled(false);

        jButtonRescan = new JButton("Rescan");
        jButtonRescan.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (RescanEvent ev : rescanHandlerList) {
                    ev.rescan("");
                }
            }
        });
        setLayout(new GridLayout(3, 4));

        add(_commList);
        add(jComboBoxBitRate);
        add(jButtonRescan);
        add(_buttonConnect);
        add(_disconnectButton);

    }

    public void addRescanEventHandler(RescanEvent ev) {
        rescanHandlerList.add(ev);
    }

    public void addPort(String port) {
        _commList.addItem(port);
    }

    public void addConnectionEventHandler(SerialConnectionRequestEvent ev) {
        serialConnectHandlerList.add(ev);
    }

    public void addDisconnectEventHandler(DisconnectEvent ev) {
        serialDisconnectHandlerList.add(ev);
    }

    void clearPortList() {
        _commList.removeAllItems();
    }
}
