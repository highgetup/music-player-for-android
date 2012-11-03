package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

public final class DATA extends Atom {

	private static final String TO_STRING_FORMAT = "'{'data: text/{0}, data/{1}, image/{2}, tempo-compilation/{3}, value/{4}'}'";

	private static final int FLAG_DATA = 0x00;
	private static final int FLAG_TEXT = 0x01;
	private static final int FLAG_TEMPO_COMPILATION = 0x15;
	private static final int FLAG_IMAGE = 0x0d;

	private byte[] rawBytes;

	public DATA(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile, true);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		final int size = (int) (super.seekToBodyStart(mpegFile) - 4);
		mpegFile.skipBytes(4); // Reserved
		rawBytes = new byte[size];
		mpegFile.readFully(rawBytes);
	}

	public boolean text() {
		return flags() == FLAG_TEXT;
	}

	public boolean data() {
		return flags() == FLAG_DATA;
	}

	public boolean image() {
		return flags() == FLAG_IMAGE;
	}

	public boolean tempoOrCompilation() {
		return flags() == FLAG_TEMPO_COMPILATION;
	}

	public String asString() {
		if (text()) {
			try {
				return new String(rawBytes, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				throw new RuntimeException("Unable to use UTF8 encoding", uee);
			}
		} else if (data() || tempoOrCompilation()) {
			if (rawBytes.length > 0) {
				final StringBuilder sb = new StringBuilder();
				sb.append("{ ");
				sb.append(toHex(rawBytes[0]));
				for (int pos = 1; pos < rawBytes.length; ++pos) {
					sb.append(", ");
					sb.append(toHex(rawBytes[pos]));
				}
				sb.append(" }");
				return sb.toString();
			} else {
				return "<no data>";
			}
		} else {
			return "<" + rawBytes.length + " bytes not shown>";
		}
	}

	public byte[] asBytes() {
		return rawBytes;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { text(),
				data(), image(), tempoOrCompilation(), asString() });
	}

	private static String toHex(final byte b) {
		int i = ByteBuffer.wrap(new byte[] { 0x00, 0x00, 0x00, b }).getInt();
		return (i < 15 ? "0x0" : "0x") + Integer.toHexString(i);
	}

}
