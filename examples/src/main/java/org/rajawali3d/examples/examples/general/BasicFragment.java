package org.rajawali3d.examples.examples.general;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.rajawali3d.Object3D;
import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

public class BasicFragment extends AExampleFragment implements SensorEventListener {

	private static final String TAG = "BasicFragment";

	private double lookAtX = 0d;
	private double lookAtY = 0d;
	private double lookAtZ = 0d;
	private double speed = 0.1d;

	BasicRenderer basicRenderer = null;

	private float[] accValues = new float[3];
	private float[] magValues = new float[3];
	// 旋转矩阵，用来保存磁场和加速度的数据
	private float r[] = new float[9];
	// 模拟方向传感器的数据（原始数据为弧度）
	private float values[] = new float[3];

	private SensorManager mSensorManager;
	private Sensor acc_sensor;
	private Sensor mag_sensor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);
		mSensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
		acc_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mag_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// 给传感器注册监听：
		mSensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mag_sensor, SensorManager.SENSOR_DELAY_GAME);


		if(mRenderer instanceof BasicRenderer) {
			basicRenderer = (BasicRenderer) mRenderer;
		}
		rootView.findViewById(R.id.btn_left).setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				lookAtX += speed;
//				basicRenderer.setLookAtX(lookAtX);
				basicRenderer.getCurrentCamera().rotate(Vector3.Axis.Y, -0.1);
				Log.i(TAG, "btn_left touch " + event.getAction());
				return true;
			}
		});

		rootView.findViewById(R.id.btn_right).setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				basicRenderer.getCurrentCamera().rotate(Vector3.Axis.Y, 0.1);
				Log.i(TAG, "btn_right touch " + event.getAction());
				return true;
			}
		});

		rootView.findViewById(R.id.btn_up).setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				basicRenderer.getCurrentCamera().moveUp(0.01);
				return false;
			}
		});

		rootView.findViewById(R.id.btn_down).setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				basicRenderer.getCurrentCamera().moveUp(-0.01);
				return false;
			}
		});
		return rootView;
	}

	@Override
    public AExampleRenderer createRenderer() {
		return new BasicRenderer(getActivity(), this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		switch (sensorType) {
			case Sensor.TYPE_ACCELEROMETER:
				accValues = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				magValues = event.values.clone();
				break;
		}
		SensorManager.getRotationMatrix(r, null, accValues, magValues);
		SensorManager.getOrientation(r, values);

		onAngleChanged(Math.toDegrees(values[1]), Math.toDegrees(values[2]));

	}

	private void onAngleChanged(double rotateByX, double rotateByY) {
		Log.w(TAG, "onAngleChanged " + rotateByX + ", " + rotateByY);
//		basicRenderer.setRotateByX(rotateByX);
		basicRenderer.setRotateByY(rotateByY*-0.03);
//		basicRenderer.getCurrentCamera().rotate(Vector3.Axis.X, rotateByX);
//		basicRenderer.getCurrentCamera().rotate(Vector3.Axis.Y, rotateByY);
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public static final class BasicRenderer extends AExampleRenderer {
		private double lookAtX = 0d;
		private double lookAtY = 0d;
		private double lookAtZ = 0d;

		public void setLookAtX(double lookAtX) {
			this.lookAtX = lookAtX;
			Log.i(TAG, "setLookAtX " + lookAtX);
		}

		public void setLookAtY(double lookAtY) {
			this.lookAtY = lookAtY;
		}

		public void setLookAtZ(double lookAtZ) {
			this.lookAtZ = lookAtZ;
		}

		private double rotateByX = 0d;
		private double rotateByY = 0d;

		public void setRotateByX(double rotateByX) {
			this.rotateByX = rotateByX;
		}

		public void setRotateByY(double rotateByY) {
			this.rotateByY = rotateByY;
		}

		private Object3D mSphere;

		public BasicRenderer(Context context, @Nullable AExampleFragment fragment) {
			super(context, fragment);
		}

        @Override
		protected void initScene() {

			try {
				getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
						R.drawable.posy, R.drawable.negy, R.drawable.posz, R.drawable.negz);
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}
			try {
				Material material = new Material();
				material.addTexture(new Texture("earthColors",
												R.drawable.earthtruecolor_nasa_big));
				material.setColorInfluence(0);
				mSphere = new Sphere(1, 24, 24);
				mSphere.setMaterial(material);
				getCurrentScene().addChild(mSphere);
			} catch (ATexture.TextureException e) {
				e.printStackTrace();
			}

			Log.d(TAG, "Camera initial1 orientation: " + getCurrentCamera().getOrientation());
			Log.d(TAG, "Camera initial2 orientation: " + getCurrentCamera().getOrientation().inverse());
			Log.d(TAG, "Camera initial3 orientation: " + getCurrentCamera().getOrientation());
            getCurrentCamera().enableLookAt();
            getCurrentCamera().setLookAt(0, 0, 0);
            getCurrentCamera().setZ(20);

//			getCurrentCamera().setOrientation(getCurrentCamera().getOrientation().inverse());

        }

        @Override
        public void onRender(final long elapsedTime, final double deltaTime) {
			super.onRender(elapsedTime, deltaTime);
			mSphere.rotate(Vector3.Axis.Y, 1.0);
//			getCurrentCamera().rotate(Vector3.Axis.X, -0.1);
//			getCurrentCamera().setRotation(Vector3.Axis.X, rotateByX);
//			getCurrentCamera().setRotation(Vector3.Axis.Y, rotateByY);
			getCurrentCamera().setRotation(0, rotateByY, rotateByX);
			Log.e(TAG, "onRender " + rotateByX + ", " + rotateByY);
		}
	}
}
