package org.bangbang.song.focuslayer;

import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_REPLACE;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_COORD_ARRAY;
import static android.opengl.GLES10.GL_TEXTURE_ENV;
import static android.opengl.GLES10.GL_TEXTURE_ENV_MODE;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.glTexEnvf;
import static android.opengl.GLES10.glTexParameterf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.bangbang.song.android.commonlib.LogRender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

public class GLFocusLayer extends GLSurfaceView implements IFocusAnimationLayer {
	public static final boolean DEBUG = true;
	public static final String TAG = GLFocusLayer.class.getSimpleName();

	private AnimationConfigure mConfig;
	private SurfaceHolder mHolder;
	private RectModel mTransfer;

	public GLFocusLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public GLFocusLayer(Context context) {
		super(context);

		init();
	}

	private void init() {
		setId(Utils.FOCUS_LAYER_ID);
		
		mConfig = new AnimationConfigure();
		mConfig.mDisableAutoGenBitmap = false;
		mConfig.mDisableScaleAnimation = false;

		// We want an 8888 pixel format because that's required for
		// a translucent window.
		// And we want a depth buffer.
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);		
		mHolder = getHolder();
		// for show what we draw on content.
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		
		setRenderer(new LayerRender());

		setRenderMode(RENDERMODE_WHEN_DIRTY);
//		setRenderMode(RENDERMODE_CONTINUOUSLY);

		setDebugFlags(DEBUG_LOG_GL_CALLS|DEBUG_CHECK_GL_ERROR|GLDebugHelper.CONFIG_LOG_ARGUMENT_NAMES);
		
		mTransfer = new RectModel();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			mConfig.onNewFocus(this, v);
			int w = getWidth();
			int h = getHeight();
			RectF r = new RectF();
//			(x-(-1) / 2) == l/w
			r.left = (float)mConfig.mCurrentScaledFocusRect.left * 2 /w  -1.f;
			r.right = ((float)mConfig.mCurrentScaledFocusRect.right *2 / (w) - 1);
			// (y - (-1) ) / 2 = - (t /h)
			r.bottom = -((float)mConfig.mCurrentScaledFocusRect.bottom * 2/  h  - 1);
			r.top = -((float)mConfig.mCurrentScaledFocusRect.top * 2 / ( h) - 1);
			
//			-0.90555555, 0.9400353, -0.18333334, 0.4814815
//			r = new RectF(-.9f, .9f, -.1f, .4f);
			Log.d(TAG, "updateRect r: " + mConfig.mCurrentScaledFocusRect);
			Log.d(TAG, "updateRect w: " + w + " h: " + h);
			Log.d(TAG, "updateRect rect: " + r);
			mTransfer.updateRect(r);
		}

		requestRender();
	}

	@Override
	public void onFocusSessionEnd(View lastFocus) {
		mConfig.onFocusSessionEnd(lastFocus);
	}

	class LayerRender extends LogRender {

		private int mTextureId;

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			super.onSurfaceCreated(gl, config);

			gl.glClearColor(0f, 0f, .0f, 0f);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			
			int[] textures = new int[1];
			gl.glGenTextures(1, textures, 0);			
			mTextureId = textures[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
					GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D,
					GL_TEXTURE_MAG_FILTER,
					GL_LINEAR);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
					GL_CLAMP_TO_EDGE);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
					GL_CLAMP_TO_EDGE);

			glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,
					GL_REPLACE);
	        
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			super.onSurfaceChanged(gl, w, h);

			gl.glViewport(0, 0, w, h); 
			float ratio = (float) w / h;
			gl.glMatrixMode(GL10.GL_PROJECTION); 
			gl.glLoadIdentity();
//			gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			super.onDrawFrame(gl);
			
			// Set GL_MODELVIEW transformation mode
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity(); // reset the matrix to its default state

			// XXX bysong why we need this ???
			// When using GL_MODELVIEW, you must set the view point
//			 GLU.gluLookAt(gl, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glActiveTexture(GL10.GL_TEXTURE0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);			

			Bitmap bitmap = mConfig.mCurrentFocusBitmap;
			if (null != bitmap){
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			}
			mTransfer.draw(gl);
			
//			 gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}

	}

	class RectModel {
		private static final int INDEX_COUNT = 6;
		private static final int VERTEX_COUNT = 4;
		private static final int COORDS_PER_VERTEX = 2;
		private static final int TEXTURE_PER_VERTEX = 2;
		
		private static final int BYTE_PER_FLOAT = 4;
		private static final int BYTE_PER_SHORT = 2;

		public RectF mRect;
		public FloatBuffer mVertexBuffer;
		private ShortBuffer mIndexBuffer;
		private FloatBuffer mTexureBuffer;

		public RectModel() {
			ByteBuffer vbb = ByteBuffer.allocateDirect(VERTEX_COUNT
					* COORDS_PER_VERTEX * BYTE_PER_FLOAT);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			
			ByteBuffer tbb = ByteBuffer.allocateDirect(VERTEX_COUNT
					* TEXTURE_PER_VERTEX * BYTE_PER_FLOAT);
			tbb.order(ByteOrder.nativeOrder());
			mTexureBuffer = tbb.asFloatBuffer();

			ByteBuffer ibb = ByteBuffer.allocateDirect(INDEX_COUNT
					* BYTE_PER_SHORT);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			
			mRect = new RectF();
		}

		public void updateRect(RectF r) {
			mRect = new RectF(r);
		}

		public void draw(GL10 gl) {
			calculate();

			gl.glFrontFace(GL10.GL_CCW);
			gl.glVertexPointer(COORDS_PER_VERTEX, GL10.GL_FLOAT, 0, mVertexBuffer);
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glTexCoordPointer(TEXTURE_PER_VERTEX, GL10.GL_FLOAT, 0, mTexureBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLES, 6,
					GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		}

		private void calculate() {

			mVertexBuffer.position(0);
			mVertexBuffer.put(mRect.left);
			mVertexBuffer.put(mRect.top);
			mVertexBuffer.put(mRect.left);
			mVertexBuffer.put(mRect.bottom);
			mVertexBuffer.put(mRect.right);
			mVertexBuffer.put(mRect.bottom);
			mVertexBuffer.put(mRect.right);
			mVertexBuffer.put(mRect.top);
			mVertexBuffer.position(0);

			mTexureBuffer.position(0);
			mTexureBuffer.put(0);
			mTexureBuffer.put(1);
			mTexureBuffer.put(0);
			mTexureBuffer.put(0);
			mTexureBuffer.put(1);
			mTexureBuffer.put(0);
			mTexureBuffer.put(1);
			mTexureBuffer.put(1);
			mTexureBuffer.position(0);

			mIndexBuffer.position(0);
			mIndexBuffer.put((short) 0);
			mIndexBuffer.put((short) 1);
			mIndexBuffer.put((short) 2);
			mIndexBuffer.put((short) 0);
			mIndexBuffer.put((short) 2);
			mIndexBuffer.put((short) 3);
			mIndexBuffer.position(0);
			
		}
	}	
}
