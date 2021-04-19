/*
	Etheric Synthesizer, a theremin synthesizer for Android mobile devices
	Copyright (C) 2016 Nutchanon Wetchasit
	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License along
	with this program; if not, write to the Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.ethersynth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/** Display output option listing */
public class OutputOptionsActivity extends Activity implements DialogInterface.OnClickListener {
	private boolean recording;
	private ConfigOptions conf;
	private Spinner waveformSelector;
	private Spinner sampleRateSelector;
	private AlertDialog outputChangeDialog;
	
	// Message constants
	public static final String MESSAGE_RECORDING="recording";
	
	// Dialog constants
	private final int DIALOG_OUTPUT_CHANGE_WHILE_RECORDING=1;
	
	// Result contstants
	public static final int RESULT_OUTPUT_CHANGED=Activity.RESULT_FIRST_USER;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ArrayAdapter adapter;
		
		super.onCreate(savedInstanceState);
		
		conf=new ConfigOptions(this);
		setContentView(R.layout.outputoptions);
		recording=getIntent().getBooleanExtra(MESSAGE_RECORDING,false);
		waveformSelector=(Spinner)findViewById(R.id.waveformselect);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Sine (default)","Triangle","Sawtooth","Square"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		waveformSelector.setAdapter(adapter);
		switch(conf.getOutputWaveform()) {
			case 3: waveformSelector.setSelection(3); break;
			case 2: waveformSelector.setSelection(2); break;
			case 1: waveformSelector.setSelection(1); break;
			case 0:
			default:
				waveformSelector.setSelection(0);
		}
		sampleRateSelector=(Spinner)findViewById(R.id.samplerate);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{
			"System default ("+AudioOutput.getNativeSampleRate()+" Hz)","8000 Hz","11025 Hz","12000 Hz","16000 Hz","22050 Hz","24000 Hz","32000 Hz","44100 Hz","48000 Hz"
		});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sampleRateSelector.setAdapter(adapter);
		if(conf.isUsingNativeOutputSampleRate()) {
			sampleRateSelector.setSelection(0);
		} else {
			switch(conf.getOutputSampleRate()) {
				case 8000: sampleRateSelector.setSelection(1); break;
				case 11025: sampleRateSelector.setSelection(2); break;
				case 12000: sampleRateSelector.setSelection(3); break;
				case 16000: sampleRateSelector.setSelection(4); break;
				case 22050: sampleRateSelector.setSelection(5); break;
				case 24000: sampleRateSelector.setSelection(6); break;
				case 32000: sampleRateSelector.setSelection(7); break;
				case 44100: sampleRateSelector.setSelection(8); break;
				case 48000: sampleRateSelector.setSelection(9); break;
				default:
					sampleRateSelector.setSelection(0);
			};
		}
	}
	
	/** Called to create dialog */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==DIALOG_OUTPUT_CHANGE_WHILE_RECORDING) {
			return outputChangeDialog=new AlertDialog.Builder(this).setTitle("Recording in Progress").setMessage("Changing audio output settings will stop current recording. Are you sure you do want to save these settings?").setPositiveButton("Save",this).setNegativeButton("Discard",this).create();
		} else {
			return null;
		}
	}
	
	/** Called when dialog button is pressed. */
	@Override
	public void onClick(DialogInterface dialog,int button) {
		if(dialog==outputChangeDialog) {
			if(button==DialogInterface.BUTTON_POSITIVE) {
				applyChangesFinish(true);
			} else if(button==DialogInterface.BUTTON_NEGATIVE) {
				finish();
			} 
		}
	}
	
	/** Called when back button was pressed. */
	@Override
	public void onBackPressed() {
		applyChangesFinish(false);
	}
	
	/** Apply configuration change, and exit */
	private void applyChangesFinish(boolean forced) {
		int waveform;
		int samplerate;
		boolean nativesamplerate;
		boolean outputchanged;
		
		switch(waveformSelector.getSelectedItemPosition()) {
			case 3: waveform=conf.OUTPUT_WAVEFORM_SQUARE; break;
			case 2: waveform=conf.OUTPUT_WAVEFORM_SAWTOOTH; break;
			case 1: waveform=conf.OUTPUT_WAVEFORM_TRIANGLE; break;
			case 0:
			default:
				waveform=conf.OUTPUT_WAVEFORM_SINE;
		}
		nativesamplerate=false;
		outputchanged=false;
		switch(sampleRateSelector.getSelectedItemPosition()) {
			case 1: samplerate=8000; break;
			case 2: samplerate=11025; break;
			case 3: samplerate=12000; break;
			case 4: samplerate=16000; break;
			case 5: samplerate=22050; break;
			case 6: samplerate=24000; break;
			case 7: samplerate=32000; break;
			case 8: samplerate=44100; break;
			case 9: samplerate=48000; break;
			case 0:
			default:
				nativesamplerate=true;
				samplerate=AudioOutput.getNativeSampleRate();
		}
		if(nativesamplerate!=conf.isUsingNativeOutputSampleRate() || samplerate!=conf.getOutputSampleRate())
			outputchanged=true;
		
		if(recording && outputchanged && !forced) {
			showDialog(DIALOG_OUTPUT_CHANGE_WHILE_RECORDING);
			return;
		}
		
		conf.setOutputWaveform(waveform);
		conf.setUsingNativeOutputSampleRate(nativesamplerate);
		conf.setOutputSampleRate(samplerate);
		if(outputchanged) setResult(RESULT_OUTPUT_CHANGED);
		finish();
	}
}
