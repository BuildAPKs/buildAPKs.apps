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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/** Display About Etheric Synthesizer screen */
public class AboutActivity extends Activity {
	/** Called when the activity is first created */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		try {
			((TextView)findViewById(R.id.progidentifier)).setText(((TextView)findViewById(R.id.progidentifier)).getText()+" "+getPackageManager().getPackageInfo(getPackageName(),0).versionName);
		} catch(PackageManager.NameNotFoundException e) {}
	}
	
	/** Called when the Read GNU General Public License button was pressed */
	public void showLicense(View view) {
		Intent command;
		
		command=new Intent(this,LicenseActivity.class);
		startActivity(command);
	}
}
