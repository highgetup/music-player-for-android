/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package entagged.audioformats.ape.util;

import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.EncodingInfo;
import entagged.audioformats.exceptions.CannotReadException;

public class MonkeyInfoReader {

	public EncodingInfo read(RandomAccessFile raf) throws CannotReadException,
			IOException {
		if (raf.length() == 0) {
			throw new CannotReadException("File is empty");
		}
		raf.seek(0);
		MonkeyDescriptor md = new MonkeyDescriptor(raf);

		EncodingInfo info = new EncodingInfo();
		info.setLength(md.lengthInSeconds());
		info.setChannelNumber(md.channels());
		info.setSamplingRate(md.sampleRate());
		info.setBitrate(md.bitrate());
		info.setEncodingType("Monkey Audio v"
				+ (((double) md.version()) / 1000) + ", compression level "
				+ md.compressionLevelAsString());
		info.setLossless(true);
		info.setVbr(true);
		return info;
	}

}
