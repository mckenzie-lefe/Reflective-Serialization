package ObjectPool;

public class ArrayOfObjects{
    
    private SimpleObject[] simpleObjs;

    public ArrayOfObjects(int size) {
        simpleObjs = new SimpleObject[size];
    }

    public void setObjectArrayElement(int index, SimpleObject obj) {
        simpleObjs[index] = obj;
    }
}
