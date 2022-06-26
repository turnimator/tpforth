/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ConnectionDisplayEvent;
import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.events.FileSaveEvent;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionEvent;
import com.turnimator.fide.events.SerialDisconnectEvent;
import com.turnimator.fide.events.TelnetConnectionEvent;
import com.turnimator.fide.events.TelnetDisconnectEvent;
import com.turnimator.fide.events.TransmitEvent;
import com.turnimator.fide.events.UploadEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author atle
 */
public class FrameMain extends JFrame {

    ConnectionType _connectionType;
    String _connectionSource;

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DataFlavor dataFlavor = DataFlavor.stringFlavor;
    
    private ArrayList<SerialConnectionEvent> serialConnectHandlerList = new ArrayList<>();
    private ArrayList<SerialDisconnectEvent> serialDisconnectHandlerList = new ArrayList<>();
    private ArrayList<TelnetConnectionEvent> telnetConnectHandlerList = new ArrayList<>();
    private ArrayList<TelnetDisconnectEvent> telnetDisconnectHandlerList = new ArrayList<>();

    private final ArrayList<ConnectionDisplayEvent> connectionDisplayHandlerList = new ArrayList<>();
    private final ArrayList<TransmitEvent> transmitEventHandlerList = new ArrayList<>();
    private final ArrayList<ConnectionCloseEvent> connectionCloseHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();

    private final ArrayList<FileOpenEvent> fileOpenHandlerList = new ArrayList<>();
    private final ArrayList<UploadEvent> uploadHandlerList = new ArrayList<>();
    private final ArrayList<FileSaveEvent> fileSaveHandlerList = new ArrayList<>();

    private final HashMap<String, PanelEditor> editorPanelMap = new HashMap<>();

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menuFile = new JMenu("File");
    private final JMenu menuEdit = new JMenu("Edit");
    private final JToolBar toolBar = new JToolBar("ToolBar", JToolBar.HORIZONTAL);
    private final JPanel panel = new JPanel();
    private final PanelConnections panelConnections = new PanelConnections();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel statusPanel = new JPanel();
    private final JLabel statusLabel = new JLabel();
    private final JProgressBar statusProgressBar = new JProgressBar();

    /**
     * There is one list of event handlers for FrameMain + ONE LIST OF EVENT
     * HANDLER FOR EACH CONNECTION
     */
    public FrameMain() {
        //super();
        setTitle("Fide Forth IDE");
        initComponents();
        setVisible(true);
        panelConnections.setVisible(false);
        panelConnections.addSerialConnectionEventHandler(new SerialConnectionEvent() {
            @Override
            public void connect(ConnectionType ct, String serialPort, int bitRate) {
                for (SerialConnectionEvent ev : serialConnectHandlerList) {
                    ev.connect(ct, serialPort, bitRate);
                }
            }
        });

        panelConnections.addSerialDisconnectEventHandler(new SerialDisconnectEvent() {
            @Override
            public void disconnect(String source) {
                for (SerialDisconnectEvent ev : serialDisconnectHandlerList) {
                    ev.disconnect(source);
                }
            }
        });
        panelConnections.addRescanHandler(new RescanEvent() {
            @Override
            public void rescan() {
                for (RescanEvent ev : rescanHandlerList) {
                    ev.rescan();
                }
            }
        });

        panelConnections.addTelnetConnectionHandler(new TelnetConnectionEvent() {
            @Override
            public void connect(String connectionString, int port) {
                for (TelnetConnectionEvent ev : telnetConnectHandlerList) {
                    ev.connect(connectionString, port);
                }
            }
        });

        panelConnections.addTelnetDisconnectHandler(new TelnetDisconnectEvent() {
            @Override
            public void disconnect(String source) {
                for (TelnetDisconnectEvent ev : telnetDisconnectHandlerList) {
                    ev.disconnect(source);
                }
            }
        });

    }

    private void addMenu() {
        menuBar.add(menuFile);
        JMenuItem itemNew = menuFile.add(new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionDisplayEvent ev : connectionDisplayHandlerList) {
                    ev.displayConnections(true);
                }
            }
        });
        JMenuItem itemOpen = menuFile.add(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (FileOpenEvent ev : fileOpenHandlerList) {
                    ev.open();
                }
            }
        });

        JMenuItem itemSave = menuFile.add(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (FileSaveEvent ev : fileSaveHandlerList) {
                    ev.save(_connectionSource);
                }
            }
        });

        menuFile.addSeparator();
        JMenuItem itemExit = menuFile.add(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuEdit.add(new JMenuItem(new AbstractAction("Copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelEditor editor = editorPanelMap.get(_connectionSource);
                if (editor != null){
                    String s = editor.getSelectedText();
                    clipboard.setContents(new StringSelection(s), new ClipboardOwner() {
                        @Override
                        public void lostOwnership(Clipboard clipboard, Transferable contents) {
                            
                        }
                    });
                }
            }
        }));
        menuEdit.add(new JMenuItem(new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelEditor editor = editorPanelMap.get(_connectionSource);
                if (editor != null){
                 String s = editor.getSelectedText();
                    clipboard.setContents(new StringSelection(s), new ClipboardOwner() {
                        @Override
                        public void lostOwnership(Clipboard clipboard, Transferable contents) {
                            
                        }
                    });
                    editor.deleteSelectedText();
                }
            }
        }));
        menuEdit.add(new JMenuItem(new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelEditor editor = editorPanelMap.get(_connectionSource);
                String data="";
                if (editor != null){
                    try {
                        data = (String) clipboard.getData(DataFlavor.stringFlavor);
                    } catch (UnsupportedFlavorException | IOException ex) {
                        Logger.getLogger(FrameMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    editor.paste(data);
                }
            }
        }));

        menuBar.add(menuEdit);
        JMenu menuConnection = new JMenu("Connection");
        menuBar.add(menuConnection);

        setJMenuBar(menuBar);
        menuBar.setVisible(true);

    }

    private void addToolBar() {
        add(toolBar, BorderLayout.PAGE_START);
        JButton buttonConnect = new JButton(new ImageIcon("icons/devices/modem-symbolic.symbolic.png"));
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionDisplayEvent ev : connectionDisplayHandlerList) {
                    ev.displayConnections(true);
                }
            }
        });
        buttonConnect.setToolTipText("Connect to Forth");
        toolBar.add(buttonConnect);

        JButton buttonUpload = new JButton(new ImageIcon("icons/actions/document-send-symbolic.symbolic.png"));
        buttonUpload.setToolTipText("Upload Forth code");
        buttonUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelEditor get = editorPanelMap.get(_connectionSource);
                String text = "";
                if (get != null) {
                    text = get.getEditorText();
                }
                for (UploadEvent ev : uploadHandlerList) {
                    ev.upload(_connectionType, _connectionSource, text);
                }
            }
        });
        toolBar.add(buttonUpload);

        JButton buttonWords = new JButton("W");
        buttonWords.setToolTipText("Retrieve list of Forth WORDS");
        toolBar.add(buttonWords);
        toolBar.setVisible(true);

    }

    private void initComponents() {

        setMinimumSize(new Dimension(600, 300));
        addMenu();
        addToolBar();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panelConnections);
        panelConnections.setMaximumSize(new Dimension(300, 180));
        panelConnections.setVisible(true);
        panel.add(tabbedPane);
        tabbedPane.setVisible(true);
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                _connectionSource = tabbedPane.getTitleAt(selectedIndex);
                Logger.getAnonymousLogger().log(Level.INFO, "Tab source " + _connectionSource);
                PanelEditor editor = (PanelEditor) tabbedPane.getComponentAt(selectedIndex);
                _connectionType = editor.getConnectionTyype();
            }
        });
        this.add(panel);
        addStatusLine();
    }

    public void addUploadHandler(UploadEvent ev) {
        uploadHandlerList.add(ev);
    }

    /**
     * We need to know which connection the user is working on right now
     *
     * @param ct
     */
    public void setConnectionType(ConnectionType ct) {
        _connectionType = ct;
    }

    /**
     * * We need to know which connection the user is working on right now
     *
     * @param sc
     */
    public void setConnectionSource(String sc) {
        _connectionSource = sc;
    }

    public void addFileOpenHandler(FileOpenEvent ev) {
        fileOpenHandlerList.add(ev);
    }

    public void addFileSaveHandler(FileSaveEvent ev) {
        fileSaveHandlerList.add(ev);
    }

    public void addRescanHandler(RescanEvent ev) {
        rescanHandlerList.add(ev);
    }

    public void addSerialConnectionCloseEventHandler(ConnectionCloseEvent ev) {
        connectionCloseHandlerList.add(ev);
    }

    public void addDisplayConnectionsHandler(ConnectionDisplayEvent ev) {
        connectionDisplayHandlerList.add(ev);
    }

    public void addSerialPortToList(String s) {
        panelConnections.addSerialPortToList(s);
    }

    public void addSerialConnectionEventHandler(SerialConnectionEvent ev) {
        serialConnectHandlerList.add(ev);
    }

    public void addSerialDisconnectEventHandler(SerialDisconnectEvent ev) {
        serialDisconnectHandlerList.add(ev);
    }

    public void addTelnetConnectionEventHandler(TelnetConnectionEvent ev) {
        telnetConnectHandlerList.add(ev);
    }

    public void addTelnetDisconnectEventHandler(TelnetDisconnectEvent ev) {
        telnetDisconnectHandlerList.add(ev);
    }

    public void addTransmitEventHandler(TransmitEvent ev) {
        transmitEventHandlerList.add(ev);
    }

    public void setEditorTab(ConnectionType ct, String source) {
        PanelEditor editorPanel = editorPanelMap.get(source);
        if (editorPanel == null) {
            editorPanel = new PanelEditor(ct, source);
            editorPanel.setMinimumSize(new Dimension(300, 400));
            editorPanelMap.put(source, editorPanel);
            tabbedPane.addTab(source, editorPanel);
            editorPanel.addTransmitEventHandler(new TransmitEvent() {
                @Override
                public void transmit(ConnectionType t, String source, String text) {
                    for (TransmitEvent ev : transmitEventHandlerList) {
                        ev.transmit(t, source, text);
                    }
                }
            });
            editorPanel.addCloseEventHandler(new ConnectionCloseEvent() {
                @Override
                public void close(ConnectionType ct, String source) {
                    for (ConnectionCloseEvent ev : connectionCloseHandlerList) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Close Request from editorPanel");
                        ev.close(ct, source);
                    }
                }
            });
        }
        _connectionSource = source;
        _connectionType = ct;
    }

    public void removeEditorTab(ConnectionType ct, String source) {

        PanelEditor get = editorPanelMap.get(source);
        if (get != null) {
            tabbedPane.remove(get);
        } else {
            Logger.getAnonymousLogger().log(Level.SEVERE, "EditorPanel does not contain tab:" + source);

        }
        editorPanelMap.remove(source);
        _connectionSource = "No connection";
        _connectionType = ConnectionType.Undefined;
    }

    public void setConnectionsVisible(boolean b) {
        panelConnections.setVisible(b);
    }

    public void appendResponseText(ConnectionType ct, String source, String text) {
        PanelEditor editor = editorPanelMap.get(source);
        if (editor == null) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Unknown source:" + source + ":" + text);
            setEditorTab(ct, source);
            editor = editorPanelMap.get(source);
        }
        editor.appendText(text);
    }

    public void clearSerialPortList() {
        panelConnections.clearSerialPortsList();
    }

    public void appendProgramText(ConnectionType _connectionType, String _connectionSource, String string) {
        appendResponseText(_connectionType, _connectionSource, string);
    }

    public void setPogress(int max, int min, int i) {
        statusProgressBar.setMaximum(max);
        statusProgressBar.setMinimum(min);
        statusProgressBar.setValue(i);
        statusPanel.invalidate();
        Thread.yield();
    }

    private void addStatusLine() {
        statusPanel.setLayout(new FlowLayout());
        statusPanel.add(statusLabel);
        statusPanel.add(statusProgressBar);
        panel.add(statusPanel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public String getEditorContent() {

        PanelEditor editor = editorPanelMap.get(_connectionSource);
        if (editor != null) {
            return editor.getEditorText();
        }
        return ("Could not get editor text");
    }
}
