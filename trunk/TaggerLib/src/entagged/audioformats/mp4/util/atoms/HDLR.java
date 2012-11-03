package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;

public final class HDLR extends Atom {

	enum Type {
		vide {
			@Override
			public String toString() {
				return "video";
			}
		},
		soun {
			@Override
			public String toString() {
				return "sound";
			}
		},
		hint {
			@Override
			public String toString() {
				return "hint";
			}
		},
		mdir {
			@Override
			public String toString() {
				return "Apple iTunes meta-data";
			}
		},
		NULL {
			@Override
			public String toString() {
				return "<none>";
			}
		}
	};

	private static final String TO_STRING_FORMAT = "'{'hdlr: " + "type/{0}"
			+ ", name/{1}" + "'}'";

	private Type type;
	private String name;

	public HDLR(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile, true);
		type = Type.NULL;
		name = null;
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		long bytesRemaining = seekToBodyStart(mpegFile);
		mpegFile.skipBytes(4);
		final byte[] handlerId = new byte[4];
		mpegFile.readFully(handlerId);
		final String handler = new String(handlerId);
		type = Type.valueOf(handler);
		if (type == null) {
			type = Type.NULL;
		}
		mpegFile.skipBytes(12);
		bytesRemaining -= 20;
		final byte[] buf = new byte[(int) bytesRemaining];
		mpegFile.readFully(buf);
		int usedChars = 0;
		for (int pos = 0; pos < buf.length; ++pos) {
			if (buf[pos] != 0) {
				++usedChars;
			} else {
				break;
			}
		}
		if (usedChars > 0) {
			name = new String(buf, 0, usedChars, "UTF-8");
		}
	}

	public boolean video() {
		return type == Type.vide;
	}

	public boolean audio() {
		return type == Type.soun;
	}

	public boolean appleMetadata() {
		return type == Type.mdir;
	}

	public boolean hint() {
		return type == Type.hint;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT,
				new Object[] { type, name });
	}

}
