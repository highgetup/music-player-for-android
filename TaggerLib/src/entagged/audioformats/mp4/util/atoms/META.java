package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class META extends Atom {

	private static final String TO_STRING_FORMAT = "'{'meta: " + "hdlr [{0}]"
			+ ", ilst [{1}]" + "'}'";

	private HDLR hdlr;
	private ILST ilst;

	public META(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile, true);
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
			if (Arrays.equals(Ids.HDLR, subAtom.id())) {
				hdlr = (HDLR) subAtom;
			} else if (Arrays.equals(Ids.ILST, subAtom.id())) {
				ilst = (ILST) subAtom;
			}
		}
		if (hdlr != null) {
			hdlr.parse(mpegFile);
		}
		if (ilst != null) {
			ilst.parse(mpegFile);
		}
	}

	public ILST ilst() {
		return ilst;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT,
				new Object[] { hdlr, ilst });
	}

}
