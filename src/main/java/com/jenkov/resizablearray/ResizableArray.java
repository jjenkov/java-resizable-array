package com.jenkov.resizablearray;

import java.nio.ByteBuffer;

/**
 * Created by jjenkov on 16-10-2015.
 */
public class ResizableArray {

    private ResizableArrayBuffer resizableArrayBuffer = null;

    public byte[] sharedArray = null;
    public int    offset      = 0; //offset into sharedArray where this message data starts.
    public int    capacity    = 0; //the size of the section in the sharedArray allocated to this message.
    public int    length      = 0; //the number of bytes used of the allocated section.

    public ResizableArray(ResizableArrayBuffer resizableArrayBuffer) {
        this.resizableArrayBuffer = resizableArrayBuffer;
    }

    /**
     * Writes data from the ByteBuffer into this message - meaning into the buffer backing this message.
     *
     * @param byteBuffer The ByteBuffer containing the message data to write.
     * @return
     */
    public int writeToMessage(ByteBuffer byteBuffer){
        int remaining = byteBuffer.remaining();

        while(length + remaining > capacity){
            //expand message.
            if(!this.resizableArrayBuffer.expandArray(this)) {
                return -1;
            }
        }

        
        byteBuffer.get(this.sharedArray, this.offset + this.length, remaining);
        this.length += bytesToCopy;

        return bytesToCopy;
    }

    public void free() {
        this.resizableArrayBuffer.free(this);
    }




}
