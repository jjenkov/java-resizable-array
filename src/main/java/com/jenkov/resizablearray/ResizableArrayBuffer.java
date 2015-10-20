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


    //package scope (default) - so they can be accessed from unit tests.
    byte[] sharedArray = null;

    private int capacity = 0;

    private int smallBlockSize  = 0;
    private int mediumBlockSize = 0;
    private int largeBlockSize  = 0;

    private int smallBlockCount  = 0;
    private int mediumBlockCount = 0;
    private int largeBlockCount  = 0;


    QueueIntFlip smallFreeBlocks  = null;
    QueueIntFlip mediumFreeBlocks = null;
    QueueIntFlip largeFreeBlocks  = null;

    public ResizableArrayBuffer(int smallBlockSize, int smallBlockCount, int mediumBlockSize, int mediumBlockCount, int largeBlockSize, int largeBlockCount) {
        this.capacity = smallBlockSize * smallBlockCount + mediumBlockSize * mediumBlockCount + largeBlockSize * largeBlockCount;
        this.sharedArray = new byte[this.capacity];

        this.smallBlockSize   = smallBlockSize;
        this.smallBlockCount  = smallBlockCount;
        this.mediumBlockSize  = mediumBlockSize;
        this.mediumBlockCount = mediumBlockCount;
        this.largeBlockSize   = largeBlockSize;
        this.largeBlockCount  = largeBlockCount;

        this.smallFreeBlocks  = new QueueIntFlip(smallBlockCount);
        this.mediumFreeBlocks = new QueueIntFlip(mediumBlockCount);
        this.largeFreeBlocks  = new QueueIntFlip(largeBlockCount);

        //add all free sections to all free section queues.
        int smallBlocksEndIndex   = smallBlockSize * smallBlockCount;
        for(int i=0; i<smallBlocksEndIndex; i+= smallBlockSize){
            this.smallFreeBlocks.put(i);
        }
        int mediumBlocksStartIndex = smallBlockCount * smallBlockSize;
        int mediumBlocksEndIndex   = mediumBlocksStartIndex + mediumBlockSize * mediumBlockCount;
        for(int i=mediumBlocksStartIndex; i<mediumBlocksEndIndex; i+= mediumBlockSize){
            this.mediumFreeBlocks.put(i);
        }
        int largeBlocksStartIndex = mediumBlocksEndIndex;
        int largeBlocksEndIndex   = largeBlocksStartIndex + largeBlockSize * largeBlockCount;
        for(int i=largeBlocksStartIndex; i<largeBlocksEndIndex; i+= largeBlockSize){
            this.largeFreeBlocks.put(i);
        }
    }

    public ResizableArray getArray() {
        int nextFreeSmallBlock = this.smallFreeBlocks.take();

        if(nextFreeSmallBlock == -1) return null;

        ResizableArray resizableArray = new ResizableArray(this);       //todo get from Message pool - caps memory usage.

        resizableArray.sharedArray = this.sharedArray;
        resizableArray.capacity    = smallBlockSize;
        resizableArray.offset      = nextFreeSmallBlock;
        resizableArray.length      = 0;

        return resizableArray;
    }

    public boolean expandArray(ResizableArray resizableArray){
        if(resizableArray.capacity == smallBlockSize){
            return moveArray(resizableArray, this.smallFreeBlocks, this.mediumFreeBlocks, mediumBlockSize);
        } else if(resizableArray.capacity == mediumBlockSize){
            return moveArray(resizableArray, this.mediumFreeBlocks, this.largeFreeBlocks, largeBlockSize);
        } else {
            return false;
        }
    }

    private boolean moveArray(ResizableArray resizableArray, QueueIntFlip srcBlockQueue, QueueIntFlip destBlockQueue, int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if(nextFreeBlock == -1) return false;

        System.arraycopy(this.sharedArray, resizableArray.offset, this.sharedArray, nextFreeBlock, resizableArray.length);

        srcBlockQueue.put(resizableArray.offset); //free smaller block after copy

        resizableArray.sharedArray = this.sharedArray;
        resizableArray.offset      = nextFreeBlock;
        resizableArray.capacity    = newCapacity;
        return true;
    }





}
