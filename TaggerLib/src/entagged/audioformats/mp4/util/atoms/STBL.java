package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class STBL extends Atom {

	private static final String TO_STRING_FORMAT = "'{'stbl: "
			+ "stsd [{0}]'}'";

	private STSD stsd;

	public STBL(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		long bytesRemaining = seekToBodyStart(mpegFile);
		while (bytesRemaining > 8) {
			final Atom subAtom = Atom.nextAtom(mpegFile);
			if (subAtom == null)
				break;
			bytesRemaining -= subAtom.size();
			if (Arrays.equals(Atom.Ids.STSD, subAtom.id())) {
				stsd = (STSD) subAtom;
				break;
			}
		}
		if (stsd != null) {
			stsd.parse(mpegFile);
		}
	}

	public STSD stsd() {
		return stsd;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { stsd });
	}

}
