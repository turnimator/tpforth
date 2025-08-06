/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.turnimator.fide;

import com.turnimator.fide.events.ConnectionsDisplayEvent;
import com.turnimator.fide.events.FileOpenEvent;
import com.turnimator.fide.view.FrameMain;

/**
 *
 * @author atle
 */
public class Main {

    static FrameMain frameMain = new FrameMain();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        frameMain.setVisible(true);
        frameMain.addFileOpenHandler(new FileOpenEvent() {
            @Override
            public void open() {

            }
        });
        frameMain.addDisplayConnectionsRequestHandler(new ConnectionsDisplayEvent() {
            @Override
            public void setVisible(boolean b) {
                frameMain.setConnectionsVisible(true);
            }
        });
    }

}
