//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.processor;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import com.reconova.appdemo.NativeImageProcessor;
import java.nio.ByteBuffer;

public class ImageHolder {
    public static final int YUVSP420_TYPE = 0;
    public static final int RGBA8888_TYPE = 1;
    private int width;
    private int height;
    private int width16;
    private byte[] imageData;
    private int type;
    private int ratio;
    private float time_interval = -1.0F;

    public ImageHolder(byte[] imageData, int width, int height, int type) {
        this.imageData = imageData;
        this.width = width;
        this.height = height;
        this.type = type;
        this.ratio = 1;
        this.generateWidth();
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.generateWidth();
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getRatio() {
        return this.ratio;
    }

    public int getWidth() {
        return this.width / this.ratio;
    }

    public int getWidth16() {
        return this.width16 / this.ratio;
    }

    public int getHeight() {
        return this.height / this.ratio;
    }

    public int getOriginalHeight() {
        return this.height;
    }

    public int getOriginalWidth() {
        return this.width;
    }

    public void setTimeInterval(float time_interval) {
        this.time_interval = time_interval;
    }

    public float getTime_interval() {
        return this.time_interval;
    }

    public void setImageType(int type) {
        this.type = type;
    }

    public int getImageType() {
        return this.type;
    }

    private void generateWidth() {
        this.width16 = this.width + 15 >> 4 << 4;
    }

    public static ImageHolder createFromBitmap(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        byte[] pixels_target = new byte[width * height * 4];
        bitmap.copyPixelsToBuffer(ByteBuffer.wrap(pixels_target));
        ImageHolder imageHolder = new ImageHolder(pixels_target, width, height, 1);
        return imageHolder;
    }

    public static ImageHolder createFromImagePath(String path) {
        Bitmap bitmap = ImageHolder.BitmapUtils.createBitmap(path);
        if (bitmap == null) {
            return null;
        } else {
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            byte[] pixels_target = new byte[width * height * 4];
            bitmap.copyPixelsToBuffer(ByteBuffer.wrap(pixels_target));
            ImageHolder imageHolder = new ImageHolder(pixels_target, width, height, 1);
            bitmap.recycle();
            bitmap = null;
            return imageHolder;
        }
    }

    public static ImageHolder createFromUri(Uri uri, Context context) {
        String path = ImageHolder.BitmapUtils.getRealPathFromURI(uri, context);
        return createFromImagePath(path);
    }

    public static byte[] allocateBGR888(ImageHolder imageHolder) {
        return new byte[3 * imageHolder.width16 * imageHolder.height / (imageHolder.ratio * imageHolder.ratio)];
    }

    public static byte[] allocateGray8(ImageHolder imageHolder) {
        return new byte[1 * imageHolder.width16 * imageHolder.height / (imageHolder.ratio * imageHolder.ratio)];
    }

    public boolean verifyCapacityOfGray8(byte[] gray8) {
        int capacity = 3 * this.width16 * this.height;
        return gray8.length >= capacity;
    }

    public boolean verifyCapacityOfBGR888(byte[] bgr888) {
        int capacity = 3 * this.width16 * this.height;
        return bgr888.length >= capacity;
    }

    public static void convertToBGR888AndGray(ImageHolder image, byte[] bgr888, byte[] gray8) {
        int width = image.width;
        int height = image.height;
        int width16 = image.width16;
        byte[] data = image.imageData;
        if (image.getImageType() == 0) {
            NativeImageProcessor.convertYUV420SP2GrayBGR888(data, gray8, bgr888, width16 / image.ratio, height / image.ratio, image.ratio);
        } else if (image.getImageType() == 1) {
            NativeImageProcessor.convertRGBA88882BGR888(data, bgr888, width, height, width16 * 3);
            int src_widthstep = 3 * width16;
            NativeImageProcessor.convertBGR8882Gray8(bgr888, gray8, width16, height, src_widthstep, width16);
        }

    }

    public static void convertToBGR888(ImageHolder image, byte[] bgr888, byte[] gray8) {
        int width = image.width;
        int height = image.height;
        int width16 = image.width16;
        byte[] data = image.imageData;
        if (image.getImageType() == 0) {
            NativeImageProcessor.convertYUV420SP2RGB888(image.imageData, bgr888, width16 / image.ratio, height / image.ratio, image.ratio);
        } else if (image.getImageType() == 1) {
            NativeImageProcessor.convertRGBA88882BGR888(data, bgr888, width, height, width16 * 3);
        }

    }

    public static void convertToGray8(ImageHolder image, byte[] gray8) {
        int width = image.width;
        int height = image.height;
        int width16 = image.width16;
        byte[] data = image.imageData;
        if (image.getImageType() == 0) {
            NativeImageProcessor.convertYUV420SP2Gray8(image.imageData, gray8, width16 / image.ratio, height / image.ratio, image.ratio);
        } else if (image.getImageType() == 1) {
            byte[] bgr888 = allocateBGR888(image);
            NativeImageProcessor.convertRGBA88882BGR888(data, bgr888, width, height, width16 * 3);
            int src_widthstep = 3 * width16;
            NativeImageProcessor.convertBGR8882Gray8(bgr888, gray8, width16, height, src_widthstep, width16);
        }

    }

    private static class BitmapUtils {
        private BitmapUtils() {
        }

        public static Bitmap createBitmap(String pathName) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            if (options.outWidth < 4800 && options.outHeight < 4800) {
                if (options.outWidth < 3600 && options.outHeight < 3600) {
                    if (options.outWidth < 2400 && options.outHeight < 2400) {
                        if (options.outWidth < 1200 && options.outHeight < 1200) {
                            options.inSampleSize = 1;
                        } else {
                            options.inSampleSize = 2;
                        }
                    } else {
                        options.inSampleSize = 4;
                    }
                } else {
                    options.inSampleSize = 4;
                }
            } else {
                options.inSampleSize = 8;
            }

            options.inJustDecodeBounds = false;

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
                return bitmap;
            } catch (OutOfMemoryError var3) {
                var3.printStackTrace();
                return null;
            }
        }

        private static String getRealPathFromURI(Uri contentURI, Context context) {
            Cursor cursor = context.getContentResolver().query(contentURI, (String[])null, (String)null, (String[])null, (String)null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex("_data");
            return cursor.getString(idx);
        }
    }
}
