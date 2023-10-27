import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.lang.reflect.Field;

import java.io.IOException;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;

public class Serializer {

    public Serializer() {
    }

    public Document serialize(Object obj) {
        Element rootElement = new Element("serialized");
        Document document = new Document(rootElement);

        serializeObject(obj, rootElement);

        return document;
    }

    private void serializeObject(Object obj, Element parentElement) {
        if (obj == null) {
            return;
        }

        // TO DO: serialize objects
    }
}