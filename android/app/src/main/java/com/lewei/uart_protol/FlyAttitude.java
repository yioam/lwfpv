package com.lewei.uart_protol;


/**
 * ////姿态角
 * ***/
public class FlyAttitude {
	public float pitch;
	public float roll;
	public float yaw;

	@Override
	public String toString() {
		return "FlyAttitude{" +
				"pitch=" + pitch +
				", roll=" + roll +
				", yaw=" + yaw +
				'}';
	}
}
