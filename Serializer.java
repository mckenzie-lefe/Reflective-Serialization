import ObjectPool.*;

import org.jdom2.*;
//import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.lang.reflect.Field;

import java.io.IOException;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;


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

        // TO DO: handle arrays

        if (objectMap.containsKey(obj)) {
            System.out.println("HERE X02");
            // Object already serialized, add a reference element
            Element referenceElement = new Element("reference");
            referenceElement.setText(objectMap.get(obj).toString());
            parentElement.addContent(referenceElement);

        } else {
            // Object not serialized yet, serialize it
            int objectId = idCounter++;
            objectMap.put(obj, objectId);

            Element objectElement = new Element("object");
            objectElement.setAttribute("class", obj.getClass().getName());
            objectElement.setAttribute("id", Integer.toString(objectId));
            parentElement.addContent(objectElement);

            serializeFields(obj, parentElement, objectElement);
        }
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
                    Element valueElement = new Element("value");
                    valueElement.setText(fieldObj.toString());
                    fieldElement.addContent(valueElement);

                } else if (fieldObj != null){
                    if (!objectMap.containsKey(fieldObj)) 
                        serializeObject(fieldObj, parentElement);

                    Element refElement = new Element("reference");
                    refElement.setText(objectMap.get(fieldObj).toString());
                    fieldElement.addContent(refElement);
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
        Object exampleObject = null;

        try {
            exampleObject = new ClassB();
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