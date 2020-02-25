#ifndef _Strategy_H
#define _Strategy_H


#include <stdlib.h>
#include <string.h>
#include <vector>
#include "include/nfaster_mtcnn.h"


void *  init( const char *model_path , const char *fn_fdir_database, S_JniContext * ctx );

int InitChipset(const char *dev_name, int baud_rate );

int  finalize( void * handle_java );

const char * getVersion (void * handle_java);

int setFaceSize(void * handle, int minSize, int maxSize);

int  fastDetectFace(void *handle, const unsigned char* img, int img_w, int img_h, int widthstep,rwftface::FaceList* mFacelist, int  * size );

#endif
