//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.appdemo;

public class NativeImageProcessor {
    public NativeImageProcessor() {
    }

    public static native String ImageUtilVersion();

    public static native String cpuFeature();

    public static native void convertYUV420SP2Gray8(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV420SP2Gray8888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV420SP2RGB888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV420SP2RGBA8888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV420SP2GrayBGR888(byte[] var0, byte[] var1, byte[] var2, int var3, int var4, int var5);

    public static native void convertYUV422I2Gray8(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV422I2Gray8888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV422I2RGB888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV422I2RGBA8888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertYUV422I2GrayBGR888(byte[] var0, byte[] var1, byte[] var2, int var3, int var4, int var5);

    public static native void convertRGBA88882BGR888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertBGR8882RGBA8888(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertBGR8882RGB565(byte[] var0, byte[] var1, int var2, int var3, int var4);

    public static native void convertBGR8882Gray8(byte[] var0, byte[] var1, int var2, int var3, int var4, int var5);

    public static native void cropImagePatchBGRGray(byte[] var0, byte[] var1, int var2, int var3, int var4, int var5, byte[] var6, byte[] var7, int var8, int var9, int var10, int var11, int var12, int var13);

    public static native void cropImagePatchRGBAGray(byte[] var0, byte[] var1, int var2, int var3, int var4, int var5, byte[] var6, byte[] var7, int var8, int var9, int var10, int var11, int var12, int var13);

    public static native int convertBGR2SaveBMP(byte[] var0, int var1, int var2, String var3);

    static {
        System.loadLibrary("ImageUtil");
    }
}
