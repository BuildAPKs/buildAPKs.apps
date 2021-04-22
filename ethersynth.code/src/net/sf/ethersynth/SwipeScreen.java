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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.MotionEvent;

/** Handle user's finger touch */
public class SwipeScreen extends View implements Runnable {
	private Oscillator osc;
	private float highestFrequency;
	private float lowestFrequency;
	private volatile boolean showBackground;
	private volatile boolean showTouchPoint;
	private final float TOP_AMPLITUDE=1;
	private final float BOTTOM_AMPLITUDE=0;
	private float pointerX=Float.NaN;
	private float pointerY=Float.NaN;
	private Paint stroke;
	private Bitmap background;
	private boolean horizontalOrientation;
	private boolean invertFrequency;
	private boolean invertAmplitude;
	private float deadZone;
	private float saturationZone;
	
	/** Constructor, pass along activity Context */
	public SwipeScreen(Context context,Oscillator osc,float lowestFrequency,float highestFrequency,boolean showBackground,boolean showTouchPoint,boolean horizontalOrientation,boolean invertFrequency,boolean invertAmplitude,float deadZone,float saturationZone) {
		super(context);
		this.osc=osc;
		this.lowestFrequency=lowestFrequency;
		this.highestFrequency=highestFrequency;
		this.showBackground=showBackground;
		this.showTouchPoint=showTouchPoint;
		this.horizontalOrientation=horizontalOrientation;
		this.invertFrequency=invertFrequency;
		this.invertAmplitude=invertAmplitude;
		this.deadZone=deadZone;
		this.saturationZone=saturationZone;
		stroke=new Paint();
		stroke.setColor(Color.GRAY);
		stroke.setStrokeWidth(1);
	}
	
	/** Set lowest/highest frequency range on the screen */
	public void setFrequencyRange(float lowestFrequency,float highestFrequency) {
		synchronized(this) {
			this.lowestFrequency=lowestFrequency;
			this.highestFrequency=highestFrequency;
		}
	}
	
	/** Set whether to show rainbow background on the screen */
	public void setShowingBackground(boolean showBackground) {
		this.showBackground=showBackground;
		post(this);
	}
	
	/** Set whether to show touch location on the screen */
	public void setShowingTouchPoint(boolean showTouchPoint) {
		this.showTouchPoint=showTouchPoint;
		post(this);
	}
	
	/** Set whether to use horizontal control orientation */
	public void setHorizontalOrientation(boolean horizontalOrientation) {
		synchronized(this) {
			this.horizontalOrientation=horizontalOrientation;
			this.background=null;
		}
		post(this);
	}
	
	/** Set whether to use inverted frequency control */
	public void setInvertingFrequencyControl(boolean invertFrequency) {
		synchronized(this) {
			this.invertFrequency=invertFrequency;
			this.background=null;
		}
		post(this);
	}
	
	/** Set whether to use inverted amplitude control */
	public void setInvertingAmplitudeControl(boolean invertAmplitude) {
		synchronized(this) {
			this.invertAmplitude=invertAmplitude;
			this.background=null;
		}
		post(this);
	}
	
	/** Set dead zone and saturation zone */
	public void setDeadSaturationZone(float deadZone,float saturationZone) {
		synchronized(this) {
			this.deadZone=deadZone;
			this.saturationZone=saturationZone;
			this.background=null;
		}
		post(this);
	}
	
	/** Trigger screen drawing */
	@Override
	public void run() {
		invalidate();
	}
	
	/** Draw the screen. */
	@Override
	public void onDraw(Canvas canvas) {
		int width;
		int height;
		int x,y;
		byte color[];
		int rgb[];
		boolean horizontalOrientation;
		Bitmap background;
		float ratio;
		boolean invertFrequency,invertAmplitude;
		
		width=getWidth();
		height=getHeight();
		
		if(showBackground) {
			synchronized(this) {
				background=this.background;
				horizontalOrientation=this.horizontalOrientation;
				invertFrequency=this.invertFrequency;
				invertAmplitude=this.invertAmplitude;
			}
			if(background==null || background.getWidth()!=width || background.getHeight()!=height) {
				width=getWidth();
				height=getHeight();
				rgb=new int[width*height];
				color=new byte[3];
				if(horizontalOrientation) {
					for(y=0;y<height;y++) {
						for(x=0;x<width;x++) {
							ratio=invertFrequency?1.0F-(float)x/(width-1):(float)x/(width-1);
							rainbowColor(color,1.0F - ratio);
							ratio=invertAmplitude?1.0F-(float)(height-1-y)/(height-1):(float)(height-1-y)/(height-1);
							if(saturationZone>0 && ratio>=1.0F-saturationZone) ratio=1;
							else if(deadZone>0 && ratio<deadZone) ratio=0;
							else ratio=(ratio-deadZone)/(1.0F-saturationZone-deadZone);
							rgb[y*width + x]=0xFF000000 | (int)((color[0]&0xFF) * ratio)<<16 | (int)((color[1]&0xFF) * ratio)<<8 | (int)((color[2]&0xFF) * ratio);
						}
					}
				} else {
					for(y=0;y<height;y++) {
						ratio=invertFrequency?1.0F-(float)y/(height-1):(float)y/(height-1);
						rainbowColor(color,1.0F - ratio);
						for(x=0;x<width;x++) {
							ratio=invertAmplitude?1.0F-(float)(width-1-x)/(width-1):(float)(width-1-x)/(width-1);
							if(saturationZone>0 && ratio>=1.0F-saturationZone) ratio=1;
							else if(deadZone>0 && ratio<deadZone) ratio=0;
							else ratio=(ratio-deadZone)/(1.0F-saturationZone-deadZone);
							rgb[y*width + x]=0xFF000000 | (int)((color[0]&0xFF) * ratio)<<16 | (int)((color[1]&0xFF) * ratio)<<8 | (int)((color[2]&0xFF) * ratio);
						}
					}
				}
				background=Bitmap.createBitmap(rgb,width,height,Bitmap.Config.ARGB_8888);
				background.setDensity(Bitmap.DENSITY_NONE);
				synchronized(this) {
					this.background=background;
				}
			}
			canvas.drawBitmap(background,0,0,null);
		} else {
			synchronized(this) {
				this.background=null;
			}
			super.onDraw(canvas);
		}
		
		if(showTouchPoint && !Float.isNaN(pointerX) && !Float.isNaN(pointerY)) {
			canvas.drawLine(0,pointerY,width,pointerY,stroke);
			canvas.drawLine(pointerX,0,pointerX,height,stroke);
		}
	}
	
	/** Calculate rainbow color (range 0 to just before 1) */
	public static void rainbowColor(byte[] rgb,float position) {
		if(position>=0 && position<(float)1/6) {
			rgb[0]=(byte)255;
			rgb[1]=(byte)(255*Math.sin((position*6)*Math.PI/2));
			rgb[2]=0;
		} else if(position>=(float)1/6 && position<(float)2/6) {
			rgb[0]=(byte)(255*Math.sin((2 - position*6)*Math.PI/2));
			rgb[1]=(byte)255;
			rgb[2]=0;
		} else if(position>=(float)2/6 && position<(float)3/6) {
			rgb[0]=0;
			rgb[1]=(byte)255;
			rgb[2]=(byte)(255*Math.sin((position*6 - 2)*Math.PI/2));;
		} else if(position>=(float)3/6 && position<(float)4/6) {
			rgb[0]=0;
			rgb[1]=(byte)(255*Math.sin((4 - position*6)*Math.PI/2));
			rgb[2]=(byte)255;
		} else if(position>=(float)4/6 && position<(float)5/6) {
			rgb[0]=(byte)(255*Math.sin((position*6 - 4)*Math.PI/2));
			rgb[1]=0;
			rgb[2]=(byte)255;
		} else if(position>=(float)5/6 && position<1) {
			rgb[0]=(byte)255;
			rgb[1]=0;
			rgb[2]=(byte)(255*Math.sin((6 - position*6)*Math.PI/2));
		} else {
			rgb[0]=0x00;
			rgb[1]=0x00;
			rgb[2]=0x00;
		}
	}
	
	/** Handle touch events */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float freqratio,ampratio,freq;
		int width,height;
		float highestFrequency,lowestFrequency;
		boolean invertFrequency,invertAmplitude;
		float deadZone,saturationZone;
		
		if(event.getAction()==event.ACTION_DOWN || event.getAction()==event.ACTION_MOVE) {
			synchronized(this) {
				invertFrequency=this.invertFrequency;
				invertAmplitude=this.invertAmplitude;
				deadZone=this.deadZone;
				saturationZone=this.saturationZone;
				highestFrequency=this.highestFrequency;
				lowestFrequency=this.lowestFrequency;
			}
			width=getWidth();
			height=getHeight();
			pointerX=event.getX();
			pointerY=event.getY();
			if(pointerX<0) pointerX=0;
			if(pointerY<0) pointerY=0;
			if(pointerX>width-1) pointerX=width-1;
			if(pointerY>height-1) pointerY=height-1;
			if(horizontalOrientation) {
				freqratio=((float)pointerX)/(width-1);
				ampratio=((float)height-1-pointerY)/(height-1);
			} else {
				freqratio=((float)height-1-pointerY)/(height-1);
				ampratio=((float)width-1-pointerX)/(width-1);
			}
			if(invertFrequency) freqratio=1-freqratio;
			if(invertAmplitude) ampratio=1-ampratio;
			
			if(saturationZone>0 && ampratio>=1.0F-saturationZone) ampratio=1;
			else if(deadZone>0 && ampratio<deadZone) ampratio=0;
			else ampratio=(ampratio-deadZone)/(1.0F-saturationZone-deadZone);
			
			freqratio=(float)((Math.pow(2.0,freqratio*Math.log((double)highestFrequency/lowestFrequency)/Math.log(2.0))-1.0)/((double)highestFrequency/lowestFrequency-1.0));
			ampratio=(float)((Math.exp(ampratio)-1)/(Math.E-1));
			freq=freqratio*highestFrequency + (1-freqratio)*lowestFrequency;
			
			osc.enable(freq,ampratio);
			invalidate();
		} else if(event.getAction()==event.ACTION_UP) {
			osc.disable();
			pointerX=Float.NaN;
			pointerY=Float.NaN;
			invalidate();
		}
		return true;
	}
}
