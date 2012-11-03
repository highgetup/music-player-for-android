package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class TRAK extends Atom {

	private static final String TO_STRING_FORMAT = "'{'trak: "
			+ "mdia [{0}]'}'";

	private MDIA mdia;

	public TRAK(final RandomAccessFile mpegFile) throws IOException {
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
			if (Arrays.equals(Atom.Ids.MDIA, subAtom.id())) {
				mdia = (MDIA) subAtom;
				break;
			}
		}
		if (mdia != null) {
			mdia.parse(mpegFile);
		}
	}

	public MDIA mdia() {
		return mdia;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { mdia });
	}

}
