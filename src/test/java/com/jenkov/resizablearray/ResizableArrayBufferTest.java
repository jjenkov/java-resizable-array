package com.jenkov.resizablearray;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class ResizableArrayBufferTest {

    @Test
    public void testGetMessage() {

        ResizableArrayBuffer resizableArrayBuffer = new ResizableArrayBuffer(4 * 1024, 2, 128 * 1024, 10, 1024 * 1024, 1);

        ResizableArray resizableArray = resizableArrayBuffer.getArray();

        assertNotNull(resizableArray);
        assertEquals(0       , resizableArray.offset);
        assertEquals(0       , resizableArray.length);
        assertEquals(4 * 1024, resizableArray.capacity);

        ResizableArray resizableArray2 = resizableArrayBuffer.getArray();

        assertNotNull(resizableArray2);
        assertEquals(4096    , resizableArray2.offset);
        assertEquals(0       , resizableArray2.length);
        assertEquals(4 * 1024, resizableArray2.capacity);

        //todo test what happens if the small buffer space is depleted of messages.
        assertNull(resizableArrayBuffer.getArray());

    }


    @Test
    public void testExpandMessage(){
        ResizableArrayBuffer resizableArrayBuffer = new ResizableArrayBuffer(4 * 1024, 10, 128 * 1024, 10, 1024 * 1024, 1);

        ResizableArray resizableArray = resizableArrayBuffer.getArray();

        assertNotNull(resizableArray);
        assertEquals(0       , resizableArray.offset);
        assertEquals(0       , resizableArray.length);
        assertEquals(4 * 1024, resizableArray.capacity);

        resizableArrayBuffer.expandArray(resizableArray);
        assertEquals(40960     , resizableArray.offset);
        assertEquals(0         , resizableArray.length);
        assertEquals(128 * 1024, resizableArray.capacity);

        resizableArrayBuffer.expandArray(resizableArray);
        assertEquals(1351680    , resizableArray.offset);
        assertEquals(0          , resizableArray.length);
        assertEquals(1024 * 1024, resizableArray.capacity);

        //next expansion should not be possible.
        assertFalse(resizableArrayBuffer.expandArray(resizableArray));
        assertEquals(1351680    , resizableArray.offset);
        assertEquals(0          , resizableArray.length);
        assertEquals(1024 * 1024, resizableArray.capacity);



    }
}
