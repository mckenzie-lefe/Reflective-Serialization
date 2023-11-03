import ObjectPool.*;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.lang.reflect.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

public class Serializer {
    private Map<Object, Integer> objectMap;
    private int idCounter;

    public Serializer() {
        objectMap = new IdentityHashMap<>();
        idCounter = 0;
    }

    public Document serialize(Object obj) {
        Element rootElement = new Element("serialized");
        Document document = new Document(rootElement);

        if(Collection.class.isAssignableFrom(obj.getClass())) {
            try {
                Iterator<?> iter = ((Iterable<?>) obj).iterator();
                while(iter.hasNext()) {
                    serializeObject((Object) iter.next(), rootElement);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else
            serializeObject(obj, rootElement);
  
        return document;
    }

    protected void serializeObject(Object obj, Element parentElement) {
        if (obj == null || objectMap.containsKey(obj)) 
            return;

        Class<?> clazz = obj.getClass();
        int objectId = idCounter++;
        Element objectElement = new Element("object");
        objectMap.put(obj, objectId);

        objectElement.setAttribute("class", obj.getClass().getName());
        objectElement.setAttribute("id", Integer.toString(objectId));
        parentElement.addContent(objectElement);

        if (clazz.isArray()) {
            System.out.println("is array " + clazz.getName());

            boolean primitive = clazz.getComponentType().isPrimitive();
            int len = Array.getLength(obj);
            
            objectElement.setAttribute("length", Integer.toString(len));

            for (int i = 0; i < len; i++) {
                serializeObjectContent(Array.get(obj, i), parentElement, objectElement, primitive);
            }  
            
        } else if(Collection.class.isAssignableFrom(obj.getClass())) {
            serializeCollection(obj, parentElement);   
            //parentElement.removeContent(objectElement);

        } else 
            serializeFields(obj, parentElement, objectElement);   
        
    }

    private void serializeObjectContent(Object obj, Element parentElement, Element objectElement, boolean primitive) {
        if (obj == null) {
            System.out.println("Object is null!!!");
            return; 
        }

        // serialize value
        if (primitive)                          
            addChildElement(objectElement, "value", obj.toString());
        // serialize reference
        else  {     
            if (!objectMap.containsKey(obj)) 
                serializeObject(obj, parentElement);
        
            addChildElement(objectElement, "reference", objectMap.get(obj).toString());
        } 
    }

    private void serializeCollection(Object obj, Element parentElement) {
        if(Collection.class.isAssignableFrom(obj.getClass())) {
            try {
                Iterator<?> iter = ((Iterable<?>) obj).iterator();
                System.out.println(iter);

                while(iter.hasNext()) {
                    serializeObject((Object) iter.next(), parentElement);    
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addChildElement(Element parentElement, String name, String value) {
        Element childElement = new Element(name);
        childElement.setText(value);
        parentElement.addContent(childElement);
    }

    private void serializeFields(Object obj, Element parentElement, Element objectElement) {

        for (Field field : obj.getClass().getDeclaredFields()) {
            Class<?> fType = field.getType();
            Element fieldElement = new Element("field");

            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringclass", field.getDeclaringClass().getName());

            try {
                field.setAccessible(true);  
                serializeObjectContent(field.get(obj), parentElement, fieldElement, fType.isPrimitive());

            } catch ( IllegalAccessException | InaccessibleObjectException e) { 
                System.out.println("WARNING: Unable to make " +field.getName()+ " accessiable.");
            }

            objectElement.addContent(fieldElement);
        }
    }

    protected int getIdCounter() {
        return idCounter;
    }

    protected Map<Object, Integer> getObjectMap() {
        return this.objectMap;
    }

    public static void main(String[] args) {
        Serializer serializer = new Serializer();
        ArrayOfObjects exampleObject = null;
        SimpleObject so = new SimpleObject(4);
        ReferenceSimpleObject rso1 = new ReferenceSimpleObject(so);
        CircularReference cRef1 = new CircularReference();
        CircularReference cRef2 = new CircularReference(cRef1);
        cRef1.setCircularReference(cRef2);
        ArrayOfPrimitives arrPrim = new ArrayOfPrimitives(new int[] {7,8,9});
        CollectionInstance colInst = new CollectionInstance();
        colInst.addReference(so);
        colInst.addReference(rso1);
        colInst.addReference(cRef1);

        try {
            exampleObject = new ArrayOfObjects(3);
            exampleObject.setObjectArrayElement(0, so);
            exampleObject.setObjectArrayElement(1, so);
            exampleObject.setObjectArrayElement(2, new SimpleObject(6));

        } catch (Exception e) {}

        List<Object> lis = new ArrayList<>();
        lis.add(so);
        lis.add(cRef2);
        System.out.println("HI");
        Document document = serializer.serialize(lis);


        // Print the XML document
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        StringWriter stringWriter = new StringWriter();

        try {
            xmlOutputter.output(document, stringWriter);
            System.out.println(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}