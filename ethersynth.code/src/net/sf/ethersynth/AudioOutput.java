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
import java.util.Enumeration;
import java.util.Vector;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/** Drive oscillator and feed audio output (Little-endian 16-bit PCM Mono output) */
public class AudioOutput implements Runnable {
	private Oscillator osc;
	private AudioTrack lineout;
	private int sampleRate;
	private byte buffer[];
	private volatile boolean stopping;
	private volatile AudioDump dumper;
	private volatile RecordingListener listener;
	
	/** Construct audio output, connecting to oscillator */
	public AudioOutput(Oscillator osc) {
		int minbuf;
		int bufsize;
		
		this.osc=osc;
		this.sampleRate=osc.getSampleRate();
		minbuf=AudioTrack.getMinBufferSize(
			sampleRate,
			AudioFormat.CHANNEL_OUT_MONO,
			AudioFormat.ENCODING_PCM_16BIT
		);
		bufsize=minbuf;
		buffer=new byte[bufsize];
		lineout=new AudioTrack(
			AudioManager.STREAM_MUSIC,
			sampleRate,
			AudioFormat.CHANNEL_OUT_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			bufsize,
			AudioTrack.MODE_STREAM
		);
		lineout.play();
		new Thread(this).start();
	}
	
	/** Return system's native sample rate */
	public static int getNativeSampleRate() {
		return AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
	}
	
	/** Output generated audio */
	@Override
	public void run() {
		RecordingListener listener;
		int newduration=-1;
		
		while(!stopping) {
			osc.read(buffer,0,buffer.length);
			lineout.write(buffer,0,buffer.length);
			synchronized(this) {
				listener=this.listener;
				if(dumper!=null) {
					try {
						if(dumper.write(buffer,0,buffer.length)) {
							newduration=dumper.getOutputDuration();
						}
					} catch(IOException e) {
						Log.e("AudioOutput","Failed to write audio recording",e);
						try {
							dumper.close();
						} catch(IOException i) {}
						dumper=null;
						if(listener!=null) listener.recordingFailed(e);
					}
				}
			}
			if(newduration>=0 && listener!=null) {
				listener.recordingProgressed(newduration);
			}
		}
		synchronized(this) {
			this.notify();
		}
	}
	
	/** Start recording to specified output file, opens file synchronously */
	public void startRecording(File dumpFile) throws IOException {
		synchronized(this) {
			if(dumper!=null) dumper.close();
			try {
				dumper=new AudioDump(dumpFile,sampleRate);
				Log.i("AudioOutput","Recording started, writing to: "+dumpFile.getPath());
			} catch(IOException e) {
				Log.e("AudioOutput","Failed to open audio recording",e);
				throw e;
			}
		}
	}
	
	/** Check if there's any recording in progress. */
	public boolean isRecording() {
		synchronized(this) {
			return dumper!=null;
		}
	}
	
	/** Return current output recording file, or null if no recording in progress. */
	public File getRecordingFile() {
		synchronized(this) {
			return dumper!=null?dumper.getOutputFile():null;
		}
	}
	
	/** Return current recording duration (in seconds), -1 if no recording in progress. */
	public int getRecordingTime() {
		synchronized(this) {
			return dumper!=null?dumper.getOutputDuration():-1;
		}
	}
	
	/** Stop any recording in progress, flushes data and closes file synchronously. */
	public void stopRecording() {
		synchronized(this) {
			try {
				if(dumper!=null) dumper.close();
			} catch(IOException i) {}
			dumper=null;
		}
	}
	
	/** Stop audio output */
	public void stop() {
		if(stopping) return;
		
		synchronized(this) {
			stopping=true;
			try {
				this.wait();
			} catch(InterruptedException e) {}
			
			lineout.pause();
			lineout.flush();
			lineout.release();
			try {
				if(dumper!=null) dumper.close();
			} catch(IOException e) {}
			dumper=null;
		}
	}
	
	/** Set recording listener (null to remove). */
	public void setRecordingListener(RecordingListener listener) {
		this.listener=listener;
	}
}
