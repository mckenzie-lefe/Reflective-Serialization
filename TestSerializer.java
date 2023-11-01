
import org.junit.Test;
import static org.junit.Assert.*;

import org.jdom2.Element;

public class TestSerializer {
    
    private Serializer s = new Serializer();

    @Test
    public void testSerializeNullObject() {

        s.serializeObject(null,  new Element("serialized"));

        assertEquals(0, s.getIdCounter());
        assertTrue(s.getObjectMap().isEmpty());
    }
}
