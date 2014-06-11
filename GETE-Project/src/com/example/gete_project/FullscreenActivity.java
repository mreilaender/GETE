package com.example.gete_project;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import com.example.gete_project.util.SystemUiHider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Performance-Vorschläge:
 ** Statt 2 Timern einen machen und in dem einen Timer abfragen wenn TimeCounterMS (ms) = 1000 ist wird TimeCounter (s) um eins erhöht.
 *** Vorteil: Es wird kein zweites Objekt bzw kein zweiter Thread benötigt --> Performanter
 *** Nachteil: Etwas komplexer
 **
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity implements OnClickListener, OnItemSelectedListener
{
	private boolean jo, b_start;
	private float[] f1;
	private Timer tsec, tms;
	//TimeCounter - counter that increments every secound
	//TimeCounterMs - counter that increments every millisecound 
	private int timeCounter, timeCounterMS, maxValues, count;
	private ArrayList<Listener> listener;
	public Button start;
	private String[] options, numbValues;
	public TextView headLine, timeView, debugView;
	private EditText distance;
	private SensorManager sm;
	public Sensor s1, s2;
	private Spinner spinner;
	private ArrayAdapter<String> arrAdapter;
	private NumberPicker numberPicker;
	private boolean finish = false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		//		this.

		/* Primitive Attribute setzen*/
		jo = false;
		b_start = false;
		maxValues = 1000;
		f1 = new float[3];

		/* Objekt Attribute setzen */
		numbValues = new String[maxValues];
		for(int i = 0;i < numbValues.length;++i)
			numbValues[i] = ""+i;

		numberPicker = (NumberPicker) findViewById(R.id.numberPicker1);
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(maxValues);
		numberPicker.setValue(1);
		
		distance = (EditText) findViewById(R.id.editText1);

		options = new String[3];
		options[0] = "Meter";
		options[1] = "Miles";
		options[2] = "Feets";

		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for(int i = 0;i < options.length;++i)
			arrAdapter.add(options[i]);
		arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner = (Spinner) findViewById(R.id.id_spinner1);
		spinner.setSelection(1);
		spinner.setAdapter(arrAdapter);
		spinner.setOnItemSelectedListener(this);
		spinner.setVerticalScrollBarEnabled(true);
		//		spinner.setGravity(Gravity.LEFT);

		timeView = (TextView) findViewById(R.id.timeView);
		headLine = (TextView) findViewById(R.id.headLine);
		debugView = (TextView) findViewById(R.id.debug);
		//		unitView = (TextView) findViewById(R.id.id_unit);

		//		timeView.setText(""+ (findViewById(R.id.id_spinner1)));
		tsec = new Timer();
		tms = new Timer();

		start = (Button) findViewById(R.id.start_stop_button);

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);

		s2 = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		timeCounter = timeCounterMS = 0;

		/* Generic Objekte setzen */
		listener = new ArrayList<Listener>();

		listener.add(new Listener(this));
		listener.add(new Listener(this));

		start.setText("START");
		start.setOnClickListener(this);

		this.numberPicker.setVisibility(View.GONE);
		headLine.setText("GETE - Project v3.0.0 Hash-Code: "+this.hashCode());
	}
	public void listen(float[] f)
	{
		f1[0] = f[0];
		f1[1] = f[1];
		f1[2] = f[2];
		if((f[0]>5.0f || f[1]>5.0f || f[2]>5.0f) && jo == false)
		{
			jo=true;
			tsec.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						public void run() {

							if(timeCounterMS % 10 == 0)
							{
								if((f1[0] < 3.0f && f1[1] < 3.0f && f1[2] < 3.0f))
								{
									count++;
									if(count > 20)
									{
										tsec.cancel();
										tms.cancel();

										sm.unregisterListener(listener.get(0));
										start.setText("START");

										b_start = jo = false;

										tsec = new Timer();
										tms = new Timer();
										
										try {
											debugView.setText("" + (Float.parseFloat(distance.getText().toString()) / (((float)timeCounter) + ((float)timeCounter) /1000)*3.6));
										} catch(NumberFormatException ex) {
											
											debugView.setText("Keine Distanz eingegeben");
										}
										timeView.setText(timeCounter+"s " + timeCounterMS + "ms");
									}
								} else {

									count = 0;
								}
							}
							if(timeCounterMS == 1000)
							{
								timeCounter++;
								timeCounterMS = 0;
								timeView.setText(timeCounter+ "s " + timeCounterMS + "ms");
							} else
							{
								timeCounterMS++;
								timeView.setText(timeCounter + "s " + timeCounterMS + "ms");
							}
						}
					});

				}
			}, 0, 1);
		}
	}
	@Override
	public void onClick(View v)
	{

		if(!b_start)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you ready to start?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					start.setText("STOP");
					timeCounter = 0;
					timeCounterMS = 0;
					timeView.setText(timeCounter + "s " + timeCounterMS + "ms");
					sm.registerListener(listener.get(0), s2, SensorManager.SENSOR_DELAY_FASTEST);
					b_start = true;
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			builder.show();
		} else if(b_start)
		{
			this.tsec.cancel();
			this.tms.cancel();

			sm.unregisterListener(listener.get(0));
			start.setText("START");

			this.b_start = this.jo = false;

			this.tsec = new Timer();
			this.tms = new Timer();

			this.timeCounter = this.timeCounterMS = 0;
			this.timeView.setText(""+timeCounter+" s " + timeCounterMS + " ms");
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View view, int position, long id)
	{
		spinner.setSelection(position);
		//		unitView.setText(""+spinner.getSelectedItem());
		//		spinner.setPrompt("[SELECT UNIT ...] selected: " + spinner.getSelectedItem());
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}