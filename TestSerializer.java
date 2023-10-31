
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.text.html.parser.Element;

public class TestSerializer {
    
    private Serializer s;

    @Test
    public void testSerializeNullObject() {

        s.serializeObject(null,  new Element("serialized"));

        assertEquals(0, s.getIdCounter());
        assertTrue(s.getObjectMap().isEmpty());
    }
}
