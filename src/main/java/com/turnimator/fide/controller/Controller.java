/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.controller;

import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ConnectionDisplayEvent;
import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.events.ProgressEvent;
import com.turnimator.fide.events.ReceiveEvent;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionEvent;
import com.turnimator.fide.events.TransmitEvent;
import com.turnimator.fide.events.UploadEvent;
import com.turnimator.fide.model.SerialCommunicator;
import com.turnimator.fide.view.FrameMain;
import java.awt.FileDialog;
import static java.awt.FileDialog.LOAD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

/**
 *
 * @author atle
 */
public class Controller {

    ConnectionType _connectionType = ConnectionType.Undefined;
    String _connectionSource = "NO CONNECTION";
    String lastDirectory = ".";

    FrameMain frameMain;
    HashMap<String, SerialCommunicator> serialCommunicatorMap; // For Transmitting and receiving to individual panels
    SerialCommunicator serialCommunicator = new SerialCommunicator(); // For managing Connect, Disconnect to add/remove communicators

    public Controller() {
        serialCommunicatorMap = new HashMap<>();
        frameMain = new FrameMain();
        addEventHandlers();
        frameMain.setVisible(true);
        rescanSerialPorts();
    }

    void rescanSerialPorts() {
        frameMain.clearSerialPortList();
        for (String s : serialCommunicator.getPorts()) {
            frameMain.addSerialPortToList(s);
        }
    }

    private void addFileEventHandlers() {
        frameMain.addFileOpenHandler(new FileOpenEvent() {
            @Override
            public void open() {
                FileDialog f = new FileDialog(frameMain, "Open", LOAD);
                f.setDirectory(lastDirectory);
                f.setFilenameFilter(new FilenameFilter() {
                    @Override
                    /**
                     * These are the file endings for Forth source I can think
                     * of. Please, feel free to add more.
                     */
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(".fs")) {
                            return true;
                        } else if (name.endsWith(".forth")) {
                            return true;
                        } else if (name.endsWith(".fth")) {
                            return true;
                        } else {

                            return false;
                        }
                    }
                });
                f.setVisible(true);
                lastDirectory = f.getDirectory();

                String file = f.getDirectory() + "/" + f.getFile();
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    Stream<String> lines = br.lines();
                    for (Object o : lines.toArray()) {
                        frameMain.appendProgramText(_connectionType, _connectionSource, (String) o);
                    }
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frameMain, ex.toString());
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(frameMain, "File not found");
                }

            }
        });
    }

    private void addConnectionEventHandlers() {
        frameMain.addRescanHandler(new RescanEvent() {
            @Override
            public void rescan() {
                rescanSerialPorts();
            }
        });

        frameMain.addSerialConnectionEventHandler(new SerialConnectionEvent() {

            @Override
            public void connect(ConnectionType ct, String serialPort, int bitRate) {
                SerialCommunicator sc = new SerialCommunicator();

                if (!sc.connect(serialPort)) {
                    JOptionPane.showMessageDialog(frameMain, "Connection failed!");
                    return;
                }
                sc.setBitrate(bitRate);
                sc.addReceiveEventHandler(new ReceiveEvent() {
                    @Override
                    public void receive(ConnectionType ct, String source, String text) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Received " + text);
                        frameMain.appendResponseText(ct, source, text);
                    }
                });
                sc.addProgressListener(new ProgressEvent() {
                    @Override
                    public void progress(int max, int min, int i) {
                        frameMain.setPogress(max, min, i);
                    }
                    
                });
                
                frameMain.setEditorTab(ConnectionType.Serial, serialPort);
                frameMain.addConnectionCloseEventHandler(new ConnectionCloseEvent() {
                    @Override
                    public void close(ConnectionType ct, String source) {
                        frameMain.removeEditorTab(ct, source);
                        sc.disconnect();
                    }
                });

                frameMain.setConnectionsVisible(false);
                serialCommunicatorMap.put(serialPort, sc);
                _connectionType = ct;
                _connectionSource = serialPort;
            }
        });

    }

    private void addEventHandlers() {
        addFileEventHandlers();
        addConnectionEventHandlers();
        frameMain.addUploadHandler(new UploadEvent() {
            @Override
            public void upload(ConnectionType ct, String source, String text) {
                
                switch (ct) {
                    case Serial:
                        Logger.getAnonymousLogger().log(Level.INFO, "Controller  sending to " + source);
                        SerialCommunicator get = serialCommunicatorMap.get(source);
                        if (get != null){
                            if ( ! get.send(text)){
                                Logger.getAnonymousLogger().log(Level.WARNING, get.getErrorText());
                            }
                        }
                        break;
                    case Telnet:
                        break;
                    case Undefined:
                    default:
                        Logger.getAnonymousLogger().log(Level.SEVERE, "Connection type: UNDEFINED");
                }

            }
        });
        /**
         * This is called in response to a click on the Connection toolbar or
         * menu
         */
        frameMain.addDisplayConnectionsHandler(new ConnectionDisplayEvent() {
            @Override
            public void displayConnections(boolean b) {
                frameMain.setConnectionsVisible(true);
            }
        });
        frameMain.addTransmitEventHandler(new TransmitEvent() {
            @Override
            public void transmit(ConnectionType t, String source, String text) {
                switch (t) {
                    case Serial:
                        SerialCommunicator get = serialCommunicatorMap.get(source);
                        if (get != null) {
                            get.send(text);
                        }
                        break;
                    case Telnet:
                        break;
                    case Undefined:
                        break;

                }
            }
        });
    }
}
