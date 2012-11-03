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

import entagged.audioformats.generic.Utils;

public class MonkeyHeader {

	byte[] b;

	public MonkeyHeader(byte[] b) {
		this.b = b;
	}

	public int getCompressionLevel() {
		return Utils.getNumber(b, 0, 1);
	}

	public int getFormatFlags() {
		return Utils.getNumber(b, 2, 3);
	}

	public long getBlocksPerFrame() {
		return Utils.getLongNumber(b, 4, 7);
	}

	public long getFinalFrameBlocks() {
		return Utils.getLongNumber(b, 8, 11);
	}

	public long getTotalFrames() {
		return Utils.getLongNumber(b, 12, 15);
	}

	public int getLength() {
		return (int) (getBlocksPerFrame() * (getTotalFrames() - 1.0) + getFinalFrameBlocks())
				/ getSamplingRate();
	}

	public float getPreciseLength() {
		return (float) ((double) (getBlocksPerFrame() * (getTotalFrames() - 1) + getFinalFrameBlocks()) / (double) getSamplingRate());
	}

	public int getBitsPerSample() {
		return Utils.getNumber(b, 16, 17);
	}

	public int getChannelNumber() {
		return Utils.getNumber(b, 18, 19);
	}

	public int getSamplingRate() {
		return Utils.getNumber(b, 20, 23);

	}
}
