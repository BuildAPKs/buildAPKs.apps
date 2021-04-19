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

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/** Display main configuration option listing */
public class OptionsActivity extends Activity implements DialogInterface.OnClickListener, RecordingListener {
	private boolean recording;
	private boolean changingControls;
	private String recordingFilename;
	private volatile Throwable recordingError;
	private ConfigOptions conf;
	private AlertDialog recordingFilenameDialog;
	private EditText recordingFilenameInput;
	private AlertDialog existingFilenameDialog;
	
	// Dialog constants
	private final int DIALOG_RECORDING_FILENAME=1;
	private final int DIALOG_EMPTY_FILENAME=2;
	private final int DIALOG_EXISTING_FILENAME=3;
	private final int DIALOG_RECORDING_NO_AVAILABLE_FILENAME=4;
	private final int DIALOG_RECORDING_START_FAILED=5;
	private final int DIALOG_RECORDING_WRITE_FAILED=6;
	
	// Activity request code constants
	private final int ACTIVITY_OUTPUT_OPTIONS=1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		conf=new ConfigOptions(this);
		checkRecordingStatus();
		((EtherSynthApplication)getApplication()).addRecordingListener(this);
	}
	
	/** Called when the Record button is pressed. */
	public void toggleRecording(View view) {
		if(!recording) {
			if(conf.isAskingForRecordingFilename()) {
				if(recordingFilenameInput!=null) recordingFilenameInput.setText(null);
				showDialog(DIALOG_RECORDING_FILENAME);
				return;
			} else {
				recordingFilename=null;
				startRecording();
			}
		} else {
			Log.i("OptionsActivity","Stopping recording");
			((EtherSynthApplication)getApplicationContext()).getAudioOutput().stopRecording();
			checkRecordingStatus();
		}
	}
	
	/** Start recording. */
	private void startRecording() {
		File recorddir,recordfile;
		
		Log.i("OptionsActivity","Starting recording");
		recorddir=conf.getRecordingDirectory();
		try {
			if(recordingFilename==null) {
				recordfile=getRecordingFilename(recorddir);
				if(recordfile==null) showDialog(DIALOG_RECORDING_NO_AVAILABLE_FILENAME);
				else ((EtherSynthApplication)getApplicationContext()).getAudioOutput().startRecording(recordfile);
			} else {
				((EtherSynthApplication)getApplicationContext()).getAudioOutput().startRecording(new File(recordingFilename));
			}
		} catch(IOException e) {
			Log.e("OptionsActivity","Recording start failed",e);
			recordingError=e;
			showDialog(DIALOG_RECORDING_START_FAILED);
		}
		checkRecordingStatus();
	}
	
	/** Generate recording filename, or null if exhausted trying */
	private File getRecordingFilename(File directory) {
		GregorianCalendar calendar;
		String filename;
		File target;
		int YYYY,MM,DD,hh,mm;
		int filenumber;
		
		calendar=new GregorianCalendar();
		YYYY=calendar.get(GregorianCalendar.YEAR);
		MM=calendar.get(GregorianCalendar.MONTH)+1;
		DD=calendar.get(GregorianCalendar.DATE);
		hh=calendar.get(GregorianCalendar.HOUR_OF_DAY);
		mm=calendar.get(GregorianCalendar.MINUTE);
		target=null;
		for(filenumber=0;filenumber<Integer.MAX_VALUE;filenumber++) {
			if(filenumber==0) filename=String.format("ethersynth_%04d-%02d-%02d_%02d-%02d.wav",YYYY,MM,DD,hh,mm);
			else filename=String.format("ethersynth_%04d-%02d-%02d_%02d-%02d.%d.wav",YYYY,MM,DD,hh,mm,filenumber);
			target=new File(directory,filename);
			if(!target.exists()) break;
		}
		if(filenumber==Integer.MAX_VALUE) return null;
		return target;
	}
	
	/** Called when the Control Options button is pressed. */
	public void showControlOptions(View view) {
		Intent command;
		
		changingControls=true;
		command=new Intent(this,ControlOptionsActivity.class);
		startActivity(command);
	}
	
	/** Called when the Output Options button is pressed. */
	public void showOutputOptions(View view) {
		Intent command;
		
		command=new Intent(this,OutputOptionsActivity.class);
		command.putExtra(OutputOptionsActivity.MESSAGE_RECORDING,recording);
		startActivityForResult(command,ACTIVITY_OUTPUT_OPTIONS);
	}
	
	/** Called when the Recording Options button is pressed. */
	public void showRecordingOptions(View view) {
		Intent command;
		
		command=new Intent(this,RecordingOptionsActivity.class);
		startActivity(command);
	}
	
	/** Called when the Help button is pressed. */
	public void showHelp(View view) {
		Intent command;
		
		command=new Intent(this,HelpActivity.class);
		startActivity(command);
	}
	
	/** Called when the About button is pressed. */
	public void showAbout(View view) {
		Intent command;
		
		command=new Intent(this,AboutActivity.class);
		startActivity(command);
	}
	
	/** Check and display recording status. */
	private void checkRecordingStatus() {
		AudioOutput output;
		File recordingfile;
		int recordingtime;
		
		Log.i("OptionsActivity","Checking recording status");
		output=((EtherSynthApplication)getApplicationContext()).getAudioOutput();
		recordingfile=output.getRecordingFile();
		if(recordingfile!=null) {
			recordingFilename=recordingfile.getPath();
			recordingtime=output.getRecordingTime();
			recording=true;
			((Button)findViewById(R.id.recordbutton)).setText("Stop Recording");
			((TextView)findViewById(R.id.recordstatus)).setText(String.format("Recording (%02d:%02d) to %s",recordingtime/60,recordingtime%60,recordingFilename));
			((TextView)findViewById(R.id.recordstatus)).setEnabled(true);
		} else {
			recording=false;
			((Button)findViewById(R.id.recordbutton)).setText("Start Recording");
			((TextView)findViewById(R.id.recordstatus)).setText("No recording is in progress.");
			((TextView)findViewById(R.id.recordstatus)).setEnabled(false);
		}
	}
	
	/** Called to create dialogs. */
	@Override
	protected Dialog onCreateDialog(int id) {
		Throwable recordingError;
		
		if(id==DIALOG_RECORDING_FILENAME) {
			if(recordingFilenameInput==null) recordingFilenameInput=new EditText(this);
			return recordingFilenameDialog=new AlertDialog.Builder(this).setTitle("Recording Filename").setMessage("Enter filename to save a new recording to:").setView(recordingFilenameInput).setPositiveButton("OK",this).setNegativeButton("Cancel",this).create();
		} else if(id==DIALOG_EMPTY_FILENAME) {
			return new AlertDialog.Builder(this).setTitle("Empty Filename").setMessage("No filename entered, recording will not start.").create();
		} else if(id==DIALOG_RECORDING_NO_AVAILABLE_FILENAME) {
			return new AlertDialog.Builder(this).setTitle("Recording Failed").setMessage("Unable to find a new filename for the recording.").create();
		} else if(id==DIALOG_EXISTING_FILENAME) {
			return existingFilenameDialog=new AlertDialog.Builder(this).setTitle("Existing Filename").setMessage("This file name is already exists. Are you sure you do want to overwrite it?").setPositiveButton("Yes",this).setNegativeButton("No",this).create();
		} else if(id==DIALOG_RECORDING_START_FAILED) {
			recordingError=this.recordingError;
			return new AlertDialog.Builder(this).setTitle("Recording Failed").setMessage("Failed to start recording"+(recordingError!=null?" ("+recordingError.getClass().getName()+")":"")+(recordingError!=null && recordingError.getMessage()!=null?": "+recordingError.getMessage():".")).create();
		} else if(id==DIALOG_RECORDING_WRITE_FAILED) {
			recordingError=this.recordingError;
			return new AlertDialog.Builder(this).setTitle("Recording Failed").setMessage("Failed to write recording"+(recordingError!=null?" ("+recordingError.getClass().getName()+")":"")+(recordingError!=null && recordingError.getMessage()!=null?": "+recordingError.getMessage():".")).create();
		} else {
			return null;
		}
	}
	
	/** Called when dialog button is pressed. */
	@Override
	public void onClick(DialogInterface dialog,int button) {
		File recorddir;
		String filename;
		
		recorddir=conf.getRecordingDirectory();
		if(dialog==recordingFilenameDialog) {
			if(button==DialogInterface.BUTTON_POSITIVE) {
				filename=recordingFilenameInput.getText().toString().trim();
				if(filename.length()==0) {
					showDialog(DIALOG_EMPTY_FILENAME);
					return;
				}
				filename=addFileExtension(filename);
				
				if(new File(recorddir,filename).exists()) {
					showDialog(DIALOG_EXISTING_FILENAME);
					return;
				}
				
				recordingFilename=new File(recorddir,filename).getPath();
				startRecording();
			}
		} else if(dialog==existingFilenameDialog) {
			if(button==DialogInterface.BUTTON_POSITIVE) {
				recordingFilename=new File(recorddir,addFileExtension(recordingFilenameInput.getText().toString().trim())).getPath();
				startRecording();
			} else {
				showDialog(DIALOG_RECORDING_FILENAME);
			}
		}
	}
	
	/** Automatically append .wav extension to filename without it */
	private String addFileExtension(String filename) {
		int extindex;
		
		extindex=filename.toLowerCase().lastIndexOf(".wav");
		if(extindex<0 || extindex!=filename.length()-4) {
			extindex=filename.toLowerCase().lastIndexOf(".wave");
			if(extindex<0 || extindex!=filename.length()-5) {
				filename+=".wav";
			}
		}
		return filename;
	}
	
	/** Called when sub-Activity returns result */
	@Override
	protected void onActivityResult(int request,int result,Intent data) {
		if(request==ACTIVITY_OUTPUT_OPTIONS) {
			if(result==OutputOptionsActivity.RESULT_OUTPUT_CHANGED) {
				Log.i("OptionsActivity","Output settings changed");
				((EtherSynthApplication)getApplicationContext()).getOscillator().setSampleRate(conf.isUsingNativeOutputSampleRate()?AudioOutput.getNativeSampleRate():conf.getOutputSampleRate());
				((EtherSynthApplication)getApplicationContext()).restartAudioOutput();
				checkRecordingStatus();
			}
			Log.i("OptionsActivity","Oscillator settings changed");
			((EtherSynthApplication)getApplicationContext()).getOscillator().setWaveform(conf.getOutputWaveform());
		}
	}
	
	/** Called when recording progressed for another second. */
	@Override
	public boolean recordingProgressed(int duration) {
		runOnUiThread(new Runnable() {
			public void run() {
				checkRecordingStatus();
			}
		});
		return true;
	}
	
	/** Called when recording failed in the middle. */
	@Override
	public boolean recordingFailed(Throwable t) {
		recordingError=t;
		runOnUiThread(new Runnable() {
			public void run() {
				checkRecordingStatus();
				showDialog(DIALOG_RECORDING_WRITE_FAILED);
			}
		});
		return true;
	}
	
	/** Called when Back button is pressed, returning to EtherSynthActivity. */
	@Override
	public void onBackPressed() {
		Intent command;
		
		if(changingControls) {
			command=new Intent(this,EtherSynthActivity.class);
			command.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			command.putExtra(EtherSynthActivity.MESSAGE_CONTROLS_CHANGE,true);
			startActivity(command);
		}
		((EtherSynthApplication)getApplicationContext()).removeRecordingListener(this);
		finish();
	}
}
