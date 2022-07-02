/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.turnimator.fide.controller;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.events.ReceiveEvent;
import com.turnimator.fide.model.CommunicatorInterface;
import com.turnimator.fide.model.SerialCommunicator;
import com.turnimator.fide.model.TelnetCommunicator;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author atle
 */
public class CommunicationDispatcher {

    private final ArrayList<ReceiveEvent> _receiveHandlerList = new ArrayList<>();
    private final HashMap<ConnectionId, CommunicatorInterface> _communicatorMap = new HashMap<>(); // For Transmitting and receiving to individual panels
    private CommunicatorInterface _communicator;

    public CommunicationDispatcher() {

    }

    void createCommunicator(ConnectionType connectionType, String host, String port) {
        switch (connectionType) {
            case Serial:
                _communicator = new SerialCommunicator();
                break;
            case Telnet:
                _communicator = new TelnetCommunicator();
                break;
        }
        _communicator.setHost(host);
        _communicator.setPort(port);
        _communicator.addReceiveEventHandler(new ReceiveEvent() {
            @Override
            public void receive(ConnectionId id, String text) {
                for (ReceiveEvent evt : _receiveHandlerList) {
                    evt.receive(id, text);
                }
            }
        });
    }

    Iterable<String> getPorts() {
        return _communicator.getPorts();
    }

    private CommunicatorInterface ensureCommunicator(ConnectionId id) {
        if (id == null) {
            throw new NullPointerException("ConnectionId can not be null!");
        }
        if (!id.equals(_communicator.getId())) {
            _communicator = _communicatorMap.get(id);
        }
        return _communicator;
    }

    public ConnectionId connect() {
        return _communicator.connect();
    }

    public boolean disconnect(ConnectionId id) {
        ensureCommunicator(id);
        return _communicator.disconnect();
    }

    public boolean send(ConnectionId id, String s) {
        //Logger.getAnonymousLogger().log(Level.INFO, "Send from " + id + ": "+text);
        // TODO if id.getConnectionType() == ConnectionType.All loop through all entries in 
        // _communicatorMap and send to each one (except the originator?)
        
        ensureCommunicator(id);
        return _communicator.send(s);
    }

    public void addReceiveEventHandler(ReceiveEvent evt) {
        _receiveHandlerList.add(evt);
    }

    public boolean isOpen(ConnectionId id) {
        ensureCommunicator(id);
        return _communicator.isOpen();
    }

    public String getErrorText(ConnectionId id) {
        ensureCommunicator(id);
        return _communicator.getErrorText();
    }

    ConnectionId getConnectionId() {
        return _communicator.getId();
    }

}
