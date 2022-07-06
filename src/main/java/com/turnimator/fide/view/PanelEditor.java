/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.ConnectionId;
import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ExampleRequestEvent;
import com.turnimator.fide.events.TransmitEvent;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
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
    ArrayList<TransmitEvent> _transmitHandlerList = new ArrayList<>();
    ArrayList<ConnectionCloseEvent> _closeHandlerList = new ArrayList<>();
    ArrayList<ExampleRequestEvent> _exampleRequestHandlerList = new ArrayList<>();
    private final ConnectionId _connectionId;
    
    private JMenu _popup;
    private JEditorPane _editorPane;
    private JLabel replPrompt;
    private TextField replTextfield;
    private JButton closeButton;
    
    public PanelEditor(ConnectionId id) {
        _connectionId = id;
        initComponents();
    }

    private void initComponents() {
        _editorPane = new JEditorPane("", "");
        createMenu();
        _editorPane.add(_popup);
        _editorPane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3){
                    Point p = getMousePosition();
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
        replPrompt = new JLabel("REPL");
        add(replPrompt);
        replTextfield = new TextField();
        replTextfield.setMaximumSize(new Dimension(400, 64));
        replTextfield.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
             for(TransmitEvent ev: _transmitHandlerList){
                 ev.transmit(_connectionId, replTextfield.getText());
             }
            }
        });
        add(replTextfield);
        closeButton = new JButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ConnectionCloseEvent ev : _closeHandlerList){
                    ev.close(_connectionId);
                }
            }
        });
        add(closeButton);

    }
    
    public void addCloseEventHandler(ConnectionCloseEvent ev){
        _closeHandlerList.add(ev);
    }
    
    public void addTransmitEventHandler(TransmitEvent e){
        _transmitHandlerList.add(e);
    }

    public void appendText(String text) {
        String text1 = _editorPane.getText();
        _editorPane.setText(text1 + "\n" + text);
        _editorPane.setCaretPosition(text1.length()+text.length());
    }

    public String getEditorText() {
        return _editorPane.getText();
    }

    public ConnectionId getConnectionId() {
       return _connectionId;
    }

    public String getSelectedText() {
        return _editorPane.getSelectedText();
    }
    
    public void paste(String s){
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
    
    public void addExampleRequestHandler(ExampleRequestEvent ev){
        _exampleRequestHandlerList.add(ev);
    }
    private void bubbleExampleRequest(String word){
        for(ExampleRequestEvent ev:_exampleRequestHandlerList){
            ev.requestExample(word);
        }
    }
    
}
