import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Sender {   
    private static Scanner scanner = new Scanner(System.in);
       
    public static void main(String args[]) {
        Map<String, String> settings = new HashMap<>();
        settings.put("XMLFilePath", "serialized.xml");
        settings.put("hostname", "localhost");
        settings.put("port", "4000");

        while(true) {
            displayMenu(settings);
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    for(Object obj : getSendObjects()) {
                        sendObject(
                            serialzeObjects(obj, settings.get("XMLFilePath")), 
                            settings.get("hostname"), 
                            Integer.parseInt(settings.get("port"))
                        );
                    }
                    break;

                case 2:
                    changeSettings(settings);
                    break;

                default:
                   return;
            }
        }
    }

    private static void displayMenu(Map<String, String> settings) {
        System.out.println( 
            String.format("%n%-80s%n", "").replace(' ', '-') + //
            "The Sender program creates object(s) using Object Creation " + //
            "Menu, serialize these\nobjects into a JDOM document, and send" + //
            " this document to the Receiver program\nover a network.\n\n" + //
            String.format("%-80s%n", "MAIN-MENU").replace(' ', '-') + //
            "(1) Start\n\n"+ //
            "(2) Change Settings: \n\t" + //
                "Recevier Program Server:\n\t\t" + //
                    "hostname: " + settings.get("hostname") + "\n\t\t" + //
                    "port: " + settings.get("port") + "\n\t" + //
                "XML Filepath: " + settings.get("XMLFilePath")+ "\n\n" + //
            "(3) Exit program.\n");
    }

    private static Map<String, String> changeSettings(Map<String, String> settings) {
        System.out.print("Enter XML file path for JDOM document: ");
        String f = scanner.next();

        if (!f.endsWith(".xml")) 
            System.out.println("WARNING: Invalid file path. Using " + settings.get("XMLFilePath"));
        else 
            settings.put("XMLFilePath", f);

        System.out.print("Enter server hostname of the Receiver program: ");
        String host = scanner.next();

        if (!host.matches("(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])")) 
            System.out.println("WARNING: Invalid hostname. Using " + settings.get("hostname"));
        else 
            settings.put("hostname", host);

        System.out.print("Enter server port of the Receiver program: ");
        String portStr = scanner.next();

        try {
           Integer.parseInt(portStr);
           settings.put("port", portStr);

        } catch (NumberFormatException e) {
             System.out.println("WARNING: Invalid hostname. Using " + settings.get("port"));
        }

        return settings;
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
    
    private static List<Object> getSendObjects() {
        List<Object> objects = ObjectCreator.createObjects();
        if (objects.size() == 0)
            return objects;

        //System.out.println("Select reference Id of object(s) to send or -1 to finish selection:");
        System.out.println("Select reference Id of object to send:");
        for (int i = 1; i <= objects.size(); i++) {
            System.out.println("\t" +i + ". " + objects.get(i - 1));
        }

        List<Object> sendObjects = new ArrayList<>();
        while (true) {
            int id = getUserChoice();
            //if (id == -1)
            //    break;

            if( id > objects.size() || id <= 0) 
                System.out.println("ERROR: Invalid reference Id");
            else {
                sendObjects.add(objects.get(id - 1));
                break;
            }
        }
        
        return sendObjects;
    }

    private static Document serialzeObjects(Object obj, String file) {
        System.out.println("Serializing objects...");
        Document doc = new Serializer().serialize(obj);

        try {
            XMLOutputter outputXML = new XMLOutputter();
            outputXML.setFormat(Format.getPrettyFormat());
            outputXML.output(doc, new FileWriter(file));

            // print file to console
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.lines().forEach(line -> { System.out.println(line); });
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
    
    private static void sendObject(Document obj, String hostname, int port) {
        System.out.print("\nSend document? (y/n):");
        String resp = scanner.next();
        if (resp.contains("y")){
            try {
                System.out.println("Connecting to Reciever program...");
                Socket sock = new Socket(InetAddress.getByName(hostname), port);
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                System.out.println("Sending serialized objects... ");

                out.writeObject(obj);
                out.flush();
                System.out.println("Sent.");  

                sock.close();
                System.out.println("Disconnected.");

            } catch (ConnectException e) {
                System.out.println("WARNING: Unable to make socket connection.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}