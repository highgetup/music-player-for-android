package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class MINF extends Atom {

	private static final String TO_STRING_FORMAT = "'{'minf: "
			+ "stbl [{0}]'}'";

	private STBL stbl;

	public MINF(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		long bytesRemaining = seekToBodyStart(mpegFile);
		Atom smhd = null;
		while (bytesRemaining > 8) {
			final Atom subAtom = Atom.nextAtom(mpegFile);
			if (subAtom == null)
				break;
			bytesRemaining -= subAtom.size();
			if (Arrays.equals(Atom.Ids.STBL, subAtom.id())) {
				stbl = (STBL) subAtom;
			} else if (Arrays.equals(Atom.Ids.SMHD, subAtom.id())) {
				smhd = subAtom;
			}
		}
		// SMHD must be present for audio tracks
		assert smhd != null;
		stbl.parse(mpegFile);
	}

	public STBL stbl() {
		return stbl;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { stbl });
	}

}
