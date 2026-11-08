package com.stan.launcher;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GameRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private int program, textureGrass;

    private final float[] vertices = {
        -0.5f, -0.5f,  0.5f,   0.5f, -0.5f,  0.5f,   0.5f,  0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,  -0.5f,  0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f, -0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,  -0.5f,  0.5f,  0.5f,   0.5f,  0.5f,  0.5f,   0.5f,  0.5f, -0.5f,
        -0.5f, -0.5f, -0.5f,   0.5f, -0.5f, -0.5f,   0.5f, -0.5f,  0.5f,  -0.5f, -0.5f,  0.5f,
         0.5f, -0.5f, -0.5f,   0.5f,  0.5f, -0.5f,   0.5f,  0.5f,  0.5f,   0.5f, -0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f,  0.5f,  -0.5f,  0.5f,  0.5f,  -0.5f,  0.5f, -0.5f
    };

    private final float[] texCoords = {
        0, 1,  1, 1,  1, 0,  0, 0, 
        0, 1,  1, 1,  1, 0,  0, 0, 
        0, 1,  1, 1,  1, 0,  0, 0, 
        0, 1,  1, 1,  1, 0,  0, 0, 
        0, 1,  1, 1,  1, 0,  0, 0, 
        0, 1,  1, 1,  1, 0,  0, 0  
    };

    private final int[] indices = {
        0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11,
        12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23
    };

    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.7f, 1.0f, 1.0f); // Небо
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        String vs = "uniform mat4 uMVPMatrix; attribute vec4 vPosition; attribute vec2 aTexCoordinate; varying vec2 vTexCoordinate; void main() { gl_Position = uMVPMatrix * vPosition; vTexCoordinate = aTexCoordinate; }";
        String fs = "precision mediump float; uniform sampler2D uTexture; varying vec2 vTexCoordinate; void main() { gl_FragColor = texture2D(uTexture, vTexCoordinate); }";

        int v = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(v, vs);
        GLES20.glCompileShader(v);

        int f = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(f, fs);
        GLES20.glCompileShader(f);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, v);
        GLES20.glAttachShader(program, f);
        GLES20.glLinkProgram(program);

        // Загружаем ТРАВУ
        try {
            textureGrass = loadTexture(R.drawable.grass_block);
        } catch (Exception e) {}
    }

    private int loadTexture(int resId) {
        int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeResource(context.getResources(), resId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        return tex[0];
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio * 2, ratio * 2, -2, 2, 3, 10);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -4f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glUseProgram(program);

        FloatBuffer vBuf = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vBuf.put(vertices).position(0);
        int posHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(posHandle);
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, vBuf);

        FloatBuffer tBuf = ByteBuffer.allocateDirect(texCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        tBuf.put(texCoords).position(0);
        int texHandle = GLES20.glGetAttribLocation(program, "aTexCoordinate");
        GLES20.glEnableVertexAttribArray(texHandle);
        GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 0, tBuf);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureGrass);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "uTexture"), 0);

        int mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, vPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_INT, 
            ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer().put(indices).position(0));
    }
}
