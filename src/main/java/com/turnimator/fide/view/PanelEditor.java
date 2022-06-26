/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.ConnectionCloseEvent;
import com.turnimator.fide.events.ConnectionType;
import com.turnimator.fide.events.TransmitEvent;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author atle
 */
public class PanelEditor extends JPanel {
    ArrayList<TransmitEvent> transmitHandlerList = new ArrayList<>();
    ArrayList<ConnectionCloseEvent> closeHandlerList = new ArrayList<>();
    private String _source = "";
    private ConnectionType _connectionType;
    
    private JEditorPane editorPane;
    private JLabel replPrompt;
    private TextField replTextfield;
    private JButton closeButton;
    
    public PanelEditor(ConnectionType ct, String source) {
        this._source = source;
        _connectionType = ct;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        editorPane = new JEditorPane("", "");
        editorPane.setMinimumSize(new Dimension(300, 400));
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setMinimumSize(new Dimension(400, 300));
        add(scrollPane);
        replPrompt = new JLabel("REPL");
        add(replPrompt);
        replTextfield = new TextField();
        replTextfield.setMaximumSize(new Dimension(400, 64));
        replTextfield.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
             for(TransmitEvent ev: transmitHandlerList){
                 ev.transmit(_connectionType, _source, replTextfield.getText());
             }
            }
        });
        add(replTextfield);
        closeButton = new JButton(new AbstractAction("Close") {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(ConnectionCloseEvent ev : closeHandlerList){
                    ev.close(_connectionType, _source);
                }
            }
        });
        add(closeButton);

    }
    
    public void addCloseEventHandler(ConnectionCloseEvent ev){
        closeHandlerList.add(ev);
    }
    
    public void addTransmitEventHandler(TransmitEvent e){
        transmitHandlerList.add(e);
    }

    public void appendText(String text) {
        String text1 = editorPane.getText();
        editorPane.setText(text1 + "\n" + text);
        editorPane.setCaretPosition(text1.length()+text.length());
    }

    public String getEditorText() {
        return editorPane.getText();
    }

    public ConnectionType getConnectionTyype() {
       return _connectionType;
    }

    public String getSelectedText() {
        return editorPane.getSelectedText();
    }
    
    public void paste(String s){
        editorPane.setText(editorPane.getText() + s);
    }

    void deleteSelectedText() {
        int selectionStart = editorPane.getSelectionStart();
        int selectionEnd = editorPane.getSelectionEnd();
        String sstart = "";
        String send = "";
        String s = editorPane.getText();
        String s1 = s.substring(0, selectionStart);
        String s2 = s.substring(selectionEnd, s.length());
        editorPane.setText(s1 + s2);
    }
    
}
