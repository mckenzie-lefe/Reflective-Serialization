import ObjectPool.*;

import org.jdom2.*;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.lang.reflect.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


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

        serializeObject(obj, rootElement);
  
        return document;
    }

    public void pp(String m) {
        System.out.println(m);
    }

    protected void serializeObject(Object obj, Element parentElement) {
        pp("serializing; "+obj.toString());

        if (obj == null) 
            return;
 
        Class<?> clazz = obj.getClass();
        int objectId = idCounter++;
        Element objectElement = new Element("object");
        
        objectMap.put(obj, objectId);

        objectElement.setAttribute("class", obj.getClass().getName());
        objectElement.setAttribute("id", Integer.toString(objectId));
       
        parentElement.addContent(objectElement);
        if (clazz.isArray()) {
            pp("is array " + clazz.getName());
            int len = Array.getLength(obj);
            objectElement.setAttribute("length", Integer.toString(len));

            //TO DO: clean up -> check each elements type of jus component type??
            if (clazz.getComponentType().isPrimitive()){
                for (int i = 0; i < len; i++) {
                    serializeValue(objectElement, Array.get(obj, i).toString());
                }
            } else {
                for (int i = 0; i < len; i++) {
                    serializeReference(Array.get(obj, i), parentElement, objectElement);
                }
            }
            
        } else {
            serializeFields(obj, parentElement, objectElement);
        }     
    }

    private void serialzeCollection(Object obj) {
        if (obj.getClass().isInstance(Collection.class) ){

        }
    }

    private void serializeReference(Object obj, Element parentElement, Element childElement) {
        if (obj == null) {  // TO DO: handle nulls
            System.out.println("reference object is null!!!");
            return;
        }

        if (!objectMap.containsKey(obj)) 
            serializeObject(obj, parentElement);
        
        Element refElement = new Element("reference");
        refElement.setText(objectMap.get(obj).toString());
        childElement.addContent(refElement);
    }

    private void serializeValue(Element parentElement, String value) {
        Element valueElement = new Element("value");
        valueElement.setText(value);
        parentElement.addContent(valueElement);
    }

    private void serializeFields(Object obj, Element parentElement, Element objectElement) {

        for (Field field : obj.getClass().getDeclaredFields()) {
            Class<?> fType = field.getType();
            Element fieldElement = new Element("field");
            pp("Field: " + field.getName() + " Type: " + fType);
            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringclass", field.getDeclaringClass().getName());
            pp("GtYPE: " + field.getGenericType());

            try {
                field.setAccessible(true); 
                Object fieldObj = field.get(obj);
                

                if (fType.isPrimitive()) 
                    serializeValue(fieldElement, fieldObj.toString());
                else 
                    serializeReference(fieldObj, parentElement, fieldElement);

            } catch ( IllegalAccessException | InaccessibleObjectException e) { 
                pp("WARNING: Unable to make " +field.getName()+ " accessiable.");
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
       
        Document document = serializer.serialize(colInst);

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