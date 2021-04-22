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

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

/** Display Recording Options screen */
public class RecordingOptionsActivity extends Activity implements CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener, DialogInterface.OnClickListener {
	private ConfigOptions conf;
	private RadioButton defaultDirectoryButton;
	private EditText defaultDirectory;
	private RadioButton customDirectoryButton;
	private EditText customDirectory;
	private AlertDialog noOutputDirectoryDialog;
	private AlertDialog nonexistentOutputDirectoryDialog;
	private AlertDialog outputNotDirectoryDialog;
	
	// Message constants
	public static final String MESSAGE_DEFAULT_RECORDING_DIRECTORY="defaultRecordingDirectory";
	
	// Dialog constants
	private static final int DIALOG_NO_OUTPUT_DIRECTORY=1;
	private static final int DIALOG_NONEXISTENT_OUTPUT_DIRECTORY=2;
	private static final int DIALOG_OUTPUT_NOT_DIRECTORY=3;
	
	/** Called when the activity is first created */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordoptions);
		conf=new ConfigOptions(this);
		
		((CheckBox)findViewById(R.id.asksavefilename)).setChecked(conf.isAskingForRecordingFilename());
		defaultDirectoryButton=((RadioButton)findViewById(R.id.usedefaultdir));
		customDirectoryButton=((RadioButton)findViewById(R.id.usecustomdir));
		defaultDirectory=((EditText)findViewById(R.id.defaultdir));
		defaultDirectory.setText(conf.getDefaultRecordingDirectory().getPath());
		customDirectory=((EditText)findViewById(R.id.customdir));
		customDirectory.setText(conf.getCustomRecordingDirectory());
		defaultDirectoryButton.setOnCheckedChangeListener(this);
		customDirectoryButton.setOnCheckedChangeListener(this);
		if(conf.isUsingCustomRecordingDirectory()) customDirectoryButton.setChecked(true);
		else defaultDirectoryButton.setChecked(true);
	}
	
	/** Called when one of the radio buttons is changed. */
	@Override
	public void onCheckedChanged(CompoundButton button,boolean checked) {
		if(button==defaultDirectoryButton) {
			defaultDirectory.setEnabled(checked);
			customDirectoryButton.setChecked(!checked);
			customDirectory.setEnabled(!checked);
		} else if(button==customDirectoryButton) {
			defaultDirectory.setEnabled(!checked);
			defaultDirectoryButton.setChecked(!checked);
			customDirectory.setEnabled(checked);
		}
	}
	
	/** Called to create dialog. */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==DIALOG_NO_OUTPUT_DIRECTORY) {
			noOutputDirectoryDialog=new AlertDialog.Builder(this).setTitle("No Output Directory").setMessage("Custom output directory is not specified, so default one will be used instead.").create();
			noOutputDirectoryDialog.setOnDismissListener(this);
			return noOutputDirectoryDialog;
		} else if(id==DIALOG_NONEXISTENT_OUTPUT_DIRECTORY) {
			return nonexistentOutputDirectoryDialog=new AlertDialog.Builder(this).setTitle("Nonexistent Output Directory").setMessage("Specified custom output directory does not exist. Are you sure you do want to use this directory?").setPositiveButton("Yes",this).setNegativeButton("No",this).create();
		} else if(id==DIALOG_OUTPUT_NOT_DIRECTORY) {
			return outputNotDirectoryDialog=new AlertDialog.Builder(this).setTitle("Output is Non-Directory").setMessage("Specified custom output location is not a directory. Are you sure you do want to use this location?").setPositiveButton("Yes",this).setNegativeButton("No",this).create();
		} else {
			return null;
		}
	}
	
	/** Called when dialog is dismissed. */
	@Override
	public void onDismiss(DialogInterface dialog) {
		if(dialog==noOutputDirectoryDialog) {
			defaultDirectoryButton.setChecked(true);
			applyChangesFinish(true);
		}
	}
	
	/** Called when dialog button is pressed. */
	@Override
	public void onClick(DialogInterface dialog,int button) {
		if(dialog==nonexistentOutputDirectoryDialog || dialog==outputNotDirectoryDialog) {
			if(button==DialogInterface.BUTTON_POSITIVE) {
				applyChangesFinish(true);
			}
		}
	}
	
	/** Called when the Back button is pressed. */
	@Override
	public void onBackPressed() {
		applyChangesFinish(false);
	}
	
	/** Apply configuration change, and exit. */
	private void applyChangesFinish(boolean forced) {
		String customdir;
		boolean usecustomdir;
		
		usecustomdir=customDirectoryButton.isChecked();
		customdir=customDirectory.getText().toString().trim();
		if(customdir.length()==0) customdir=null;
		if(usecustomdir && !forced) {
			if(customdir==null) {
				showDialog(DIALOG_NO_OUTPUT_DIRECTORY);
				return;
			} else if(!new File(customdir).exists()) {
				showDialog(DIALOG_NONEXISTENT_OUTPUT_DIRECTORY);
				return;
			} else if(!new File(customdir).isDirectory()) {
				showDialog(DIALOG_OUTPUT_NOT_DIRECTORY);
				return;
			}
		}
		
		conf.setAskingForRecordingFilename(((CheckBox)findViewById(R.id.asksavefilename)).isChecked());
		conf.setUsingCustomRecordingDirectory(usecustomdir);
		conf.setCustomRecordingDirectory(customdir);
		finish();
	}
}
