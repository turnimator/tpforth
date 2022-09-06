/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turnimator.fide.model;

import com.fazecast.jSerialComm.SerialPort;
import static com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING;
import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.events.ProgressEvent;
import com.turnimator.fide.events.ReceiveEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atle
 */
public class SerialCommunicator implements CommunicatorInterface {

    private String _host = "";
    private String _port = "";

    private final ArrayList<ProgressEvent> progressListeners = new ArrayList<>();

    private String errorText = "";
    private int _bitrate = 115200;
    private final ArrayList<String> ports = new ArrayList<>();
    private SerialPort commPort = null;
    private final ArrayList<ReceiveEvent> recvList = new ArrayList<>();
    private final InputStream is = null;
    private BufferedReader br = null;
    private PrintWriter pw;
    private boolean _stopFlag = false;
    private String _id;

    public SerialCommunicator(String port) {
        _port = port;
        _id = "Serial:" + port;

    }

    public void addReceiveEventHandler(ReceiveEvent evt) {
        recvList.add(evt);
    }

    public void addProgressListener(ProgressEvent evt) {
        progressListeners.add(evt);
    }

    public void setBitrate(int bitrate) {
        _bitrate = bitrate;
    }

    public ArrayList<String> getPorts(String host) {
        ports.clear();
        List<SerialPort> commPorts = Arrays.asList(SerialPort.getCommPorts());
        for (SerialPort p : commPorts) {
            ports.add(p.getSystemPortName());
        }
        return ports;
    }

    /**
     *
     * @param connectionString should contain bitrate if non-default
     * @return
     */
    public String connect(String port) {
        _port = port;
        commPort = SerialPort.getCommPort(_port);

        commPort.openPort();
        commPort.setBaudRate(115200);
        commPort.setComPortTimeouts(TIMEOUT_READ_BLOCKING, 2000, 2000);
        System.out.println("Connecting " + _port + " at " + _bitrate + "");
        if (!commPort.isOpen()) {
            errorText = "Connection failed";
            return null;
        }
        pw = new PrintWriter(commPort.getOutputStream());
        br = new BufferedReader(new InputStreamReader(commPort.getInputStream()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; !_stopFlag;) {
                    String s = "";
                    try {
                        s = br.readLine();
                    } catch (IOException ex) {
                        continue;
                    }
                    if (s != null && s.length() > 0) {
                        
                        Logger.getAnonymousLogger().log(Level.INFO, "RX:"+s);
                        for (ReceiveEvent evt : recvList) {
                            evt.receive(_id, s);
                        }
                    }
                }
                commPort.closePort();
            }
        }).start();
        errorText = "Connected";

        return _id;
    }

    public boolean disconnect() {
        _stopFlag = true;
        return true;
    }

    public boolean send(String s) {
        Logger.getAnonymousLogger().log(Level.INFO, "PORT:" + _port + "TX:" + s);
        if (commPort == null) {
            errorText = "Must connect to port first!";
            Logger.getAnonymousLogger().log(Level.SEVERE, errorText);
            return false;
        }
        if (!commPort.isOpen()) {
            errorText = "Port is not open (is another process using it?)";
            Logger.getAnonymousLogger().log(Level.SEVERE, errorText);
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

    public boolean isOpen() {
        return commPort.isOpen();
    }

    public String getErrorText() {
        return errorText;
    }

    public String getId() {
        return _id;
    }

    @Override
    public void setHost(String host) {
        _host = host;
    }

    @Override
    public void setPort(String port) {
        _port = port;
    }

}
