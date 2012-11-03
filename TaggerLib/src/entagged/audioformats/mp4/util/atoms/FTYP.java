package entagged.audioformats.mp4.util.atoms;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public final class FTYP extends Atom {

	public static final String MAJOR_BRAND_ISO14496_1_BASE_MEDIA = "isom";
	public static final String MAJOR_BRAND_ISO14496_12_BASE_MEDIA = "iso2";
	public static final String MAJOR_BRAND_ISO14496_1_VERSION_1 = "mp41";
	public static final String MAJOR_BRAND_ISO14496_1_VERSION_2 = "mp42";
	public static final String MAJOR_BRAND_QUICKTIME_MOVIE = "qt  ";
	public static final String MAJOR_BRAND_JVT_AVC = "avc1";
	public static final String MAJOR_BRAND_3G_MOBILE_MP4 = "mmp4";
	public static final String MAJOR_BRAND_APPLE_AAC_AUDIO = "M4A ";
	public static final String MAJOR_BRAND_AES_ENCRYPTED_AUDIO = "M4P ";
	public static final String MAJOR_BRAND_APPLE_AUDIO = "M4B ";
	public static final String MAJOR_BRAND_ISO14496_12_MPEG7_METADATA = "mp71";

	private String majorBrand;
	private String majorBrandVersion;
	private List<String> compatibleBrands;

	public FTYP(final RandomAccessFile mpegFile) throws IOException {
		super(mpegFile);
		assert size >= 20;
		assert size % 4 == 0;
		majorBrand = majorBrandVersion = "Uninitialized, please call parse()";
		compatibleBrands = new LinkedList<String>();
	}

	@Override
	protected void parseBody(final RandomAccessFile mpegFile)
			throws IOException {
		assert mpegFile != null;
		mpegFile.seek(fileOffset + 8);
		final byte[] buf = new byte[4];
		mpegFile.readFully(buf);
		majorBrand = new String(buf);
		mpegFile.readFully(buf);
		majorBrandVersion = new String(buf);
		for (long loop = (size - 16) / 4; loop > 0; --loop) {
			mpegFile.readFully(buf);
			compatibleBrands.add(new String(buf));
		}
	}

	private static final String TO_STRING_FORMAT = "'{'ftyp: "
			+ "major brand [{0}]" + ", major brand version [{1}]"
			+ ", compatible brands {2}'}'";

	@Override
	public String toString() {
		return MessageFormat.format(TO_STRING_FORMAT, new Object[] {
				majorBrand, majorBrandVersion, compatibleBrands });
	}

}
