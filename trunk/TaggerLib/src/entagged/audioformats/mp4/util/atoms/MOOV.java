package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class MOOV extends Atom {

	private MVHD mvhd;
	private List<TRAK> traks;
	private UDTA udta;

	public MOOV(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
		traks = new LinkedList<TRAK>();
	}

	private static final String TO_STRING_FORMAT = "'{'moov: "
			+ "mvhd [{0}], traks [{1}], udta [{2}]'}'";

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		long bytesRemaining = seekToBodyStart(mpegFile);
		while (bytesRemaining > 8) {
			final Atom subAtom = Atom.nextAtom(mpegFile);
			if (subAtom == null)
				break;
			bytesRemaining -= subAtom.size();
			if (Arrays.equals(Atom.Ids.MVHD, subAtom.id())) {
				mvhd = (MVHD) subAtom;
			} else if (Arrays.equals(Atom.Ids.TRAK, subAtom.id())) {
				traks.add((TRAK) subAtom);
			} else if (Arrays.equals(Atom.Ids.UDTA, subAtom.id())) {
				udta = (UDTA) subAtom;
			}
		}
		mvhd.parse(mpegFile);
		for (TRAK trak : traks) {
			trak.parse(mpegFile);
		}
		if (udta != null) {
			udta.parse(mpegFile);
		}
	}

	public MVHD mvhd() {
		return mvhd;
	}

	public List<TRAK> traks() {
		return traks;
	}

	public UDTA udta() {
		return udta;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { mvhd,
				traks, udta });
	}

}
