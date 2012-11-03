package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;

/**
 * All audio entries under STSD have the first 20 bytes (in addition to the
 * previous 8 bytes from SampleEntry) in common, some fields we care about and
 * some we don't.
 */
public class AudioSampleEntry extends SampleEntry {

	private static final String TO_STRING_FORMAT = "channels/{0,number,#}"
			+ ", sample size/{1,number,#} bits"
			+ ", sample rate/{2,number,#}Hz";

	protected int channels;
	protected int sampleSize;
	protected int sampleRate;

	public AudioSampleEntry(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		super.seekToBodyStart(mpegFile);
		mpegFile.skipBytes(8); // Reserved
		channels = mpegFile.readShort();
		sampleSize = mpegFile.readShort();
		mpegFile.skipBytes(4); // (short) "pre-defined" + (short) reserved
		sampleRate = mpegFile.readInt() >>> 16;
	}

	@Override
	protected long seekToBodyStart(final RandomAccessFile mpegFile)
			throws IOException {
		final long originalBodyLength = super.seekToBodyStart(mpegFile);
		mpegFile.skipBytes(20);
		return originalBodyLength - 20;
	}

	public int channels() {
		return channels;
	}

	public int sampleRate() {
		return sampleRate;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] { channels,
				sampleSize, sampleRate });
	}

}
