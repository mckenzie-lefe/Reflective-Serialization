import ObjectPool.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ObjectCreator {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        createObjects();
    }

    public static List<Object> createObjects() {
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
                    createdObjects.add(createReferenceSimpleObject(createdObjects));
                    System.out.println("Created ReferenceSimpleObject with reference id: "+ createdObjects.size());
                    break;

                case 3:
                    for (CircularReference cRef : createCircularReference(createdObjects)) {
                        createdObjects.add(cRef);
                        System.out.println("Created CircularReference with reference id: "+ createdObjects.size());
                    }
                    break;

                case 4:
                    createdObjects.add(createArrayOfPrimitives());
                    System.out.println("Created ArrayOfPrimitives with reference id: "+ createdObjects.size());
                    break;

                case 5:
                    createdObjects.add(createArrayOfObjects(createdObjects));
                    System.out.println("Created ArrayOfObjects with reference id: "+ createdObjects.size());
                    break;

                case 6:
                    createdObjects.add(createCollectionInstance(createdObjects));
                    System.out.println("Created CollectionInstance with reference id: " + createdObjects.size());
                    break;

                case 7:
                    displayCreatedObjects(createdObjects);
                    break;

                case 8: //Exit menu
                    return createdObjects;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println(String.format("%n%-80s", "OBJECT-CREATION-MENU").replace(' ', '-'));
        System.out.println("(1) Create Simple Object\n");
        System.out.println("(2) Create Object with Reference to Simple Object\n");
        System.out.println("(3) Create Object with CircularReferencey\n");
        System.out.println("(4) Create Object with Primitive Array\n");
        System.out.println("(5) Create Object with Object Array\n");
        System.out.println("(6) Create Object with Collection\n");
        System.out.println("(7) Display Created Objects\n");
        System.out.println("(8) Exit\n");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next(); // consume the invalid input
        }
        System.out.println();
        return scanner.nextInt();
    }
    
    private static Object getReferenceId(String idType, List<Object> createdObjects) {
        System.out.print("Enter a reference id of an existing " + idType + " (enter 0 if none): ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a reference id of " + idType + " (enter 0 if none): ");
            scanner.next(); // consume the invalid input
        }
        int referenceId = scanner.nextInt();

        if (referenceId >= 1 && referenceId <= createdObjects.size()) 
            return createdObjects.get(referenceId - 1);
        else if (referenceId != 0)
            System.out.println("Invalid reference id. Setting to none.");

        return null;
    }

    private static SimpleObject createSimpleObject() {
        System.out.print("Enter an integer value for the simple object: ");
        int value = scanner.nextInt();
        return new SimpleObject(value);
    }

    private static CircularReference[] createCircularReference(List<Object> createdObjects) {
        System.out.println("(1) Create CirularReference object with circularRef field set to null");
        System.out.println("(2) Set the circularRef field of existing CirularReference Object");
        System.out.println("(3) Create two new CirularReference Objects and set their circularRef fields\nsuch that they reference each other");
        int choice = getUserChoice();

        switch (choice) {
            case 1:
                return new CircularReference[] { new CircularReference() };

            case 2:
                try {
                    return new CircularReference[] { 
                        new CircularReference(
                            (CircularReference) getReferenceId(
                                "CircularReference object ", createdObjects))};

                } catch (ClassCastException e) {
                    System.out.println("Invalid CircularReference object " +
                        "reference id. Setting circularRef field to none.");
                    return new CircularReference[] { new CircularReference()};
                }

            case 3:
                CircularReference cRef1 = new CircularReference();
                CircularReference cRef2 = new CircularReference(cRef1);
                cRef1.setCircularReference(cRef2);

                return new CircularReference[] { cRef1, cRef2 };

            default:
                System.out.println("Invalid choice. Returning to Main Menu");
                return new CircularReference[] {};
        }
    }

    private static ReferenceSimpleObject createReferenceSimpleObject(List<Object> createdObjects) {
        try {
            return new ReferenceSimpleObject((SimpleObject) getReferenceId("SimpleObject ", createdObjects));

        } catch (ClassCastException e) {
            System.out.println("Invalid SimpleObject reference id. Setting simpleObj field to none.");
            return new ReferenceSimpleObject();
        }
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
                object.setObjectArrayElement(i, new SimpleObject(3));
            }
        }
        return object;
    }

    private static CollectionInstance createCollectionInstance(List<Object> createdObjects) {
        System.out.print("Enter the size of the collection: ");
        int size = scanner.nextInt();
        CollectionInstance object = new CollectionInstance();

        for (int i = 0; i < size; i++) {     
            object.addReference(getReferenceId("object for collection element " + i, createdObjects));
        }
        return object;
    }

    private static void displayCreatedObjects(List<Object> createdObjects) {
        System.out.println("Created Objects:");
        for (int i = 1; i <= createdObjects.size(); i++) {
            System.out.println(i + ". " + createdObjects.get(i - 1));
        }
    }
}
