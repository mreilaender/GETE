package com.example.gete_project;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class Listener implements SensorEventListener
{
	private int i;
	private FullscreenActivity ma;
	public Listener(FullscreenActivity ma)
	{
		this.ma = ma;
		this.i = 1;
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSensorChanged(SensorEvent event)
	{
//		ma.start.setText("Sensor Changed Values: " + i);
		ma.listen(event.values);
		++this.i;
	}
}