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
package entagged.audioformats.mp4.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import entagged.audioformats.EncodingInfo;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.mp4.util.atoms.Atom;
import entagged.audioformats.mp4.util.atoms.AudioSampleEntry;
import entagged.audioformats.mp4.util.atoms.ESDS;
import entagged.audioformats.mp4.util.atoms.MOOV;
import entagged.audioformats.mp4.util.atoms.STSD;
import entagged.audioformats.mp4.util.atoms.TRAK;

public class Mp4InfoReader {

	public EncodingInfo read(final RandomAccessFile raf)
			throws CannotReadException, IOException {
		final EncodingInfo info = new EncodingInfo();
		raf.seek(0);
		for (Atom atom = Atom.nextAtom(raf); atom != null; atom = Atom
				.nextAtom(raf)) {
			if (atom instanceof MOOV) {
				atom.parse(raf);
				final MOOV movie = (MOOV) atom;
				info.setLength((int) movie.mvhd().lengthInSeconds());
				final List<TRAK> tracks = movie.traks();
				boolean foundAudio = false;
				for (TRAK trak : tracks) {
					if (trak.mdia().hdlr().audio()) {
						if (!foundAudio) {
							foundAudio = true;
							populate(info, trak.mdia().minf().stbl().stsd(),
									raf.length());
						} else {
							throw new CannotReadException(
									"Found more than one audio track,"
											+ " not sure which one to use");
						}
					}
				}
				if (!foundAudio) {
					throw new CannotReadException("No audio tracks found");
				}
				break;
			}
		}
		return info;
	}

	private static void populate(final EncodingInfo info, final STSD stsd,
			final long filesize) {
		assert info != null;
		assert stsd != null;
		info.setEncrypted(stsd.drmProtected());
		info.setLossless(stsd.lossless());
		if (stsd.lossless()) {
			info.setVbr(true);
			if (stsd.alac().bitrate() > 0) {
				info.setBitrate(stsd.alac().bitrate());
			} else {
				// Estimate: I'm not sure how to actually calculate this but it
				// should be close enough. This should only happen for some
				// non-iTunes encoders -- all the files I've tested with using
				// iTunes had a bitrate, all the ones with Max 0.7.1 had a value
				// of 0
				info.setBitrate((int) ((filesize / info.getLength()) * 8));
			}
		} else {
			final ESDS esds = stsd.drmProtected() ? stsd.drms().esds() : stsd
					.mp4a().esds();
			info.setVbr(esds.averageBitrate() != esds.maxBitrate());
			info.setBitrate(esds.averageBitrate() / 1000);
		}
		final AudioSampleEntry ase = stsd.drmProtected() ? stsd.drms() : stsd
				.lossless() ? stsd.alac() : stsd.mp4a();
		info.setChannelNumber(ase.channels());
		info.setSamplingRate(ase.sampleRate());
	}

}
