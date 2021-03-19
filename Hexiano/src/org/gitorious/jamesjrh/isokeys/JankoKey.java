/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *                                                                         *
 *   Hexiano™, an isomorphic musical keyboard for Android                  *
 *   Copyright © 2012 James Haigh                                          *
 *   Copyright © 2011 David A. Randolph                                    *
 *                                                                         *
 *   FILE: JankoKey.java                                                   *
 *                                                                         *
 *   This file is part of Hexiano, an open-source project hosted at:       *
 *   https://gitorious.org/hexiano                                         *
 *                                                                         *
 *   Hexiano is free software: you can redistribute it and/or              *
 *   modify it under the terms of the GNU General Public License           *
 *   as published by the Free Software Foundation, either version          *
 *   3 of the License, or (at your option) any later version.              *
 *                                                                         *
 *   Hexiano is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with Hexiano.  If not, see <http://www.gnu.org/licenses/>.      *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.gitorious.jamesjrh.isokeys;

import android.content.Context;
import android.graphics.Paint;

public class JankoKey extends HexKey
{
	private int mOctaveGroupNumber;
	
	public JankoKey(Context context, int radius, Point center,
			int midiNoteNumber, Instrument instrument, int octaveGroupNumber)
	{
		super(context, radius, center, midiNoteNumber, instrument);

        mOctaveGroupNumber = octaveGroupNumber;
        
		mPaint.setColor(getColor());
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
       
		mOverlayPaint.setColor(mOutlineColor);
        mOverlayPaint.setAntiAlias(true);
        mOverlayPaint.setStyle(Paint.Style.STROKE);
        mOverlayPaint.setStrokeWidth(2);
        
		mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(20);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        
		mBlankPaint.setColor(mBlankColor);
        mBlankPaint.setStyle(Paint.Style.FILL);
	}

	protected void getPrefs()
	{
		mKeyOrientation = mPrefs.getString("jankoKeyOrientation", null);
	}

	private boolean inOddOctave()
	{
		if (mOctaveGroupNumber % 2 == 0)
		{
			return false;
		}
		
		return true;
	}
	
	public int getColor()
	{
		String sharpName = mNote.getSharpName();
		int color = mWhiteColor;
		if (sharpName.contains("#"))
		{	
			color = mBlackColor;
			if (inOddOctave())
			{
				color = mBlackHighlightColor;
			}
		}
		else if (inOddOctave())
		{
			color = mWhiteHighlightColor;
		}
		
		return color;
	}
}
