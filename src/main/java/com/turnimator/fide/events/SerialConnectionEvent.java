/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.events;

import com.turnimator.fide.events.ConnectionType;

/**
 *
 * @author atle
 */
public abstract class SerialConnectionEvent {
    
    public abstract void connect(ConnectionType ct, String serialPort, int bitRate);
}
