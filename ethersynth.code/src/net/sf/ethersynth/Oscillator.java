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

/** Sine wave oscillator, with Little-endian 16-bit PCM Mono output */ 
public class Oscillator {
	private int sampleRate;
	private int interpolationPoints;
	private int position=-1;
	private boolean enabled;
	private float targetFrequency;
	private int interpolationLeft;
	private float targetAmplitude;
	private float frequency;
	private float amplitude;
	private float phase;
	private Waveform waveform;
	
	/** Constructor, initializes specified waveform */
	public Oscillator(int samplerate,int waveform) {
		setSampleRate(samplerate);
		setWaveform(waveform);
	}
	
	/** Set oscillator's output sample rate, reset phase, position, and interpolation */
	public void setSampleRate(int sampleRate) {
		
		synchronized(this) {
			if(sampleRate<=0) throw new IllegalArgumentException("Invalid output sample rate");
			this.sampleRate=sampleRate;
			position=-1;
			phase=0;
			frequency=targetFrequency;
			interpolationLeft=0;
			amplitude=targetAmplitude;
			interpolationPoints=sampleRate/20;
		}
	}
	
	/** Return current oscillator's output sample rate */
	public int getSampleRate() {
		synchronized(this) {
			return sampleRate;
		}
	}
	
	/** Set oscillator's output waveform */
	public void setWaveform(int waveform) {
		synchronized(this) {
			switch(waveform) {
				case ConfigOptions.OUTPUT_WAVEFORM_SINE: this.waveform=new SineWaveform(); break;
				case ConfigOptions.OUTPUT_WAVEFORM_TRIANGLE: this.waveform=new TriangleWaveform(); break;
				case ConfigOptions.OUTPUT_WAVEFORM_SAWTOOTH: this.waveform=new SawtoothWaveform(); break;
				case ConfigOptions.OUTPUT_WAVEFORM_SQUARE: this.waveform=new SquareWaveform(); break;
				default: throw new IllegalArgumentException("Invalid waveform number");
			}
		}
	}
	
	/** Turn oscillator on, with specified frequency and amplitude */
	public void enable(float frequency,float amplitude) {
		synchronized(this) {
			if(!this.enabled) {
				// special case: don't interpolate anything when switching from OFF to ON
				this.interpolationLeft=1;
			} else {
				this.interpolationLeft=interpolationPoints;
			}
			this.targetFrequency=frequency;
			this.targetAmplitude=amplitude;
			this.enabled=true;
		}
	}
	
	/** Turn oscillator off, let the sound die out, then reset position and phase */
	public void disable() {
		synchronized(this) {
			interpolationLeft=interpolationPoints;
			targetAmplitude=0;
			enabled=false;
		}
	}
	
	/** Read (synthesize) audio data */
	public void read(byte[] buffer,int offset,int length) {
		float sample;
		int samples;
		int i;
		boolean usephase;
		Waveform waveform;
		
		if(length%2 != 0) {
			throw new IllegalArgumentException("Oscillator reading block must be aligned to frame boundary");
		}
		
		synchronized(this) {
			waveform=this.waveform;
			samples=length/2;
			
			// Generate frequency: use position, reset phase to follow it
			for(i=0;i<samples;i++) {
				usephase=false;
				if(interpolationLeft>0) {
					if(interpolationLeft<=1) {
						frequency=targetFrequency;
						amplitude=targetAmplitude;
					} else {
						frequency+=(targetFrequency-frequency)/interpolationLeft;
						amplitude+=(targetAmplitude-amplitude)/interpolationLeft;
					}
					interpolationLeft--;
					
					// Frequency changed: reset position, use phase to reconstruct it
					usephase=true;
					
					if(amplitude==0) {
						// Oscillator turned off, reset position and phase
						position=-1;
						phase=0;
					}
				}
				
				if(position>=Integer.MAX_VALUE) {
					// integer near overflow: reset position, use phase to reconstruct it
					usephase=true;
				}
				
				if(usephase) {
					// Shift on phase in this sample, then reconstruct next position from shifted phase
					usephase=false;
					phase+=((double)1*frequency/sampleRate);
					phase%=1;
					position=(int)Math.round(phase*sampleRate/frequency);
				} else {
					// Generate according to shifted position (normal case)
					position++;
					phase=(float)((double)position*frequency/sampleRate%1);
				}
				sample=waveform.getAmplitude(phase)*amplitude;
				buffer[offset + i*2]=(byte)((int)(32767*sample)&0xFF);
				buffer[offset + i*2 +1]=(byte)(((int)(32767*sample)>>8)&0xFF);
			}
		}
	}
}
