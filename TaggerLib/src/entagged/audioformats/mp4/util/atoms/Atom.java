package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class Atom {

	public interface Ids {
		static final byte[] ALAC = { 'a', 'l', 'a', 'c' }; // Apple lossless
		static final byte[] DRMS = { 'd', 'r', 'm', 's' }; // Apple DRM
		static final byte[] ESDS = { 'e', 's', 'd', 's' }; // ES descriptor
		static final byte[] FTYP = { 'f', 't', 'y', 'p' }; // File type
		static final byte[] HDLR = { 'h', 'd', 'l', 'r' }; // Handler
		static final byte[] ILST = { 'i', 'l', 's', 't' }; // Apple list item
		static final byte[] MDIA = { 'm', 'd', 'i', 'a' }; // Media stream
		static final byte[] META = { 'm', 'e', 't', 'a' }; // Metadata
		static final byte[] MINF = { 'm', 'i', 'n', 'f' }; // Media info
		static final byte[] MVHD = { 'm', 'v', 'h', 'd' }; // Movie header
		static final byte[] MOOV = { 'm', 'o', 'o', 'v' }; // Movie info
		static final byte[] MP4A = { 'm', 'p', '4', 'a' }; // AAC audio
		static final byte[] SMHD = { 's', 'm', 'h', 'd' }; // Sound media hdr
		static final byte[] STBL = { 's', 't', 'b', 'l' }; // Sample table
		static final byte[] STSD = { 's', 't', 's', 'd' }; // Sample table desc
		static final byte[] TRAK = { 't', 'r', 'a', 'k' }; // Track info
		static final byte[] UDTA = { 'u', 'd', 't', 'a' }; // User data
	}

	protected final long fileOffset;
	protected final long size;
	protected final byte[] id;

	private final boolean hasVersionAndFlags;
	private final byte version;
	private final int flags;

	@SuppressWarnings("unchecked")
	public static Atom nextAtom(final RandomAccessFile mpegFile)
			throws IOException {
		if (mpegFile.getFilePointer() < mpegFile.length() - 8) {
			final byte[] buf = new byte[4];
			// Peek at the next atom id to see if we have a subclass for it
			mpegFile.skipBytes(4);
			mpegFile.readFully(buf);
			final String atomId = new String(buf);
			// Rewind, Atom and subclass constructors expect the file to be
			// positioned at the very beginning of the atom
			mpegFile.seek(mpegFile.getFilePointer() - 8);
			final String atomSpecificClassName = Atom.class.getPackage()
					.getName()
					+ "." + atomId.toUpperCase();
			try {
				Class atomSpecificClass = Class.forName(atomSpecificClassName);
				Constructor ctor = atomSpecificClass
						.getConstructor(RandomAccessFile.class);
				return (Atom) ctor.newInstance(mpegFile);
			} catch (ClassNotFoundException cnfe) {
				return new Atom(mpegFile);
			} catch (SecurityException se) {
				return new Atom(mpegFile);
			} catch (NoSuchMethodException nsme) {
				return new Atom(mpegFile);
			} catch (IllegalArgumentException ilae) {
				return new Atom(mpegFile);
			} catch (InstantiationException ee) {
				return new Atom(mpegFile);
			} catch (IllegalAccessException iae) {
				return new Atom(mpegFile);
			} catch (InvocationTargetException ite) {
				return new Atom(mpegFile);
			}
		} else {
			return null;
		}
	}

	protected Atom(final RandomAccessFile mpegFile) throws IOException {
		this(mpegFile, false);
	}

	protected Atom(final RandomAccessFile mpegFile, boolean hasVersionAndFlags)
			throws IOException {
		assert mpegFile != null;
		this.hasVersionAndFlags = hasVersionAndFlags;
		fileOffset = mpegFile.getFilePointer();
		long atomSize = mpegFile.readInt();
		if (atomSize == 0) {
			// Use the remainder of the file
			atomSize = mpegFile.length() - fileOffset;
		} else if (atomSize == 1) {
			// 64-bit size extension
			atomSize = mpegFile.readLong();
		}
		size = atomSize;
		id = new byte[4];
		mpegFile.readFully(id);
		if (hasVersionAndFlags) {
			version = mpegFile.readByte();
			final byte[] buf4 = { 0x00, 0x00, 0x00, 0x00 };
			mpegFile.readFully(buf4, 1, 3);
			flags = ByteBuffer.wrap(buf4).getInt();
		} else {
			version = -1;
			flags = -1;
		}
		mpegFile.seek(fileOffset + size);
	}

	public final void parse(final RandomAccessFile mpegFile) throws IOException {
		synchronized (mpegFile) {
			parseBody(mpegFile);
		}
	}

	public final byte[] id() {
		final byte[] rv = new byte[4];
		System.arraycopy(id, 0, rv, 0, id.length);
		return rv;
	}

	public final long size() {
		return size;
	}

	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		// Children for specific atom types should override this
	}

	/**
	 * @return the number of bytes in the atom body
	 * @throws IOException
	 */
	protected long seekToBodyStart(final RandomAccessFile mpegFile)
			throws IOException {
		mpegFile.seek(fileOffset + (hasVersionAndFlags ? 12 : 8));
		return size - (hasVersionAndFlags ? 12 : 8);
	}

	protected final byte version() {
		assert hasVersionAndFlags;
		return version;
	}

	protected final int flags() {
		assert hasVersionAndFlags;
		return flags;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{" + new String(id)
				+ ": file offset/" + fileOffset + ", atom size/" + size);
		if (hasVersionAndFlags) {
			sb.append(", version/" + version() + ", flags/" + flags());
		}
		sb.append("}");
		return sb.toString();
	}

	protected static Calendar createCalendar(
			final long secondsSinceUtcMidnightJan1_1904) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.set(1904, Calendar.JANUARY, 1, 0, 0, 0);
		long secondsRemainingToAdd = secondsSinceUtcMidnightJan1_1904;
		while (secondsRemainingToAdd > 0) {
			int secondsAdded = secondsRemainingToAdd > new Long(
					Integer.MAX_VALUE) ? Integer.MAX_VALUE
					: (int) secondsRemainingToAdd;
			c.add(Calendar.SECOND, secondsAdded);
			secondsRemainingToAdd -= secondsAdded;
		}
		return c;
	}

	public static void main(final String[] mpegFilenames) throws Exception {
		System.out.println();
		for (String mpegFilename : mpegFilenames) {
			System.out.println(mpegFilename + " atoms ==>");
			final RandomAccessFile mpegFile = new RandomAccessFile(
					mpegFilename, "r");
			final List<Atom> atoms = new LinkedList<Atom>();
			for (Atom atom = nextAtom(mpegFile); atom != null; atom = nextAtom(mpegFile)) {
				atoms.add(atom);
			}
			for (Atom atom : atoms) {
				atom.parse(mpegFile);
				System.out.println("\t" + atom);
			}
			System.out.println();
		}
	}

}
