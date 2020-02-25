
#ifndef __NRWFTFACE_H__
#define __NRWFTFACE_H__

#include<string>
#include<vector>

#ifndef EXPORT
#ifdef WIN32
#define __IMPL__
#ifdef _EXPORTS
#define EXPORT __declspec(dllexport)
#else
#define EXPORT __declspec(dllimport)
#endif
#else
#define EXPORT          __attribute__ ((visibility ("default")))
#endif
#endif


#include <jni.h>

typedef
struct JniContext
{
    JNIEnv *env;
    jobject jAppCtx;
} S_JniContext;


namespace rwftface{

	struct Bbox
	{
		float score;
		int x1;
		int y1;
		int x2;
		int y2;
		float area;
		float ppoint[14];
		float regreCoord[4];
		Bbox()
		{
			score = 0.0f;
			x1 = 0.0f;
			y1 = 0.0f;
			x2 = 0.0f;
			y2 = 0.0f;
			area = 0.0f;
			memset(ppoint, 0, sizeof(ppoint));
			memset(regreCoord, 0, sizeof(regreCoord));
		}
	};

	typedef struct rwPoint
	{
		float x;
		float y;
		
		rwPoint()
		{
			x = 0.0f;
			y = 0.0f;
		}
	}rwPoint;

	typedef struct FaceBox {
		float xmin;
		float ymin;
		float xmax;
		float ymax;
		float score;
		
		FaceBox()
		{
			xmin = 0.0f;
			ymin = 0.0f;
			xmax = 0.0f;
			ymax = 0.0f;
			score = 0.0f;
		}
	} FaceBox;

	typedef struct FaceList {
		float pitch;
		float yaw;
		float roll;
		float landmark[14];
		FaceBox facebox;
		
		FaceList()
		{
			pitch = 0.0f;
			yaw = 0.0f;
			roll = 0.0f;
			memset(landmark, 0, sizeof(landmark));
		}
		
	} FaceList;

	struct DetectParam
	{
		//全图检测人脸大小
		int Min_face = 80;
		int Max_face = 400;

		//全图检测网络阈值,阈值大减少误检
		float net1_TH = 0.8f;
		float net2_TH = 0.8f;
		float net3_TH = 0.8f;

		//roi区域检测人脸大小，可以设置为上一帧人脸0.75倍到1.25倍;如果划定比较大的区域可以选择与全图检测的初始值
		int roi_Min_face = 80;
		int roi_Max_face = 400;

		//已知ROI区域内有人脸，阈值可以适当设置小点
		float roi_net1_TH = 0.5f;
		float roi_net2_TH = 0.5f;
		float roi_net3_TH = 0.5f;

		//全图缩小为多少后检测人脸,系数越小图片缩放越小，检测速度越快
		//例子：1280*960的图片场景需要检测最小人脸为100时，scale不能设置小于48/100=0.48,因为网络能检测最小人脸48左右
		float scale = 2.0f;

		//金字塔缩放系数
		float factor = 0.709f;

		//全图检测网络候选框个数,根据场景需要限定输出人脸个数
		int net1_BOXES = 10;
		int net2_BOXES = 3;

		//人脸跟踪分数,追踪到的人脸分数大于0.5才认为跟踪上
		float track_score = 0.5;
		
		//detect_face_fast没隔多少帧进行一次全图检测，剩下的结果为跟踪
		int update_num = 15;

	};

}


#ifdef __cplusplus
extern "C"
{
#endif


	/*
	* 人脸检测的初始化方法
	*
	* 参数说明：
	*   p_handle                       - 人脸检测的句柄
	*
	* 返回值：
	*   0表示成功，小于0表示失败。
	*/
	EXPORT
		int rwNRwFaceDetector_Init(void **p_handle, const char *model_path, const char *fn_fdir_database,void *context);

	/*
	* 结束人脸检测的功能，通过句柄释放相关的资源。
	*
	* 参数说明：
	*   handle         - 人脸检测验器的句柄
	*
	* 返回值：
	*   0表示成功，小于0表示失败。
	*/
	EXPORT
		int rwNRwFaceDetector_Finalize(void *handle);
	

		/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   param          - 人脸检测参数
	* 返回值：
	*   1表示成功，0表示失败
	*/
	EXPORT
		bool rwNRwFaceDetector_InitParam(void *handle, const rwftface::DetectParam &param);	

	/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   img_data       - 灰度数据图像指针
	*   img_w          - 图像的宽
	*   img_h          - 图像的高
	*   widthstep      - 灰度图片等于img_w
	*   faces          - 返回人脸检测信息
	* 返回值：
	*   0表示成功，小于0表示失败
	*/

	EXPORT
		int rwNRwFaceDetector_DetectFace(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, 
		std::vector<rwftface::FaceList>& faces);

	/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   img_data       - 灰度数据图像指针
	*   img_w          - 图像的宽
	*   img_h          - 图像的高
	*   widthstep      - 灰度图片等于img_w
	*   faces          - 返回人脸检测信息
	* 返回值：
	*   0表示成功，小于0表示失败
	*/

	EXPORT
		int rwNRwFaceDetector_DetectFaceTwo(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, 
		std::vector<rwftface::FaceList>& faces);
		
	/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   img_data       - 灰度数据图像指针
	*   img_w          - 图像的宽
	*   img_h          - 图像的高
	*   widthstep      - 灰度图片等于img_w
	*   roi_x          - 待检测roi区域的左上角x坐标
	*   roi_y          - 待检测roi区域的左上角y坐标
	*   roi_w          - 待检测roi区域的宽坐标
	*   roi_h          - 待检测roi区域的高坐标
	*   faces          - 返回人脸检测信息
	* 返回值：
	*   0表示成功，小于0表示失败
	*/

	EXPORT
		int rwNRwFaceDetector_DetectFaceByRoi(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, int roi_x, int roi_y, int roi_w, int roi_h, 
		std::vector<rwftface::FaceList>& faces);

		
	/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   img_data       - 灰度数据图像指针
	*   img_w          - 图像的宽
	*   img_h          - 图像的高
	*   widthstep      - 灰度图片等于img_w
	*   pre_faces      - 上一帧人脸位置信息
	*   faces          - 返回跟踪的人脸检测信息
	* 返回值：
	*   0表示成功，小于0表示失败
	*/

	EXPORT
		int rwNRwFaceDetector_TrackFace(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, std::vector<rwftface::FaceList>& pre_faces, 
		std::vector<rwftface::FaceList>& faces);
		
		
	/*
	*
	*
	* 参数说明：
	*   handle         - 人脸检测器的句柄
	*   img_data       - 灰度数据图像指针
	*   img_w          - 图像的宽
	*   img_h          - 图像的高
	*   widthstep      - 灰度图片等于img_w
	*   faces          - 返回跟踪的人脸检测信息
	* 返回值：
	*   0表示成功，小于0表示失败
	*/

	EXPORT
		int rwNRwFaceDetector_DetectFaceFast(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep, 
		std::vector<rwftface::FaceList>& faces);


	/*
	* 获取算法代码和模型的版本号
	*
	* 参数说明：
	*   handle         - 人脸检测器句柄
	* 返回值：
	*   版本号字符串
	*/
	EXPORT
		const char* rwNRwFaceDetector_GetVersion(void *handle);


#ifdef __cplusplus
}
#endif

#endif  /* __NRWFTFACE_H__ */



