/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.enums.ResponseOutputType;
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
import com.turnimator.fide.events.WordClickEvent;
import com.turnimator.fide.events.WordsRequestEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author atle
 */
public class FrameMain extends JFrame {

    private ResponseOutputType _responseOutputType = ResponseOutputType.Editor;

    private final HashMap<ConnectionId, PanelEditor> _editorPanelMap = new HashMap<>();
    private PanelEditor _currentEditorPanel = null;

    String slash = System.getProperty("file.separator");

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DataFlavor dataFlavor = DataFlavor.stringFlavor;

    private ArrayList<SerialConnectionRequestEvent> _serialConnectRequestHandlerList = new ArrayList<>();
    private ArrayList<DisconnectEvent> _disconnectHandlerList = new ArrayList<>();
    private ArrayList<TelnetConnectionRequestEvent> _telnetConnectRequestHandlerList = new ArrayList<>();

    private final ArrayList<ConnectionsDisplayEvent> _connectionDisplayHandlerList = new ArrayList<>();
    private final ArrayList<TransmitEvent> _transmitEventHandlerList = new ArrayList<>();
    private final ArrayList<ConnectionCloseEvent> _connectionCloseHandlerList = new ArrayList<>();
    private final ArrayList<RescanEvent> _rescanHandlerList = new ArrayList<>();

    private final ArrayList<WordsRequestEvent> _wordsRequestHandlerList = new ArrayList<>();

    public void addWordsRequestHandler(WordsRequestEvent ev) {
        _wordsRequestHandlerList.add(ev);
    }

    public void bubbleWordsRequest(ConnectionId id) {
        for (WordsRequestEvent ev : _wordsRequestHandlerList) {
            ev.requestWords(id);
        }
    }

    private final ArrayList<FileOpenEvent> _fileOpenHandlerList = new ArrayList<>();
    private final ArrayList<UploadEvent> _uploadRequestHandlerList = new ArrayList<>();
    private final ArrayList<FileSaveEvent> _fileSaveHandlerList = new ArrayList<>();

    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu menuFile = new JMenu("File");
    private final JMenu menuEdit = new JMenu("Edit");
    private final JToolBar _toolBar = new JToolBar("ToolBar", JToolBar.HORIZONTAL);
    private final JPanel _mainPanel = new JPanel();
    private final JPanel _mainPanelLeft = new JPanel();
    private final JPanel _mainPanelRight = new JPanel();
    private final PanelConnections _panelConnections = new PanelConnections();
    private final JTabbedPane _tabbedEditorPane = new JTabbedPane();
    private final JPanel _statusPanel = new JPanel();
    private final JLabel _statusLabel = new JLabel();
    private final JProgressBar _statusProgressBar = new JProgressBar();
    private PanelWords _panelWords;

    private PanelEditor ensurePanelEditor(ConnectionId id) {
        if (!id.equals(_currentEditorPanel.getConnectionId())) {
            _currentEditorPanel = _editorPanelMap.get(id);
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
        _panelConnections.setVisible(false);
        _panelConnections.addSerialConnectionEventHandler(new SerialConnectionRequestEvent() {
            @Override
            public void connect(String serialPort, int bitRate) {
                for (SerialConnectionRequestEvent ev : _serialConnectRequestHandlerList) {
                    ev.connect(serialPort, bitRate);
                }
            }
        });

        _panelConnections.addDisconnectEventHandler(new DisconnectEvent() {
            @Override
            public void disconnect(String source) {
                for (DisconnectEvent ev : _disconnectHandlerList) {
                    ev.disconnect(source);
                }
            }
        });
        _panelConnections.addRescanHandler(new RescanEvent() {
            @Override
            public void rescan() {
                for (RescanEvent ev : _rescanHandlerList) {
                    ev.rescan();
                }
            }
        });

        _panelConnections.addTelnetConnectionHandler(new TelnetConnectionRequestEvent() {
            @Override
            public void connect(String host, String port) {
                for (TelnetConnectionRequestEvent ev : _telnetConnectRequestHandlerList) {
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
                for (ConnectionsDisplayEvent ev : _connectionDisplayHandlerList) {
                    ev.setVisible(true);
                }
            }
        });
        JMenuItem itemOpen = menuFile.add(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (FileOpenEvent ev : _fileOpenHandlerList) {
                    ev.open();
                }
            }
        });

        JMenuItem itemSave = menuFile.add(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (FileSaveEvent ev : _fileSaveHandlerList) {
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

        add(_toolBar, BorderLayout.PAGE_START);
        JButton buttonConnect = new JButton(new ImageIcon(icon_path + "devices" + slash + "modem-symbolic.symbolic.png"));
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionsDisplayEvent ev : _connectionDisplayHandlerList) {
                    ev.setVisible(!_panelConnections.isVisible());
                }
            }
        });
        buttonConnect.setToolTipText("Connect to Forth");
        _toolBar.add(buttonConnect);

        JButton buttonUpload = new JButton(new ImageIcon(icon_path + "actions" + slash + "document-send-symbolic.symbolic.png"));
        buttonUpload.setToolTipText("Upload Forth code");
        buttonUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_currentEditorPanel == null) {
                    return;
                }
                if (_currentEditorPanel.getConnectionId().getConnectionType() == ConnectionType.None) {
                    JOptionPane.showMessageDialog(rootPane, "Can't upload without a connection.");
                    return;
                }
                String text = _currentEditorPanel.getEditorText();
                for (UploadEvent ev : _uploadRequestHandlerList) {
                    ev.upload(_currentEditorPanel.getConnectionId(), text);
                }
            }
        });
        _toolBar.add(buttonUpload);

        JButton buttonWords = new JButton("W");
        buttonWords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bubbleWordsRequest(_currentEditorPanel.getConnectionId());
            }
        });
        buttonWords.setToolTipText("Retrieve list of Forth WORDS");
        _toolBar.add(buttonWords);
        _toolBar.setVisible(true);

    }

    private void initComponents() {

        setSize(new Dimension(400, 400));
        addMenu();
        addToolBar();
        _panelWords = new PanelWords();
        _mainPanel.setLayout(new BoxLayout(_mainPanel, BoxLayout.X_AXIS)); // Left panel and right panel

        _mainPanel.add(_mainPanelLeft);
        _mainPanel.add(_mainPanelRight);

        _mainPanelLeft.setBorder(new BevelBorder(BevelBorder.LOWERED));
        _mainPanelLeft.setLayout(new BoxLayout(_mainPanelLeft, BoxLayout.Y_AXIS));
        _mainPanelLeft.setAlignmentX(TOP_ALIGNMENT);

        _mainPanelLeft.add(_panelConnections); // 1

        _panelConnections.setVisible(true);

        _mainPanelLeft.add(_tabbedEditorPane); // 2

        _tabbedEditorPane.setVisible(true);
        _tabbedEditorPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = _tabbedEditorPane.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                // Logger.getAnonymousLogger().log(Level.INFO, "Tab source " + _connectionSource);
                _currentEditorPanel = (PanelEditor) _tabbedEditorPane.getComponentAt(selectedIndex);

            }
        });

        _mainPanelRight.setBorder(new BevelBorder(BevelBorder.LOWERED));
        _panelWords.addWordClickHandleer(new WordClickEvent() {
            @Override
            public void wordClicked(String word) {
                JOptionPane.showMessageDialog(rootPane, "Not implemented yet");
            }
        });
        _mainPanelRight.add(_panelWords);

        _statusPanel.setLayout(new FlowLayout());
        _statusPanel.setMaximumSize(new Dimension(600, 64));
        _statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        _statusPanel.add(_statusLabel);
        _statusPanel.add(_statusProgressBar);
        _statusPanel.setAlignmentY(BOTTOM_ALIGNMENT);
        _mainPanelLeft.add(_statusPanel);

        this.add(_mainPanel);

    }

    public void addUploadRequestHandler(UploadEvent ev) {
        _uploadRequestHandlerList.add(ev);
    }

    public void setConnectionId(ConnectionId sc) {
        ensurePanelEditor(sc);
    }

    public void addFileOpenHandler(FileOpenEvent ev) {
        _fileOpenHandlerList.add(ev);
    }

    public void addFileSaveHandler(FileSaveEvent ev) {
        _fileSaveHandlerList.add(ev);
    }

    public void addRescanHandler(RescanEvent ev) {
        _rescanHandlerList.add(ev);
    }

    public void addConnectionCloseEventHandler(ConnectionCloseEvent ev) {
        _connectionCloseHandlerList.add(ev);
    }

    public void addDisplayConnectionsRequestHandler(ConnectionsDisplayEvent ev) {
        _connectionDisplayHandlerList.add(ev);
    }

    public void addSerialPortToList(String s) {
        _panelConnections.addSerialPortToList(s);
    }

    public void addSerialConnectionRequestHandler(SerialConnectionRequestEvent ev) {
        _serialConnectRequestHandlerList.add(ev);
    }

    public void addDisconnectEventHandler(DisconnectEvent ev) {
        _disconnectHandlerList.add(ev);
    }

    public void addTelnetConnectionRequestHandler(TelnetConnectionRequestEvent ev) {
        _telnetConnectRequestHandlerList.add(ev);
    }

    public void addTransmitEventHandler(TransmitEvent ev) {
        _transmitEventHandlerList.add(ev);
    }

    public void setEditorTab(ConnectionId id) {
        ensurePanelEditor(id);
        for (int idx = 0; idx < _tabbedEditorPane.getTabCount(); idx++) {
            String titleAt = _tabbedEditorPane.getTitleAt(idx);
            if (titleAt.equals(id.toString())) {
                _tabbedEditorPane.setSelectedIndex(idx);
                break;
            }
        }
    }

    /**
     * Set Current editor to id and select corresponding tab
     *
     * @param id
     */
    public void addEditorTab(ConnectionId id) {
        if (id == null) {
            throw new NullPointerException("ConnectionId is null");
        }

        _currentEditorPanel = new PanelEditor(id);
        _currentEditorPanel.setSize(new Dimension(600, 400));
        _editorPanelMap.put(id, _currentEditorPanel);
        _tabbedEditorPane.addTab(id.toString(), _currentEditorPanel);
        _currentEditorPanel.addTransmitEventHandler(new TransmitEvent() {
            @Override
            public void transmit(ConnectionId id, String text) {
                if (id.getConnectionType() == ConnectionType.None) {
                    return;
                }

                for (TransmitEvent ev : _transmitEventHandlerList) {
                    ev.transmit(id, text);
                }
            }
        });
        _currentEditorPanel.addCloseEventHandler(new ConnectionCloseEvent() {
            @Override
            public void close(ConnectionId id) {
                for (ConnectionCloseEvent ev : _connectionCloseHandlerList) {
                    Logger.getAnonymousLogger().log(Level.INFO, "Close Request from editorPanel");
                    ev.close(id);
                }
            }
        });
    }

    public void removeEditorTab(ConnectionId id) {

        PanelEditor get = ensurePanelEditor(id);
        if (get != null) {
            _tabbedEditorPane.remove(get);
        } else {
            Logger.getAnonymousLogger().log(Level.SEVERE, "EditorPanel does not contain tab:" + id);

        }
        _editorPanelMap.remove(id);
        if (_editorPanelMap.isEmpty()) {
            _currentEditorPanel = null;
        } else {
            int i = _tabbedEditorPane.getSelectedIndex();
            if (i != -1) {
                _currentEditorPanel = (PanelEditor) _tabbedEditorPane.getSelectedComponent();
            }
        }
    }

    public void setConnectionsVisible(boolean b) {
        _panelConnections.setVisible(b);
    }

    public void appendResponseText(ConnectionId id, String text) {
        ensurePanelEditor(id);
        switch (_responseOutputType) {
            case Editor:
                _currentEditorPanel.appendText(text);
                break;
            case Words:
                _panelWords.addWords(text);
                _panelWords.setVisible(true);
                if (text.length() < 10 && text.contains("ok")) {
                    _responseOutputType = ResponseOutputType.Editor;
                    _currentEditorPanel.appendText(text);
                }
                break;

        }
    }

    public void clearSerialPortList() {
        _panelConnections.clearSerialPortsList();
    }

    public void appendProgramText(String string) {
        appendResponseText(_currentEditorPanel.getConnectionId(), string);
    }

    public void setPogress(int max, int min, int i) {
        _statusProgressBar.setMaximum(max);
        _statusProgressBar.setMinimum(min);
        _statusProgressBar.setValue(i);
        _statusPanel.invalidate();
    }

    public void setStatus(String status) {
        _statusLabel.setText(status);
    }

    public void setOutputType(ResponseOutputType t) {
        _responseOutputType = t;
    }

    public String getEditorContent() {
        if (_currentEditorPanel != null) {
            return _currentEditorPanel.getEditorText();
        }
        return null;
    }
}
