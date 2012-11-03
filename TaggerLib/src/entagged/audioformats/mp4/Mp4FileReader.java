/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
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
package entagged.audioformats.mp4;

import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.EncodingInfo;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.AudioFileReader;
import entagged.audioformats.mp4.util.Mp4InfoReader;
import entagged.audioformats.mp4.util.Mp4TagReader;

public class Mp4FileReader extends AudioFileReader {

	private Mp4InfoReader ir = new Mp4InfoReader();
	private Mp4TagReader tr = new Mp4TagReader();

	protected EncodingInfo getEncodingInfo(final RandomAccessFile raf)
			throws CannotReadException, IOException {
		return ir.read(raf);
	}

	protected Tag getTag(final RandomAccessFile raf)
			throws CannotReadException, IOException {
		return tr.read(raf);
	}

}
