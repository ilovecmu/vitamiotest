#include "include/libavutil/common.h"
#include <jni.h>
#include "include/libswscale/swscale.h"

#include "include/libavcodec/avcodec.h"
AVCodecContext *pCodecCtx= NULL;
AVPacket avpkt;
FILE * video_file;
unsigned char *outbuf=NULL;
unsigned char *yuv420buf=NULL;
static int outsize=0;
static int inited = 0;
int mwidth = 1920;
int mheight = 1080;

#define LOGI printf
#define LOGE printf
/*
* encording init
*/
JNIEXPORT jint JNICALL Java_com_example_gangzhang_myapplication_VideoPlayerActivity_initH264(JNIEnv * env, jclass obj,jbyteArray filename)
{
    LOGI("%s\n", __func__);
    AVCodec *pCodec = NULL;
    avcodec_register_all();
    pCodec = avcodec_find_encoder(AV_CODEC_ID_MPEG1VIDEO);
    if (pCodec == NULL) {
      LOGE("++++++++++++codec not found\n");
      return -1;
    }
    pCodecCtx = avcodec_alloc_context3(pCodec);
    if (pCodecCtx == NULL) {
      LOGE("++++++Could not allocate video codec context\n");
      return -1;
    }
    /* put sample parameters */
    pCodecCtx->bit_rate = 400000;
    /* resolution must be a multiple of two */
    pCodecCtx->width = mwidth;
    pCodecCtx->height = mheight;
    /* frames per second */
    pCodecCtx->time_base = (AVRational) {1, 25};
    pCodecCtx->gop_size = 10; /* emit one intra frame every ten frames */
    pCodecCtx->max_b_frames = 1;
    pCodecCtx->pix_fmt = AV_PIX_FMT_YUV420P;//AV_PIX_FMT_YUYV422;
    /* open it */
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
      LOGE("+++++++Could not open codec\n");
      return -1;
    }
    outsize = mwidth * mheight * 2;
    outbuf = malloc(outsize * sizeof(char));
    yuv420buf = malloc(outsize * sizeof(char));
//    jbyte *filedir = (jbyte * )(*env)->GetByteArrayElements(env, filename, 0);
    jbyte *filedir = "/storage/sdcard/out.mp4";

    if ((video_file = fopen(filedir, "wb")) == NULL) {
      LOGE("++++++++++++open %s failed\n", filedir);
      return -1;
    }
//    (*env)->ReleaseByteArrayElements(env, filename, filedir, 0);
    return 1;
}

JNIEXPORT jint JNICALL Java_com_example_gangzhang_myapplication_VideoPlayerActivity_startH264(JNIEnv * env, jclass obj,jbyteArray yuvdata)
{
  int frameFinished=0,size=0;
  jbyte *ydata = (jbyte*)(*env)->GetByteArrayElements(env, yuvdata, 0);
//    fwrite(ydata,ydata,1920*1080,video_file);
//  AVFrame * yuv420pframe=NULL;
//  AVFrame * yuv422frame=NULL;
//  struct SwsContext *swsctx = NULL;
//  yuv420pframe=avcodec_alloc_frame();
//  yuv422frame=avcodec_alloc_frame();
//  avpicture_fill((AVPicture *) yuv420pframe, (uint8_t *)yuv420buf, AV_PIX_FMT_YUV420P,mwidth,mheight);
//  avpicture_fill((AVPicture *) yuv422frame, (uint8_t *)ydata, AV_PIX_FMT_YUYV422,mwidth,mheight);
//  swsctx = sws_getContext(mwidth,mheight, AV_PIX_FMT_YUYV422, mwidth, mheight,AV_PIX_FMT_YUV420P, SWS_BICUBIC, NULL, NULL, NULL);
//  sws_scale(swsctx,(const uint8_t* const*)yuv422frame->data,yuv422frame->linesize,0,mheight,yuv420pframe->data,yuv420pframe->linesize);
//  av_init_packet(&avpkt);
//  size = avcodec_encode_video2(pCodecCtx, &avpkt, yuv420pframe, &frameFinished);
//  if (size < 0) {
//    LOGE("+++++Error encoding frame\n");
//    return -1;
//  }
//  if(frameFinished)
//    fwrite(avpkt.data,avpkt.size,1,video_file);
//  av_free_packet(&avpkt);
//  sws_freeContext(swsctx);
//  av_free(yuv420pframe);
//  av_free(yuv422frame);
  (*env)->ReleaseByteArrayElements(env, yuvdata, ydata, 0);
}

//JNIEXPORT jint JNICALL Java_com_hclydao_webcam_Ffmpeg_videoclose(JNIEnv * env, jclass obj)
//{
//  fclose(video_file);
//  avcodec_close(pCodecCtx);
//  av_free(pCodecCtx);
//  free(outbuf);
//}