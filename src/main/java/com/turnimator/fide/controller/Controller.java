/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.controller;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.events.ConnectionsDisplayEvent;
import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.enums.ResponseOutputType;
import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.events.FileSaveEvent;
import com.turnimator.fide.events.ReceiveEvent;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionRequestEvent;
import com.turnimator.fide.events.TelnetConnectionRequestEvent;
import com.turnimator.fide.events.TransmitEvent;
import com.turnimator.fide.events.UploadEvent;
import com.turnimator.fide.events.WordsRequestEvent;
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
public final class Controller {
    String lastDirectory = ".";
    FrameMain _frameMain;
    CommunicationDispatcher _dispatcher = new CommunicationDispatcher();
    
    public Controller() {
        lastDirectory = System.getProperty("LastDir");

        _frameMain = new FrameMain();
        addEventHandlers();
        _frameMain.setVisible(true);
        _dispatcher.createCommunicator(ConnectionType.Serial, "", "");
        _dispatcher.addReceiveEventHandler(new ReceiveEvent() {
            @Override
            public void receive(ConnectionId id, String text) {
                _frameMain.appendResponseText(id, text);
            }
        });
        scanPorts();
        _frameMain.addEditorTab(new ConnectionId(ConnectionType.None, "Scratchpad"));
    }

    void scanPorts() {
        _frameMain.clearSerialPortList();
        for (String s : _dispatcher.getPorts()) {
            _frameMain.addSerialPortToList(s);
        }
    }

    private void addFileEventHandlers() {
        _frameMain.addFileOpenHandler(new FileOpenEvent() {
            @Override
            public void open() {
                FileDialog f = new FileDialog(_frameMain, "Open", LOAD);
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
                ConnectionId id = _dispatcher.getConnectionId();
                if (id != null) {
                    _frameMain.setConnectionId(id);
                }
                String file = f.getDirectory() + "/" + f.getFile();
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    Stream<String> lines = br.lines();
                    for (Object o : lines.toArray()) {
                        _frameMain.appendProgramText((String) o);
                    }
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(_frameMain, ex.toString());
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(_frameMain, "File not found");
                }

            }
        });

        _frameMain.addFileSaveHandler(new FileSaveEvent() {
            @Override
            public void save(ConnectionId source) {
                FileDialog f = new FileDialog(_frameMain, "Open", FileDialog.SAVE);
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
                    String s = _frameMain.getEditorContent();
                    pw.println(s);
                    pw.flush();
                    pw.close();
                } catch (FileNotFoundException ex) {
                    _frameMain.setStatus(ex.toString());
                }

            }

        });
    }

    private void addConnectionEventHandlers() {
        _frameMain.addRescanHandler(new RescanEvent() {
            @Override
            public void rescan() {
                scanPorts();
            }
        });
        _frameMain.addTelnetConnectionRequestHandler(new TelnetConnectionRequestEvent() {
            @Override
            public void connect(String host, String port) {
                /**
                 * TELNET COMMUNICATOR AND EDITOR ADDED TO THE TABBED PANE
                 */
                _dispatcher.createCommunicator(ConnectionType.Telnet, host, port);
                ConnectionId connectionId = _dispatcher.connect();
                if (connectionId == null) {
                    JOptionPane.showMessageDialog(_frameMain, "Connection failed", "Error", JOptionPane.OK_OPTION);
                    return;
                }
                _frameMain.setConnectionsVisible(false);
                _frameMain.addEditorTab(connectionId);
                _frameMain.setEditorTab(connectionId);
            }
        });

        _frameMain.addSerialConnectionRequestHandler(new SerialConnectionRequestEvent() {

            @Override
            public void connect(String port, int bitRate) {
                _dispatcher.createCommunicator(ConnectionType.Serial, "", port);
                ConnectionId connectionId = _dispatcher.connect();
                if (connectionId == null) {
                    JOptionPane.showMessageDialog(_frameMain, "Connection failed", "Error", JOptionPane.OK_OPTION);
                    return;
                }
                _frameMain.setConnectionsVisible(false);
                _frameMain.addEditorTab(connectionId);
                _frameMain.setEditorTab(connectionId);
            }
        });
        _frameMain.addConnectionCloseEventHandler(new ConnectionCloseEvent() {
            @Override
            public void close(ConnectionId id) {
                _dispatcher.disconnect(id);
                _frameMain.removeEditorTab(id);
            }
        });

    }

    private void addEventHandlers() {
        addFileEventHandlers();
        addConnectionEventHandlers();
        _frameMain.addUploadRequestHandler(new UploadEvent() {
            @Override
            public void upload(ConnectionId id, String text) {
                _dispatcher.send(id, text);
            }
        });
        /**
         * Listening for clicks on the W toolbar icon
         */
        _frameMain.addWordsRequestHandler(new WordsRequestEvent() {
            @Override
            public void requestWords(ConnectionId id) {
                _frameMain.setOutputType(ResponseOutputType.Words);
                _dispatcher.send(id, "words");
            }
        });
        /**
         * This is called in response to a click on the Connection toolbar or
         * menu
         */
        _frameMain.addDisplayConnectionsRequestHandler(new ConnectionsDisplayEvent() {
            @Override
            public void setVisible(boolean b) {
                _frameMain.setConnectionsVisible(b);
            }
        });
        _frameMain.addTransmitEventHandler(new TransmitEvent() {
            @Override
            public void transmit(ConnectionId id, String text) {
                _dispatcher.send(id, text);
            }
        });
    }
}
