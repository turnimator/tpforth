/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import java.lang.System;
import com.turnimator.fide.events.WordClickEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;

import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;

/**
 *
 * @author Jan Atle Ramsli <jaramsli@gmail.com>
 */
public class PanelWords extends JPanel {
    private JPanel _panel = new JPanel();
    private HashSet<String> _wordSet = new HashSet<>();
    // private JScrollPane _scrollPane;
    String[] numbers = {"One", "Two", "Three", "Four", "Five"};
    private java.awt.List _wordsList;
    
    private final ArrayList<WordClickEvent> _wordRequestHandlerList = new ArrayList<>();
    
    public PanelWords() {
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
    
    public void addWordClickHandleer(WordClickEvent ev) {
        _wordRequestHandlerList.add(ev);
    }

    public void bubbleWordsRequest() {
        String w = (String) _wordsList.getSelectedItem();
        for (WordClickEvent ev : _wordRequestHandlerList) {
            ev.wordClicked(w);
        }
    }
    
    public void addWords(String words) {
        System.out.println(words);
        List<String> asList = Arrays.asList(words.split(" "));
        _wordSet.addAll(asList);
        for (String s : _wordSet) {
            _wordsList.add(s);
        }
        
    }
    
    private void initComponents() {
        this.setSize(new Dimension(620, 1090));
        _wordsList = new java.awt.List();
        
        _wordsList.setSize(new Dimension(600, 1080));
        _panel.setAlignmentY(TOP_ALIGNMENT);
        _panel.setAlignmentX(LEFT_ALIGNMENT);
        _panel.setLayout(new FlowLayout());
        _panel.setSize(new Dimension(620, 1090));
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));
        
        _wordsList.setMaximumSize(new Dimension(1600, 1080));
        _panel.add(_wordsList);
        this.add(_panel);
        _wordsList.setMinimumSize(_panel.getSize());
    }
}
