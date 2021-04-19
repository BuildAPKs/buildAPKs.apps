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
import java.util.Arrays;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

/** Load settings from SharedPreferences, with sanity checks*/
public class ConfigOptions {
	private SharedPreferences pref;
	private SharedPreferences.Editor prefedit;
	
	// Property keys
	private final String HIGHEST_FREQUENCY="ethersynth.highestFrequency";
	private final String HIGHEST_FREQUENCY_SEMITONE_MODE="ethersynth.setHighestFrequencyInNote";
	private final String LOWEST_FREQUENCY="ethersynth.lowestFrequency";
	private final String LOWEST_FREQUENCY_SEMITONE_MODE="ethersynth.setLowestFrequencyInNote";
	private final String SHOW_BACKGROUND="ethersynth.showBackground";
	private final String SHOW_TOUCH_POINT="ethersynth.showTouchPoint";
	private final String LOCK_SCREEN_ORIENTATION="ethersynth.lockScreenOrientation";
	private final String CONTROL_AXES="ethersynth.controlAxes";
	private final String CONTROL_INVERT_FREQUENCY="ethersynth.invertFrequencyControl";
	private final String CONTROL_INVERT_AMPLITUDE="ethersynth.invertAmplitudeControl";
	private final String CONTROL_DEAD_ZONE="ethersynth.deadZone";
	private final String CONTROL_SATURATION_ZONE="ethersynth.saturationZone";
	private final String OUTPUT_WAVEFORM="ethersynth.outputWaveform";
	private final String OUTPUT_USE_NATIVE_SAMPLE_RATE="ethersynth.outputUseNativeSampleRate";
	private final String OUTPUT_SAMPLE_RATE="ethersynth.outputSampleRate";
	private final String RECORDING_ASK_FILENAME="ethersynth.askRecordingFilename";
	private final String RECORDING_USE_CUSTOM_DIRECTORY="ethersynth.useCustomRecordingDirectory";
	private final String RECORDING_CUSTOM_DIRECTORY="ethersynth.customRecordingDirectory";
	
	// Enum constants
	public static final int LOCK_SCREEN_ORIENTATION_NONE=0;
	public static final int LOCK_SCREEN_ORIENTATION_PORTRAIT=1;
	public static final int LOCK_SCREEN_ORIENTATION_LANDSCAPE=2;
	public static final int CONTROL_AXES_VERTICAL_FREQUENCY=0;
	public static final int CONTROL_AXES_HORIZONTAL_FREQUENCY=1;
	public static final int OUTPUT_WAVEFORM_SINE=0;
	public static final int OUTPUT_WAVEFORM_TRIANGLE=1;
	public static final int OUTPUT_WAVEFORM_SAWTOOTH=2;
	public static final int OUTPUT_WAVEFORM_SQUARE=3;
	public static final int[] OUTPUT_SAMPLE_RATES={8000,11025,12000,16000,22050,24000,32000,44100,48000};
	
	// Default values
	public static final float HIGHEST_FREQUENCY_DEFAULT=1046.5023F;
	public static final boolean HIGHEST_FREQUENCY_SEMITONE_MODE_DEFAULT=true;
	public static final float LOWEST_FREQUENCY_DEFAULT=523.25116F;
	public static final boolean LOWEST_FREQUENCY_SEMITONE_MODE_DEFAULT=true;
	public static final boolean SHOW_BACKGROUND_DEFAULT=true;
	public static final boolean SHOW_TOUCH_POINT_DEFAULT=true;
	public static final int LOCK_SCREEN_ORIENTATION_DEFAULT=LOCK_SCREEN_ORIENTATION_NONE;
	public static final int CONTROL_AXES_DEFAULT=CONTROL_AXES_VERTICAL_FREQUENCY;
	public static final boolean CONTROL_INVERT_FREQUENCY_DEFAULT=false;
	public static final boolean CONTROL_INVERT_AMPLITUDE_DEFAULT=false;
	public static final float CONTROL_DEAD_ZONE_DEFAULT=0.0F;
	public static final float CONTROL_SATURATION_ZONE_DEFAULT=0.0F;
	public static final int OUTPUT_WAVEFORM_DEFAULT=OUTPUT_WAVEFORM_SINE;
	public static final boolean OUTPUT_USE_NATIVE_SAMPLE_RATE_DEFAULT=true;
	public static final int OUTPUT_SAMPLE_RATE_DEFAULT=44100;
	public static final boolean RECORDING_ASK_FILENAME_DEFAULT=false;
	public static final boolean RECORDING_USE_CUSTOM_DIRECTORY_DEFAULT=false;
	
	/** Constructor, obtain SharedPreferences object */
	public ConfigOptions(Context context) {
		pref=context.getSharedPreferences("ethersynth.properties",Context.MODE_PRIVATE);
		prefedit=pref.edit();
	}
	
	/** Return configured highest frequency of theremin control */
	public float getHighestFrequency() {
		return getLowestHighestFrequency(true);
	}
	
	/** Return configured lowest frequency of theremin control */
	public float getLowestFrequency() {
		return getLowestHighestFrequency(false);
	}
	
	/** Return configured lowest/highest frequency of theremin control (with sanity check) */
	private float getLowestHighestFrequency(boolean highest) {
		float lowestfreq,highestfreq;
		
		if(!pref.contains(LOWEST_FREQUENCY) || !pref.contains(HIGHEST_FREQUENCY)) {
			highestfreq=HIGHEST_FREQUENCY_DEFAULT;
			lowestfreq=LOWEST_FREQUENCY_DEFAULT;
		} else {
			highestfreq=pref.getFloat(HIGHEST_FREQUENCY,HIGHEST_FREQUENCY_DEFAULT);
			lowestfreq=pref.getFloat(LOWEST_FREQUENCY,LOWEST_FREQUENCY_DEFAULT);
		}
		if(Float.isNaN(lowestfreq) || Float.isNaN(highestfreq) || Float.isInfinite(lowestfreq) || Float.isInfinite(highestfreq) || lowestfreq<=0 || highestfreq<=0 || lowestfreq>highestfreq) {
			highestfreq=HIGHEST_FREQUENCY_DEFAULT;
			lowestfreq=LOWEST_FREQUENCY_DEFAULT;
		}
		if(isHighestFrequencyInNote()) highestfreq=midiFrequency(midiNote(highestfreq));
		if(isLowestFrequencyInNote()) lowestfreq=midiFrequency(midiNote(lowestfreq));
		return highest?highestfreq:lowestfreq;
	}
	
	/** Set configured frequency bound of theremin control */
	public void setLowestHighestFrequency(float lowest,float highest) {
		if(Float.isNaN(highest) || Float.isInfinite(highest) || highest<=0) {
			throw new IllegalArgumentException("Invalid highest frequency");
		} else if(Float.isNaN(lowest) || lowest<=0 || Float.isInfinite(lowest)) {
			throw new IllegalArgumentException("Invalid lowest frequency");
		} else if(lowest>highest) {
			throw new IllegalArgumentException("Lowest frequency must not be higher than highest frequency");
		}
		prefedit.putFloat(LOWEST_FREQUENCY,lowest);
		prefedit.putFloat(HIGHEST_FREQUENCY,highest);
		prefedit.commit();
	}
	
	/** Return whether to show highest frequency configuration in SPN or not. */
	public boolean isHighestFrequencyInNote() {
		return pref.getBoolean(HIGHEST_FREQUENCY_SEMITONE_MODE,HIGHEST_FREQUENCY_SEMITONE_MODE_DEFAULT);
	}
	
	/** Set whether to show highest frequency configuration in SPN. */
	public void setHighestFrequencyInNote(boolean usenote) {
		prefedit.putBoolean(HIGHEST_FREQUENCY_SEMITONE_MODE,usenote);
		prefedit.commit();
	}
	
	/** Return whether to show lowest frequency configuration in SPN or not. */
	public boolean isLowestFrequencyInNote() {
		return pref.getBoolean(LOWEST_FREQUENCY_SEMITONE_MODE,LOWEST_FREQUENCY_SEMITONE_MODE_DEFAULT);
	}
	
	/** Set whether to show lowest frequency configuration in SPN. */
	public void setLowestFrequencyInNote(boolean usenote) {
		prefedit.putBoolean(LOWEST_FREQUENCY_SEMITONE_MODE,usenote);
		prefedit.commit();
	}
	
	/** Return frequency of MIDI note number. */
	public static float midiFrequency(int notenum) {
		return (float)(440.0*Math.pow(2,((double)notenum-69)/12));
	}
	
	/** Return nearest MIDI note number of specified frequency (trimmed to MIDI note range). */
	public static int midiNote(float frequency) {
		double note;
		
		note=Math.round(12*Math.log((double)frequency/440)/Math.log(2) + 69);
		if(note>=127.0) return 127;
		else if(note>=0 && note<127) return (int)note;
		else return 0;
	}
	
	/** Return true if configured to show rainbow background, false otherwise */
	public boolean isShowingBackground() {
		return pref.getBoolean(SHOW_BACKGROUND,SHOW_BACKGROUND_DEFAULT);
	}
	
	/** Configure whether to show rainbow background */
	public void setShowingBackground(boolean showingBackground) {
		prefedit.putBoolean(SHOW_BACKGROUND,showingBackground);
		prefedit.commit();
	}
	
	/** Return true if configured to show touch location, false otherwise */
	public boolean isShowingTouchPoint() {
		return pref.getBoolean(SHOW_TOUCH_POINT,SHOW_TOUCH_POINT_DEFAULT);
	}
	
	/** Configure wheter to show touch location */
	public void setShowingTouchPoint(boolean showingTouchPoint) {
		prefedit.putBoolean(SHOW_TOUCH_POINT,showingTouchPoint);
		prefedit.commit();
	}
	
	/** Return orientation lock configuration (with sanity check) */
	public int getOrientationLock() {
		int orientationLock;
		
		orientationLock=pref.getInt(LOCK_SCREEN_ORIENTATION,LOCK_SCREEN_ORIENTATION_DEFAULT);
		if(orientationLock!=LOCK_SCREEN_ORIENTATION_NONE && orientationLock!=LOCK_SCREEN_ORIENTATION_PORTRAIT && orientationLock!=LOCK_SCREEN_ORIENTATION_LANDSCAPE)
			return LOCK_SCREEN_ORIENTATION_DEFAULT;
		return orientationLock;
	}
	
	/** Configure orientation lock */
	public void setOrientationLock(int orientationLock) {
		if(orientationLock!=LOCK_SCREEN_ORIENTATION_NONE && orientationLock!=LOCK_SCREEN_ORIENTATION_PORTRAIT && orientationLock!=LOCK_SCREEN_ORIENTATION_LANDSCAPE)
			throw new IllegalArgumentException("Invalid screen orientation lock number");
		prefedit.putInt(LOCK_SCREEN_ORIENTATION,orientationLock);
		prefedit.commit();
	}
	
	/** Return current control axes orientation (with sanity check) */
	public int getControlAxes() {
		int controlAxes;
		
		controlAxes=pref.getInt(CONTROL_AXES,CONTROL_AXES_DEFAULT);
		if(controlAxes!=CONTROL_AXES_VERTICAL_FREQUENCY && controlAxes!=CONTROL_AXES_HORIZONTAL_FREQUENCY)
			return CONTROL_AXES_DEFAULT;
		return controlAxes;
	}
	
	/** Configure control axes orientation */
	public void setControlAxes(int controlAxes) {
		if(controlAxes!=CONTROL_AXES_VERTICAL_FREQUENCY && controlAxes!=CONTROL_AXES_HORIZONTAL_FREQUENCY)
			throw new IllegalArgumentException("Invalid control axes orientation number");
		prefedit.putInt(CONTROL_AXES,controlAxes);
		prefedit.commit();
	}
	
	/** Return configured output waveform (with sanity check) */
	public int getOutputWaveform() {
		int waveform;
		
		waveform=pref.getInt(OUTPUT_WAVEFORM,OUTPUT_WAVEFORM_DEFAULT);
		if(waveform!=OUTPUT_WAVEFORM_SINE && waveform!=OUTPUT_WAVEFORM_TRIANGLE && waveform!=OUTPUT_WAVEFORM_SAWTOOTH && waveform!=OUTPUT_WAVEFORM_SQUARE)
			return OUTPUT_WAVEFORM_DEFAULT;
		return waveform;
	}
	
	/** Set configured output waveform */
	public void setOutputWaveform(int waveform) {
		if(waveform!=OUTPUT_WAVEFORM_SINE && waveform!=OUTPUT_WAVEFORM_TRIANGLE && waveform!=OUTPUT_WAVEFORM_SAWTOOTH && waveform!=OUTPUT_WAVEFORM_SQUARE)
			throw new IllegalArgumentException("Invalid waveform number");
		prefedit.putInt(OUTPUT_WAVEFORM,waveform);
		prefedit.commit();
	}
	
	/** Return true if configured to use system's native output sample rate */
	public boolean isUsingNativeOutputSampleRate() {
		return pref.getBoolean(OUTPUT_USE_NATIVE_SAMPLE_RATE,OUTPUT_USE_NATIVE_SAMPLE_RATE_DEFAULT);
	}
	
	/** Configure wheter to use system's native output sample rate */
	public void setUsingNativeOutputSampleRate(boolean nativeSampleRate) {
		prefedit.putBoolean(OUTPUT_USE_NATIVE_SAMPLE_RATE,nativeSampleRate);
		prefedit.commit();
	}
	
	/** Return configured non-native output sample rate (with sanity check) */
	public int getOutputSampleRate() {
		int sampleRate;
		int index;
		
		sampleRate=pref.getInt(OUTPUT_SAMPLE_RATE,OUTPUT_SAMPLE_RATE_DEFAULT);
		index=Arrays.binarySearch(OUTPUT_SAMPLE_RATES,sampleRate);
		if(index<0) return OUTPUT_SAMPLE_RATE_DEFAULT;
		return sampleRate;
	}
	
	/** Set non-native output sample rate */
	public void setOutputSampleRate(int sampleRate) {
		if(Arrays.binarySearch(OUTPUT_SAMPLE_RATES,sampleRate)<0)
			throw new IllegalArgumentException("Invalid output sample rate");
		prefedit.putInt(OUTPUT_SAMPLE_RATE,sampleRate);
		prefedit.commit();
	}
	
	/** Return current frequency control inversion status */
	public boolean isInvertingFrequencyControl() {
		return pref.getBoolean(CONTROL_INVERT_FREQUENCY,CONTROL_INVERT_FREQUENCY_DEFAULT);
	}
	
	/** Set frequency control inversion status */
	public void setInvertingFrequencyControl(boolean invertFrequency) {
		prefedit.putBoolean(CONTROL_INVERT_FREQUENCY,invertFrequency);
		prefedit.commit();
	}
	
	/** Return current amplitude control inversion status */
	public boolean isInvertingAmplitudeControl() {
		return pref.getBoolean(CONTROL_INVERT_AMPLITUDE,CONTROL_INVERT_AMPLITUDE_DEFAULT);
	}
	
	/** Set amplitude control inversion status */
	public void setInvertingAmplitudeControl(boolean invertAmplitude) {
		prefedit.putBoolean(CONTROL_INVERT_AMPLITUDE,invertAmplitude);
		prefedit.commit();
	}
	
	/** Return current dead zone/saturation zone fraction (with sanity check */
	private float getDeadSaturationZone(boolean saturation) {
		float deadZone,saturationZone;
		
		if(!pref.contains(CONTROL_DEAD_ZONE) || !pref.contains(CONTROL_SATURATION_ZONE))
			return saturation?CONTROL_SATURATION_ZONE_DEFAULT:CONTROL_DEAD_ZONE_DEFAULT;
		
		deadZone=pref.getFloat(CONTROL_DEAD_ZONE,CONTROL_DEAD_ZONE_DEFAULT);
		saturationZone=pref.getFloat(CONTROL_SATURATION_ZONE,CONTROL_SATURATION_ZONE_DEFAULT);
		if(
			Float.isNaN(deadZone) || Float.isInfinite(deadZone) || deadZone<0 || deadZone>1 ||
			Float.isNaN(saturationZone) || Float.isInfinite(saturationZone) || saturationZone<0 || saturationZone>1 ||
			deadZone+saturationZone>1
		) return saturation?CONTROL_SATURATION_ZONE_DEFAULT:CONTROL_DEAD_ZONE_DEFAULT;
		
		return saturation?saturationZone:deadZone;
	}
	
	/** Return current dead zone fraction */
	public float getDeadZone() {
		return getDeadSaturationZone(false);
	}
	
	/** Return current saturation zone fraction */
	public float getSaturationZone() {
		return getDeadSaturationZone(true);
	}
	
	/** Set dead zone and saturation zone fraction, combined must not exceed 1.0 */
	public void setDeadSaturationZone(float deadZone,float saturationZone) {
		if(Float.isNaN(deadZone) || Float.isInfinite(deadZone) || deadZone<0 || deadZone>1)
			throw new IllegalArgumentException("Invalid dead zone factor");
		else if(Float.isNaN(saturationZone) || Float.isInfinite(saturationZone) || saturationZone<0 || saturationZone>1)
			throw new IllegalArgumentException("Invalid saturation zone factor");
		else if(deadZone+saturationZone>1)
			throw new IllegalArgumentException("Dead zone combined with saturation zone must be <= 1");
		prefedit.putFloat(CONTROL_DEAD_ZONE,deadZone);
		prefedit.putFloat(CONTROL_SATURATION_ZONE,saturationZone);
		prefedit.commit();
	}
	
	/** Return current recording filename prompt preference */
	public boolean isAskingForRecordingFilename() {
		return pref.getBoolean(RECORDING_ASK_FILENAME,RECORDING_ASK_FILENAME_DEFAULT);
	}
	
	/** Set current recording filename prompt preference */
	public void setAskingForRecordingFilename(boolean askFilename) {
		prefedit.putBoolean(RECORDING_ASK_FILENAME,askFilename);
		prefedit.commit();
	}
	
	/** Return current recording directory preference */
	public boolean isUsingCustomRecordingDirectory() {
		return pref.getBoolean(RECORDING_USE_CUSTOM_DIRECTORY,RECORDING_USE_CUSTOM_DIRECTORY_DEFAULT);
	}
	
	/** Set recording directory preference */
	public void setUsingCustomRecordingDirectory(boolean customDirectory) {
		prefedit.putBoolean(RECORDING_USE_CUSTOM_DIRECTORY,customDirectory);
		prefedit.commit();
	}
	
	/** Return current custom recording directory location, null if none specified */
	public String getCustomRecordingDirectory() {
		return pref.getString(RECORDING_CUSTOM_DIRECTORY,null);
	}
	
	/** Set custom recording directory location, null to unset */
	public void setCustomRecordingDirectory(String customDirectory) {
		prefedit.putString(RECORDING_CUSTOM_DIRECTORY,customDirectory);
		prefedit.commit();
	}
	
	/** Return default recording directory location. */
	public File getDefaultRecordingDirectory() {
		return Build.VERSION.SDK_INT>=8?Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC):Environment.getExternalStorageDirectory();
	}
	
	/** Return current effective recording directory location. */
	public File getRecordingDirectory() {
		boolean usecustomdir;
		String customdir;
		
		usecustomdir=isUsingCustomRecordingDirectory();
		customdir=getCustomRecordingDirectory();
		if(usecustomdir && customdir!=null) {
			return new File(customdir);
		} else {
			return getDefaultRecordingDirectory();
		}
	}
}
