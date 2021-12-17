package com.klh.lwsample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.klh.lwsample.controller.FlyCtrl;
import com.klh.lwsample.controller.FlyModel;
import com.klh.lwsample.util.LWLogUtils;
import com.klh.lwsample.util.Utils;
import com.lewei.uart_protol.ControlPara;
import com.sensetime.hand.CvHandTrack;
import com.sensetime.hand.model.CvHandInfo;
import com.sensetime.trackapi.BodyObjectDetect;
import com.sensetime.trackapi.ObjectTrack;
import com.sensetime.trackapi.model.CvPixelFormat;
import com.sensetime.trackapi.model.ImageOrientation;
import com.sensetime.trackapi.model.ObjectInfo;
import com.sensetime.util.HandOrientation;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.Semaphore;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MySurfaceView extends BaseGLView implements View.OnTouchListener{
	public  static long sumTime = 0;
	public  static int  mframe = 1;
	public  static int  mAvegLoss =0;

	/** Camera Dispaly**/
//    private SurfaceTexture mSurfaceTexture;
	/** 鏄惁婊戝姩鏂瑰紡璁剧疆璺熻釜鍖哄煙**/
	private volatile  boolean mSetTargetByMove=false;
	/** 鏄惁璁剧疆浜嗚窡韪尯鍩?**/
	private boolean   mHasSetTarget=false;
	/** 鏄惁鏄偣鍑讳睛娴嬩簡涓?娆? **/
	private volatile  boolean mSetTargetByDetect=false;

	/**tracking 鐨勭粨鏋滄暟缁?**/
	private ObjectInfo[] result;
	/** tracking 鐨勭粨鏋滄暟缁勫鍒朵唤**/
	private ObjectInfo[] copyResult;
	/** 鐐瑰嚮灞忓箷鐨剎杞村拰y杞村潗鏍?**/
	private int clickX = 0; // point
	private int clickY = 0;
	/** 渚︽祴缁撴灉鏁扮粍**/
	private ObjectInfo [] mDetectionResult;
	/** OpenGL 甯姪绫?**/
	private OpenGLHelper mOpenGLHelper;
	private HandlerThread mHandlerThread;
	private Handler mBackgroundHandler;
	/** 鎹㈢畻鎴愯鍥剧殑鍧愭爣绯婚噷闈㈢殑鍧愭爣闇?瑕佹崲绠楃殑姣旂巼**/
	private float mViewRadio=1.0f;
	/** 鎹㈢畻鎴愰瑙堢敾闈㈢殑鍧愭爣绯婚噷闈㈢殑鍧愭爣鐨勬崲绠楁瘮渚?**/
	private float mPreviewRadio=1.0f;
	/** 淇″彿閲?,淇濊瘉Tracking鍑烘潵鐨勬暟鎹湪涓荤嚎绋嬪拰GL绾跨▼,瀹夊叏**/
	private Semaphore mLock=new Semaphore(1,true);

	private BodyObjectDetect mObjectDetect;
	private ObjectTrack mObjectTrack;
	private int U_INDEX = 0;
	private int V_INDEX = 0;
	private int LENGTH = 0;
	private int LENGTH_4 = 0;

	private int g_width = 0;
	private int g_height = 0;
	private ByteBuffer yBuffer;
	private ByteBuffer uBuffer;
	private ByteBuffer vBuffer;
	private  byte[] glYdata, glUdata, glVdata;//Y U V鏁版嵁
	private CvHandTrack mCvHandTrack;
	public MySurfaceView(Context context) {
		super(context);
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/** 鍒濆鍖?**/
	private void init(){
		setRenderer(this);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setOnTouchListener(this);
		mHandlerThread=new HandlerThread("CameraGLView");
		mHandlerThread.start();
		mBackgroundHandler=new Handler(mHandlerThread.getLooper());
		mOpenGLHelper=new OpenGLHelper();
		if(FlyModel.getInstance().cpuV7a){
			mObjectDetect = new BodyObjectDetect(Utils.BODY_OBJECT_DETECT_PATH);
			mObjectTrack = new ObjectTrack(Utils.OBJECT_TRACK_MODEL_PATH);
		}
//		glYdata = new byte[4096*2160];   //4096×2160
//		glUdata = new byte[4096*2160 / 4];
//		glVdata = new byte[4096*2160 / 4];
		
		glYdata = new byte[2560*1440];   //4096×2160
		glUdata = new byte[2560*1440 / 4];
		glVdata = new byte[2560*1440 / 4];
		if(FlyModel.getInstance().cpuV7a){
			initHandDetect();
		}
	}

	private void initHandDetect() {
		long time = System.currentTimeMillis();
		mCvHandTrack = new CvHandTrack(null, null);
		Log.d(TAG, "initHandDetect initDetect time: " + (System.currentTimeMillis() - time) + "ms");
	}

	private void releaseDetect() {
		Log.i(TAG, "HandDetect releaseDetect");
		if (mCvHandTrack != null) {
			mCvHandTrack.release();
			mCvHandTrack = null;
		}
	}

	/** 涓荤嚎绋婾I **/
	private Handler mMainHandler=new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MySurfaceView.this.setOnTouchListener(MySurfaceView.this);
			mHasSetTarget=(Boolean)msg.obj;
			if(!mHasSetTarget){
				mSetTargetByDetect=true;
//                if(msg.what==0) {
//                    Toast.makeText(mContext, "setTargetObjectInfo 澶辫触", Toast.LENGTH_SHORT).show();
//                }
//
//                if(msg.what==1){
//                    Toast.makeText(mContext, "setTargetObjectInfo  澶辫触", Toast.LENGTH_SHORT).show();
//                }
			}else {

			}
		}
	};

	private volatile boolean stopDetect=false;
	private volatile boolean detactDraw=false;

	float poinX=0;
	float poinY=0;
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(!FlyModel.getInstance().cpuV7a){
			return false;
		}
		if(!canTouch){
			return false;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
//                mHasSetTarget = false;

				break;
			}

			case MotionEvent.ACTION_UP:
				//Log.e("ZL","MotionEvent.ACTION_UP");

				if(mHasSetTarget){  //这里是有目标了，重新detect
					mHasSetTarget=false;
					stopDetect=false;
					result=null;
//                    mDetectionResult=null;
					detectData();

				}else {  //这里是正在detact，选中目标
					poinX = (int) event.getX();
					poinY = (int) event.getY();
					if (mDetectionResult != null && mDetectionResult.length > 0) {
						for (int i = 0; i < mDetectionResult.length; i++) {
							mDetectionResult[i].objectRect.left *= mViewRadio;
							mDetectionResult[i].objectRect.top *= mViewRadio;
							mDetectionResult[i].objectRect.bottom *= mViewRadio;
							mDetectionResult[i].objectRect.right *= mViewRadio;
							mDetectionResult[i].blurValue *= mViewRadio;

						}
						LWLogUtils.e("mViewRadio="+mViewRadio);
						LWLogUtils.e("aaaa 这里填充数据  这里刷新2222222");
						for(int i = 0; i < mDetectionResult.length; i++){
//                            mOpenGLHelper.drawCenterPoint((mDetectionResult[i].objectRect.left * 2 + mDetectionResult[i].objectRect.width()) / 2,
//                                    (mDetectionResult[i].objectRect.top * 2 + mDetectionResult[i].objectRect.height()) / 2, mViewWidth, mViewHeight);
//                            mOpenGLHelper.drawRect(mDetectionResult[i], mViewWidth, mViewHeight);
							float topLeftX=mDetectionResult[i].objectRect.left;
							float topLeftY=mDetectionResult[i].objectRect.top;
							float bottomRightX=mDetectionResult[i].objectRect.right;
							float bottomRightY=mDetectionResult[i].objectRect.bottom;
							LWLogUtils.e("topLeftX="+topLeftX+",topLeftY="+topLeftY+",bottomRightX="+bottomRightX
									+",bottomRightY="+bottomRightY+",poinX="+poinX+",poinY="+poinY);

							if ((poinX>=topLeftX)&&(poinX<=bottomRightX)&&(poinY>=topLeftY)&&(poinY<=bottomRightY)) {
								int ret=-1;
								LWLogUtils.e("topLeftX="+topLeftX+",topLeftY="+topLeftY+",bottomRightX="+bottomRightX
										+",bottomRightY="+bottomRightY+",poinX="+poinX+",poinY="+poinY+"点击中了");

								mDetectionResult[i].objectRect.left *= mPreviewRadio;
								mDetectionResult[i].objectRect.top *= mPreviewRadio;
								mDetectionResult[i].objectRect.bottom *= mPreviewRadio;
								mDetectionResult[i].objectRect.right *= mPreviewRadio;
								mDetectionResult[i].blurValue *= mPreviewRadio;

								mObjectTrack.setTargetObjectInfo(detectData, CvPixelFormat.YUV420P,mPreviewWidth,mPreviewHight,mPreviewWidth, ImageOrientation.ROTAION_0,
										(int)mDetectionResult[i].objectRect.left,
										(int)mDetectionResult[i].objectRect.top,
										(int)mDetectionResult[i].objectRect.width(),
										(int)(mDetectionResult[i].objectRect.height()/3*2)
								);
								ret =mObjectTrack.mResultCode[0];
								if(ret==0){
									mHasSetTarget=true;
									mDetectionResult=null;
									stopDetect=true;
									break;
								}

							}



						}
						LWLogUtils.e("aaaa 这里填充数据  这里刷新333333333");
						if(mDetectionResult!=null){
							/**可能GL线程连续获得同步锁**/
							for (int i = 0; i < mDetectionResult.length; i++) {
								mDetectionResult[i].objectRect.left *= mPreviewRadio;
								mDetectionResult[i].objectRect.top *= mPreviewRadio;
								mDetectionResult[i].objectRect.bottom *= mPreviewRadio;
								mDetectionResult[i].objectRect.right *= mPreviewRadio;
								mDetectionResult[i].blurValue *= mPreviewRadio;
							}
						}

					}


					lateTime=System.currentTimeMillis();
					if(timer==null){
						timer = new Timer();
						timer.schedule(new MyTask(), 2, 50);
						LWLogUtils.e("timeInterval new ");
					}
				}

//                lateTime=System.currentTimeMillis();
//                if(timer==null){
//                    timer = new Timer();
//                    timer.schedule(new MyTask(), 2, 50);
//                    LWLogUtils.e("timeInterval new ");
//                }

				break;

		}
		return canTouch;
	}

	Timer timer;

	@Override
	protected void whenSurfaceCreate() {
		try {
			mOpenGLHelper.OpenGLInit(mContext);
			firstSetBuf=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//杩欎釜鏄瑙堝浘鐗囩殑鍒嗚鲸鐜囩殑瀹?
	int mPreviewWidth=1280;
	//杩欎釜鏄瑙堝浘鐗囩殑鍒嗚鲸鐜囩殑楂?
	int mPreviewHight=720;

	@Override
	protected void callWhenSurfaceChanged(int width, int height) {
//        mViewRadio = (mViewWidth * 1.0f / mPreviewWidth);
//        mPreviewRadio = (mPreviewWidth * 1.0f / mViewWidth);
	}

	private boolean firstSetBuf=true;

	private int preObjWidth;
	private int preObjHeight;

	private int reSetFlyCtrlData=0;

	private volatile byte[] detectData=null;
	private volatile boolean copydDetectData=false;
	int followType=0;
	
	
//	public void release(){
//		trackData=null;
//		trackHandData=null;
//		detectData=null;
//		glYdata =null;
//		glUdata =null;
//		glVdata =null;
//	}

	
	public void setFollowType(int follow_Type){
		followType=follow_Type;
	}
	public void copyBmpBuffer(byte[] data,  int width, int height) {
		//LWLogUtils.e("width="+width);
		//LWLogUtils.e("widthheight="+height);
		//LWLogUtils.e("widthmPreviewWidth="+width);
		//LWLogUtils.e("widthmPreviewHight="+height);
		if(firstSetBuf){
			mPreviewWidth=width;
			mPreviewHight=height;
		
			mViewRadio = (mViewWidth * 1.0f / mPreviewWidth);
			mPreviewRadio = (mPreviewWidth * 1.0f / mViewWidth);
			firstSetBuf=false;
			
//			initHandDetect();
			if(FlyModel.getInstance().cpuV7a){
				trackData();
				detectHand();
			}
		}
		if(!trackDating){
			if(trackData==null){
				trackData=new byte[data.length];
				
			}
			System.arraycopy(data,0,trackData,0,data.length);
			copyData=true;
		}
		
//		if(!copyHandData){
//			if(trackHandData==null){
//				trackHandData=new byte[data.length];
//			}
//			System.arraycopy(data,0,trackHandData,0,data.length);
//			copyHandData=true;
//		}
//
//		if(!copydDetectData){
//			if(detectData==null){
//				detectData=new byte[data.length];
//			}
//			System.arraycopy(data,0,detectData,0,data.length);
//			copydDetectData=true;
//		}
		
		if(trackHandData==null){
			trackHandData=new byte[data.length];
		}
		if(!copyHandData){
			System.arraycopy(data,0,trackHandData,0,data.length);
			copyHandData=true;
		}

		if(detectData==null){
			detectData=new byte[data.length];
		}
		if(!copydDetectData){
			System.arraycopy(data,0,detectData,0,data.length);
			copydDetectData=true;
		}
		if (g_width != width || g_height != height) {
			g_width = width;
			g_height = height;

			U_INDEX = width * height;
			V_INDEX = width * height * 5 / 4;
			LENGTH = width * height;
			LENGTH_4 = width * height / 4;
			yBuffer = ByteBuffer.wrap(glYdata, 0, LENGTH);
			uBuffer = ByteBuffer.wrap(glUdata, 0, LENGTH_4);
			vBuffer = ByteBuffer.wrap( glVdata , 0, LENGTH_4);

		}
		System.arraycopy(data, 0, glYdata, 0, LENGTH);
		System.arraycopy(data, U_INDEX,glUdata, 0, LENGTH_4);
		System.arraycopy(data, V_INDEX,  glVdata , 0, LENGTH_4);

		mRendering=true;
		requestRender();
	}

	class MyTask extends java.util.TimerTask{
		//        private  long lateTime=0;   //鏈?鏂扮殑鏃堕棿
//        private  long timeInterval=0;
//        public MyTask(long timeInterval,long lateTime){
//            this.timeInterval=timeInterval;
//            this.lateTime=lateTime;
//        }
		public void run(){

			timeInterval=System.currentTimeMillis()-lateTime;
//			Log.e("","timeInterval ="+timeInterval);
			if(timeInterval>40){
				FlyCtrl.rudderdata[4] = 1500 ;
				FlyCtrl.rudderdata[3] = 1500;
				FlyCtrl.rudderdata[1] = 1500;
				FlyCtrl.rudderdata[2] = 1500;
				result=null;
			}
			lateTime=System.currentTimeMillis();

		}
	}


	private volatile boolean trackDating=false;   //trackDating鐨勬椂鍊欐墠鏀炬暟鎹?
	private boolean stopTrackDating=false;  //鏄惁鍋滄TrackData
	private volatile byte[] trackData=null;
	private volatile boolean copyData=false;  //鏄惁鎷疯礉浜嗘暟鎹?
	private volatile long lateTime=0;   //鏈?鏂扮殑鏃堕棿
	private volatile long lateTimeHadResults=0;   //鏈?鍚庝竴娆℃娴嬪埌鐗╀綋鐨勬椂鍊?
	private volatile long timeInterval=0;

	private boolean canTouch=false;//杩欓噷浠ｈ〃鐨勬槸璺熻釜鎸夐挳娌℃湁浜捣鏉ョ殑鏃跺?欙紝涓嶈偗鐐瑰嚮锛屽氨鏄笉浼氭湁鐐瑰嚮鏃堕棿锛岃缃窡韪洰鏍?
	public void SetCanTouch(){
		canTouch=true;
		mHasSetTarget=false;
		stopDetect=false;
		detectData();
	}

	private boolean canHand=false;
	public void setCanHand(boolean can){
		this.canHand=can;
	}

	public void setTargetFalse(){
		mHasSetTarget=false;
		canTouch=false;
//		mHasSetTarget=true;
		mDetectionResult=null;
		stopDetect=true;
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(200);//浼戠湢3绉?
					result=null;
				} catch (InterruptedException e) {
					e.printStackTrace();
					result=null;
				}
			}
		}).start();
		result=null;

	}
	private String[] mHandType = { "OK", "剪刀", "点赞", "布", "手枪", "爱心",
			"托举", "拳头", "作揖", "比心", "指尖", "666", "双手合十", "未知" };
	private volatile byte[] trackHandData=null;
	private boolean stopTrackHandDating=false;  //鏄惁鍋滄TrackData
	private volatile boolean copyHandData=false;  //鏄惁鎷疯礉浜嗘暟鎹?
	int handCheckNumbePhoto=0;
	int handCheckNumbeRec=0;
	private void detectHand(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopTrackHandDating){
					if(copyHandData){

						if(!canHand){
							try {
//								LWLogUtils.e(" hand!  start222222");
								Thread.sleep(1100);
								continue;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}



						//LWLogUtils.e(" hand!  start");
						CvHandInfo[] handInfos = null;
						mCvHandTrack.reset();
						handInfos = mCvHandTrack.track(trackHandData, com.sensetime.hand.model.CvPixelFormat.YUV420P,mPreviewWidth,mPreviewHight, HandOrientation.UP);
						// 鏃犳墜鍔?
						if (handInfos == null || handInfos.length <= 0) {
							//LWLogUtils.e("this image is no hand! 2222222222222222");

						}else {

							String faceMsg = "";
							String type="";
							for (int i = 0; i < handInfos.length; i++) {
								type=mHandType[handInfos[i].handType];
								faceMsg = faceMsg + "index:" + i + " handId:" + handInfos[i].handType + " handType:" + mHandType[handInfos[i].handType] + " score:" + handInfos[i].score + "\n";
								//LWLogUtils.e("faceMsg="+faceMsg);
								if(handInfos[i].handType==3 ){
									handCheckNumbePhoto=0;
									handCheckNumbeRec=handCheckNumbeRec+1;
									if(handCheckNumbeRec==4){
										handCheckNumbeRec=0;
										
										mLWHandCb.onTakeRecord();
										
									}
									break;
								}else if(handInfos[i].handType==1){
									handCheckNumbeRec=0;
									handCheckNumbePhoto=handCheckNumbePhoto+1;
									if(handCheckNumbePhoto==4){
										handCheckNumbePhoto=0;
										
										mLWHandCb.onTakePhoto(type);
										
									}

									break;
								}

							}
//							if(mLWHandCb!=null){
//								if(handInfos[0].handType==0 ){
//									mLWHandCb.onTakeRecord();
//								}else if(handInfos[0].handType==1){
//									mLWHandCb.onTakePhoto(type);
//								}
//
//							}
							//LWLogUtils.e("this image had hand!  33333333333333333+faceMsg="+faceMsg);
						}
						//LWLogUtils.e(" hand!  finish");
//						try {
//							Thread.sleep(1100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}

						copyHandData=false;
					}
					
				}

			}
		}).start();



	}
	private void trackData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopTrackDating){
					if(!copyData){
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					trackDating=true;
					if(mHasSetTarget){
						long a=System.currentTimeMillis();
						try {
							mLock.acquire();
//                          LWLogUtils.e("ZL 123 mHasSetTarget="+mHasSetTarget);
							Log.d(TAG,"track in");
							long b = System.currentTimeMillis();
							result = mObjectTrack.track(trackData, CvPixelFormat.YUV420P, mPreviewWidth,mPreviewHight,mPreviewWidth,ImageOrientation.ROTAION_0);
							long e = System.currentTimeMillis();
							//mObjectTrack.mResultCode[0]==1;//todo 杩欓噷鏄窡韪埌鐗╀綋浜嗭紝鍚﹀垯灏辨槸娌℃湁鐗╀綋锛屽氨涓嶉渶瑕佸啀 mObjectTrack.track銆傞渶瑕侀噸鏂癝etTarget
							if(mObjectTrack.mResultCode[0]!=1){
								result=null;

							}
							Log.d(TAG,"track out");

							if (result != null && result.length > 0) {
								Log.e("ZL", "left="+(int)result[0].objectRect.left+",top="+(int)result[0].objectRect.top
										+",right="+(int)result[0].objectRect.right+",bottom="+(int)result[0].objectRect.bottom);
								Log.d(TAG, "track focus");

								if (copyResult == null || copyResult.length != result.length) {
									copyResult = new ObjectInfo[result.length];
								}
								System.arraycopy(result, 0, copyResult, 0, result.length);
								Log.d(TAG,"track focus out");
								lateTime=System.currentTimeMillis();
								lateTimeHadResults=System.currentTimeMillis();
								try {                                    //100
								
//									if(LeweiLib.HD_flag==1){
//										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.bmpHeight=720;
//										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.bmpWidth=1280;
//								    }

									
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.bmpHeight=mPreviewHight;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.bmpWidth=mPreviewWidth;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.rect.objectHeight=result[0].objectRect.height();
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.rect.objectWidth=result[0].objectRect.width();
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.rect.objectX=result[0].objectRect.left;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.rect.objectY=result[0].objectRect.top;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.resultTime=System.currentTimeMillis();
									FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode= ControlPara.ControlMode.CTL_VisionFollow;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.autoMode=1;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.trackElvatorSpeed=10;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.trackRudderSpeed=5;
//									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
//									=ControlPara.VisionTrackID.VisionTrack_JX1802;
									LWLogUtils.e("followType="+followType);
									if(followType==0){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_None;
									}else if(followType==1){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_HelicuteH818HW;
									}else if(followType==2){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_AttopXT1PLUS;
									}else if(followType==3){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_JX1802;
									}else if(followType==4){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_HeliMax1332;
									}else if(followType==5){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_WLModelQ636;
									}else if(followType==6){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_LMRCF2;
									}else if(followType==7){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_JYGPS020;
									}else if(followType==8){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										= ControlPara.VisionTrackID.VisionTrack_XLLA6HW;
									}else if(followType==9){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_YH19HW;
									}else if(followType==10){
										FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.droneID
										=ControlPara.VisionTrackID.VisionTrack_FY603R;
									}
									
									if(!canTouch){
										FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode= ControlPara.ControlMode.CTL_Joystick;
										FlyCtrl.rudderdata[4] = 1500 ;
										FlyCtrl.rudderdata[3] = 1500;
										FlyCtrl.rudderdata[1] = 1500;
										FlyCtrl.rudderdata[2] = 1500;
									}

								


//                                    FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.trackRudderSpeed=10;
//                                    FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.trackElvatorSpeed=5;
//                                    LWLogUtils.e("data: 68888888888888888");
								} catch (Exception e1) {
									e1.printStackTrace();
									FlyCtrl.rudderdata[4] = 1500 ;
									FlyCtrl.rudderdata[3] = 1500;
									FlyCtrl.rudderdata[1] = 1500;
									FlyCtrl.rudderdata[2] = 1500;
									FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.supportSearch=0;
								}
								preObjWidth=(int)result[0].objectRect.width();
								preObjHeight=(int)result[0].objectRect.height();
								reSetFlyCtrlData=0;


							}else{
								reSetFlyCtrlData=reSetFlyCtrlData+1;
								if(reSetFlyCtrlData>=1){
									FlyCtrl.rudderdata[4] = 1500 ;
									FlyCtrl.rudderdata[3] = 1500;
									FlyCtrl.rudderdata[1] = 1500;
									FlyCtrl.rudderdata[2] = 1500;
									Log.d(TAG,"track fail 22222");
								}
								Log.d(TAG,"track fail");

							}
							long f = System.currentTimeMillis();
							Log.e("ZL","KcfTracker.tracking 鑰楁椂==="+(e-b)+"姣 涓婂眰澶勭悊鎿嶄綔=="+(f-e)+"姣"+ Arrays.toString(result)+"鎬绘椂闂? ==="+(f-b)+"姣   ");
						} catch (Exception e1) {
							e1.printStackTrace();
						}finally {
							mLock.release();
						}
						if(!canTouch){
							FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode= ControlPara.ControlMode.CTL_Joystick;
							FlyCtrl.rudderdata[4] = 1500 ;
							FlyCtrl.rudderdata[3] = 1500;
							FlyCtrl.rudderdata[1] = 1500;
							FlyCtrl.rudderdata[2] = 1500;
						}
						long g=System.currentTimeMillis();
						//Log.e("ZL","receive 涓?娆℃墽琛岃?楁椂==="+(g-a)+"姣");
						mframe++;
						sumTime +=(g-a);
						mAvegLoss = (int) (sumTime / mframe);
					}


					trackDating=false;
					copyData=false;
					mRendering=true;
//                    LWLogUtils.e("aaaa 杩欓噷濉厖鏁版嵁  杩欓噷鍒锋柊   杩欓噷璇锋眰");
//                    trackRequestRender=true;
					MySurfaceView.this.requestRender();
				}

			}
		}).start();
	}


//    private volatile boolean trackRequestRender=false;

	public void setVRView(){
		mOpenGLHelper.setVRView();


	}

	public void setPlaneView(){
		mOpenGLHelper.setPlaneView();
	}


	@Override
	protected void showOwnFrame() {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        LWLogUtils.e("aaaa 这里填充数据  这里刷新");
		//LWLogUtils.e("nnnnnnnnnnn渲染数据");
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE,GLES20.GL_ONE_MINUS_SRC_ALPHA);

		//render the texture to FBO if new frame is available
		if(mRendering){
//            mSurfaceTexture.updateTexImage();

//            if(!trackRequestRender){
//
//            }
			mOpenGLHelper.drawCameraFrameView(mPreviewWidth,mPreviewHight,yBuffer,uBuffer,vBuffer);
			try {

				mLock.acquire();
				if (result != null && result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						result[i].objectRect.left *= mViewRadio;
						result[i].objectRect.top *= mViewRadio;
						result[i].objectRect.bottom *= mViewRadio;
						result[i].objectRect.right *= mViewRadio;
						result[i].blurValue *= mViewRadio;

					}
					LWLogUtils.e("mViewRadio="+mViewRadio);
					LWLogUtils.e("aaaa 这里填充数据  这里刷新2222222");
					mOpenGLHelper.drawCenterPoint((result[0].objectRect.left * 2 + result[0].objectRect.width()) / 2,
							(result[0].objectRect.top * 2 + result[0].objectRect.height()) / 2, mViewWidth, mViewHeight);
					mOpenGLHelper.drawRect(result[0], mViewWidth, mViewHeight);
					LWLogUtils.e("aaaa 这里填充数据  这里刷新333333333");
					/**可能GL线程连续获得同步锁**/
					for (int i = 0; i < result.length; i++) {
						result[i].objectRect.left *= mPreviewRadio;
						result[i].objectRect.top *= mPreviewRadio;
						result[i].objectRect.bottom *= mPreviewRadio;
						result[i].objectRect.right *= mPreviewRadio;
						result[i].blurValue *= mPreviewRadio;
					}
				}
				detactDraw=true;
				if (!mHasSetTarget&&mDetectionResult != null && mDetectionResult.length > 0) {

					for (int i = 0; i < mDetectionResult.length; i++) {
						mDetectionResult[i].objectRect.left *= mViewRadio;
						mDetectionResult[i].objectRect.top *= mViewRadio;
						mDetectionResult[i].objectRect.bottom *= mViewRadio;
						mDetectionResult[i].objectRect.right *= mViewRadio;
						mDetectionResult[i].blurValue *= mViewRadio;
						
					}
//                    LWLogUtils.e("mViewRadio="+mViewRadio);
//                    LWLogUtils.e("aaaa 这里填充数据  这里刷新2222222");

					for(int i = 0; i < mDetectionResult.length; i++){
						if(mDetectionResult[i].objectRect.width()>0){
							mOpenGLHelper.drawCenterPoint((mDetectionResult[i].objectRect.left * 2 + mDetectionResult[i].objectRect.width()) / 2,
									(mDetectionResult[i].objectRect.top * 2 + mDetectionResult[i].objectRect.height()) / 2, mViewWidth, mViewHeight);
							mOpenGLHelper.drawRect(mDetectionResult[i], mViewWidth, mViewHeight);
							LWLogUtils.e("mDetectionResult["+i+"].objectRect.width()="+mDetectionResult[i].objectRect.width());
						}

					}
//                    LWLogUtils.e("aaaa 这里填充数据  这里刷新333333333");
					/**可能GL线程连续获得同步锁**/
					for (int i = 0; i < mDetectionResult.length; i++) {
						mDetectionResult[i].objectRect.left *= mPreviewRadio;
						mDetectionResult[i].objectRect.top *= mPreviewRadio;
						mDetectionResult[i].objectRect.bottom *= mPreviewRadio;
						mDetectionResult[i].objectRect.right *= mPreviewRadio;
						mDetectionResult[i].blurValue *= mPreviewRadio;
					}

				}
				detactDraw=false;

//                mDetectionResult=null;
//                trackRequestRender=false;
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				mLock.release();
//                trackRequestRender=false;
			}
		}
		GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	int detectNumCount=0;
	
	private void detectData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopDetect){


					if(!copydDetectData){
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					/** 子线程中执行侦测物体的接口,绘制图形 **/
					long start=System.currentTimeMillis();
//                    Log.e("ZL", "(int)mDetectionResult[0].ob333333333333333333333333");
					ObjectInfo[] result = mObjectDetect.bodyDetect(detectData,CvPixelFormat.YUV420P,mPreviewWidth,mPreviewHight,
							ImageOrientation.ROTAION_0,5);
//                    Log.e("ZL", "(int)mDetectionResult[0].ob3334444444444444444444444");
					long end=System.currentTimeMillis();

					int real_num=mObjectDetect.mResultCode[0];//todo 代表实际了多少个人
					Log.e("ZL", "real_num="+real_num);
					if(mHasSetTarget){
						break;
					}
					if (result != null && result.length > 0&&(mObjectDetect.mResultCode[0]>0)) {
						detectNumCount=detectNumCount+1;
						if(detectNumCount<3){
							
						}else {
							detectNumCount=0;
							if(!detactDraw){
								mDetectionResult = result.clone();
							}

//	                        Log.e("ZL", "3333333333"+mDetectionResult.);
							int ret=-1;
							if((real_num==1)&&((int)result[0].objectRect.width()>0)
									&&((int)result[0].objectRect.height()>0)
									&&((int)result[0].objectRect.width()<=mPreviewWidth)
									&&((int)result[0].objectRect.height()<=mPreviewHight)){
//	                            Log.e("ZL", "一个人设置目标");
								mObjectTrack.setTargetObjectInfo(detectData, CvPixelFormat.YUV420P,mPreviewWidth,mPreviewHight,mPreviewWidth, ImageOrientation.ROTAION_0,
										(int)result[0].objectRect.left,
										(int)result[0].objectRect.top,
										(int)result[0].objectRect.width(),
										(int)(result[0].objectRect.height()*2/3)
								);

								ret =mObjectTrack.mResultCode[0];
								if(ret==0){
//	                                Log.e("ZL", "一个人设置目标 成功");
									mHasSetTarget=true;
									mDetectionResult=null;
									stopDetect=true;
									copydDetectData=false;
									break;
								}
							}else {

							}
//	                if (ret == 0) {
//	                    Message message = Message.obtain();
//	                    message.obj = true;
//	                    mainHandler.sendMessage(message);
//	                    Log.e("ZL", "setTargetObjectInfo 设置目标成功=======>>>>>");
//	                    FlyCtrl.mLWUartProtolBean.mControlPara.visionPara.start=1;
//	                } else {
//	                    Message message = Message.obtain();
//	                    message.obj = false;
//	                    message.what=0;
//	                    mainHandler.sendMessage(message);
//	                    Log.e("ZL", "setTargetObjectInfo 设置目标失败=======>>>>>" + ret);
//	                }
//	                Log.e("ZL","侦测接口侦测出来了...");
						}
						
					}else{
						mDetectionResult=null;
						Log.e("ZL","侦测接口没有侦测出来...");
					}
					copydDetectData=false;
					MySurfaceView.this.requestRender();
					if(!canTouch){
//						mHasSetTarget=true;
						mDetectionResult=null;
						stopDetect=true;
					}
				}

				Log.e("mDetectionResult退出"," mDetectionResult=null;...");
			}
		}).start();
	}


//	@Override
	public void des(SurfaceHolder holder){
//		super.surfaceDestroyed(holder);
		mRendering = false;
		stopTrackDating=true;
//        mSurfaceTexture.release();
//        CameraInstance.getInstance().stopCamera();
		stopDetect=true;
		if (mBackgroundHandler != null) {
			mBackgroundHandler.removeCallbacksAndMessages(null);
		}
		if (mMainHandler != null) {
			mMainHandler.removeCallbacksAndMessages(null);
		}
		if (mHandlerThread != null) {
			mHandlerThread.quitSafely();
		}
		if (mObjectTrack != null) {
			mObjectTrack.release();
		}
		if (mObjectDetect != null) {
			mObjectDetect.release();
		}
		releaseDetect();
		//release();
		Log.e(TAG, "onDestroy==="+"onDestroy()");
	}

	public void pause(){
		mRendering=false;
		super.onPause();
	}


	public void resume(){
		mRendering=true;
		super.onResume();
	}

	private LWHandCb mLWHandCb=null;
	public void setLWHandCb(LWHandCb lwHandCb){
		this.mLWHandCb=lwHandCb;
	}

	public interface LWHandCb{
		void onTakePhoto(String msg);
		void onTakeRecord();
	}
}
