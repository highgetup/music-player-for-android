package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class MP4A extends AudioSampleEntry {

	private static final String TO_STRING_FORMAT = "'{'mp4a: {0}"
			+ ", esds [{1}]'}'";

	private ESDS esds;

	public MP4A(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		super.parseBody(mpegFile);
		long bytesRemaining = seekToBodyStart(mpegFile);
		while (bytesRemaining > 8) {
			final Atom subAtom = Atom.nextAtom(mpegFile);
			if (subAtom == null)
				break;
			bytesRemaining -= subAtom.size();
			if (Arrays.equals(Atom.Ids.ESDS, subAtom.id())) {
				esds = (ESDS) subAtom;
				break;
			}
		}
		esds.parse(mpegFile);
	}

	public ESDS esds() {
		return esds;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				super.toString(), esds });
	}

}
