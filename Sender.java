import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Sender {   
    private static Scanner scanner = new Scanner(System.in);
/* 
    private static String getFilePath() {
        System.out.print("Enter XML file path for JDOM document: ");
        String f = scanner.nextLine();

        if (!f.endsWith(".xml")) {
            System.out.println("Invalid file path. Using default " + defaultXMLFilePath);
            return defaultXMLFilePath;
        }
        return f;
    }

    private static String getServerHost() {
        System.out.print("Enter server hostname of the Receiver program: ");
        String host = scanner.nextLine();

        if (!host.matches("(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])")) {
            System.out.println("Invalid hostname. Using default " + defaultServerHost);
            return defaultServerHost;
        }
        return host;
    }

    private static int getServerPort() {
        System.out.print("Enter server port of the Receiver program: ");
        String port = scanner.nextLine();

        try {
           return Integer.parseInt(port);
        } catch (NumberFormatException e) {
             System.out.println("Invalid hostname. Using default " + defaultServerPort);
            return defaultServerPort;
        }
    }
*/
    private static Map<String, String> changeSettings(Map<String, String> settings) {

        System.out.print("Enter XML file path for JDOM document: ");
        String f = scanner.nextLine();

        if (!f.endsWith(".xml")) 
            System.out.println("Invalid file path. Using " + settings.get("XMLFilePath"));
        else 
            settings.put("XMLFilePath", f);

        System.out.print("Enter server hostname of the Receiver program: ");
        String host = scanner.nextLine();

        if (!host.matches("(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])")) 
            System.out.println("Invalid hostname. Using " + settings.get("hostname"));
        else 
            settings.put("hostname", host);

        System.out.print("Enter server port of the Receiver program: ");
        String portStr = scanner.nextLine();

        try {
           Integer.parseInt(portStr);
           settings.put(host, portStr);
        } catch (NumberFormatException e) {
             System.out.println("Invalid hostname. Using " + settings.get("port"));
        }

        return settings;
    }

    private static void displayMenu(Map<String, String> settings) {
        System.out.println( 
            "The Sender program:\n" +//
            " - create object(s) using Object Creation Menu," + // 
            " - serialize these objects into a JDOM document, and\n" + // 
            " - send this document to the Receiver program over network\n\n" + //
            String.format("%-80s%n", "MAIN-MENU").replace(' ', '-') + //
            "(1) Change Settings: \n\t" + //
                "Recevier Program Server:\n\t\t" + //
                    "hostname: " + settings.get("hostname") + "\n\t\t" + //
                    "port: " + settings.get("port") + "\n\t" + //
                "XML Filepath: " + settings.get("XMLFilePath")+ "\n" + //
            "(2) Start\n"+ //
            "(3) Exit program.");
    }

    private static int getUserChoice() {
        System.out.print("Enter your choice: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next(); // consume the invalid input
        }
        System.out.println();
        return scanner.nextInt();
    }

    public static void main(String args[])
    {
        Map<String, String> settings = new HashMap<>();
        settings.put("XMLFilePath", "serialized.xml");
        settings.put("hostname", "localhost");
        settings.put("port", "4000");
        Document doc;
        List<Object> objects;
        String outXMLFilePath;
        String serverHost;
        int serverPort;

        while(true)
        {
            displayMenu(settings);
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    changeSettings(settings);
                    for (Map.Entry<String, String> e: settings.entrySet())
                    {
                        System.out.println(e.getValue());
                    }
                    break;

                case 2:
                    objects = ObjectCreator.createObjects();
                    System.out.println("Serializing objects... (to stdout and in file " + settings.get("XMLFilePath")+ ")");
                    doc = new Serializer().serialize(objects);
                    try {
                        XMLOutputter outputXML = new XMLOutputter();
                        outputXML.setFormat(Format.getPrettyFormat());
                        outputXML.output(doc, new FileWriter(settings.get("XMLFilePath")));

                        // print file to console
                        BufferedReader in = new BufferedReader(new FileReader(settings.get("XMLFilePath")));
                        in.lines().forEach(line -> { System.out.println(line); });
                        in.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sendObject(doc, settings.get("hostname"), Integer.parseInt(settings.get("port")));
                    break;
            
                default:
                   return;
            }
            //objects = ObjectCreator.createObjects();
            //System.out.println(String.format("%-80s", "").replace(' ', '='));
            //outXMLFilePath = getFilePath();
            //System.out.println("Serializing objects... (to stdout and in file " + outXMLFilePath + ")");
            //System.out.println(String.format("%-80s", "").replace(' ', '='));
            //doc = new Serializer().serialize(objects);
/* 
            try
            {
                XMLOutputter outputXML = new XMLOutputter();
                outputXML.setFormat(Format.getPrettyFormat());
                outputXML.output(doc, new FileWriter(outXMLFilePath));

                // print file to console
                BufferedReader in = new BufferedReader(new FileReader(outXMLFilePath));
                in.lines().forEach(line -> { System.out.println(line); });
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
*/
            //System.out.println(String.format("%-80s", "").replace(' ', '='));
            //sendObject(doc, getServerHost(), getServerPort());
            //System.out.println(String.format("%-80s", "").replace(' ', '='));
            //System.out.println("Exit program? (y/n)");
            //if(scanner.nextLine().contains("y"))
            //    break;
            //System.out.println(String.format("%-80s", "").replace(' ', '='));
        }
    }
    
    private static void sendObject(Object obj, String hostname, int port)
    {
        System.out.println("Sending serialized object to " + hostname + " at port " + port);
        try
        {
            Socket socket = new Socket(InetAddress.getByName(hostname), port);
            System.out.println("Socket connected at " + hostname + ":" + port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to output stream");
            out.writeObject(obj);
            out.flush();
            System.out.println("Object written to output stream");  
            socket.close();
            System.out.println("Socket closed");

        } catch (ConnectException e) {
            System.out.println("Unable to connect to socket.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}