package entagged.audioformats.util;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IOUtil {
	/**
	 * read 4 bytes and return it as an unsigned value; Java is dumb, so, this
	 * returns an int and checks that it does not overflow...
	 */
	public static int readUint32AsInt(DataInput di) throws IOException {
		final long l = readUint32(di);
		if (l > Integer.MAX_VALUE) {
			throw new IOException("uint32 value read overflows int");
		}
		return (int) l;
	}

	public static long readUint32(DataInput di) throws IOException {
		final byte[] buf8 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		di.readFully(buf8, 4, 4);
		final long l = ByteBuffer.wrap(buf8).getLong();
		return l;
	}

	public static int readUint16(DataInput di) throws IOException {
		final byte[] buf = { 0x00, 0x00, 0x00, 0x00 };
		di.readFully(buf, 2, 2);
		final int i = ByteBuffer.wrap(buf).getInt();
		return i;
	}

	public static String readString(DataInput di, int charsToRead)
			throws IOException {
		final byte[] buf = new byte[charsToRead];
		di.readFully(buf);
		return new String(buf);
	}

}
