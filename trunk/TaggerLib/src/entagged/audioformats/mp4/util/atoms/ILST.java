package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class ILST extends Atom {

	private static final String TO_STRING_FORMAT = "'{'ilst: "
			+ "Apple meta-data elements [{0}]'}'";

	private List<AppleAnnotation> annotations;

	public ILST(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
		annotations = new LinkedList<AppleAnnotation>();
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
			if (AppleAnnotation.isAnnotation(subAtom.id())) {
				final long savepoint = mpegFile.getFilePointer();
				mpegFile.seek(subAtom.fileOffset);
				final AppleAnnotation annotation = new AppleAnnotation(mpegFile);
				annotations.add(annotation);
				mpegFile.seek(savepoint);
			}
		}
		for (AppleAnnotation annotation : annotations) {
			annotation.parse(mpegFile);
		}
	}

	public DATA findById(final byte[] boxId) {
		assert boxId != null;
		assert boxId.length == 4;
		for (AppleAnnotation aa : annotations) {
			if (Arrays.equals(boxId, aa.id())) {
				return aa.data();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT,
				new Object[] { annotations });
	}

}
