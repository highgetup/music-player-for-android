package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.MessageFormat;

/**
 * This class should ONLY be used by {@link ALAC} directly.
 */
public final class EmbeddedALAC extends Atom {

	private static final String TO_STRING_FORMAT = "'{'sub-alac: max samples per frame/{0,number,#}"
			+ ", sample size/{1,number,#}"
			+ ", max sample size/{2,number,#}"
			+ ", bitrate/{3,number,#}" + ", sampleRate/{4,number,#}" + "'}'";

	private long maxSamplesPerFrame;
	private int sampleSize;
	private long maxSampleSize;
	private int bitrate;
	private long sampleRate;

	public EmbeddedALAC(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
		maxSamplesPerFrame = -1;
		sampleSize = -1;
	}

	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		seekToBodyStart(mpegFile);
		mpegFile.skipBytes(4); // ???
		final byte[] buf8 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		mpegFile.readFully(buf8, 4, 4);
		maxSamplesPerFrame = ByteBuffer.wrap(buf8).getLong();
		sampleSize = mpegFile.readUnsignedShort();

		mpegFile.skipBytes(4); // Rice information(3) + ???(1)
		mpegFile.skipBytes(2); // ???
		mpegFile.skipBytes(4); // ???

		// I noticed that with Max 0.7.1 this field is set to 0, but with iTunes
		// it is the (average?) bitrate. Not sure how to compute this if it is
		// set to 0
		mpegFile.readFully(buf8, 4, 4);
		bitrate = ByteBuffer.wrap(buf8, 4, 4).getInt();

		mpegFile.readFully(buf8, 4, 4);
		sampleRate = ByteBuffer.wrap(buf8).getLong();
	}

	public int bitrate() {
		return bitrate;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				maxSamplesPerFrame, sampleSize, maxSampleSize, bitrate,
				sampleRate });
	}

}
