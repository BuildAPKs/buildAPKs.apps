/*
	Etheric Synthesizer, a theremin synthesizer for Android mobile devices
	Copyright (C) 2017 Nutchanon Wetchasit
	
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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class EtherSynthActivity extends Activity implements DialogInterface.OnClickListener, RecordingListener {
	private ConfigOptions conf;
	private SwipeScreen swiper;
	private Oscillator osc;
	private AlertDialog recordingExitDialog;
	private volatile Throwable recordingError;
	
	// Status flags
	private boolean configuring;
	
	// Message constants
	public static final String MESSAGE_CONTROLS_CHANGE="controlsChanged";
	
	// Dialog constants
	private final int DIALOG_EXIT_WHILE_RECORDING=1;
	private final int DIALOG_RECORDING_WRITE_FAILED=2;
	
	// Activity request code constants
	private final int ACTIVITY_OPTIONS=1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		conf=new ConfigOptions(this);
		switch(conf.getOrientationLock()) {
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_LANDSCAPE: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_PORTRAIT: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_NONE:
			default:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		
		((EtherSynthApplication)getApplicationContext()).init();
		((EtherSynthApplication)getApplicationContext()).addRecordingListener(this);
		osc=((EtherSynthApplication)getApplicationContext()).getOscillator();
		swiper=new SwipeScreen(this,osc,conf.getLowestFrequency(),conf.getHighestFrequency(),conf.isShowingBackground(),conf.isShowingTouchPoint(),conf.getControlAxes()==ConfigOptions.CONTROL_AXES_HORIZONTAL_FREQUENCY,conf.isInvertingFrequencyControl(),conf.isInvertingAmplitudeControl(),conf.getDeadZone(),conf.getSaturationZone());
		setContentView(swiper);
	}
	
	/** Called when the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		((EtherSynthApplication)getApplicationContext()).destroy();
	}
	
	/** Called when there's a message */
	@Override
	public void onNewIntent(Intent command) {
		if(command.getBooleanExtra(MESSAGE_CONTROLS_CHANGE,false)) {
			Log.i("EtherSynthActivity","Control settings changed");
			switch(conf.getOrientationLock()) {
				case ConfigOptions.LOCK_SCREEN_ORIENTATION_LANDSCAPE: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
				case ConfigOptions.LOCK_SCREEN_ORIENTATION_PORTRAIT: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); break;
				case ConfigOptions.LOCK_SCREEN_ORIENTATION_NONE:
				default:
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
			swiper.setFrequencyRange(conf.getLowestFrequency(),conf.getHighestFrequency());
			swiper.setShowingBackground(conf.isShowingBackground());
			swiper.setShowingTouchPoint(conf.isShowingTouchPoint());
			swiper.setHorizontalOrientation(conf.getControlAxes()==ConfigOptions.CONTROL_AXES_HORIZONTAL_FREQUENCY);
			swiper.setInvertingFrequencyControl(conf.isInvertingFrequencyControl());
			swiper.setInvertingAmplitudeControl(conf.isInvertingAmplitudeControl());
			swiper.setDeadSaturationZone(conf.getDeadZone(),conf.getSaturationZone());
		}
	}
	
	/** Called when recording progressed for another second (unused). */
	@Override
	public boolean recordingProgressed(int duration) {
		return false;
	}
	
	/** Called when recording failed in the middle. */
	@Override
	public boolean recordingFailed(Throwable t) {
		recordingError=t;
		runOnUiThread(new Runnable() {
			public void run() {
				showDialog(DIALOG_RECORDING_WRITE_FAILED);
			}
		});
		return true;
	}
	
	/** Called when menu button is pressed. */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Intent command;
		
		synchronized(this) {
			if(configuring) return false;
			configuring=true;
		}
		command=new Intent(this,OptionsActivity.class);
		startActivityForResult(command,ACTIVITY_OPTIONS);
		return false;
	}
	
	/** Called when Back button is pressed. */
	@Override
	public void onBackPressed() {
		if(((EtherSynthApplication)getApplicationContext()).getAudioOutput().isRecording()) {
			showDialog(DIALOG_EXIT_WHILE_RECORDING);
		} else {
			super.onBackPressed();
		}
	}
	
	/** Called to create dialog */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		Throwable recordingError;
		
		if(id==DIALOG_EXIT_WHILE_RECORDING) {
			return recordingExitDialog=new AlertDialog.Builder(this).setTitle("Recording in Progress").setMessage("Exiting Etheric Synthesizer will stop current recording. Are you sure you do want to quit?").setPositiveButton("Yes",this).setNegativeButton("No",this).create();
		} else if(id==DIALOG_RECORDING_WRITE_FAILED) {
			recordingError=this.recordingError;
			return new AlertDialog.Builder(this).setTitle("Recording Failed").setMessage("Failed to write recording"+(recordingError!=null?" ("+recordingError.getClass().getName()+")":"")+(recordingError!=null && recordingError.getMessage()!=null?": "+recordingError.getMessage():".")).create();
		} else {
			return null;
		}
	}
	
	/** Called when dialog button is pressed */
	@Override
	public void onClick(DialogInterface dialog,int button) {
		if(dialog==recordingExitDialog) {
			if(button==DialogInterface.BUTTON_POSITIVE) {
				finish();
			}
		}
	}
	
	/** Called when sub-Activity returns result. */
	@Override
	protected void onActivityResult(int request,int result,Intent data) {
		if(request==ACTIVITY_OPTIONS) {
			synchronized(this) {
				configuring=false;
			}
		}
	}
}
