/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.turnimator.fide.model;

import com.turnimator.fide.events.ReceiveEvent;

/**
 *
 * @author atle
 */
public interface CommunicatorInterface {
    boolean connect(String connectionString);
    boolean disconnect();
    boolean send(String s);
    void addReceiveEventHandler(ReceiveEvent evt);
    boolean isOpen();
    String getErrorText();
    String getSourceId();
    
}