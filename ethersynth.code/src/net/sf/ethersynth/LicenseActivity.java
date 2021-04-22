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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Display GNU General Public License text */
public class LicenseActivity extends Activity implements Runnable {
	private ScrollView scroller;
	private LinearLayout textList;
	private float scrollFraction;
	
	/** Called when the activity is first created, or resurrected. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		BufferedReader rdr;
		StringBuffer buffer;
		String line;
		int spacecount;
		TextView text;
		LinearLayout.LayoutParams layoutparam;
		DisplayMetrics dspinfo;
		
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null) {
			scrollFraction=savedInstanceState.getFloat("scrollFraction",0.0F);
		}
		textList=new LinearLayout(this);
		textList.setOrientation(LinearLayout.VERTICAL);
		dspinfo=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dspinfo);
		
		spacecount=0;
		buffer=new StringBuffer();
		try {
			rdr=new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.gpl)));
			while(true) {
				line=rdr.readLine();
				
				if(line==null || line.length()==0) {
					if(buffer.length()>0) {
						text=new TextView(this);
						text.setAutoLinkMask(Linkify.WEB_URLS);
						text.setText(buffer.toString());
						layoutparam=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						layoutparam.topMargin=(int)(10*spacecount*dspinfo.scaledDensity);
						textList.addView(text,layoutparam);
						buffer.setLength(0);
						spacecount=0;
					}
					
					if(line==null) break;
					else spacecount++;
				} else {
					if(buffer.length()>0) buffer.append('\n');
					buffer.append(line);
				}
			}
			rdr.close();
		} catch(IOException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.gnu.org/licenses/gpl.html")));
			finish();
			return;
		}
		
		scroller=new ScrollView(this);
		scroller.addView(textList);
		scroller.post(this);
		setContentView(scroller);
	}
	
	/** Called when the ScrollView is first displayed. */
	@Override
	public void run() {
		scroller.scrollTo(0,(int)(scrollFraction*textList.getHeight()));
	}
	
	/** Called when the activity is about to suspend and need to save data. */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putFloat("scrollFraction",(float)scroller.getScrollY()/textList.getHeight());
	}
}
