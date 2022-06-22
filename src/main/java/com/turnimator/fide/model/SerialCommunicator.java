/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turnimator.fide.model;

import com.fazecast.jSerialComm.SerialPort;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.ProgressEvent;
import com.turnimator.fide.events.ReceiveEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atle
 */
public class SerialCommunicator implements CommunicatorInterface {

    ArrayList<ProgressEvent> progressListeners = new ArrayList<>();

    String errorText = "";
    int _bitrate = 115200;
    ArrayList<String> ports = new ArrayList<>();
    SerialPort commPort = null;
    ArrayList<ReceiveEvent> recvList = new ArrayList<>();
    InputStream is = null;
    BufferedReader br = null;
    PrintWriter pw;

    @Override
    public void addReceiveEventHandler(ReceiveEvent evt) {
        recvList.add(evt);
    }

    public void addProgressListener(ProgressEvent evt) {
        progressListeners.add(evt);
    }

    public void setBitrate(int bitrate) {
        _bitrate = bitrate;
    }

    public ArrayList<String> getPorts() {
        ports.clear();
        var commPorts = Arrays.asList(SerialPort.getCommPorts());
        commPorts.forEach(p -> ports.add(p.getSystemPortName()));
        return ports;
    }

    @Override
    public boolean connect(String connectionString) {
        commPort = SerialPort.getCommPort(connectionString);

        commPort.openPort();
        commPort.setBaudRate(115200);
        commPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING, 2000, 2000);
        System.out.println("Connecting " + connectionString + " at " + _bitrate + "");
        if (!commPort.isOpen()) {
            errorText = "Connection failed";
            return false;
        }
        pw = new PrintWriter(commPort.getOutputStream());
        br = new BufferedReader(new InputStreamReader(commPort.getInputStream()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    String s = "";
                    try {
                        s = br.readLine();
                    } catch (IOException ex) {
                        continue;
                    }
                    if (s != null && s.length() > 0) {
                        for (ReceiveEvent evt : recvList) {
                            evt.receive(ConnectionType.Serial, commPort.getSystemPortName(), s);
                        }
                    }
                }
            }
        }).start();
        errorText = "Connected";
        return true;
    }

    @Override
    public boolean disconnect() {

        boolean closePort = commPort.closePort();
        commPort.removeDataListener();
        try {
            br.close();
        } catch (IOException ex) {
            errorText = ex.toString();
            return false;
        }
        pw.close();

        errorText = "Disconnected";
        return true;
    }

    @Override
    public boolean send(String s) {
        if (commPort == null) {
            errorText = "Must connect to port first!";
            return false;
        }
        if (!commPort.isOpen()) {
            errorText = "Port is not open (is another process using it?)";
            return false;
        }
        String[] split = s.split("\n");
        int max = split.length;
        int min = 0;
        int i = 0;
        for (String s1 : split) {
            for (ProgressEvent evt : progressListeners) {
                evt.progress(max, min, i++);
            }
            pw.println(s1);
            pw.flush();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    @Override
    public boolean isOpen() {
        return commPort.isOpen();
    }

    @Override
    public String getErrorText() {
        return errorText;
    }

    @Override
    public String getSourceId() {
        if (commPort == null) {
            return "UNCONNECTED";
        } else {
            return commPort.getSystemPortName();
        }
    }

}
