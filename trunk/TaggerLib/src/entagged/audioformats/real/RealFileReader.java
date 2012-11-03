package entagged.audioformats.real;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.EncodingInfo;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.AudioFileReader;
import entagged.audioformats.generic.GenericTag;
import entagged.audioformats.util.IOUtil;

/**
 * Real Media File Format: Major Chunks: .RMF PROP MDPR CONT DATA INDX
 * 
 * @author alex
 * 
 */
public class RealFileReader extends AudioFileReader {

    @Override
    protected EncodingInfo getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException {

        final EncodingInfo rv = new EncodingInfo();
        final RealChunk prop = findPropChunk(raf);
        final DataInputStream dis = prop.getDataInputStream();
        final int objVersion = IOUtil.readUint16(dis);
        if (objVersion == 0) {
            final long maxBitRate = IOUtil.readUint32(dis) / 1000;
            final long avgBitRate = IOUtil.readUint32(dis) / 1000;
            final long maxPacketSize = IOUtil.readUint32(dis);
            final long avgPacketSize = IOUtil.readUint32(dis);
            final long packetCnt = IOUtil.readUint32(dis);
            final int duration = IOUtil.readUint32AsInt(dis) / 1000;
            final long preroll = IOUtil.readUint32(dis);
            final long indexOffset = IOUtil.readUint32(dis);
            final long dataOffset = IOUtil.readUint32(dis);
            final int numStreams = IOUtil.readUint16(dis);
            final int flags = IOUtil.readUint16(dis);
            int channelNum = 0;
            String type = null;
            boolean isEncrypted = false;
            String infos = null;
            int length = 0;
            boolean isLossless = false;

            rv.setBitrate((int) avgBitRate);
            rv.setChannelNumber(channelNum);
            rv.setEncodingType(type);
            rv.setEncrypted(isEncrypted);
            rv.setExtraEncodingInfos(infos);
            rv.setLength(duration);
            rv.setLossless(isLossless);
            rv.setVbr(maxBitRate != avgBitRate);
        }
        return rv;
    }

    private RealChunk findPropChunk(RandomAccessFile raf) throws IOException {
        final RealChunk rmf = RealChunk.readChunk(raf);
        final RealChunk prop = RealChunk.readChunk(raf);
        return prop;
    }

    private RealChunk findContChunk(RandomAccessFile raf) throws IOException {
        final RealChunk rmf = RealChunk.readChunk(raf);
        final RealChunk prop = RealChunk.readChunk(raf);
        RealChunk rv = RealChunk.readChunk(raf);
        while (!rv.isCONT())
            rv = RealChunk.readChunk(raf);
        return rv;
    }

    @Override
    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException {
        final RealChunk cont = findContChunk(raf);
        final DataInputStream dis = cont.getDataInputStream();
        final String title = IOUtil.readString(dis, IOUtil.readUint16(dis));
        final String author = IOUtil.readString(dis, IOUtil.readUint16(dis));
        final String copyright = IOUtil.readString(dis, IOUtil.readUint16(dis));
        final String comment = IOUtil.readString(dis, IOUtil.readUint16(dis));
        final GenericTag rv = new GenericTag();
        // NOTE: frequently these fields are off-by-one, thus the crazy
        // logic below...
        rv.addTitle(title.length() == 0 ? author : title);
        rv.addArtist(title.length() == 0 ? copyright : author);
        rv.addComment(comment);
        return rv;
    }

    public static void main(String[] args) throws IOException, CannotReadException {
        for (int i = 0; i < args.length; i++) {
            final String filename = args[i];
            System.out.println("=================================");
            System.out.println("Filename: " + filename);
            final RandomAccessFile raf = new RandomAccessFile(filename, "r");
            final RealFileReader rfr = new RealFileReader();
            System.out.println(rfr.getEncodingInfo(raf));
            raf.seek(0);
            System.out.println(rfr.getTag(raf));
        }

    }
}
