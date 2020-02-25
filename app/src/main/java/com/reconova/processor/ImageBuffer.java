//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.processor;

public class ImageBuffer {
    private byte[] mBGR = null;
    private int mBGRWidth16 = 0;
    private int mBGRHeight = 0;
    private byte[] mGray8 = null;
    private int mGray8Width16 = 0;
    private int mGray8Height = 0;

    public ImageBuffer() {
    }

    public byte[] getBGR888(ImageHolder imageHolder) {
        if (this.mBGRWidth16 != imageHolder.getWidth16() || this.mBGRHeight != imageHolder.getHeight()) {
            this.mBGR = ImageHolder.allocateBGR888(imageHolder);
            this.mBGRWidth16 = imageHolder.getWidth16();
            this.mBGRHeight = imageHolder.getHeight();
        }

        return this.mBGR;
    }

    public byte[] getGray8(ImageHolder imageHolder) {
        if (this.mGray8Width16 != imageHolder.getWidth16() || this.mGray8Height != imageHolder.getHeight()) {
            this.mGray8 = ImageHolder.allocateGray8(imageHolder);
            this.mGray8Width16 = imageHolder.getWidth16();
            this.mGray8Height = imageHolder.getHeight();
        }

        return this.mGray8;
    }

    public void clear() {
        this.clearBGR();
        this.clearGray8();
    }

    public void clearBGR() {
        this.mBGR = null;
        this.mBGRWidth16 = 0;
        this.mBGRHeight = 0;
    }

    public void clearGray8() {
        this.mGray8 = null;
        this.mGray8Width16 = 0;
        this.mGray8Height = 0;
    }
}
