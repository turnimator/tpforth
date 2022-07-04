/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.view;

import java.lang.System;
import com.turnimator.fide.events.WordClickEvent;
import java.awt.BorderLayout;
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
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Jan Atle Ramsli <jaramsli@gmail.com>
 */
public class PanelWords extends JPanel {
    private JPanel _panel = new JPanel();
    private HashSet<String> _wordSet = new HashSet<>();
    private JScrollPane _scrollPane;
    String[] numbers = {"One", "Two", "Three", "Four", "Five"};
    private JTree _wordsTree;
    private DefaultTreeModel _model;
    
    private final ArrayList<WordClickEvent> _wordRequestHandlerList = new ArrayList<>();
    
    public PanelWords() {
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
    
    public void addWordClickHandleer(WordClickEvent ev) {
        _wordRequestHandlerList.add(ev);
    }

    public void bubbleWordsRequest() {
        TreePath selectionPath = _wordsTree.getSelectionPath();
        for (WordClickEvent ev : _wordRequestHandlerList) {
            ev.wordClicked(selectionPath.getLastPathComponent().toString());
        }
    }
    
    public void addWords(String words) {
        System.out.println(words);
        List<String> asList = Arrays.asList(words.split(" "));
        _wordSet.addAll(asList);
        int i = 0;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) _model.getRoot();
        for (String s : _wordSet) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(s);
            child.add(new DefaultMutableTreeNode("Description"));
            node.add(child);
            
        }
        _model.reload();
        _wordsTree.expandRow(3);
        
    }
    
    private void initComponents() {
        // Perhaps give up this shit and use something else!!!!
        // A JTREE!!!
        _model = new DefaultTreeModel(new DefaultMutableTreeNode());
        
        _wordsTree = new JTree();
        _wordsTree.setModel(_model);
        _wordsTree.setSize(new Dimension(400, 1200));
        
        setLayout(new BorderLayout());
        
        this.setBorder(new BevelBorder(BevelBorder.LOWERED));
         
        _scrollPane = new JScrollPane(_wordsTree);
        
        
        add(_scrollPane);
        
        //this.add(_panel);
        
    }
}
