/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ConnectionsDisplayEvent;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.events.FileSaveEvent;
import com.turnimator.fide.events.RescanEvent;
import com.turnimator.fide.events.SerialConnectionRequestEvent;
import com.turnimator.fide.events.DisconnectEvent;
import com.turnimator.fide.events.TelnetConnectionRequestEvent;
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
import java.io.File;
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

    private final HashMap<ConnectionId, PanelEditor> editorPanelMap = new HashMap<>();
    private PanelEditor _currentEditorPanel = null;

    String slash = System.getProperty("file.separator");

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DataFlavor dataFlavor = DataFlavor.stringFlavor;

    private ArrayList<SerialConnectionRequestEvent> serialConnectRequestHandlerList = new ArrayList<>();
    private ArrayList<DisconnectEvent> disconnectHandlerList = new ArrayList<>();
    private ArrayList<TelnetConnectionRequestEvent> telnetConnectRequestHandlerList = new ArrayList<>();

    private final ArrayList<ConnectionsDisplayEvent> connectionDisplayHandlerList = new ArrayList<>();
    private final ArrayList<TransmitEvent> transmitEventHandlerList = new ArrayList<>();
    private final ArrayList<ConnectionCloseEvent> connectionCloseHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> rescanHandlerList = new ArrayList<>();

    private final ArrayList<FileOpenEvent> fileOpenHandlerList = new ArrayList<>();
    private final ArrayList<UploadEvent> _uploadRequestHandlerList = new ArrayList<>();
    private final ArrayList<FileSaveEvent> fileSaveHandlerList = new ArrayList<>();

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

    private PanelEditor ensurePanelEditor(ConnectionId id) {
        if (!id.equals(_currentEditorPanel.getConnectionId())) {
            _currentEditorPanel = editorPanelMap.get(id);
        }
        return _currentEditorPanel;
    }

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
        panelConnections.addSerialConnectionEventHandler(new SerialConnectionRequestEvent() {
            @Override
            public void connect(String serialPort, int bitRate) {
                for (SerialConnectionRequestEvent ev : serialConnectRequestHandlerList) {
                    ev.connect(serialPort, bitRate);
                }
            }
        });

        panelConnections.addDisconnectEventHandler(new DisconnectEvent() {
            @Override
            public void disconnect(String source) {
                for (DisconnectEvent ev : disconnectHandlerList) {
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

        panelConnections.addTelnetConnectionHandler(new TelnetConnectionRequestEvent() {
            @Override
            public void connect(String host, String port) {
                for (TelnetConnectionRequestEvent ev : telnetConnectRequestHandlerList) {
                    ev.connect(host, port);
                }
            }
        });

    }

    private void addMenu() {
        menuBar.add(menuFile);
        JMenuItem itemNew = menuFile.add(new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionsDisplayEvent ev : connectionDisplayHandlerList) {
                    ev.setVisible(true);
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
                    ev.save(_currentEditorPanel.getConnectionId());
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
                PanelEditor editor = _currentEditorPanel;
                if (editor != null) {
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
                PanelEditor editor = _currentEditorPanel;
                if (editor != null) {
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
                PanelEditor editor = _currentEditorPanel;
                String data = "";
                if (editor != null) {
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
        String class_path = System.getProperty("java.class.path");
        System.out.println(class_path);
        slash = File.separator;

        String exec_path = class_path.split(File.pathSeparator)[0];
        if (exec_path.endsWith(".jar")) {
            exec_path = new File(exec_path).getParent();
        }

        System.out.println("Exec_path: " + exec_path);

        String icon_path = exec_path + slash + ".." + slash;
        if (exec_path.endsWith("classes")) {
            icon_path = icon_path + ".." + slash;
        }
        icon_path += "icons" + slash;

        System.out.println("Icon_path: " + icon_path);

        add(toolBar, BorderLayout.PAGE_START);
        JButton buttonConnect = new JButton(new ImageIcon(icon_path + "devices" + slash + "modem-symbolic.symbolic.png"));
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionsDisplayEvent ev : connectionDisplayHandlerList) {
                    ev.setVisible(true);
                }
            }
        });
        buttonConnect.setToolTipText("Connect to Forth");
        toolBar.add(buttonConnect);

        JButton buttonUpload = new JButton(new ImageIcon(icon_path + "actions" + slash + "document-send-symbolic.symbolic.png"));
        buttonUpload.setToolTipText("Upload Forth code");
        buttonUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelEditor get = _currentEditorPanel;
                String text = "";
                if (get != null) {
                    text = get.getEditorText();
                }
                for (UploadEvent ev : _uploadRequestHandlerList) {
                    ev.upload(_currentEditorPanel.getConnectionId(), text);
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
                // Logger.getAnonymousLogger().log(Level.INFO, "Tab source " + _connectionSource);
                _currentEditorPanel = (PanelEditor) tabbedPane.getComponentAt(selectedIndex);
                
            }
        });
        this.add(panel);
        addStatusLine();
    }

    public void addUploadRequestHandler(UploadEvent ev) {
        _uploadRequestHandlerList.add(ev);
    }

    public void setConnectionId(ConnectionId sc) {
        ensurePanelEditor(sc);
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

    public void addConnectionCloseEventHandler(ConnectionCloseEvent ev) {
        connectionCloseHandlerList.add(ev);
    }

    public void addDisplayConnectionsRequestHandler(ConnectionsDisplayEvent ev) {
        connectionDisplayHandlerList.add(ev);
    }

    public void addSerialPortToList(String s) {
        panelConnections.addSerialPortToList(s);
    }

    public void addSerialConnectionRequestHandler(SerialConnectionRequestEvent ev) {
        serialConnectRequestHandlerList.add(ev);
    }

    public void addDisconnectEventHandler(DisconnectEvent ev) {
        disconnectHandlerList.add(ev);
    }

    public void addTelnetConnectionRequestHandler(TelnetConnectionRequestEvent ev) {
        telnetConnectRequestHandlerList.add(ev);
    }

    public void addTransmitEventHandler(TransmitEvent ev) {
        transmitEventHandlerList.add(ev);
    }

    public void setEditorTab(ConnectionId id) {
        ensurePanelEditor(id);
        int idx = 0;
        for (idx = 0; idx < tabbedPane.getTabCount(); idx++) {
            String titleAt = tabbedPane.getTitleAt(idx);
            if (titleAt.equals(id.toString())) {
                break;
            }
        }
        tabbedPane.setSelectedIndex(idx);
    }

    public void addEditorTab(ConnectionId id) {
        _currentEditorPanel = new PanelEditor(id);
        _currentEditorPanel.setMinimumSize(new Dimension(300, 400));
        editorPanelMap.put(id, _currentEditorPanel);
        tabbedPane.addTab(id.toString(), _currentEditorPanel);
        _currentEditorPanel.addTransmitEventHandler(new TransmitEvent() {
            @Override
            public void transmit(ConnectionId id, String text) {
                Logger.getAnonymousLogger().log(Level.INFO, "TransmitRequest from " + id + ": "+text);
                for (TransmitEvent ev : transmitEventHandlerList) {
                    ev.transmit(id, text);
                }
            }
        });
        _currentEditorPanel.addCloseEventHandler(new ConnectionCloseEvent() {
            @Override
            public void close(ConnectionId id) {
                for (ConnectionCloseEvent ev : connectionCloseHandlerList) {
                    Logger.getAnonymousLogger().log(Level.INFO, "Close Request from editorPanel");
                    ev.close(id);
                }
            }
        });
    }

    public void removeEditorTab(ConnectionId id) {

        PanelEditor get = ensurePanelEditor(id);
        if (get != null) {
            tabbedPane.remove(get);
        } else {
            Logger.getAnonymousLogger().log(Level.SEVERE, "EditorPanel does not contain tab:" + id);

        }
        editorPanelMap.remove(id);
        if (editorPanelMap.isEmpty()) {
            _currentEditorPanel = null;
        } else {
            int i = tabbedPane.getSelectedIndex();
            if (i != -1) {
                _currentEditorPanel = (PanelEditor) tabbedPane.getSelectedComponent();
            }
        }
    }

    public void setConnectionsVisible(boolean b) {
        panelConnections.setVisible(b);
    }

    public void appendResponseText(ConnectionId id, String text) {
        PanelEditor editor = ensurePanelEditor(id);
        _currentEditorPanel.appendText(text);
    }

    public void clearSerialPortList() {
        panelConnections.clearSerialPortsList();
    }

    public void appendProgramText(String string) {
        appendResponseText(_currentEditorPanel.getConnectionId(), string);
    }

    public void setPogress(int max, int min, int i) {
        statusProgressBar.setMaximum(max);
        statusProgressBar.setMinimum(min);
        statusProgressBar.setValue(i);
        statusPanel.invalidate();
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
        if (_currentEditorPanel != null) {
            return _currentEditorPanel.getEditorText();
        }
        return null;
    }
}
