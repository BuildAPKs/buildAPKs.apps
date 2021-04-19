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
import java.io.RandomAccessFile;

/** Dump audio to Little-endian PCM 44100 Hz 16-bit Mono RIFF WAVE file */
public class AudioDump {
	private int sampleRate;
	private volatile long dataLength;
	private File file;
	private RandomAccessFile out;
	
	/** Constructor, open file and write necessary headers */
	public AudioDump(File file,int sampleRate) throws IOException {
		this.file=file;
		this.sampleRate=sampleRate;
		out=new RandomAccessFile(file,"rw");
		out.setLength(0);
		writeHeader();
	}
	
	/** Return current output File */
	public File getOutputFile() {
		return file;
	}
	
	/** Return current output duration, in seconds. */
	public int getOutputDuration() {
		return (int)(dataLength/sampleRate/2);
	}
	
	/** Write minimal RIFF WAVE header to output, according to currently-available audio length */
	private void writeHeader() throws IOException {
		out.write("RIFF".getBytes("us-ascii"));
		writeDWORD(4+8+16+8+dataLength);
		out.write("WAVE".getBytes("us-ascii"));
		
		out.write("fmt ".getBytes("us-ascii"));
		writeDWORD(16);
		writeWORD(1);             // sample format 1 (Linear PCM)
		writeWORD(1);             // 1 channel
		writeDWORD(sampleRate);   // sampling rate
		writeDWORD(sampleRate*2); // sampling rate *2 bytes/sec
		writeWORD(2);             // 2-byte frame
		writeWORD(16);            // 16-bit depth
		
		out.write("data".getBytes("us-ascii"));
		writeDWORD(dataLength);
	}
	
	/** Write unsigned 32-bit little-endian integer to output */
	private void writeDWORD(long dword) throws IOException {
		out.write((int)(dword&0xFF));
		out.write((int)((dword>>8)&0xFF));
		out.write((int)((dword>>16)&0xFF));
		out.write((int)((dword>>24)&0xFF));
	}
	
	/** Write unsigned 16-bit little-endian integer to output */
	private void writeWORD(int word) throws IOException {
		out.write(word&0xFF);
		out.write((word>>8)&0xFF);
	}
	
	/** Write data to output file. return true when number of duration (in seconds) is changed. */
	public boolean write(byte buf[],int offset,int length) throws IOException {
		long lastduration;
		
		if(length%2 != 0) {
			throw new IllegalArgumentException("WAVE writing block must be aligned to frame boundary");
		}
		out.write(buf,offset,length);
		lastduration=dataLength/sampleRate/2;
		dataLength+=length;
		return (dataLength/sampleRate/2)>lastduration;
	}
	
	/** Flush metadata, and close output file */
	public void close() throws IOException {
		// NOTE: since samples use 2 byte/frame, no need to pad the data chunk
		out.seek(0);
		writeHeader();
		out.close();
	}
}
