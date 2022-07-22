/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.enums.ConnectionType;
import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ExampleRequestEvent;
import com.turnimator.fide.events.TransmitEvent;
import java.awt.Cursor;
import java.awt.datatransfer.StringSelection;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author atle
 */
public class PanelEditor extends JPanel {

    private String _programText = "";
    ArrayList<TransmitEvent> _transmitHandlerList = new ArrayList<>();
    ArrayList<ConnectionCloseEvent> _closeHandlerList = new ArrayList<>();
    ArrayList<ExampleRequestEvent> _exampleRequestHandlerList = new ArrayList<>();
    private final String _connectionId;
    private ConnectionType _connectionType = ConnectionType.None;

    Clipboard _clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DataFlavor _dataFlavor = DataFlavor.stringFlavor;

    private JMenu _popup;
    private JEditorPane _editorPane;
    private JLabel _replPrompt;
    private TextField _replTextfield;
    private JButton _closeButton;
    private JButton _clearOutputButton;
    
    public PanelEditor(String id, ConnectionType t) {
        _connectionId = id;
        _connectionType = t;
        initComponents();
    }

    private void initComponents() {
        _editorPane = new JEditorPane("", "");
        createMenu();
        _editorPane.add(_popup);
        _editorPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Point p = _editorPane.getMousePosition();
                    _popup.setLocation(p);
                    _popup.setPopupMenuVisible(true);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(_editorPane);

        add(scrollPane);
        _replPrompt = new JLabel("REPL");
        add(_replPrompt);
        _replTextfield = new TextField();
        _replTextfield.setMaximumSize(new Dimension(400, 64));
        _replTextfield.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (TransmitEvent ev : _transmitHandlerList) {
                    ev.transmit(_connectionId, _replTextfield.getText());
                }
            }
        });
        add(_replTextfield);
        _closeButton = new JButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (ConnectionCloseEvent ev : _closeHandlerList) {
                    ev.close(_connectionId);
                }
            }
        });
        
        _clearOutputButton = new JButton(new AbstractAction("Clear Output") {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOutputText();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(_closeButton);
        buttonPanel.add(_clearOutputButton);
        add(buttonPanel);

    }

    public void addCloseEventHandler(ConnectionCloseEvent ev) {
        _closeHandlerList.add(ev);
    }

    public void addTransmitEventHandler(TransmitEvent e) {
        _transmitHandlerList.add(e);
    }

    public void appendOutputText(String text) {
        if (text == null) return;
        String text1 = _editorPane.getText();
        _editorPane.setText(text1 + "\n" + text);
        _editorPane.setCaretPosition(text1.length() + text.length());
    }

    public void appendProgramText(String text) {
        _programText += text + "\n";
        String text1 = _editorPane.getText();
        _editorPane.setText(text1 + "\n" + text);
        _editorPane.setCaretPosition(text1.length() + text.length());
    }

    public void clearOutputText(){
        _editorPane.setText(_programText);
        _editorPane.setCaretPosition(_programText.length());
    }
    
    public String getEditorText() {
        return _editorPane.getText();
    }

    public String getConnectionId() {
        return _connectionId;
    }

    public String getSelectedText() {
        return _editorPane.getSelectedText();
    }

    public void paste(String s) {
        _editorPane.setText(_editorPane.getText() + s);
    }

    void deleteSelectedText() {
        int selectionStart = _editorPane.getSelectionStart();
        int selectionEnd = _editorPane.getSelectionEnd();
        String s = _editorPane.getText();
        String s1 = s.substring(0, selectionStart);
        String s2 = s.substring(selectionEnd, s.length());
        _editorPane.setText(s1 + s2);
    }

    private void createMenu() {
        _popup = new JMenu();
        JMenuItem _copyItem = new JMenuItem(new AbstractAction("Copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = _editorPane.getSelectedText();
                if (text == null || text.length() == 0) {
                    return;
                }
                _clipboard.setContents(new StringSelection(text), new ClipboardOwner() {
                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {

                    }
                });
                _popup.setPopupMenuVisible(false);
            }
        });
        _popup.add(_copyItem);

        JMenuItem _cutItem = new JMenuItem(new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = _editorPane.getSelectedText();
                if (text == null || text.length() == 0) {
                    return;
                }
                _clipboard.setContents(new StringSelection(text), new ClipboardOwner() {
                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {

                    }
                });
                _popup.setPopupMenuVisible(false);
            }
        });
        _popup.add(_cutItem);

        JMenuItem _pasteItem = new JMenuItem(new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s2 = "";
                try {
                    s2 = (String) _clipboard.getData(_dataFlavor);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PanelEditor.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(PanelEditor.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                int pos = _editorPane.getCaretPosition();
                String text = _editorPane.getText();
                String s1 = text.substring(0, pos);
                String s3 = text.substring(pos, text.length());
                _editorPane.setText(s1 + s2 + s3);
                _popup.setPopupMenuVisible(false);
            }
        });
        _popup.add(_pasteItem);

        JMenuItem _exampleItem = new JMenuItem(new AbstractAction("Example") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cp = _editorPane.getCaretPosition();
                String selectedText = _editorPane.getSelectedText();
                Logger.getAnonymousLogger().log(Level.INFO, "Example requested for " + selectedText);
                _popup.setPopupMenuVisible(false);
                bubbleExampleRequest(selectedText);
            }
        });
        _popup.add(_exampleItem);
    }

    public void addExampleRequestHandler(ExampleRequestEvent ev) {
        _exampleRequestHandlerList.add(ev);
    }

    private void bubbleExampleRequest(String word) {
        for (ExampleRequestEvent ev : _exampleRequestHandlerList) {
            ev.requestExample(word);
        }
    }

    ConnectionType getConnectionType() {
        return _connectionType;
    }

}
