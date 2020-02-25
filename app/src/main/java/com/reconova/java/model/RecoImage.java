//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.java.model;

import com.reconova.processor.ImageHolder;

public class RecoImage {
    public static final int FORMAT_NORMAL_IMAGE = 0;
    public static final int FORMAT__RAW_BGR888 = 1;
    public static final int FORMAT__RAW_GRAY = 2;
    public byte[] imagedata;
    public int width;
    public int height;
    public int format;

    public RecoImage() {
    }

    public static RecoImage convertToRecoImage(ImageHolder imageHolder) {
        RecoImage image = new RecoImage();
        byte[] grayImageData = ImageHolder.allocateGray8(imageHolder);
        ImageHolder.convertToGray8(imageHolder, grayImageData);
        image.imagedata = grayImageData;
        image.format = 2;
        image.width = imageHolder.getWidth16();
        image.height = imageHolder.getHeight();
        return image;
    }
}
