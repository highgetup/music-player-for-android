/*
 * Entagged Audio Tag library
 * Copyright (c) 2004-2005 Christian Laireiter <liree@web.de>
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
package entagged.audioformats.asf;

import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.EncodingInfo;
import entagged.audioformats.Tag;
import entagged.audioformats.asf.data.AsfHeader;
import entagged.audioformats.asf.data.AudioStreamChunk;
import entagged.audioformats.asf.io.AsfHeaderReader;
import entagged.audioformats.asf.util.TagConverter;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.AudioFileReader;

/**
 * This reader can read asf files containing any content (stream type). <br>
 * 
 * @author Christian Laireiter
 */
public class AsfFileReader extends AudioFileReader {

	/**
	 * (overridden)
	 * 
	 * @see entagged.audioformats.generic.AudioFileReader#getEncodingInfo(java.io.RandomAccessFile)
	 */
	protected EncodingInfo getEncodingInfo(RandomAccessFile raf)
			throws CannotReadException, IOException {
		raf.seek(0);
		EncodingInfo info = new EncodingInfo();
		try {
			AsfHeader header = AsfHeaderReader.readHeader(raf);
			if (header == null) {
				throw new CannotReadException(
						"Some values must have been "
								+ "incorrect for interpretation as asf with wma content.");
			}
			info.setBitrate(header.getAudioStreamChunk().getKbps());
			info.setChannelNumber((int) header.getAudioStreamChunk()
					.getChannelCount());
			info.setEncodingType("ASF (audio): "
					+ header.getAudioStreamChunk().getCodecDescription());

			info.setLossless(header.getAudioStreamChunk().getCompressionFormat() == AudioStreamChunk.WMA_LOSSLESS);

			info.setPreciseLength(header.getFileHeader().getPreciseDuration());
			info.setSamplingRate((int) header.getAudioStreamChunk()
					.getSamplingRate());

			if(header.getEncryptionChunk() != null) {
				if(header.getEncryptionChunk().getProtectionType().toUpperCase().startsWith("DRM"))
					info.setEncrypted(true);
            } else {
				info.setEncrypted(false);
			}
		} catch (Exception e) {
			if (e instanceof IOException)
				throw (IOException) e;
			else if (e instanceof CannotReadException)
				throw (CannotReadException) e;
			else {
				throw new CannotReadException("Failed to read. Cause: "
						+ e.getMessage());
			}
		}
		return info;
	}

	/**
	 * (overridden)
	 * 
	 * @see entagged.audioformats.generic.AudioFileReader#getTag(java.io.RandomAccessFile)
	 */
	protected Tag getTag(RandomAccessFile raf) throws CannotReadException,
			IOException {
		raf.seek(0);
		Tag tag;
		try {
			AsfHeader header = AsfHeaderReader.readHeader(raf);
			if (header == null) {
				throw new CannotReadException(
						"Some values must have been "
								+ "incorrect for interpretation as asf with wma content.");
			}

			tag = TagConverter.createTagOf(header);

		} catch (Exception e) {
			if (e instanceof IOException)
				throw (IOException) e;
			else if (e instanceof CannotReadException)
				throw (CannotReadException) e;
			else {
				throw new CannotReadException("Failed to read. Cause: "
						+ e.getMessage());
			}
		}
		return tag;
	}

}