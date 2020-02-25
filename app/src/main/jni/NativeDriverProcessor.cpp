//
// Created by ruiwei on 2019/7/20.
//

#include<stdio.h>

#include <android/log.h>
#include <string.h>
#include <fcntl.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <sys/select.h>
#include <linux/videodev2.h>
#include <unistd.h>

#include "include/nfaster_mtcnn.h"

#include "Strategy.h"

#ifdef __cplusplus
extern "C" {
#endif

#define PRINTF_LOG (1)

#include "com_reconova_java_FastDetectJni.h"

#define LOG_TAG "C_NATIVE_DRIVER_PRO"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


char* jstringToNative(JNIEnv *env, jstring jstr)
{
	if ((env)->ExceptionCheck() == JNI_TRUE || jstr == NULL)
	{
		(env)->ExceptionDescribe();
		(env)->ExceptionClear();
		printf("jstringToNative函数转换时,传入的参数str为空");
		return NULL;
	}

	jbyteArray bytes = 0;
	jthrowable exc;
	char *result = 0;
	if ((env)->EnsureLocalCapacity(2) < 0) {
		return 0; /* out of memory error */
	}
	jclass jcls_str = (env)->FindClass("java/lang/String");
	jmethodID MID_String_getBytes = (env)->GetMethodID(jcls_str, "getBytes", "()[B");

	bytes = (jbyteArray)(env)->CallObjectMethod(jstr, MID_String_getBytes);
	exc = (env)->ExceptionOccurred();
	if (!exc) {
		jint len = (env)->GetArrayLength( bytes);
		result = (char *)malloc(len + 1);
		if (result == 0) {
			//JNU_ThrowByName(env, "java/lang/OutOfMemoryError", 	0);
			(env)->DeleteLocalRef(bytes);
			return 0;
		}
		(env)->GetByteArrayRegion(bytes, 0, len, (jbyte *)result);
		result[len] = 0; /* NULL-terminate */
	} else {
		(env)->DeleteLocalRef(exc);
	}
	(env)->DeleteLocalRef(bytes);
	return (char*)result;
}



//=================================================================================

  JNIEXPORT jlong JNICALL Java_com_reconova_java_FastDetectJni_initFace
    (JNIEnv * env , jclass obj, jstring model_path_java, jstring lic_path_java, jobject conext_java)
    {

/*
        LOGD("init：wc--->\n");
        //1、找到jclass
        jclass j_class = env->FindClass("java/lang/Object");
        LOGD("init：0--->\n");
        //2、获取要调用方法的id
        jmethodID id = env->GetMethodID(j_class, "toString", "()Ljava/lang/String;");
        LOGD("init：1--->n");
        //4、进行调用
        jstring stringContext = (jstring)env->CallObjectMethod(conext_java, id);
        LOGD("init：2--->\n");
        const char * contextString = jstringToNative(env, stringContext);
        LOGD(" contextString = %s \n", contextString);
*/
        S_JniContext ctx;
        ctx.env = env;
        ctx.jAppCtx = conext_java;

        //void * p_handle ;
        const char *model_path  = jstringToNative(env, model_path_java);
        const char *lic_path = jstringToNative(env, lic_path_java);
#if PRINTF_LOG
        LOGD("init：model_path = %s \n", model_path);
#endif
        //int  ret = rwNRwFaceDetector_Init( &p_handle,  (const char *)model_path, ( const char *)lic_path, &ctx );
        //int ret  = init( p_handle, (const char *)model_path, ( const char *)lic_path, &ctx);
        void * p_handle = init(model_path, lic_path, &ctx);
#if PRINTF_LOG
        LOGD("##init   p_handle = %p \n" , (void *)p_handle);
#endif
        return (jlong)p_handle;
    }


    JNIEXPORT jint JNICALL Java_com_reconova_java_FastDetectJni_InitChipset
      (JNIEnv * env, jclass obj, jstring dev_name_java, jint baud_rate_java)
      {
          const char *dev_name = jstringToNative(env, dev_name_java);
          int baud_rate = baud_rate_java;
          return 0; //rwNDynamicLiveDetector_ChipsetConfig_LicenseParamInit(dev_name, baud_rate);
      }

    JNIEXPORT void JNICALL Java_com_reconova_java_FastDetectJni_destroyFace
      (JNIEnv * env, jclass obj, jlong handle_java)
      {
#if PRINTF_LOG
            LOGD(" Java_com_reconova_java_FastDetectJni_destroyFace = %p \n",(void *)handle_java);
#endif
            //int ret = rwNRwFaceDetector_Finalize((void *)handle_java);
            int ret =finalize((void *)handle_java);
#if PRINTF_LOG
             LOGD(" Java_com_reconova_java_FastDetectJni_destroyFace  ret = % d\n",ret);
#endif
      }


      JNIEXPORT jstring JNICALL Java_com_reconova_java_FastDetectJni_getVersion
          (JNIEnv *env, jclass obj, jlong handle_j)
      {

            //LOGD(" len = %s \n", rwNRwFaceDetector_GetVersion((void *)handle ));
            //const char * version = rwNRwFaceDetector_GetVersion((void *)handle_j );
            const char * version = getVersion ((void *)handle_j);
#if PRINTF_LOG
            LOGD(" Verion = %s \n",version);
#endif
            #define MAX_VERSION_LEN (64)
            char buf[MAX_VERSION_LEN]="ERR";
            int len  = strlen(version);
            if(len > MAX_VERSION_LEN){
                len  = MAX_VERSION_LEN;
            }
#if PRINTF_LOG
            LOGD(" len = %d \n",len);
#endif
            for(int i =0 ; i< len; i++){
              buf[i] = *(version+i);
            }

             buf[len -1] = '\0';

            return  (env)->NewStringUTF(buf);
      }

JNIEXPORT jint JNICALL Java_com_reconova_java_FastDetectJni_initParam
  (JNIEnv * env, jclass obj, jlong handle_j, jobject param_obj)
  {
    jclass classParam = (env)->FindClass("com/reconova/java/model/FastDetectParam");

    jfieldID m_fid_Min_face = (env)->GetFieldID(classParam, "Min_face", "I");
    int getMin_face = (env)->GetIntField(param_obj, m_fid_Min_face);
    jfieldID m_fid_Max_face = (env)->GetFieldID(classParam, "Max_face", "I");
    int getMax_face = (env)->GetIntField(param_obj, m_fid_Max_face);
#if PRINTF_LOG
    LOGD("getMin_face = %d; getMax_face = %d \n", getMin_face, getMax_face);
#endif

    jfieldID m_fid_net1_TH = (env)->GetFieldID(classParam, "net1_TH", "F");
    float getNet1_TH = (env)->GetFloatField(param_obj, m_fid_net1_TH);
    jfieldID m_fid_net2_TH = (env)->GetFieldID(classParam, "net2_TH", "F");
    float getNet2_TH = (env)->GetFloatField(param_obj, m_fid_net2_TH);
    jfieldID m_fid_net3_TH = (env)->GetFieldID(classParam, "net3_TH", "F");
    float getNet3_TH = (env)->GetFloatField(param_obj, m_fid_net3_TH);

#if PRINTF_LOG
    LOGD("getNet1_TH = %f; getNet2_TH = %f , m_fid_net3_TH = %f  \n", getNet1_TH, getNet2_TH, getNet3_TH );
#endif

    jfieldID m_fid_roi_Min_face = (env)->GetFieldID(classParam, "roi_Min_face", "I");
    int getRoi_Min_face = (env)->GetIntField(param_obj, m_fid_roi_Min_face);
    jfieldID m_fid_roi_Max_face = (env)->GetFieldID(classParam, "roi_Max_face", "I");
    int getRoi_Max_face = (env)->GetIntField(param_obj, m_fid_roi_Max_face);
#if PRINTF_LOG
    LOGD("getRoi_Min_face = %d; getRoi_Max_face = %d \n", getRoi_Min_face, getRoi_Max_face);
#endif

    jfieldID m_fid_roi_net1_TH = (env)->GetFieldID(classParam, "roi_net1_TH", "F");
    float getRoi_net1_TH = (env)->GetFloatField(param_obj, m_fid_roi_net1_TH);
    jfieldID m_fid_roi_net2_TH = (env)->GetFieldID(classParam, "roi_net2_TH", "F");
    float getRoi_net2_TH = (env)->GetFloatField(param_obj, m_fid_roi_net2_TH);
    jfieldID m_fid_Roi_net3_TH = (env)->GetFieldID(classParam, "roi_net3_TH", "F");
    float getRoi_net3_TH = (env)->GetFloatField(param_obj, m_fid_Roi_net3_TH);
#if PRINTF_LOG
    LOGD("getRio_net1_TH = %f; getRoi_net2_TH = %f , m_fid_net3_TH = %f\n", getRoi_net1_TH, getRoi_net2_TH, getRoi_net3_TH );
#endif

    jfieldID m_fid_scale = (env)->GetFieldID(classParam, "scale", "F");
    float getScale = (env)->GetFloatField(param_obj, m_fid_scale);
    jfieldID m_fid_factor = (env)->GetFieldID(classParam, "factor", "F");
    float getFactor = (env)->GetFloatField(param_obj, m_fid_factor);
#if PRINTF_LOG
    LOGD("getScale = %f; getFactor = %f\n", getScale, getFactor );
#endif

    jfieldID m_fid_net1_BOXES = (env)->GetFieldID(classParam, "net1_BOXES", "I");
    int getNet1_BOXES = (env)->GetIntField(param_obj, m_fid_net1_BOXES);
    jfieldID m_fid_net2_BOXES = (env)->GetFieldID(classParam, "net2_BOXES", "I");
    int getNet2_BOXES = (env)->GetIntField(param_obj, m_fid_net2_BOXES);
#if PRINTF_LOG
    LOGD("getNet1_BOXES = %d; getNet2_BOXES = %d \n", getNet1_BOXES, getNet2_BOXES);
#endif

    jfieldID m_fid_track_score = (env)->GetFieldID(classParam, "track_score", "F");
    float getTrack_score = (env)->GetFloatField(param_obj, m_fid_track_score);
    jfieldID m_fid_update_num = (env)->GetFieldID(classParam, "update_num", "I");
    int getUpdate_num = (env)->GetIntField(param_obj, m_fid_update_num);
#if PRINTF_LOG
    LOGD("getTrack_score = %f; getUpdate_num = %d \n", getTrack_score, getUpdate_num);
#endif

    rwftface::DetectParam mParam;
    mParam.Min_face = getMin_face;
    mParam.Max_face = getMax_face;

    mParam.net1_TH = getNet1_TH;
    mParam.net2_TH = getNet2_TH;
    mParam.net3_TH = getNet3_TH;

    mParam.roi_Min_face = getRoi_Min_face;
    mParam.roi_Max_face = getRoi_Max_face;

    mParam.roi_net1_TH = getRoi_net1_TH;
    mParam.roi_net2_TH = getRoi_net2_TH;
    mParam.roi_net3_TH = getRoi_net3_TH;

    mParam.scale = getScale;
    mParam.factor = getFactor;

    mParam.net1_BOXES = getNet1_BOXES;
    mParam.net2_BOXES = getNet2_BOXES;

    mParam.track_score = getTrack_score;
    mParam.update_num = getUpdate_num;

    bool retBool = rwNRwFaceDetector_InitParam((void *)handle_j, (const rwftface::DetectParam) *(&mParam));
#if PRINTF_LOG
    LOGD("retBool = %d; \n", retBool);
#endif

   (env)->DeleteLocalRef(classParam);
   if(retBool == 0){
        return -1;
   }
   return 0;
  }


  JNIEXPORT jint JNICALL Java_com_reconova_java_FastDetectJni_SetFaceSize
    (JNIEnv * env, jclass obj, jlong handle_j, jint min_face_j, jint max_face_j)
    {
        return setFaceSize((void * )handle_j,  min_face_j,  max_face_j);
    }



          JNIEXPORT jobjectArray JNICALL Java_com_reconova_java_FastDetectJni_detectFace
            (JNIEnv * env, jclass obj, jlong handle, jobject faceImage, jint method)
            {
                 if (faceImage != NULL)
                    {
                        jclass classRecoImage = env->FindClass("com/reconova/java/model/RecoImage");
                        jfieldID m_fid_5 = env->GetFieldID(classRecoImage, "width", "I");
                        jfieldID m_fid_6 = env->GetFieldID(classRecoImage, "height", "I");
                        jfieldID m_fid_7 = env->GetFieldID(classRecoImage, "format", "I");

                        jfieldID imgArrays = (env)->GetFieldID(classRecoImage,"imagedata","[B");
                        jbyteArray jByte_arr = (jbyteArray)(env)->GetObjectField(faceImage,imgArrays);
                        jbyte* byte_arr = (env)->GetByteArrayElements(jByte_arr,NULL);
                        jsize len = (env)->GetArrayLength(jByte_arr);

                        int width = (env)->GetIntField(faceImage, m_fid_5);
                        int height = (env)->GetIntField(faceImage, m_fid_6);
                        int formatData = (env)->GetIntField(faceImage, m_fid_7);

#if PRINTF_LOG
                        LOGD("ExtractFaceFeat : width = %d; height = %d , formatData = %d， len= %d \n", width, height, formatData, len);
#endif

                        unsigned char *img_Gray = (unsigned char *)byte_arr;
                        if(img_Gray == NULL)
                        {
                            env->ReleaseByteArrayElements(jByte_arr,byte_arr,0);
                            env->DeleteLocalRef(classRecoImage);
#if PRINTF_LOG
                            LOGD("Err: img_Gray == NULL");
#endif
                            return NULL;
                        }


                          //std::vector<rwftface::FaceList> faces;
                          //int ret =  rwNRwFaceDetector_DetectFaceFast((void *)handle, (const unsigned char* )img_Gray, width, height, width, faces);
    rwftface::FaceList mFacelist[10];
    int size = 0;
    int ret = fastDetectFace((void *)handle, (const unsigned char* )img_Gray, width, height, width, (rwftface::FaceList* )&mFacelist, &size );

                          // 释放图片内存
                          env->ReleaseByteArrayElements(jByte_arr,byte_arr,0);
                          env->DeleteLocalRef(classRecoImage);
                          if(ret != 0)
                          {
#if PRINTF_LOG
                              LOGD("ERR size  = %d , ret = %d \n",size, ret);
#endif
                              return NULL;
                          }

                          if(size <= 0)
                          {
#if PRINTF_LOG
                              LOGD("ERR size  = %d , ret = %d \n",size, ret);
#endif
                              return NULL;
                          }

                        int list_size = size;

                        jclass infoClass = env->FindClass("com/reconova/java/model/RwFaceRect");
                        jobjectArray infos = env->NewObjectArray(list_size,infoClass,NULL);
                        jmethodID Info = env->GetMethodID(infoClass,"<init>","()V");

                        jfieldID track_no = (env)->GetFieldID(infoClass, "track_no", "I");
                        jfieldID person_no = (env)->GetFieldID(infoClass, "person_no", "I");

                        jfieldID left = (env) ->GetFieldID(infoClass,"left","I");
                        jfieldID top = (env) ->GetFieldID(infoClass,"top","I");
                        jfieldID right = (env) ->GetFieldID(infoClass,"right","I");
                        jfieldID bottom = (env) ->GetFieldID(infoClass,"bottom","I");

                        jfieldID lefteye_x = (env) ->GetFieldID(infoClass,"lefteye_x","I");
                        jfieldID lefteye_y = (env) ->GetFieldID(infoClass,"lefteye_y","I");
                        jfieldID righteye_x = (env) ->GetFieldID(infoClass,"righteye_x","I");
                        jfieldID righteye_y = (env) ->GetFieldID(infoClass,"righteye_y","I");
                        jfieldID nose_x = (env) ->GetFieldID(infoClass,"nose_x","I");
                        jfieldID nose_y = (env) ->GetFieldID(infoClass,"nose_y","I");
                        jfieldID centermouth_x = (env) ->GetFieldID(infoClass,"centermouth_x","I");
                        jfieldID centermouth_y = (env) ->GetFieldID(infoClass,"centermouth_y","I");

                        jfieldID facial_score = (env) ->GetFieldID(infoClass,"facial_score","F");
                        jfieldID type = (env) ->GetFieldID(infoClass,"type","I");
                        jfieldID glassness = (env) ->GetFieldID(infoClass,"glassness","F");

                        jfieldID pitch = (env) ->GetFieldID(infoClass,"pitch","F");
                        jfieldID yaw = (env) ->GetFieldID(infoClass,"yaw","F");
                        jfieldID roll = (env) ->GetFieldID(infoClass,"roll","F");

LOGD("list_size  = %d, left = %f \n", list_size, mFacelist[0].facebox.xmin);


                        for (int i = 0; i < list_size; i++)
                        {
                            jobject infoObject = env->NewObject(infoClass,Info);

                            rwftface::FaceList * pFaceList = (rwftface::FaceList *)&(mFacelist[i]);
                            //const RwFaceRectEx& rect = face_rect_list[i];

                            env->SetIntField(infoObject, track_no, -1);
                            env->SetIntField(infoObject, person_no, -1);
                            env->SetIntField(infoObject, lefteye_x, -1);
                            env->SetIntField(infoObject, lefteye_y, -1);
                            env->SetIntField(infoObject, righteye_x, -1);
                            env->SetIntField(infoObject, righteye_y, -1);
                            env->SetIntField(infoObject, nose_x, -1);
                            env->SetIntField(infoObject, nose_y, -1);
                            env->SetIntField(infoObject, centermouth_x, -1);
                            env->SetIntField(infoObject, centermouth_y, -1);
                            //list FaceRect FaceAttr

                            env->SetIntField(infoObject, left, mFacelist[i].facebox.xmin);
                            env->SetIntField(infoObject, top, mFacelist[i].facebox.ymin);
                            env->SetIntField(infoObject, right, mFacelist[i].facebox.xmax);
                            env->SetIntField(infoObject, bottom, mFacelist[i].facebox.ymax);

                            env->SetFloatField(infoObject, facial_score, mFacelist[i].facebox.score);
                            env->SetIntField(infoObject, type, -1);
                            env->SetFloatField(infoObject, glassness, -1);

                            env->SetFloatField(infoObject, pitch, mFacelist[i].pitch);
                            env->SetFloatField(infoObject, yaw, mFacelist[i].yaw);
                            env->SetFloatField(infoObject, roll, mFacelist[i].roll);

                            env->SetObjectArrayElement(infos,i,infoObject);
                        }

                         env->DeleteLocalRef(infoClass);

                        return infos;
                 }
               return NULL;
        }



#ifdef __cplusplus
}
#endif

