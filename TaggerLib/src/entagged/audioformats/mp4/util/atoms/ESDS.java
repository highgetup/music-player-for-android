package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;

public final class ESDS extends Atom {

	private static final String TO_STRING_FORMAT = "'{'esds: max bitrate/{0,number,#}"
			+ ", average bitrate/{1,number,#}'}'";

	private int maxBitrate;
	private int avgBitrate;

	public ESDS(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
		maxBitrate = avgBitrate = -1;
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		seekToBodyStart(mpegFile);
		mpegFile.skipBytes(22);
		maxBitrate = mpegFile.readInt();
		avgBitrate = mpegFile.readInt();
	}

	public int averageBitrate() {
		return avgBitrate;
	}

	public int maxBitrate() {
		return maxBitrate;
	}

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				maxBitrate, avgBitrate });
	}

}
