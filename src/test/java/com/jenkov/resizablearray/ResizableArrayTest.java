package com.jenkov.resizablearray;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


/**
 * Created by jjenkov on 18-10-2015.
 */
public class ResizableArrayTest {


    @Test
    public void testWriteToMessage() {
        ResizableArrayBuffer resizableArrayBuffer = new ResizableArrayBuffer(4 * 1024, 10, 128 * 1024, 10, 1024 * 1024, 1);

        ResizableArray resizableArray = resizableArrayBuffer.getArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);

        fill(byteBuffer, 4096);

        int written = resizableArray.writeToMessage(byteBuffer);
        assertEquals(4096, written);
        assertEquals(4096, resizableArray.length);

        fill(byteBuffer, 124 * 1024);
        written = resizableArray.writeToMessage(byteBuffer);
        assertEquals(124 * 1024, written);
        assertEquals(128 * 1024, resizableArray.length);

        fill(byteBuffer, (1024-128) * 1024);
        written = resizableArray.writeToMessage(byteBuffer);
        assertEquals(896  * 1024, written);
        assertEquals(1024 * 1024, resizableArray.length);

        fill(byteBuffer, 1);
        written = resizableArray.writeToMessage(byteBuffer);
        assertEquals(-1, written);

    }

    private void fill(ByteBuffer byteBuffer, int length){
        byteBuffer.clear();
        for(int i=0; i<length; i++){
            byteBuffer.put((byte) (i%128));
        }
        byteBuffer.flip();
    }
}
