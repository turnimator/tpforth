/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.turnimator.fide.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author atle
 */
public class HelpServer {

    Document doc = null;

    public HelpServer() {

        try {
            doc = Jsoup.parse(new File("forth.help.html"), "ISO-8859-1");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("NO HELP FILE!!");
        } // right
        if (doc == null) {
            System.out.println("NO HELP FILE!!");
        }
        String p = System.getProperty("fide.execPath");
        Logger.getAnonymousLogger().log(Level.INFO, p);
    }

    public String getHelpHtml(String topic) {
        boolean found = false;
        for (Element e : doc.select("h1,h2,h3")) {
            System.out.println(e.text());
            if (e.text().equalsIgnoreCase(topic)) {
                found = true;
                String rv = e.toString() + "\n" + e.nextSibling().toString();

                System.out.println("Found:" + rv);
                Element e2 = e.nextElementSibling();
                while (!e2.is("h1,h2")) {
                    rv += e2.toString() + "\n" + e2.nextSibling().toString();
                    System.out.println("Sibling" + e2.toString());
                    e2 = e2.nextElementSibling();
                }

                return rv;
            }
        }
        System.out.println("Help not found");
        return ("<html><body><h1>No help for " + topic + "</h1></body></html>");
    }
    public String getHelpText(String topic){
        
        for (Element e : doc.select("h1,h2,h3")) {
            System.out.println(e.text());
            if (e.text().equalsIgnoreCase(topic)) {
                
                 String rv = e.toString() + "\n" + e.nextSibling().toString();
                
                return rv;
            }
        }
        return topic + " not found";
    }
}