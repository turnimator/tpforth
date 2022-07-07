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
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

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
        doc.outputSettings().prettyPrint(false);
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

    public String getHelpText(String topic) {

        for (Element e : doc.select("h1,h2,h3")) {
            System.out.println(e.text());
            if (e.text().equalsIgnoreCase(topic)) {

                String rv = e.nextSibling().toString();

                return rv;
            }
        }
        return topic + " not found";
    }

    public void p(String s) {
        System.out.println(s);
    }

    public String getExample(String topic) {
        Elements select = doc.select("H2");
        String rv = "";
        for (Element e : select) {
            if (e.ownText().equalsIgnoreCase(topic)) {
                System.out.println("ownText():" + e.ownText());

                p("Text:" + e.nextSibling().toString());

                for (Element e2 : e.nextElementSiblings()) {
                    p("e2.text():" + e2.text());
                    p("e2.nextSibling.toString()" + e2.nextSibling().toString());
                    if (e2.text().equalsIgnoreCase("Examples")) {
                        Node n = e2.nextSibling();
                        while (n != null) {
                            p("Adding node to return value" + n.toString());
                            rv += n.toString();
                            if (n.nextSibling() == null || n.nextSibling().toString().startsWith("<")) {
                                return rv;
                            }
                            n = n.nextSibling();
                            if (n.nextSibling().toString().startsWith("<")){
                                return rv;
                            }
                        }

                    }
                    if (e2.tagName().equalsIgnoreCase("h2")) {
                        p("END");
                        break;
                    }

                }
            }
        }
        return topic;
    }
}
