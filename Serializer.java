import ObjectPool.*;

import org.jdom2.*;
//import org.jdom2.Document;
//mport org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.lang.reflect.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.text.html.parser.Element;


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

    protected void serializeObject(Object obj, Element parentElement) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        int objectId = idCounter++;
        Element objectElement = new Element("object");

        objectMap.put(obj, objectId);

        objectElement.setAttribute("class", obj.getClass().getName());
        objectElement.setAttribute("id", Integer.toString(objectId));

        if (clazz.isArray()) {
            int len = Array.getLength(clazz);
            objectElement.setAttribute("length", len);

            //TO DO: clean up -> check each elements type of jus component type??
            if (clazz.getComponentType().isPrimitive()){
                for (int i = 0; i < len; i++) {
                    serializeValue(objectElement, Array.get(obj, i).toString());
                }
            } else {
                for (int i = 0; i < len; i++) {
                    serializeReference(Array.get(obj, i).toString(), objectElement);
                }
            }
            
        } else {
            serializeFields(obj, parentElement, objectElement);
        }

        parentElement.addContent(objectElement);
    }

    private void serializeReference(Object obj, Element parentElement) {
        if (obj == null) {  // TO DO: handle nulls
            System.out.println("reference object is null!!!");
            return;
        }
        if (!objectMap.containsKey(obj)) 
            serializeObject(obj, parentElement);

        Element refElement = new Element("reference");
        refElement.setText(objectMap.get(obj).toString());
        parentElement.addContent(refElement);
    }

    private void serializeValue(Element parentElement, String value) {
        Element valueElement = new Element("value");
        valueElement.setText(value);
        parentElement.addContent(valueElement);
    }

    private void serializeFields(Object obj, Element parentElement, Element objectElement) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Make private fields accessible

            Element fieldElement = new Element("field");
            fieldElement.setAttribute("name", field.getName());
            fieldElement.setAttribute("declaringclass", field.getDeclaringClass().getName());

            try {
                Object fieldObj = field.get(obj);
                Class<?> fType = field.getType();

                if (fType.isPrimitive()) {
                    serializeValue(fieldElement, fieldObj.toString());

                } else if (fieldObj != null) { 
                    serializeReference(fieldObj, parentElement);
                    
                    /* 
                    if (!objectMap.containsKey(fieldObj)) 
                        serializeObject(fieldObj, parentElement);

                    Element refElement = new Element("reference");
                    refElement.setText(objectMap.get(fieldObj).toString());
                    fieldElement.addContent(refElement);
                    */
                }
                objectElement.addContent(fieldElement);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /** For testing
     * 
     * @return current count of serialized elements
     */
    protected int getIdCounter() {
        return idCounter;
    }

    /** For testing
     * 
     * @return map of serialized elements
     */
    protected Map<Object, Integer> getObjectMap() {
        return this.objectMap;
    }

    public static void main(String[] args) {
        Serializer serializer = new Serializer();
        ArrayOfObjects exampleObject = null;

        try {
            exampleObject = new ArrayOfObjects(3);
            exampleObject.setObjectArrayElement(0, new SimpleObject(4));
            exampleObject.setObjectArrayElement(1, new SimpleObject(5));
            exampleObject.setObjectArrayElement(2, new SimpleObject(6));

        } catch (Exception e) {}
       
        Document document = serializer.serialize(exampleObject);

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