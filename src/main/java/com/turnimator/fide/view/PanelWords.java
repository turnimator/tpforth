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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;

import javax.swing.border.BevelBorder;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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

    public void bubbleWordClickedRequest() {
        TreePath selectionPath = _wordsTree.getSelectionPath();
        if (selectionPath == null) {
            return;
        }
        for (WordClickEvent ev : _wordRequestHandlerList) {
            TreePath parentPath = selectionPath.getParentPath();
            if (parentPath != null) {
                ev.wordClicked(selectionPath.getParentPath().getLastPathComponent().toString());
            }
        }
    }

    public void addWords(String words) {
        System.out.println(words);
        List<String> asList = Arrays.asList(words.split(" "));
        _wordSet.addAll(asList);
        int i = 0;
        _model = new DefaultTreeModel(new DefaultMutableTreeNode("Forth words"));
        _wordsTree.setModel(_model);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) _model.getRoot();
        for (String s : _wordSet) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(s);
            child.add(new DefaultMutableTreeNode("Description"));
            node.add(child);
        }
        _model.reload();
        _wordsTree.expandRow(3);
        this.getParent().invalidate();

    }

    public void setHelp(String word, String text) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) _model.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if (word.equals((String) node.getUserObject())) {
                Logger.getAnonymousLogger().log(Level.INFO, "Found: " + word);
                if (node.getChildCount() < 2) {

                    DefaultMutableTreeNode helpNode = new DefaultMutableTreeNode(text);
                    node.add(helpNode);
                    _model.reload();
                    _wordsTree.expandRow(i + 1);
                }
                break;
            }
        }

    }

    private void initComponents() {

        _model = new DefaultTreeModel(new DefaultMutableTreeNode("Forth words"));

        _wordsTree = new JTree();
        _wordsTree.setModel(_model);
        
        _wordsTree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {

            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {

            }

        });
        _wordsTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                bubbleWordClickedRequest();
            }
        });

        setLayout(new BorderLayout());

        this.setBorder(new BevelBorder(BevelBorder.LOWERED));

        
        _scrollPane = new JScrollPane(_wordsTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        _scrollPane.setMaximumSize(new Dimension(400, 800));

        add(_scrollPane);

        //this.add(_panel);
    }
}
