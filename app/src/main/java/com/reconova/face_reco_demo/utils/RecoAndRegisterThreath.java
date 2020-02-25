package com.reconova.face_reco_demo.utils;

/**
 * 这个文件主要是用于5000张人脸的注册和500张人脸的识别，还有一些设置人脸识别的参数的函数
 */

import android.util.Log;

import com.reconova.java.FastDetectJniInf;
import com.reconova.java.model.RecoImage;
import com.reconova.java.model.RwFaceRect;
import com.reconova.processor.ImageHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class RecoAndRegisterThreath {

    final String TAG = "nwamtf_Reco_Register";
    //定义宏
    final int REGIST_FACE = 1;
    final int DETECT_FACE = 2;
    static int  count = 0;
    static int registerCountErr = 0;
    static int countReco = 0;

    static int testSaveNum = 0;
    BitmapHelper myBitMapHelper = new BitmapHelper();

    FileHelper myFileHelper = new FileHelper();





    private RwFaceRect getBiggestFace(ArrayList<RwFaceRect> faceRects) {
        int maxWidth = 0;
        RwFaceRect biggestFace = null;
        Iterator faceRectList = faceRects.iterator();

        while (faceRectList.hasNext()) {
            RwFaceRect faceRect = (RwFaceRect) faceRectList.next();
            int width = faceRect.right - faceRect.left;
            if (width > maxWidth) {
                biggestFace = faceRect;
                maxWidth = width;
            }
        }

        return biggestFace;
    }

    public String getHexString(byte[] b, int start, int end) {
        StringBuilder buf = new StringBuilder();
        for (int i = start; i < end; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            buf.append(hex.toUpperCase()).append(" ");
        }
        return buf.toString();
    }

    void saveSCameraImageToSdcard (ImageHolder imageHolder, String picName){
//        Log.i("saveImg", "imageHolder type = "+ imageHolder.getImageType());
//
//        String path = "/mnt/sdcard/testImg1/"  + picName + "_" +  BitmapHelper.countBitmap++ +".jpg";
//        myBitMapHelper.saveImage(imageHolder,path);
//        Log.i("saveImg", "path" + path);
    }

    public synchronized ArrayList<RwFaceRect> faceDetect(ImageHolder imageHolder)
    {
        RecoImage image = RecoImage.convertToRecoImage(imageHolder);
        long startFastTime = System.currentTimeMillis();
        ArrayList<RwFaceRect>  DetectFaceFast = FastDetectJniInf.getInstance().DetectFaceFast(image,0);
        Log.i("TestTime","time " + (System.currentTimeMillis() - startFastTime));
        return DetectFaceFast;
    }

    void registFace( File readfile)
    {
        final String TAG_REGISTER = "SD_faceRegisterTimeTest";
        ImageHolder imageHolder = ImageHolder.createFromImagePath(readfile.getAbsolutePath());
        if(null == imageHolder)
        {
            Log.i(TAG, "ERR : imageHolder = null" + imageHolder);
            return ;
        }
        if( null == imageHolder.getImageData()) {
            Log.i(TAG, "ERR : imageHolder.getImageData() = null" );
            return ;
        }


        //检测detect
        //RecoFaceProcessor.getInstance().setFaceSize(10,900);
        long faceDetectTime = System.currentTimeMillis();

        ArrayList<RwFaceRect> faceList =   faceDetect(imageHolder);
        Log.e(TAG_REGISTER, "faceDetectTime:" + (System.currentTimeMillis() - faceDetectTime));
        if (faceList == null)//注册不成功的图片，探测的数据facelist不是空，但是里面的数据是空的
        {
            Log.i(TAG, "ERR : faceList = null" );
            Log.i("faceRegister",",faceRegister ERR  resul  ;"+ readfile.getName() );
            return ;
        }
        if(0 == faceList.size())
        {
            Log.i(TAG, "ERR : faceList.size() = null" );
            Log.i("faceRegister",",faceRegister ERR  resul"+ readfile.getName() );
            return ;
        }
        Log.i(TAG, "ok: faceList.size() =  " + faceList.size() +";name = "+ readfile.getName() );

    }



    void faceReco( File readfile)//SD卡识别
    {
        return;
    }


    /**
     * 读取某个文件夹下的所有文件
     */
    static int picCount = 0;
    public  boolean readfile(String filepath, int choice) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
//                Log.i("filePath1", "file path=" + file.getPath());
//                Log.i("filePath1", "file absolutepath=" + file.getAbsolutePath());
//                Log.i("filePath1", "file name=" + file.getName());
            } else if (file.isDirectory()) {
                //Log.i("filePath", "文件夹" + file.list().length);
                String[] filelist = file.list();
                Arrays.sort(filelist, String.CASE_INSENSITIVE_ORDER);
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "//" + filelist[i]);

                    // 文件夹下的文件
                    if (!readfile.isDirectory()) {
                        Log.i("filePath", "path=" + readfile.getPath());
                        Log.i("fileabsolutePath", "absolutepath="+ readfile.getAbsolutePath());
                        Log.i("filePathGetName", "name=" + readfile.getName());

                        if(REGIST_FACE == choice) {
                            // 注册代码
                            registFace( readfile);
                        }else if(DETECT_FACE == choice){
                            //识别数据
                            faceReco( readfile);
                        }

                    } else if (readfile.isDirectory()) {
                        readfile(filepath + "//" + filelist[i] ,choice);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            Log.i("filePath", "readfile()   Exception:" + e.getMessage());
        }
        return true;
    }

}
