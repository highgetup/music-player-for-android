package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Calendar;

public final class MVHD extends Atom {

	private static final String TO_STRING_FORMAT = "'{'mvhd: length/{2}s"
			+ ", created/{0,date,EEE, d MMM yyyy HH:mm:ss Z}"
			+ ", modified/{1,date,EEE, d MMM yyyy HH:mm:ss Z}" + "'}'}";

	private Calendar creationTime;
	private Calendar modificationTime;
	private long length;

	public MVHD(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile, true);
		creationTime = modificationTime = null;
		length = -1;
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		if (version() != 0 && version() != 1) {
			throw new IOException("I don't know how to handle version "
					+ version() + " of MVHD");
		}
		seekToBodyStart(mpegFile);
		final byte[] buf8 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		mpegFile
				.readFully(buf8, version() == 0 ? 4 : 0, version() == 0 ? 4 : 8);
		creationTime = createCalendar(ByteBuffer.wrap(buf8).getLong());
		for (int pos = 0; pos < buf8.length; ++pos)
			buf8[pos] = 0x00;
		mpegFile
				.readFully(buf8, version() == 0 ? 4 : 0, version() == 0 ? 4 : 8);
		modificationTime = createCalendar(ByteBuffer.wrap(buf8).getLong());
		final long timescale = mpegFile.readInt();
		final long duration = version() == 0 ? mpegFile.readInt() : mpegFile
				.readLong();
		length = duration / timescale;
	}

	public Calendar creationTime() {
		return creationTime != null ? (Calendar) creationTime.clone() : null;
	}

	public Calendar modificationTime() {
		return modificationTime != null ? (Calendar) modificationTime.clone()
				: null;
	}

	public long lengthInSeconds() {
		return length;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				creationTime != null ? creationTime.getTime() : null,
				modificationTime != null ? modificationTime.getTime() : null,
				length });
	}

}
