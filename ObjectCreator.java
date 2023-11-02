import ObjectPool.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class ObjectCreator {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<Object> createdObjects = new ArrayList<>();

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    createdObjects.add(createSimpleObject());
                    System.out.println("Created SimpleObject with reference id: "+ createdObjects.size());
                    break;
                case 2:
                    createdObjects.add(createReferenceToObject(createdObjects));
                    System.out.println("Created ReferenceToObject with reference id: "+ createdObjects.size());
                    break;
                case 3:
                    createdObjects.add(createArrayOfPrimitives());
                    System.out.println("Created ArrayOfPrimitives with reference id: "+ createdObjects.size());
                    break;
                case 4:
                    createdObjects.add(createArrayOfObjects(createdObjects));
                    System.out.println("Created ArrayOfObjects with reference id: "+ createdObjects.size());
                    break;
                case 5:
                    createdObjects.add(createCollectionInstance(createdObjects));
                    System.out.println("Created CollectionInstance with reference id: " + createdObjects.size());
                    break;
                case 6:
                    displayCreatedObjects(createdObjects);
                    break;
                case 7:
                    System.out.println("Exiting program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println(String.format("%-75s%n", " MENU").replace(' ', '-'));
        System.out.println("(1) Create Simple Object");
        System.out.println("(2) Create Object with References");
        System.out.println("(3) Create Object with Primitive Array");
        System.out.println("(4) Create Object with Object Array");
        System.out.println("(5) Create Object with Collection");
        System.out.println("(6) Display Created Objects");
        System.out.println("(7) Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next(); // consume the invalid input
        }
        return scanner.nextInt();
    }
    
    private static SimpleObject createSimpleObject() {
        System.out.print("Enter an integer value for the simple object: ");
        int value = scanner.nextInt();
        return new SimpleObject(value);
    }

    private static ReferenceToObject createReferenceToObject(List<Object> createdObjects) {

        ReferenceToObject refObj = new ReferenceToObject();
        try {
            refObj.setReferenceObject(getReferenceId("object ", createdObjects));

        } catch (IllegalArgumentException | NullPointerException e) { }

        /* 
        System.out.print("Enter a reference id of an existing object (enter 0 if none): ");
        int referenceId = scanner.nextInt();

        ReferenceToObject refObj = new ReferenceToObject();

        try {
            if (referenceId >= 0 && referenceId <= createdObjects.size()) {
                
                if(referenceId != 0) {
                    refObj.setReferenceObject(createdObjects.get(referenceId - 1));
                }
                return refObj;
            } 
        } catch (IllegalArgumentException e) { }

        System.out.println("Invalid reference id. Object reference set as none.");
        */
        return refObj;
    }
    
    private static ArrayOfPrimitives createArrayOfPrimitives() {
        System.out.print("Enter the size of the primitive integer array: ");
        int size = scanner.nextInt();

        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            System.out.print("Enter an integer value for array element " + i + ": ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Please enter an integer value for array element " + i + ": ");
                scanner.next(); // consume the invalid input
            }
            array[i] = scanner.nextInt();           
        }

        return new ArrayOfPrimitives(array);
    }
    
    private static ArrayOfObjects createArrayOfObjects(List<Object> createdObjects) {
        System.out.print("Enter the size of the object array: ");
        int size = scanner.nextInt();

        ArrayOfObjects object = new ArrayOfObjects(size);

        for (int i = 0; i < size; i++) {

            try {
                object.setObjectArrayElement(i, (SimpleObject) getReferenceId("SimpleObject for array element " + i, createdObjects));
                continue;
            } catch (ClassCastException e) {
                System.out.println("This is not a valid reference id of a SimpleObject. Setting array element "+ i + " to none.");
                object.setObjectArrayElement(i, new SimpleObject());
            }
            
            /* 
            System.out.print("Enter a reference id of an existing SimpleObject for array element " + i + " (enter 0 if none): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Please enter a reference id of an existing SimpleObject for array element " + i + " (enter 0 if none): ");
                scanner.next(); // consume the invalid input
            }
            int referenceId = scanner.nextInt();

            if (referenceId >= 0 && referenceId <= createdObjects.size()) {
                if (referenceId != 0) {
                    try {
                        object.setObjectArrayElement(i, (SimpleObject) createdObjects.get(referenceId - 1));
                        continue;
                    } catch (ClassCastException e) {
                        System.out.println("This is not the reference id of a SimpleObject. Setting array element "+ i + " to none.");
                    }
                }
                
            } else 
                System.out.println("Invalid reference id. setting array element "+ i + " to none.");
            
            object.setObjectArrayElement(i, new SimpleObject());
            */
        }

        return object;
    }

    private static Object getReferenceId(String idType, List<Object> createdObjects) {
        System.out.print("Enter a reference id of an existing " + idType + " (enter 0 if none):");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a reference id of " + idType + " (enter 0 if none):");
            scanner.next(); // consume the invalid input
        }
        int referenceId = scanner.nextInt();

        if (referenceId >= 1 && referenceId <= createdObjects.size()) 
            return createdObjects.get(referenceId - 1);
        else if (referenceId != 0)
            System.out.println("Invalid reference id. Setting to none.");

        return null;
    }

    private static CollectionInstance createCollectionInstance(List<Object> createdObjects) {
        System.out.print("Enter the size of the collection: ");
        int size = scanner.nextInt();

        CollectionInstance object = new CollectionInstance();
        //int referenceId;
        for (int i = 0; i < size; i++) {
             
            object.addReference(getReferenceId("object for collection element " + i, createdObjects));
            
            /*
            if (referenceId > 0) {
                object.addReference(createdObjects.get(referenceId - 1));
            } else 
                object.addReference(new Object());

            System.out.print("Enter a reference id of an existing object for collection element " + i + "(enter 0 if none): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Please enter a reference id of an existing object for collection element " + i + " (enter 0 if none): ");
                scanner.next(); // consume the invalid input
            }
            int referenceId = scanner.nextInt();

            if (referenceId >= 0 && referenceId <= createdObjects.size()) {
                if (referenceId != 0) {
                    object.addReference(createdObjects.get(referenceId - 1));
                    continue;
                }
            } else 
                System.out.println("Invalid reference id. Setting collection element "+ i + " to none.");
            object.addReference(new Object());
             */
        }

        return object;
    }

    private static void displayCreatedObjects(List<Object> createdObjects) {
        System.out.println("Created Objects:");
        for (int i = 1; i <= createdObjects.size(); i++) {
            System.out.println(i + ". " + createdObjects.get(i - 1));
        }
        /* 
        System.out.println("Enter id to visualize:");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please try again.");
            scanner.next(); // consume the invalid input
        }
        int referenceId = scanner.nextInt();
        if (referenceId == -1) 
            return;
        SwingUtilities.invokeLater(() -> { new ObjectVisualizer(createdObjects.get(referenceId - 1)).setVisible(true);});
        */
    }

}
