package com.reconova.face_reco_demo.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import com.reconova.java.model.RwFaceRect;
import com.reconova.processor.ImageHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapHelper {
    public static  int countBitmap = 0;

    private static String imageDir = "/mnt/sdcard/fpimg/test/imgTest/";// Environment.getExternalStorageDirectory() + "/RecoGateData/RecoPicture/";
    public static byte[] getBiggestFaceImageByte(ImageHolder imageHolder, RwFaceRect faceRect) {
        YuvImage image = new YuvImage(imageHolder.getImageData(),
                ImageFormat.NV21, imageHolder.getWidth(),
                imageHolder.getHeight(), null);
        if (image != null) {
            try {
                int width = faceRect.right - faceRect.left;
                int height = faceRect.bottom - faceRect.top;
                int left = faceRect.left - width / 2 < 0 ? 0 : faceRect.left - width / 2;
                int right = faceRect.right + width / 2 > image.getWidth() ? image.getWidth() : faceRect.right + width / 2;
                int top = faceRect.top - height / 2 < 0 ? 0 : faceRect.top - height / 2;
                int bottom = faceRect.bottom + height / 2 > image.getHeight() ? image.getHeight() : faceRect.bottom + height / 2;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(left, top, right, bottom), 80, stream);
                byte[] imageData = stream.toByteArray();
                stream.flush();
                stream.close();
                return imageData;
//                    return Base64.encodeToString(imageData, Base64.NO_WRAP);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("TAG", "save image failed");
                return null;
            }
        }
        Log.e("TAG", "save image failed");
        return null;
    }

    public static String saveImage(ImageHolder imageHolder, String fileName) {
        YuvImage image = new YuvImage(imageHolder.getImageData(),
                ImageFormat.NV21, imageHolder.getWidth(),
                imageHolder.getHeight(), null);
        if (image != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                image.compressToJpeg(new Rect(0, 0, imageHolder.getWidth(), imageHolder.getHeight()), 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                return "ok";//file.getAbsolutePath();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("TAG", "save image failed");
                return "";
            }
        }
        Log.e("TAG", "save image failed");
        return "";
    }

    public byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }

        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "+ file.getName());
        }
        fi.close();
        return buffer;
    }

}


