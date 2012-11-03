package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class MDIA extends Atom {

	private static final String TO_STRING_FORMAT = "'{'mdia: "
			+ "hdlr [{0}], minf [{1}]'}'";

	private HDLR hdlr;
	private MINF minf;

	public MDIA(final RandomAccessFile mpegFile) throws IOException {
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
			if (Arrays.equals(Atom.Ids.HDLR, subAtom.id())) {
				hdlr = (HDLR) subAtom;
			} else if (Arrays.equals(Atom.Ids.MINF, subAtom.id())) {
				minf = (MINF) subAtom;
			}
		}
		hdlr.parse(mpegFile);
		if (hdlr.audio()) {
			minf.parse(mpegFile);
		}
	}

	public HDLR hdlr() {
		return hdlr;
	}

	public MINF minf() {
		return minf;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT,
				new Object[] { hdlr, minf });
	}

}
