
import org.junit.Test;

import ObjectPool.*;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;
import org.jdom2.*;


public class TestSerializer {
    
    private Serializer s = new Serializer();

    @Test
    public void testSerializeNullObject() {

        s.serializeObject(null,  new Element("serialized"));

        assertEquals(0, s.getIdCounter());
        assertTrue(s.getObjectMap().isEmpty());
    }

    @Test
    public void testSerializeSimpleObject() {
        Serializer serializer = new Serializer();
        SimpleObject simpleObject = new SimpleObject(42);

        Document document = serializer.serialize(simpleObject);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        Element objectElement = rootElement.getChild("object");
        assertNotNull(objectElement);
        assertEquals(simpleObject.getClass().getName(), objectElement.getAttributeValue("class"));
        assertEquals("0", objectElement.getAttributeValue("id"));

        Element valueElement = objectElement.getChild("field").getChild("value");
        assertNotNull(valueElement);
        assertEquals("42", valueElement.getText());
    }

    @Test
    public void testSerializeArrayOfObjects() {
        Serializer serializer = new Serializer();
        ArrayOfObjects arrayOfObjects = new ArrayOfObjects(3);
        arrayOfObjects.setObjectArrayElement(0, new SimpleObject(1));
        arrayOfObjects.setObjectArrayElement(1, new SimpleObject(2));
        arrayOfObjects.setObjectArrayElement(2, new SimpleObject(3));

        Document document = serializer.serialize(arrayOfObjects);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        List<Element> objectElements = rootElement.getChildren("object");
        assertNotNull(objectElements);
        assertEquals(5, objectElements.size());
        assertEquals("1", objectElements.get(0).getChild("field").getChild("reference").getText());
    }

    @Test
    public void testSerializeCollectionInstance() {
        Serializer serializer = new Serializer();
        CollectionInstance collectionInstance = new CollectionInstance();
        collectionInstance.addReference(new SimpleObject(1));
        collectionInstance.addReference(new SimpleObject(2));

        Document document = serializer.serialize(collectionInstance);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        Element objectElement = rootElement.getChild("object");
        assertNotNull(objectElement);
        assertEquals(CollectionInstance.class.getName(), objectElement.getAttributeValue("class"));
    }

    @Test
    public void testSerializeCircularReference() {
        Serializer serializer = new Serializer();
        CircularReference cRef1 = new CircularReference();
        CircularReference cRef2 = new CircularReference(cRef1);
        cRef1.setCircularReference(cRef2);

        Document document = serializer.serialize(cRef1);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        List<Element> objectElements = rootElement.getChildren("object");
        assertEquals(2, objectElements.size());

        // Validate the circular references
        Element firstObjectElement = objectElements.get(0);
        Element secondObjectElement = objectElements.get(1);
        assertNotNull(firstObjectElement);
        assertNotNull(secondObjectElement);

        // Check that the circular references are correctly serialized
        assertEquals(CircularReference.class.getName(), firstObjectElement.getAttributeValue("class"));
        assertEquals("0", firstObjectElement.getAttributeValue("id"));

        assertEquals(CircularReference.class.getName(), secondObjectElement.getAttributeValue("class"));
        assertEquals("1", secondObjectElement.getAttributeValue("id"));

        Element circularReferenceElement = secondObjectElement.getChild("field").getChild("reference");
        assertNotNull(circularReferenceElement);
        assertEquals("0", circularReferenceElement.getText());
    }

    @Test
    public void testSerializeArrayOfPrimitives() {
        Serializer serializer = new Serializer();
        int[] primitiveArray = {1, 2, 3, 4, 5};
        ArrayOfPrimitives arrayOfPrimitives = new ArrayOfPrimitives(primitiveArray);

        Document document = serializer.serialize(arrayOfPrimitives);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        Element objectElement = rootElement.getChildren().get(1);
        assertNotNull(objectElement);
        assertEquals(ArrayOfPrimitives.class.getName(), rootElement.getChildren().get(0).getAttributeValue("class"));

        int expectedSize = primitiveArray.length;
        assertEquals(Integer.toString(expectedSize), objectElement.getAttributeValue("length"));
    }

    @Test
    public void testSerializeListOfObjects() {
        Serializer serializer = new Serializer();
        List<Object> objectList = Arrays.asList(new SimpleObject(1), new SimpleObject(2), new SimpleObject(3));

        Document document = serializer.serialize(objectList);

        // Validate the XML document structure
        Element rootElement = document.getRootElement();
        assertEquals("serialized", rootElement.getName());

        List<Element> objectElements = rootElement.getChildren("object");
        assertEquals(3, objectElements.size());
    }

}