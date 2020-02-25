//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.reconova.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileTool {
    public FileTool() {
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        copyFile(sourceFile, targetFile);
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;

        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] b = new byte[5120];

            int len;
            while((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }

            outBuff.flush();
        } finally {
            if (inBuff != null) {
                inBuff.close();
            }

            if (outBuff != null) {
                outBuff.close();
            }

        }
    }

    public static void copyAssetFiles(Context context, String baseDir) {
        File fp = new File(baseDir + "/assets");
        boolean bDir = false;
        if (fp.isDirectory()) {
            bDir = true;
        } else {
            bDir = fp.mkdir();
        }

        if (bDir) {
            try {
                AssetManager am = context.getResources().getAssets();
                String[] list_fn_level1 = am.list("");
                String[] var9 = list_fn_level1;
                int var8 = list_fn_level1.length;

                for(int var7 = 0; var7 < var8; ++var7) {
                    String fn_level1 = var9[var7];
                    if (fn_level1.compareTo("databases") != 0 && fn_level1.compareTo("images") != 0 && fn_level1.compareTo("sounds") != 0 && fn_level1.compareTo("webkit") != 0) {
                        String[] list_fn_level2 = am.list(fn_level1);
                        if (list_fn_level2.length == 0) {
                            copyOneAssetFile(context, baseDir, fn_level1);
                        } else {
                            String[] var14 = list_fn_level2;
                            int var13 = list_fn_level2.length;

                            for(int var12 = 0; var12 < var13; ++var12) {
                                String fn_level2 = var14[var12];
                                File fdir = new File(baseDir + "/assets/" + fn_level1);
                                if (!fdir.isDirectory()) {
                                    fdir.mkdir();
                                }

                                String[] list_fn_level3 = am.list(fn_level1 + "/" + fn_level2);
                                if (list_fn_level3.length == 0) {
                                    Log.d("XCameraActivity", fn_level1 + "/" + fn_level2 + ":");
                                    copyOneAssetFile(context, baseDir, fn_level1 + "/" + fn_level2);
                                }
                            }
                        }
                    }
                }
            } catch (IOException var17) {
                Log.d("XCameraActivity", "AssetFile " + var17.toString());
            }
        }

    }

    public static void copyOneAssetFile(Context context, String baseDir, String fn_src) {
        String fn_dst = baseDir + "/assets/" + fn_src;
        File fp_dst = new File(fn_dst);
        if (!fp_dst.exists()) {
            AssetManager am = context.getResources().getAssets();

            try {
                InputStream fin = am.open(fn_src);
                FileOutputStream fout = new FileOutputStream(fn_dst);
                int totalLen = 0;

                int len;
                for(byte[] buffer = new byte[16384]; (len = fin.read(buffer)) != -1; totalLen += len) {
                    fout.write(buffer, 0, len);
                }

                fin.close();
                fout.close();
                Log.d("XCameraActivity", "Copy " + fn_src + " to " + fn_dst + " " + Integer.toString(totalLen));
            } catch (IOException var11) {
                Log.e("XCameraActivity", fn_src + ":" + var11.toString());
            }

        }
    }

    public static String TestFileExist(String fn_file) {
        if (fn_file != null) {
            if (!(new File(fn_file)).exists()) {
                fn_file = "";
            }
        } else {
            fn_file = "";
        }

        return fn_file;
    }
}
