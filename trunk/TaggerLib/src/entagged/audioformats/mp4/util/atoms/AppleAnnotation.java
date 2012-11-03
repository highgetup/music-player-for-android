package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AppleAnnotation extends Atom {

	public interface Ids {
		static final byte A9 = (byte) 0xa9;
		static final byte[] ALBUM = { A9, 'a', 'l', 'b' };
		static final byte[] ARTIST = { A9, 'A', 'R', 'T' };
		static final byte[] BEATS_PER_MINUTE = { 't', 'm', 'p', 'o' };
		static final byte[] COMMENT = { A9, 'c', 'm', 't' };
		static final byte[] COMPILATION = { 'c', 'p', 'i', 'l' };
		static final byte[] COMPOSER = { A9, 'c', 'o', 'm' };
		static final byte[] COVER_ART = { 'c', 'o', 'v', 'r' };
		static final byte[] CREATED_YEAR = { A9, 'd', 'a', 'y' };
		static final byte[] DISC_NUMBER = { 'd', 'i', 's', 'k' };
		static final byte[] ENCODER = { A9, 't', 'o', 'o' };
		static final byte[] ALT_GENRE = { A9, 'g', 'e', 'n' };
		static final byte[] GENRE = { 'g', 'n', 'r', 'e' };
		static final byte[] ITUNES_SPECIFIC = { '-', '-', '-', '-' };
		static final byte[] TITLE = { A9, 'n', 'a', 'm' };
		static final byte[] TRACK_NUMBER = { 't', 'r', 'k', 'n' };
		static final byte[] WRITTEN_BY = { A9, 'w', 'r', 't' };
	}

	private static final String TO_STRING_FORMAT = "{0}: data [{1}]";

	private static final List<byte[]> ANNOTATION_IDS;
	static {
		ANNOTATION_IDS = new LinkedList<byte[]>();
		ANNOTATION_IDS.add(Ids.ALBUM);
		ANNOTATION_IDS.add(Ids.ARTIST);
		ANNOTATION_IDS.add(Ids.BEATS_PER_MINUTE);
		ANNOTATION_IDS.add(Ids.COMMENT);
		ANNOTATION_IDS.add(Ids.COMPILATION);
		ANNOTATION_IDS.add(Ids.COMPOSER);
		ANNOTATION_IDS.add(Ids.COVER_ART);
		ANNOTATION_IDS.add(Ids.CREATED_YEAR);
		ANNOTATION_IDS.add(Ids.DISC_NUMBER);
		ANNOTATION_IDS.add(Ids.ENCODER);
		ANNOTATION_IDS.add(Ids.ALT_GENRE);
		ANNOTATION_IDS.add(Ids.GENRE);
		ANNOTATION_IDS.add(Ids.ITUNES_SPECIFIC);
		ANNOTATION_IDS.add(Ids.TITLE);
		ANNOTATION_IDS.add(Ids.TRACK_NUMBER);
		ANNOTATION_IDS.add(Ids.WRITTEN_BY);
	}

	protected DATA data;

	public static boolean isAnnotation(final byte[] boxId) {
		for (byte[] id : ANNOTATION_IDS) {
			if (Arrays.equals(id, boxId)) {
				return true;
			}
		}
		return false;
	}

	public AppleAnnotation(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		super.seekToBodyStart(mpegFile);
		final Atom atom = nextAtom(mpegFile);
		if (atom instanceof DATA) {
			data = (DATA) atom;
		}
		if (data != null) {
			data.parse(mpegFile);
		}
	}

	DATA data() {
		return data;
	}

	@Override
	public String toString() {
		try {
			return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
					new String(id, "UTF-8"), data });
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Unable to use UTF8 encoding", uee);
		}
	}

}
