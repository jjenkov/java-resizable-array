package com.jenkov.resizablearray;

/**
 * A shared buffer which can contain many messages inside. A message gets a section of the buffer to use. If the
 * message outgrows the section in size, the message requests a larger section and the message is copied to that
 * larger section. The smaller section is then freed again.
 *
 *
 * Created by jjenkov on 18-10-2015.
 */
public class ResizableArrayBuffer {

    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  = 1024 * KB;

    //package scope (default) - so they can be accessed from unit tests.
    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];   //1024 x   4KB messages =  4MB.
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];   // 128 x 128KB messages = 16MB.
    byte[]  largeMessageBuffer  = new byte[16   *   1 * MB];   //  16 *   1MB messages = 16MB.

    QueueIntFlip smallMessageBufferFreeBlocks  = new QueueIntFlip(1024); // 1024 free sections
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);  // 128  free sections
    QueueIntFlip largeMessageBufferFreeBlocks  = new QueueIntFlip(16);   // 16   free sections

    //todo make all message buffer capacities and block sizes configurable
    //todo calculate free block queue sizes based on capacity and block size of buffers.

    public ResizableArrayBuffer() {
        //add all free sections to all free section queues.
        for(int i=0; i<smallMessageBuffer.length; i+= CAPACITY_SMALL){
            this.smallMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<mediumMessageBuffer.length; i+= CAPACITY_MEDIUM){
            this.mediumMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<largeMessageBuffer.length; i+= CAPACITY_LARGE){
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public ResizableArray getArray() {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if(nextFreeSmallBlock == -1) return null;

        ResizableArray resizableArray = new ResizableArray(this);       //todo get from Message pool - caps memory usage.

        resizableArray.sharedArray = this.smallMessageBuffer;
        resizableArray.capacity    = CAPACITY_SMALL;
        resizableArray.offset      = nextFreeSmallBlock;
        resizableArray.length      = 0;

        return resizableArray;
    }

    public boolean expandArray(ResizableArray resizableArray){
        if(resizableArray.capacity == CAPACITY_SMALL){
            return moveArray(resizableArray, this.smallMessageBufferFreeBlocks, this.mediumMessageBufferFreeBlocks, this.mediumMessageBuffer, CAPACITY_MEDIUM);
        } else if(resizableArray.capacity == CAPACITY_MEDIUM){
            return moveArray(resizableArray, this.mediumMessageBufferFreeBlocks, this.largeMessageBufferFreeBlocks, this.largeMessageBuffer, CAPACITY_LARGE);
        } else {
            return false;
        }
    }

    private boolean moveArray(ResizableArray resizableArray, QueueIntFlip srcBlockQueue, QueueIntFlip destBlockQueue, byte[] dest, int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if(nextFreeBlock == -1) return false;

        System.arraycopy(resizableArray.sharedArray, resizableArray.offset, dest, nextFreeBlock, resizableArray.length);

        srcBlockQueue.put(resizableArray.offset); //free smaller block after copy

        resizableArray.sharedArray = dest;
        resizableArray.offset      = nextFreeBlock;
        resizableArray.capacity    = newCapacity;
        return true;
    }





}
