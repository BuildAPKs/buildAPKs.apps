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

/** An interface for listening to recording status */
public interface RecordingListener {
	/** Called when there's another complete second of audio is recorded, with current duration in seconds, return true if the event is consumed. */
	public boolean recordingProgressed(int duration);
	
	/** Called after the recording failed (and terminated) with the error causing it, return true if the event is consumed. */
	public boolean recordingFailed(Throwable t);
}
