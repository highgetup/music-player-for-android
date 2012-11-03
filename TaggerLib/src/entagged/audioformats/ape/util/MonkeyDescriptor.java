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

import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import entagged.audioformats.exceptions.CannotReadException;

/**
 * The Monkey's Audio file format (in my opinion, completely ridiculous in this
 * day and age) is written out as raw memory contents of data structs from
 * Windows, so it is little endian and potentially byte-aligned depending on the
 * structure, word size of the computer it's built on, and the compiler.
 * ANNOYING and non-portable, but I guess it makes the original Windows source
 * code look nice.
 * 
 * As a result, this code (as well as the original MAC code, I might add) may or
 * may not break unexpectedly with newer machines having 64-bit words doing the
 * encoding -- and I'm not sure how to detect something like this if it happens :(
 */
public class MonkeyDescriptor {

	private interface SubDescriptor {

		static final int COMPRESSION_LEVEL_FAST = 1000;
		static final int COMPRESSION_LEVEL_NORMAL = 2000;
		static final int COMPRESSION_LEVEL_HIGH = 3000;
		static final int COMPRESSION_LEVEL_EXTRA_HIGH = 4000;
		static final int COMPRESSION_LEVEL_INSANE = 5000;

		static final int MAC_FORMAT_FLAG_8_BIT = 1;
		static final int MAC_FORMAT_FLAG_CRC = 2;
		static final int MAC_FORMAT_FLAG_HAS_PEAK_LEVEL = 4;
		static final int MAC_FORMAT_FLAG_24_BIT = 8;
		static final int MAC_FORMAT_FLAG_HAS_SEEK_ELEMENTS = 16;
		static final int MAC_FORMAT_FLAG_CREATE_WAV_HEADER = 32;

		int lengthInSeconds();

		int channels();

		int sampleRate();

		int bitrate();

		int compressionLevel();

	}

	private static class Version3980To3990SubDescriptor implements
			SubDescriptor {

		private static class ApeHeader {

			private final int nCompressionLevel;
			private final int nFormatFlags;
			private final long nBlocksPerFrame;
			private final long nFinalFrameBlocks;
			private final long nTotalFrames;
			private final int nBitsPerSample;
			private final int nChannels;
			private final long nSampleRate;

			private ApeHeader(final RandomAccessFile raf) throws IOException {
				nCompressionLevel = uint16_le(raf);
				nFormatFlags = uint16_le(raf);
				nBlocksPerFrame = uint32_le(raf);
				nFinalFrameBlocks = uint32_le(raf);
				nTotalFrames = uint32_le(raf);
				nBitsPerSample = uint16_le(raf);
				nChannels = uint16_le(raf);
				nSampleRate = uint32_le(raf);
			}

		}

		private final long nDescriptorBytes;
		private final long nHeaderBytes;
		private final long nSeekTableBytes;
		private final long nHeaderDataBytes;
		private final long nAPEFrameDataBytes;
		private final long nAPEFrameDataBytesHigh;
		private final long nTerminatingDataBytes;
		private final byte[] cFileMD5;

		private final ApeHeader header;

		private final long nAPETotalBytes;

		private Version3980To3990SubDescriptor(final RandomAccessFile raf)
				throws IOException {
			// EVIL byte-alignment below, should be 6 not 8
			final long descriptorBeginning = raf.getFilePointer() - 8;
			nDescriptorBytes = uint32_le(raf);
			nHeaderBytes = uint32_le(raf);
			nSeekTableBytes = uint32_le(raf);
			nHeaderDataBytes = uint32_le(raf);
			nAPEFrameDataBytes = uint32_le(raf);
			nAPEFrameDataBytesHigh = uint32_le(raf);
			nTerminatingDataBytes = uint32_le(raf);
			cFileMD5 = new byte[16];
			raf.readFully(cFileMD5);
			raf.seek(descriptorBeginning + nDescriptorBytes);
			header = new ApeHeader(raf);
			nAPETotalBytes = raf.length();
		}

		public int bitrate() {
			return lengthInSeconds() <= 0 ? 0
					: (int) (nAPETotalBytes * 8 / (lengthInSeconds() * 1000));
		}

		public int channels() {
			return header.nChannels;
		}

		public int lengthInSeconds() {
			return (int) (totalBlocks() / header.nSampleRate);
		}

		public int sampleRate() {
			return (int) header.nSampleRate;
		}

		public int compressionLevel() {
			return header.nCompressionLevel;
		}

		private int totalBlocks() {
			return header.nTotalFrames == 0 ? 0
					: (int) (((header.nTotalFrames - 1) * header.nBlocksPerFrame) + header.nFinalFrameBlocks);
		}

	}

	private static class VersionPre3980SubDescriptor implements SubDescriptor {

		private final int version;

		private final int nCompressionLevel;
		private final int nFormatFlags;
		private final int nChannels;

		private final long nSampleRate;
		private final long nHeaderBytes;
		private final long nTerminatingBytes;
		private final long nTotalFrames;
		private final long nFinalFrameBlocks;

		private final long nAPETotalBytes;

		private VersionPre3980SubDescriptor(final int version,
				final RandomAccessFile raf) throws IOException,
				CannotReadException {
			this.version = version;
			nCompressionLevel = uint16_le(raf);
			nFormatFlags = uint16_le(raf);
			nChannels = uint16_le(raf);
			raf.skipBytes(2); // EVIL byte-alignment!!
			nSampleRate = uint32_le(raf);
			nHeaderBytes = uint32_le(raf);
			nTerminatingBytes = uint32_le(raf);
			nTotalFrames = uint32_le(raf);
			if (nTotalFrames == 0) {
				throw new CannotReadException(
						"No frames in file, corrupted or non-finalized");
			}
			nFinalFrameBlocks = uint32_le(raf);
			nAPETotalBytes = raf.length();
		}

		public int bitrate() {
			return lengthInSeconds() <= 0 ? 0
					: (int) ((nAPETotalBytes * 8) / (lengthInSeconds() * 1000));
		}

		public int channels() {
			return nChannels;
		}

		public int lengthInSeconds() {
			return (int) (totalBlocks() / nSampleRate);
		}

		public int sampleRate() {
			return (int) nSampleRate;
		}

		public int compressionLevel() {
			return nCompressionLevel;
		}

		private int blocksPerFrame() {
			if (version >= 3950) {
				return 73728 * 4;
			} else if (version >= 3900
					|| (version >= 3800 && nCompressionLevel == COMPRESSION_LEVEL_EXTRA_HIGH)) {
				return 73728;
			} else {
				return 9216;
			}
		}

		private int totalBlocks() {
			return nTotalFrames == 0 ? 0
					: (int) (((nTotalFrames - 1) * blocksPerFrame()) + nFinalFrameBlocks);
		}

	}

	private final int version;
	private final SubDescriptor descriptor;

	public MonkeyDescriptor(final RandomAccessFile monkeyFile)
			throws IOException, CannotReadException {
		final byte[] buf4 = { 0x00, 0x00, 0x00, 0x00 };
		monkeyFile.readFully(buf4);
		if (!"MAC ".equals(new String(buf4))) {
			throw new CannotReadException(
					"Monkey's Audio file should start with 'MAC '");
		}
		version = uint16_le(monkeyFile);
		monkeyFile.skipBytes(2); // EVIL byte-alignment!!
		if (version > 3990) {
			throw new CannotReadException(
					"Monkey's Audio versions greater than 3990 are not supported, your version is: "
							+ version);
		} else if (version >= 3980) {
			descriptor = new Version3980To3990SubDescriptor(monkeyFile);
		} else {
			descriptor = new VersionPre3980SubDescriptor(version, monkeyFile);
		}
	}

	public int version() {
		return version;
	}

	public int lengthInSeconds() {
		return descriptor.lengthInSeconds();
	}

	public int channels() {
		return descriptor.channels();
	}

	public int sampleRate() {
		return descriptor.sampleRate();
	}

	public int bitrate() {
		return descriptor.bitrate();
	}

	public String compressionLevelAsString() {
		final int compressionLevel = descriptor.compressionLevel();
		if (compressionLevel == SubDescriptor.COMPRESSION_LEVEL_NORMAL) {
			return "normal";
		} else if (compressionLevel == SubDescriptor.COMPRESSION_LEVEL_FAST) {
			return "fast";
		} else if (compressionLevel == SubDescriptor.COMPRESSION_LEVEL_HIGH) {
			return "high";
		} else if (compressionLevel == SubDescriptor.COMPRESSION_LEVEL_EXTRA_HIGH) {
			return "extra high";
		} else if (compressionLevel == SubDescriptor.COMPRESSION_LEVEL_INSANE) {
			return "insane";
		} else {
			return "unknown";
		}
	}

	private static final int uint16_le(final DataInput di) throws IOException {
		final byte[] buf4 = { 0x00, 0x00, 0x00, 0x00 };
		di.readFully(buf4, 0, 2);
		return ByteBuffer.wrap(buf4).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	private static final long uint32_le(final DataInput di) throws IOException {
		final byte[] buf8 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		di.readFully(buf8, 0, 4);
		return ByteBuffer.wrap(buf8).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

}
