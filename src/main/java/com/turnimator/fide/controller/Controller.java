/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.controller;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.events.ConnectionsDisplayEvent;
import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.events.FileSaveEvent;
import com.turnimator.fide.events.ReceiveEvent;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionRequestEvent;
import com.turnimator.fide.events.TelnetConnectionRequestEvent;
import com.turnimator.fide.events.TransmitEvent;
import com.turnimator.fide.events.UploadEvent;
import com.turnimator.fide.view.FrameMain;
import java.awt.FileDialog;
import static java.awt.FileDialog.LOAD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;
import javax.swing.JOptionPane;

/**
 *
 * @author atle
 */
public class Controller {

    String lastDirectory = ".";
    FrameMain frameMain;
    CommunicationDispatcher _dispatcher = new CommunicationDispatcher();

    public Controller() {
        lastDirectory = System.getProperty("LastDir");

        frameMain = new FrameMain();
        addEventHandlers();
        frameMain.setVisible(true);
        _dispatcher.createCommunicator(ConnectionType.Serial, "", "");
        _dispatcher.addReceiveEventHandler(new ReceiveEvent() {
            @Override
            public void receive(ConnectionId id, String text) {
                frameMain.appendResponseText(id, text);
            }
        });
        scanPorts();
        frameMain.addEditorTab(new ConnectionId(ConnectionType.Undefined, "Scratchpad"));
    }

    void scanPorts() {
        frameMain.clearSerialPortList();
        for (String s : _dispatcher.getPorts()) {
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

                System.setProperty("LastDir", lastDirectory);
                frameMain.setConnectionId(_dispatcher.getConnectionId());
                String file = f.getDirectory() + "/" + f.getFile();
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    Stream<String> lines = br.lines();
                    for (Object o : lines.toArray()) {
                        frameMain.appendProgramText((String) o);
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

        frameMain.addFileSaveHandler(new FileSaveEvent() {
            @Override
            public void save(ConnectionId source) {
                FileDialog f = new FileDialog(frameMain, "Open", FileDialog.SAVE);
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

                System.setProperty("LastDir", lastDirectory);

                String file = f.getDirectory() + "/" + f.getFile();
                try {
                    PrintWriter pw = new PrintWriter(file);
                    String s = frameMain.getEditorContent();
                    pw.println(s);
                    pw.flush();
                    pw.close();
                } catch (FileNotFoundException ex) {
                    frameMain.setStatus(ex.toString());
                }

            }

        });
    }

    private void addConnectionEventHandlers() {
        frameMain.addRescanHandler(new RescanEvent() {
            @Override
            public void rescan() {
                scanPorts();
            }
        });
        frameMain.addTelnetConnectionRequestHandler(new TelnetConnectionRequestEvent() {
            @Override
            public void connect(String host, String port) {
                /**
                 * TELNET COMMUNICATOR AND EDITOR ADDED TO THE TABBED PANE
                 */
                _dispatcher.createCommunicator(ConnectionType.Telnet, host, port);
                ConnectionId connectionId = _dispatcher.connect();

                frameMain.setConnectionsVisible(false);
                frameMain.addEditorTab(connectionId);
            }
        });

        frameMain.addSerialConnectionRequestHandler(new SerialConnectionRequestEvent() {

            @Override
            public void connect(String port, int bitRate) {
                _dispatcher.createCommunicator(ConnectionType.Serial, "", port);
                ConnectionId connectionId = _dispatcher.connect();

                frameMain.setConnectionsVisible(false);
                frameMain.addEditorTab(connectionId);
            }
        });

    }

    private void addEventHandlers() {
        addFileEventHandlers();
        addConnectionEventHandlers();
        frameMain.addUploadRequestHandler(new UploadEvent() {
            @Override
            public void upload(ConnectionId id, String text) {
                _dispatcher.send(id, text);
            }
        });
        /**
         * This is called in response to a click on the Connection toolbar or
         * menu
         */
        frameMain.addDisplayConnectionsRequestHandler(new ConnectionsDisplayEvent() {
            @Override
            public void setVisible(boolean b) {
                frameMain.setConnectionsVisible(true);
            }
        });
        frameMain.addTransmitEventHandler(new TransmitEvent() {
            @Override
            public void transmit(ConnectionId id, String text) {
                _dispatcher.send(id, text);
            }
        });
    }
}
