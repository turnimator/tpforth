/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.turnimator.fide.model;

import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.events.ReceiveEvent;
import java.util.List;

/**
 *
 * @author atle
 */
public interface CommunicatorInterface {
    void setHost(String host); // Nor used for serial connections
    void setPort(String port);

    /**
     *
     * @param t
     * @param host
     * @param port
     * @return
     */
    
    String connect(String port);
    String getId();
    boolean disconnect();
    boolean send(String s);
    void addReceiveEventHandler(ReceiveEvent evt);
    boolean isOpen();
    String getErrorText();
    List<String> getPorts(String host);
}