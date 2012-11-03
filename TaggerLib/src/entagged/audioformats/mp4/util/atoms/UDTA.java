package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class UDTA extends Atom {

	private static final String TO_STRING_FORMAT = "'{'udta: "
			+ "meta [{0}]'}'";

	private META meta;

	public UDTA(final RandomAccessFile mpegFile) throws IOException {
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
			if (Arrays.equals(Atom.Ids.META, subAtom.id())) {
				meta = (META) subAtom;
			}
		}
		if (meta != null) {
			meta.parse(mpegFile);
		}
	}

	public META meta() {
		return meta;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { meta });
	}

}
