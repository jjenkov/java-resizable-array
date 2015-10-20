package com.jenkov.resizablearray;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class ResizableArrayBufferTest {

    @Test
    public void testGetMessage() {

        ResizableArrayBuffer resizableArrayBuffer = new ResizableArrayBuffer();

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

    }


    @Test
    public void testExpandMessage(){
        ResizableArrayBuffer resizableArrayBuffer = new ResizableArrayBuffer();

        ResizableArray resizableArray = resizableArrayBuffer.getArray();

        byte[] smallSharedArray = resizableArray.sharedArray;

        assertNotNull(resizableArray);
        assertEquals(0       , resizableArray.offset);
        assertEquals(0       , resizableArray.length);
        assertEquals(4 * 1024, resizableArray.capacity);

        resizableArrayBuffer.expandArray(resizableArray);
        assertEquals(0         , resizableArray.offset);
        assertEquals(0         , resizableArray.length);
        assertEquals(128 * 1024, resizableArray.capacity);

        byte[] mediumSharedArray = resizableArray.sharedArray;
        assertNotSame(smallSharedArray, mediumSharedArray);

        resizableArrayBuffer.expandArray(resizableArray);
        assertEquals(0          , resizableArray.offset);
        assertEquals(0          , resizableArray.length);
        assertEquals(1024 * 1024, resizableArray.capacity);

        byte[] largeSharedArray = resizableArray.sharedArray;
        assertNotSame(smallSharedArray, largeSharedArray);
        assertNotSame(mediumSharedArray, largeSharedArray);

        //next expansion should not be possible.
        assertFalse(resizableArrayBuffer.expandArray(resizableArray));
        assertEquals(0          , resizableArray.offset);
        assertEquals(0          , resizableArray.length);
        assertEquals(1024 * 1024, resizableArray.capacity);
        assertSame(resizableArray.sharedArray, largeSharedArray);



    }
}
