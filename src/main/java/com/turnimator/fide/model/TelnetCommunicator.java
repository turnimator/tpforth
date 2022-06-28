/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turnimator.fide.model;


import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.ReceiveEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atle
 */
public class TelnetCommunicator implements CommunicatorInterface {
    String errorText = "";
    ArrayList<ReceiveEvent> rcvEventList = new ArrayList<>();
    Socket socket = new Socket();
    String host = "";
    int port = 23;
    PrintWriter prw = null;
    BufferedReader brin = null;
    Thread rxThread = null;

    /**
     *
     * @param connectionString "host:port"
     * @return True if successful, false otherwise
     */
    @Override
    public boolean connect(String connectionString) {
        Logger.getAnonymousLogger().log(Level.INFO, "Connection request: " + connectionString);
        socket = new Socket();
        try {
            String[] split = connectionString.split(":");
            if (split.length != 2) {
                errorText = "connectionString must be on form host:port";
                return false;
            }
            port = Integer.parseInt(split[1]);
            
            socket.connect(new InetSocketAddress(split[0], port));
        } catch (IOException ex) {
            errorText = ex.toString();
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.toString());
            return false;
        }
        try {
            prw = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.toString());
            errorText = ex.toString();
            return false;
        }
        try {
            brin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            errorText = ex.toString();
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.toString());
            return false;
        }
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    String s = "";
                    try {
                        s = brin.readLine();
                    } catch (IOException ex) {
                        Logger.getLogger(TelnetCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                    for (ReceiveEvent evt : rcvEventList) {
                        evt.receive(ConnectionType.Telnet, host, s);
                    }
                }
            }
        });
        rxThread.start();
        errorText = "Connected";
        return true;
    }

    @Override
    public boolean disconnect() {
        rxThread.stop();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    socket.close();
                } catch (IOException ex) {
                    errorText = ex.toString();
                    return;
                }
            }
        }).start();
        errorText = "Socket closed";
        return true;
    }

    /**
     *
     * @param s
     */
    @Override
    public boolean send(final String s) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (prw != null){
                prw.println(s+"\r\n");
                prw.flush();
                } else {
                    errorText = "Not connected";
                }
            }
        });
        t.start();
        errorText = "Sent";
        try {
            t.join(2000);
        } catch (InterruptedException ex) {
            errorText = ex.getMessage();
            return false;
        }
        return true;
    }

    @Override
    public void addReceiveEventHandler(ReceiveEvent evt) {
        rcvEventList.add(evt);
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public String getErrorText() {
       return errorText;
    }

    @Override
    public String getSourceId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}