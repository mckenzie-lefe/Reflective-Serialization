package ObjectPool;

public class ReferenceToObject {
    
    private SimpleObject simpleObj;
    private ReferenceToObject circularRef;
    private ArrayOfPrimitives arrPrimitives;
    private ArrayOfObjects arrObjects;
    private CollectionInstance collectionInst;

    public ReferenceToObject(){}
    
    public void setReferenceObject(Object obj) throws IllegalArgumentException, NullPointerException {
        Class<?> clazz = obj.getClass();

        if (clazz == SimpleObject.class)
            simpleObj = (SimpleObject) obj;
        else if (clazz == ReferenceToObject.class)
            circularRef = (ReferenceToObject) obj;
        else if (clazz == ArrayOfPrimitives.class)
            arrPrimitives = (ArrayOfPrimitives) obj;
        else if (clazz == ArrayOfObjects.class)
            arrObjects = (ArrayOfObjects) obj;
        else if ( clazz == CollectionInstance.class)
            collectionInst = (CollectionInstance) obj;
        else 
            throw new IllegalArgumentException();
    }
}
