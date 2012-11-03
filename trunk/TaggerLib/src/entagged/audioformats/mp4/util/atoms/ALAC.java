package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class ALAC extends AudioSampleEntry {

	private static final String TO_STRING_FORMAT = "'{'alac: {0}, embedded alac [{1}]'}'";

	EmbeddedALAC ealac;

	public ALAC(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		super.parseBody(mpegFile);
		final List<Atom> atoms = new LinkedList<Atom>();
		for (long remainingBytes = seekToBodyStart(mpegFile); remainingBytes > 0;) {
			final Atom atom = nextAtom(mpegFile);
			remainingBytes -= atom.size();
			atoms.add(atom);
		}
		for (Atom atom : atoms) {
			if (Arrays.equals(Atom.Ids.ALAC, atom.id)) {
				mpegFile.seek(atom.fileOffset);
				ealac = new EmbeddedALAC(mpegFile);
				ealac.parseBody(mpegFile);
			}
		}
	}

	public int bitrate() {
		return ealac != null ? ealac.bitrate() : -1;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				super.toString(), ealac });
	}

}
