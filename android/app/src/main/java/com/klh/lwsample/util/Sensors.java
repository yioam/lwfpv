package com.klh.lwsample.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensors {
	private Context context;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private SensorEventListener sensorEventListener = null;
	protected String Tag = "Sensors";
	private OnSensorValue onSensorValue = null;

	int x, y;
	
	public static final int SENSOR_ROTATE_LEVEL = 60; // sensor旋转的等级，即从10~(65+10)
	public static final int SENSOR_MIN_ROTATE = 10; // sensor旋转小于5时候认为不动

	// public static final int SENSOR_MAX_ROTATE = SENSOR_MIN_ROTATE +
	// SENSOR_ROTATE_LEVEL;

	public Sensors(Context context) {
		this.context = context;
		Init_Sensor();
	}

	public void setOnSensorValue(OnSensorValue onSensorValue) {
		this.onSensorValue = onSensorValue;
	}

	public void setSpeedLevel(int isHigh) {

	}

	public void register() {
		if (sensorManager != null && sensorEventListener != null) {
			sensorManager.registerListener(sensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void unregister() {
		if (sensorManager != null && sensorEventListener != null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	private void Init_Sensor() {

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
		sensorEventListener = new SensorEventListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					///todo yioam
					if (true) {///正向横屏
						x = (int) (event.values[SensorManager.DATA_X] * 10);
						y = (int) (event.values[SensorManager.DATA_Y] * 10);
					} else if (false) {///反向横屏
						x = -(int) (event.values[SensorManager.DATA_X] * 10);
						y = -(int) (event.values[SensorManager.DATA_Y] * 10);
					}
					// int x = (int) (event.values[SensorManager.DATA_X] * 10);
					// int y = (int) (event.values[SensorManager.DATA_Y] * 10);

					if (x > SENSOR_MIN_ROTATE) {
						x -= SENSOR_MIN_ROTATE;
					} else if (x < -SENSOR_MIN_ROTATE) {
						x += SENSOR_MIN_ROTATE;
					} else {
						x = 0;
					}

					if (y > SENSOR_MIN_ROTATE) {
						y -= SENSOR_MIN_ROTATE;
					} else if (y < -SENSOR_MIN_ROTATE) {
						y += SENSOR_MIN_ROTATE;
					} else {
						y = 0;
					}

					if (x > SENSOR_ROTATE_LEVEL)
						x = SENSOR_ROTATE_LEVEL;
					if (y > SENSOR_ROTATE_LEVEL)
						y = SENSOR_ROTATE_LEVEL;
					if (x < -SENSOR_ROTATE_LEVEL)
						x = -SENSOR_ROTATE_LEVEL;
					if (y < -SENSOR_ROTATE_LEVEL)
						y = -SENSOR_ROTATE_LEVEL;

					// Log.e(Tag, "x " + x + " y " + y);

					if (onSensorValue != null) {
						onSensorValue.setValue(y, x);

					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}
		};
	}

	public interface OnSensorValue {
		public void setValue(int x, int y);
	}
}
