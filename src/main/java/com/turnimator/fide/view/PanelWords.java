/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import com.turnimator.fide.events.WordClickEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;


/**
 * 
 * @author Jan Atle Ramsli <jaramsli@gmail.com>
 */
public class PanelWords extends JPanel {
    private HashSet<String> _wordSet = new HashSet<>();
    private JScrollPane _scrollPane;
     String[] numbers = {"One", "Two", "Three", "Four", "Five"};
    private JList<String> _wordsList;
       
    private final ArrayList<WordClickEvent> _wordRequestHandlerList = new ArrayList<>();

    public PanelWords(){
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
    
    public void addWordClickHandleer(WordClickEvent ev){
        _wordRequestHandlerList.add(ev);
    }
    public void bubbleWordsRequest(){
        String w = (String) _wordsList.getSelectedValue();
        for (WordClickEvent ev:_wordRequestHandlerList){
            ev.wordClicked(w);
        }
    }
    
    
    public void addWords(String words){
        System.out.println(words);
        DefaultListModel<String> m = new DefaultListModel<>();
        _wordSet.addAll(Arrays.asList(words.split(" ")));
        for(String s:_wordSet){
            m.addElement(s);
            System.out.println("Adding: " + s);
        }
        m.addElement("WHAT");
        _wordsList.setModel(m);        
    }

    private void initComponents() {
        _wordsList = new JList<>(numbers);
       this.setBorder(new BevelBorder(BevelBorder.LOWERED));
        _scrollPane = new JScrollPane();
        _scrollPane.add(_wordsList);
        this.add(_scrollPane);
    }
}
