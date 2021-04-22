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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/** Display control and display option listing */
public class ControlOptionsActivity extends Activity implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
	private ConfigOptions conf;
	private Spinner highFrequencyNote,highFrequencyUnit;
	private Spinner lowFrequencyNote,lowFrequencyUnit;
	private int highFrequencyNoteIndex,highFrequencyUnitIndex;
	private int lowFrequencyNoteIndex,lowFrequencyUnitIndex;
	private boolean highFrequencyNoteChanged;
	private boolean lowFrequencyNoteChanged;
	
	// Dialog constants
	private final int DIALOG_INVALID_HIGHEST_FREQUENCY=1;
	private final int DIALOG_INVALID_LOWEST_FREQUENCY=2;
	private final int DIALOG_LOWEST_ABOVE_HIGHEST_FREQUENCY=3;
	private final int DIALOG_INVALID_FREQUENCY=4;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ArrayAdapter adapter;
		Spinner orientationSelector;
		Spinner axesSelector;
		
		super.onCreate(savedInstanceState);
		
		conf=new ConfigOptions(this);
		setContentView(R.layout.controloptions);
		
		((TextView)findViewById(R.id.highfreq)).setText(Float.toString(conf.getHighestFrequency()));
		highFrequencyNote=(Spinner)findViewById(R.id.highfreqspn);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,midiNoteRange());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		highFrequencyNote.setAdapter(adapter);
		highFrequencyNote.setSelection(highFrequencyNoteIndex=127-ConfigOptions.midiNote(conf.getHighestFrequency()));
		highFrequencyNote.setOnItemSelectedListener(this);
		
		highFrequencyUnit=(Spinner)findViewById(R.id.highfrequnit);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Hz","SPN"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		highFrequencyUnit.setAdapter(adapter);
		
		if(conf.isHighestFrequencyInNote()) {
			highFrequencyUnit.setSelection(highFrequencyUnitIndex=1);
			((TextView)findViewById(R.id.highfreq)).setVisibility(View.GONE);
			highFrequencyNote.setVisibility(View.VISIBLE);
		} else {
			highFrequencyUnit.setSelection(highFrequencyUnitIndex=0);
			((TextView)findViewById(R.id.highfreq)).setVisibility(View.VISIBLE);
			highFrequencyNote.setVisibility(View.GONE);
		}
		highFrequencyUnit.setOnItemSelectedListener(this);
		
		((TextView)findViewById(R.id.lowfreq)).setText(Float.toString(conf.getLowestFrequency()));
		lowFrequencyNote=(Spinner)findViewById(R.id.lowfreqspn);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,midiNoteRange());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lowFrequencyNote.setAdapter(adapter);
		lowFrequencyNote.setSelection(lowFrequencyNoteIndex=127-ConfigOptions.midiNote(conf.getLowestFrequency()));
		lowFrequencyNote.setOnItemSelectedListener(this);
		
		lowFrequencyUnit=(Spinner)findViewById(R.id.lowfrequnit);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Hz","SPN"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lowFrequencyUnit.setAdapter(adapter);
		
		if(conf.isLowestFrequencyInNote()) {
			lowFrequencyUnit.setSelection(lowFrequencyUnitIndex=1);
			((TextView)findViewById(R.id.lowfreq)).setVisibility(View.GONE);
			lowFrequencyNote.setVisibility(View.VISIBLE);
		} else {
			lowFrequencyUnit.setSelection(lowFrequencyUnitIndex=0);
			((TextView)findViewById(R.id.lowfreq)).setVisibility(View.VISIBLE);
			lowFrequencyNote.setVisibility(View.GONE);
		}
		lowFrequencyUnit.setOnItemSelectedListener(this);
				
		((CheckBox)findViewById(R.id.showbackground)).setChecked(conf.isShowingBackground());
		((CheckBox)findViewById(R.id.showtouchpoint)).setChecked(conf.isShowingTouchPoint());

		orientationSelector=(Spinner)findViewById(R.id.lockorientation);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Don\'t lock","Lock to portrait","Lock to landscape"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		orientationSelector.setAdapter(adapter);
		switch(conf.getOrientationLock()) {
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_LANDSCAPE: orientationSelector.setSelection(2); break;
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_PORTRAIT: orientationSelector.setSelection(1); break;
			case ConfigOptions.LOCK_SCREEN_ORIENTATION_NONE:
			default:
				orientationSelector.setSelection(0);
		}
		axesSelector=(Spinner)findViewById(R.id.controlaxes);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Vertical frequency, horizontal amplitude (default)","Horizontal frequency, vertical amplitude"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		axesSelector.setAdapter(adapter);
		switch(conf.getControlAxes()) {
			case ConfigOptions.CONTROL_AXES_HORIZONTAL_FREQUENCY: axesSelector.setSelection(1); break;
			case ConfigOptions.CONTROL_AXES_VERTICAL_FREQUENCY:
			default:
				axesSelector.setSelection(0);
		}
		((CheckBox)findViewById(R.id.invertfreqctrl)).setChecked(conf.isInvertingFrequencyControl());
		((CheckBox)findViewById(R.id.invertampctrl)).setChecked(conf.isInvertingAmplitudeControl());
		((SeekBar)findViewById(R.id.deadzone)).setProgress((int)(Math.round(100*conf.getDeadZone())));
		((SeekBar)findViewById(R.id.satzone)).setProgress((int)(Math.round(100*conf.getSaturationZone())));
		((TextView)findViewById(R.id.deadzonedsp)).setText(Integer.toString((int)(Math.round(100*conf.getDeadZone())))+'%');
		((TextView)findViewById(R.id.satzonedsp)).setText(Integer.toString((int)(Math.round(100*conf.getSaturationZone())))+'%');
		((SeekBar)findViewById(R.id.deadzone)).setOnSeekBarChangeListener(this);
		((SeekBar)findViewById(R.id.satzone)).setOnSeekBarChangeListener(this);
	}
	
	/** Generate list of SPN strings of MIDI note range, in descending order */
	private String[] midiNoteRange() {
		final String[] semitones={"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
		final String[] notes;
		int i;
		
		notes=new String[128];
		for(i=127;i>=0;i--) {
			notes[127-i]=semitones[i%12]+((i/12)-1);
		}
		return notes;
	}
		
	/** Called when spinner item selection is changed. */
	@Override
	public void onItemSelected(AdapterView<?> parent,View item,int pos,long id) {
		int note;
		
		if(parent==highFrequencyUnit && pos!=highFrequencyUnitIndex) {
			highFrequencyUnitIndex=pos;
			if(pos==1) {
				// Hide frequency, show SPN: derive SPN from current frequency field
				highFrequencyNoteChanged=false;
				try {
					note=ConfigOptions.midiNote(Float.parseFloat(((TextView)findViewById(R.id.highfreq)).getText().toString().trim()));
					highFrequencyNote.setSelection(highFrequencyNoteIndex=127-note);
				} catch(NumberFormatException e) {}
				((TextView)findViewById(R.id.highfreq)).setVisibility(View.GONE);
				highFrequencyNote.setVisibility(View.VISIBLE);
			} else {
				// Show frequency, hide SPN: if SPN was changed, derive frequency from SPN, if not, use original frequency
				if(highFrequencyNoteChanged) {
					((TextView)findViewById(R.id.highfreq)).setText(Float.toString(ConfigOptions.midiFrequency(127-highFrequencyNote.getSelectedItemPosition())));
					highFrequencyNoteChanged=false;
				}
				((TextView)findViewById(R.id.highfreq)).setVisibility(View.VISIBLE);
				highFrequencyNote.setVisibility(View.GONE);
			}
		} else if(parent==lowFrequencyUnit && pos!=lowFrequencyUnitIndex) {
			lowFrequencyUnitIndex=pos;
			if(pos==1) {
				// Hide frequency, show SPN: derive SPN from current frequency field
				lowFrequencyNoteChanged=false;
				try {
					note=ConfigOptions.midiNote(Float.parseFloat(((TextView)findViewById(R.id.lowfreq)).getText().toString().trim()));
					lowFrequencyNote.setSelection(lowFrequencyNoteIndex=127-note);
				} catch(NumberFormatException e) {}
				((TextView)findViewById(R.id.lowfreq)).setVisibility(View.GONE);
				lowFrequencyNote.setVisibility(View.VISIBLE);
			} else {
				// Show frequency, hide SPN: if SPN was changed, derive frequency from SPN, if not, use original frequency
				if(lowFrequencyNoteChanged) {
					((TextView)findViewById(R.id.lowfreq)).setText(Float.toString(ConfigOptions.midiFrequency(127-lowFrequencyNote.getSelectedItemPosition())));
					lowFrequencyNoteChanged=false;
				}
				((TextView)findViewById(R.id.lowfreq)).setVisibility(View.VISIBLE);
				lowFrequencyNote.setVisibility(View.GONE);
			}
		} else if(parent==highFrequencyNote && pos!=highFrequencyNoteIndex) {
			highFrequencyNoteIndex=pos;
			highFrequencyNoteChanged=true;
		} else if(parent==lowFrequencyNote && pos!=lowFrequencyNoteIndex) {
			lowFrequencyNoteIndex=pos;
			lowFrequencyNoteChanged=true;
		}
	}
	
	/** Should not be called. */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
	
	/** Called when Restore Defaults button is clicked. */
	public void restoreDefault(View view) {
		((TextView)findViewById(R.id.highfreq)).setText(Float.toString(conf.HIGHEST_FREQUENCY_DEFAULT));
		highFrequencyNote.setSelection(highFrequencyNoteIndex=127-ConfigOptions.midiNote(conf.HIGHEST_FREQUENCY_DEFAULT));
		if(conf.HIGHEST_FREQUENCY_SEMITONE_MODE_DEFAULT) {
			((TextView)findViewById(R.id.highfreq)).setVisibility(View.GONE);
			highFrequencyNote.setVisibility(View.VISIBLE);
			highFrequencyUnit.setSelection(highFrequencyUnitIndex=1);
		} else {
			((TextView)findViewById(R.id.highfreq)).setVisibility(View.VISIBLE);
			highFrequencyNote.setVisibility(View.GONE);
			highFrequencyUnit.setSelection(highFrequencyUnitIndex=0);
		}
		((TextView)findViewById(R.id.lowfreq)).setText(Float.toString(conf.LOWEST_FREQUENCY_DEFAULT));
		lowFrequencyNote.setSelection(lowFrequencyNoteIndex=127-ConfigOptions.midiNote(conf.LOWEST_FREQUENCY_DEFAULT));
		if(conf.LOWEST_FREQUENCY_SEMITONE_MODE_DEFAULT) {
			((TextView)findViewById(R.id.lowfreq)).setVisibility(View.GONE);
			lowFrequencyNote.setVisibility(View.VISIBLE);
			lowFrequencyUnit.setSelection(lowFrequencyUnitIndex=1);
		} else {
			((TextView)findViewById(R.id.lowfreq)).setVisibility(View.VISIBLE);
			lowFrequencyNote.setVisibility(View.GONE);
			lowFrequencyUnit.setSelection(lowFrequencyUnitIndex=0);
		}
	}
	
	/** Called to create dialog */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==DIALOG_INVALID_HIGHEST_FREQUENCY) {
			return new AlertDialog.Builder(this).setMessage("Invalid highest frequency entered.").create();
		} else if(id==DIALOG_INVALID_LOWEST_FREQUENCY) {
			return new AlertDialog.Builder(this).setMessage("Invalid lowest frequency entered.").create();
		} else if(id==DIALOG_LOWEST_ABOVE_HIGHEST_FREQUENCY) {
			return new AlertDialog.Builder(this).setMessage("Lowest frequency must not be higher than highest frequency.").create();
		} else if(id==DIALOG_INVALID_FREQUENCY) {
			return new AlertDialog.Builder(this).setMessage("Invalid frequency entered.").create();
		} else {
			return null;
		}
	}
	
	/** Called when seek bars value was changed */
	@Override
	public void onProgressChanged(SeekBar seekbar,int progress,boolean fromuser) {
		SeekBar deadzone;
		SeekBar satzone;
		TextView deadzonedsp;
		TextView satzonedsp;
		int deadzonevalue;
		int satzonevalue;
		
		deadzone=((SeekBar)findViewById(R.id.deadzone));
		satzone=((SeekBar)findViewById(R.id.satzone));
		deadzonedsp=((TextView)findViewById(R.id.deadzonedsp));
		satzonedsp=((TextView)findViewById(R.id.satzonedsp));
		if(seekbar==deadzone) {
			deadzonevalue=deadzone.getProgress();
			satzonevalue=satzone.getProgress();
			deadzonedsp.setText(Integer.toString(deadzonevalue)+'%');
			if(deadzonevalue+satzonevalue>100) {
				satzonevalue=100-deadzonevalue;
				satzone.setProgress(satzonevalue);
				satzonedsp.setText(Integer.toString(satzonevalue)+'%');
			}
		} else if(seekbar==satzone) {
			deadzonevalue=deadzone.getProgress();
			satzonevalue=satzone.getProgress();
			satzonedsp.setText(Integer.toString(satzonevalue)+'%');
			if(deadzonevalue+satzonevalue>100) {
				deadzonevalue=100-satzonevalue;
				deadzone.setProgress(deadzonevalue);
				deadzonedsp.setText(Integer.toString(deadzonevalue)+'%');
			}
		}
	}
	
	/** Called when user grabs a seek bar */
	@Override
	public void onStartTrackingTouch(SeekBar seekbar) {}
	
	/** Called when user releases a seek bar */
	@Override
	public void onStopTrackingTouch(SeekBar seekbar) {}
	
	
	/** Called when back button was pressed. */
	@Override
	public void onBackPressed() {
		float lowestfreq,highestfreq;
		
		if(highFrequencyUnit.getSelectedItemPosition()==1) {
			highestfreq=ConfigOptions.midiFrequency(127-highFrequencyNote.getSelectedItemPosition());
		} else {
			try {
				highestfreq=Float.parseFloat(((TextView)findViewById(R.id.highfreq)).getText().toString());
			} catch(NumberFormatException e) {
				showDialog(DIALOG_INVALID_HIGHEST_FREQUENCY);
				return;
			}
		}
		if(lowFrequencyUnit.getSelectedItemPosition()==1) {
			lowestfreq=ConfigOptions.midiFrequency(127-lowFrequencyNote.getSelectedItemPosition());
		} else {
			try {
				lowestfreq=Float.parseFloat(((TextView)findViewById(R.id.lowfreq)).getText().toString());
			} catch(NumberFormatException e) {
				showDialog(DIALOG_INVALID_LOWEST_FREQUENCY);
				return;
			}
		}
		try {
			conf.setLowestHighestFrequency(lowestfreq,highestfreq);
		} catch(IllegalArgumentException e) {
			if("Invalid highest frequency".equals(e.getMessage())) {
				showDialog(DIALOG_INVALID_HIGHEST_FREQUENCY);
			} else if("Invalid lowest frequency".equals(e.getMessage())) {
				showDialog(DIALOG_INVALID_LOWEST_FREQUENCY);
			} else if("Lowest frequency must not be higher than highest frequency".equals(e.getMessage())) {
				showDialog(DIALOG_LOWEST_ABOVE_HIGHEST_FREQUENCY);
			} else {
				showDialog(DIALOG_INVALID_FREQUENCY);
			}
			return;
		}
		conf.setHighestFrequencyInNote(highFrequencyUnit.getSelectedItemPosition()==1);
		conf.setLowestFrequencyInNote(lowFrequencyUnit.getSelectedItemPosition()==1);
		conf.setShowingBackground(((CheckBox)findViewById(R.id.showbackground)).isChecked());
		conf.setShowingTouchPoint(((CheckBox)findViewById(R.id.showtouchpoint)).isChecked());
		switch(((Spinner)findViewById(R.id.lockorientation)).getSelectedItemPosition()) {
			case 2: conf.setOrientationLock(ConfigOptions.LOCK_SCREEN_ORIENTATION_LANDSCAPE); break;
			case 1: conf.setOrientationLock(ConfigOptions.LOCK_SCREEN_ORIENTATION_PORTRAIT); break;
			case 0:
			default:
				 conf.setOrientationLock(ConfigOptions.LOCK_SCREEN_ORIENTATION_NONE);
		}
		switch(((Spinner)findViewById(R.id.controlaxes)).getSelectedItemPosition()) {
			case 1: conf.setControlAxes(ConfigOptions.CONTROL_AXES_HORIZONTAL_FREQUENCY); break;
			case 0:
			default:
				conf.setControlAxes(ConfigOptions.CONTROL_AXES_VERTICAL_FREQUENCY);
		}
		conf.setInvertingFrequencyControl(((CheckBox)findViewById(R.id.invertfreqctrl)).isChecked());
		conf.setInvertingAmplitudeControl(((CheckBox)findViewById(R.id.invertampctrl)).isChecked());
		conf.setDeadSaturationZone((float)((SeekBar)findViewById(R.id.deadzone)).getProgress()/100,(float)((SeekBar)findViewById(R.id.satzone)).getProgress()/100);
		super.onBackPressed();
	}
}
