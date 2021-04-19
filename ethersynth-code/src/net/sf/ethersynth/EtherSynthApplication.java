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

import java.util.Enumeration;
import java.util.Vector;
import android.app.Application;
import android.util.Log;

/** Etheric Synthesizer's Android application context */
public class EtherSynthApplication extends Application implements RecordingListener {
	private AudioOutput line;
	private Oscillator osc;
	private Vector<RecordingListener> recordingListeners;
	
	/** Called when the application context is started. */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("EtherSynthApplication","Starting");
		init();
	}
	
	/** Initialize audio output if not already initialized */
	public void init() {
		ConfigOptions conf;
		
		conf=new ConfigOptions(this);
		synchronized(this) {
			if(line!=null && osc!=null && recordingListeners!=null) return;
			recordingListeners=new Vector<RecordingListener>();
			restartAudioOutput();
		}
	}
	
	/** Called to obtain line out object */
	public AudioOutput getAudioOutput() {
		synchronized(this) {
			return line;
		}
	}
	
	/** Return oscillator object */
	public Oscillator getOscillator() {
		synchronized(this) {
			return osc;
		}
	}
	
	/** Re-initialize audio output */
	public void restartAudioOutput() {
		ConfigOptions conf;
		
		conf=new ConfigOptions(this);
		synchronized(this) {
			if(line!=null) line.stop();
			if(osc==null)
				osc=new Oscillator(conf.isUsingNativeOutputSampleRate()?AudioOutput.getNativeSampleRate():conf.getOutputSampleRate(),conf.getOutputWaveform());
			line=new AudioOutput(osc);
			line.setRecordingListener(this);
		}
	}
		
	/** Add recording listener (if not already added) */
	public void addRecordingListener(RecordingListener listener) {
		synchronized(recordingListeners) {
			removeRecordingListener(listener);
			recordingListeners.addElement(listener);
		}
	}
	
	/** Remove recording listener. */
	public void removeRecordingListener(RecordingListener listener) {
		Enumeration<RecordingListener> list;
		RecordingListener listed;
		int i;
		
		synchronized(recordingListeners) {
			list=recordingListeners.elements();
			for(i=0;list.hasMoreElements();i++) {
				listed=list.nextElement();
				if(listed==listener) {
					recordingListeners.removeElementAt(i);
					return;
				}
			}
		}
	}
	
	/** Broadcast recording progressed event, in stack (descending) order. */
	@Override
	public boolean recordingProgressed(int duration) {
		int l;
		RecordingListener listed;
		
		synchronized(recordingListeners) {
			for(l=recordingListeners.size()-1;l>=0;l--) {
				try {
					if(recordingListeners.elementAt(l).recordingProgressed(duration)) return true;
				} catch(Throwable t) {
					Log.e("EtherSynthApplication","Failed sending recording progress event",t);
				}
			}
		}
		return false;
	}
	
	/** Broadcast recording failed event, in stack (descending) order. */
	@Override
	public boolean recordingFailed(Throwable e) {
		int l;
		RecordingListener listed;
		
		synchronized(recordingListeners) {
			for(l=recordingListeners.size()-1;l>=0;l--) {
				try {
					if(recordingListeners.elementAt(l).recordingFailed(e)) return true;
				} catch(Throwable t) {
					Log.e("EtherSynthApplication","Failed sending recording fail event",t);
				}
			}
		}
		return false;
	}
	
	/** Called when the application terminates. */
	public void destroy() {
		Log.i("EtherSynthApplication","Stopping");
		synchronized(this) {
			if(line!=null) line.stop();
			line=null;
			osc=null;
			recordingListeners=null;
		}
	}
}
