/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide;

import com.turnimator.fide.events.ConnectionType;

/**
 *
 * @author atle
 */
public class ConnectionId {
    ConnectionType _type;
    String _connectionString;
    public ConnectionId(ConnectionType t, String s){
        _type = t;
        _connectionString = s;
    }
    public String toString(){
        return _type.name() + ":" + _connectionString;
    }
}
