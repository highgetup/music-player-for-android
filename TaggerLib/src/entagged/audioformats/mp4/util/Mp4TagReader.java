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
import java.nio.ByteBuffer;

import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.GenericTag;
import entagged.audioformats.mp4.util.atoms.AppleAnnotation;
import entagged.audioformats.mp4.util.atoms.Atom;
import entagged.audioformats.mp4.util.atoms.DATA;
import entagged.audioformats.mp4.util.atoms.ILST;
import entagged.audioformats.mp4.util.atoms.MOOV;
import entagged.audioformats.mp4.util.atoms.UDTA;

public class Mp4TagReader {

	public Tag read(final RandomAccessFile raf) throws CannotReadException,
			IOException {
		final GenericTag tag = new GenericTag();
		raf.seek(0);
		for (Atom atom = Atom.nextAtom(raf); atom != null; atom = Atom
				.nextAtom(raf)) {
			if (atom instanceof MOOV) {
				atom.parse(raf);
				final MOOV movie = (MOOV) atom;
				movie.parse(raf);
				final UDTA userData = movie.udta();
				if (userData != null && userData.meta() != null
						&& userData.meta().ilst() != null) {
					populate(tag, userData.meta().ilst());
					break;
				}
			}
		}
		return tag;
	}

	private static void populate(final GenericTag tag,
			final ILST appleMetadataList) {
		assert tag != null;
		assert appleMetadataList != null;
		DATA data = appleMetadataList.findById(AppleAnnotation.Ids.ALBUM);
		if (data != null) {
			tag.setAlbum(data.asString());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.ARTIST);
		if (data != null) {
			tag.setArtist(data.asString());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.COMMENT);
		if (data != null) {
			tag.setComment(data.asString());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.COVER_ART);
		if (data != null) {
			tag.setCoverArt(data.asBytes());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.ENCODER);
		if (data != null) {
			tag.setEncoding(data.asString());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.GENRE);
		if (data == null) {
			data = appleMetadataList.findById(AppleAnnotation.Ids.ALT_GENRE);
		}
		if (data != null) {
			if (data.text()) {
				tag.setGenre(data.asString());
			} else if (data.data()) {
				final byte[] genreBytes = data.asBytes();
				assert genreBytes.length == 2;
				final int genreIdx = ByteBuffer
						.wrap(
								new byte[] { 0x00, 0x00, genreBytes[0],
										genreBytes[1] }).getInt();
				if (genreIdx < Tag.DEFAULT_GENRES.length) {
					tag.setGenre(Tag.DEFAULT_GENRES[genreIdx]);
				}
			}
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.TITLE);
		if (data != null) {
			tag.setTitle(data.asString());
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.TRACK_NUMBER);
		if (data != null) {
			if (data.text()) {
				tag.setTrack(data.asString());
			} else {
				final byte[] trackNumberBytes = data.asBytes();
				assert trackNumberBytes.length >= 4;
				// Skip the first 16-bit number, not sure what it is
				tag.setTrack(String.valueOf(ByteBuffer.wrap(trackNumberBytes,
						2, 2).getShort()));
			}
		}
		data = appleMetadataList.findById(AppleAnnotation.Ids.CREATED_YEAR);
		if (data != null) {
			tag.setYear(data.asString());
		}
	}
}
