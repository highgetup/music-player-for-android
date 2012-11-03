package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;

public final class STSD extends Atom {

	private static final String TO_STRING_FORMAT = "'{'stsd: "
			+ "mp4a [{0}], alac [{1}], drms [{2}], lossless/{3}, DRM protected/{4}'}'";

	private ALAC alac;
	private MP4A mp4a;
	private DRMS drms;

	public STSD(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile, true);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		long bytesRemaining = seekToBodyStart(mpegFile);

		bytesRemaining -= 4; // Description count
		for (int descriptionCount = mpegFile.readInt(); descriptionCount > 0; --descriptionCount) {
			assert bytesRemaining > 8;
			final Atom subAtom = Atom.nextAtom(mpegFile);
			assert subAtom != null;
			bytesRemaining -= subAtom.size();
			if (Arrays.equals(Atom.Ids.MP4A, subAtom.id())) {
				mp4a = (MP4A) subAtom;
			} else if (Arrays.equals(Atom.Ids.DRMS, subAtom.id())) {
				drms = (DRMS) subAtom;
			} else if (Arrays.equals(Atom.Ids.ALAC, subAtom.id())) {
				alac = (ALAC) subAtom;
			}
		}
		if (mp4a != null) {
			mp4a.parse(mpegFile);
		}
		if (drms != null) {
			drms.parse(mpegFile);
		}
		if (alac != null) {
			alac.parse(mpegFile);
		}
	}

	public boolean lossless() {
		return alac != null;
	}

	public boolean drmProtected() {
		return drms != null;
	}

	public ALAC alac() {
		return alac;
	}

	public MP4A mp4a() {
		return mp4a;
	}

	public DRMS drms() {
		return drms;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { mp4a,
				alac, drms, lossless(), drmProtected() });
	}

}
