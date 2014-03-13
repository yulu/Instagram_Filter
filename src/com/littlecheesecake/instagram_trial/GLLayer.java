package com.littlecheesecake.instagram_trial;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.littlecheesecake.instagram_trial.R;

public class GLLayer implements GLSurfaceView.Renderer {
	/**
	 * This class implements our custom renderer. Note that the GL10 parameter
	 * passed in is unused for OpenGL ES 2.0 renderers -- the static class
	 * GLES20 is used instead.
	 */
	private final Context mActivityContext;

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private float[] mViewMatrix = new float[16];

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private float[] mProjectionMatrix = new float[16];

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private float[] mMVPMatrix = new float[16];

	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubePositions;
	private final FloatBuffer mCubeColors;
	private final FloatBuffer mCubeTextureCoordinates;

	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle0;
	private int mTextureUniformHandle1;
	private int mTextureUniformHandle2;
	private int mTextureUniformHandle3;
	private int mTextureUniformHandle4;

	/** This will be used to pass in model position information. */
	private int mPositionHandle;

	/** This will be used to pass in model color information. */
	// private int mColorHandle;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;

	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;

	/** Size of the color data in elements. */
	// private final int mColorDataSize = 4;

	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;

	/** This is a handle to our cube shading program. */
	private int mProgramHandle;

	/** This is a handle to our texture data. */
	private int mTextureDataHandle0;
	private int mTextureDataHandle1;
	private int mTextureDataHandle2;
	private int mTextureDataHandle3;
	private int mTextureDataHandle4;
	
	/**
	 * Shader Titles
	 */
	static public int shader_selection = 0;
	static public final int AMARO = 1;
	static public final int EARLYBIRD = 2;
	static public final int HEFE = 3;
	static public final int HUDSON = 4;
	static public final int MAYFAIR = 5;
	static public final int RISE = 6;
	static public final int TOASTER = 7;
	static public final int VALENCIA = 8;
	static public final int WILLOW = 9;
	static public final int XPRO = 10;
	//and more ...

	/**
	 * Initialize the model data.
	 */
	public GLLayer(final Context activityContext) {
		mActivityContext = activityContext;

		// Define points for a cube.

		// X, Y, Z
		final float[] cubePositionData = {
				// In OpenGL counter-clockwise winding is default. This means
				// that when we look at a triangle,
				// if the points are counter-clockwise we are looking at the
				// "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing
				// triangles are culled, since they
				// usually represent the backside of an object and aren't
				// visible anyways.

				// Front face
				-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f };

		// R, G, B, A
		final float[] cubeColorData = {
				// Front face (red)
				1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f };

		// X, Y, Z
		// The normal is used in light calculations and is a vector which points
		// orthogonal to the plane of the surface. For a cube model, the normals
		// should be orthogonal to the points of each face.

		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as
		// you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by
		// flipping the Y axis.
		// What's more is that the texture coordinates are the same for every
		// face.
		final float[] cubeTextureCoordinateData = {
				// Front face
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 0.0f };

		// Initialize the buffers.
		mCubePositions = ByteBuffer
				.allocateDirect(cubePositionData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubePositions.put(cubePositionData).position(0);

		mCubeColors = ByteBuffer
				.allocateDirect(cubeColorData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeColors.put(cubeColorData).position(0);

		mCubeTextureCoordinates = ByteBuffer
				.allocateDirect(
						cubeTextureCoordinateData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
	}

	protected String getVertexShader() {
		return RawResourceReader.readTextFileFromRawResource(mActivityContext,
				R.raw._vertex_shader);
	}

	protected String getFragmentShader() {
		int id;
		switch (shader_selection){
			case AMARO: id = R.raw.amaro_filter_shader; break;
			case EARLYBIRD: id = R.raw.earlybird_filter_shader;break;
			case HEFE: id = R.raw.hefe_filter_shader;break;
			case HUDSON: id = R.raw.hudson_filter_shader;break;
			case MAYFAIR: id = R.raw.mayfair_filter_shader;break;
			case RISE: id = R.raw.rise_filter_shader;break;
			case TOASTER: id = R.raw.toaster_filter_shader;break;
			case WILLOW: id = R.raw.willow_filter_shader;break;
			case XPRO: id = R.raw.xpro_filter_shader;break;

			default: id = R.raw._fragment_shader;break;
		}
		
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, id);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// The below glEnable() call is a holdover from OpenGL ES 1, and is not
		// needed in OpenGL ES 2.
		// Enable texture mapping
		// GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera
		// position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
		// of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices
		// separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY,
				lookZ, upX, upY, upZ);

		
		// Load the texture
		mTextureDataHandle0 = TextureHelper.loadTexture(mActivityContext,
				R.drawable.original_ps);

		// Load the texture
		mTextureDataHandle1 = TextureHelper.loadTexture(mActivityContext,
				R.drawable.filter2);
		
		// Load the texture
		mTextureDataHandle2 = TextureHelper.loadTexture(mActivityContext,
				R.drawable.hefe);
		
		// Load the texture
		mTextureDataHandle3 = TextureHelper.loadTexture(mActivityContext,
				R.drawable.hudson);
		
		// Load the texture
		mTextureDataHandle4 = TextureHelper.loadTexture(mActivityContext,
				R.drawable.toaster);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		final String vertexShader = getVertexShader();
		final String fragmentShader = getFragmentShader();

		final int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position",
						"a_TexCoordinate" });

		// Set our per-vertex lighting program.
		GLES20.glUseProgram(mProgramHandle);

		// Set program handles for cube drawing.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVPMatrix");
		mTextureUniformHandle0 = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture0");
		mTextureUniformHandle1 = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture1");
		mTextureUniformHandle2 = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture2");
		mTextureUniformHandle3 = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture3");
		mTextureUniformHandle4 = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture4");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_Position");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_TexCoordinate");

		/**
		 * First texture map
		 */
		// Set the active texture0 unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle0);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 0.
		GLES20.glUniform1i(mTextureUniformHandle0, 0);
		
		/**
		 * Second texture map filter
		 */
		// Set the active texture1 unit to texture unit 1.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle1);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 1.
		GLES20.glUniform1i(mTextureUniformHandle1, 1);
		
		/**
		 * Third texture map filter hefe
		 */
		// Set the active texture1 unit to texture unit 1.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle2);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 1.
		GLES20.glUniform1i(mTextureUniformHandle2, 2);
		
		/**
		 * Fouth texture map filter hudson
		 */
		// Set the active texture1 unit to texture unit 1.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle3);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 1.
		GLES20.glUniform1i(mTextureUniformHandle3, 3);
		
		/**
		 * Fifth texture map filter toaster
		 */
		// Set the active texture1 unit to texture unit 1.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle4);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 1.
		GLES20.glUniform1i(mTextureUniformHandle4, 4);

		// Draw some cubes.
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.2f);
		Matrix.rotateM(mModelMatrix, 0, 0.0f, 1.0f, 1.0f, 0.0f);
		drawCube();
	}

	/**
	 * Draws a cube.
	 */
	private void drawCube() {
		// Pass in the position information
		mCubePositions.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize,
				GLES20.GL_FLOAT, false, 0, mCubePositions);

		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Pass in the texture coordinate information
		mCubeTextureCoordinates.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
				mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
				mCubeTextureCoordinates);

		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// Draw the cube.
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
}
