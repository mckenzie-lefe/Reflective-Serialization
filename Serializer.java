import org.jdom2.Document;
import org.jdom2.Element;
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
            // TO DO: handle already serialized object
        } else {
            // Object not serialized yet, serialize it
            int objectId = idCounter++;
            objectMap.put(obj, objectId);

            Element objectElement = new Element("object");
            objectElement.setAttribute("class", obj.getClass().getName());
            objectElement.setAttribute("id", Integer.toString(objectId));
            parentElement.addContent(objectElement);

            serializeFields(obj, objectElement);
        }
    }

    private void serializeFields(Object obj, Element objectElement) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            // TO DO: Serialize fields
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
}