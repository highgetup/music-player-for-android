package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * All entries (atoms) under STSD have the first 8 bytes in common, and for our
 * purposes can be ignored.
 */
public class SampleEntry extends Atom {

	public SampleEntry(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected long seekToBodyStart(final RandomAccessFile mpegFile)
			throws IOException {
		final long originalBodyLength = super.seekToBodyStart(mpegFile);
		mpegFile.skipBytes(8);
		return originalBodyLength - 8;
	}

}
