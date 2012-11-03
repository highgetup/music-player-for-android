/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package entagged.audioformats;

import java.io.File;
import java.util.Hashtable;

import entagged.audioformats.ape.MonkeyFileReader;
import entagged.audioformats.asf.AsfFileReader;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.flac.FlacFileReader;
import entagged.audioformats.generic.AudioFileModificationListener;
import entagged.audioformats.generic.AudioFileReader;
import entagged.audioformats.generic.ModificationHandler;
import entagged.audioformats.generic.Utils;
import entagged.audioformats.mp3.Mp3FileReader;
import entagged.audioformats.mp4.Mp4FileReader;
import entagged.audioformats.mpc.MpcFileReader;
import entagged.audioformats.ogg.OggFileReader;
import entagged.audioformats.real.RealFileReader;
import entagged.audioformats.wav.WavFileReader;

/**
 * <p>
 * The main entry point for the Tag Reading/Writing operations, this class will
 * select the appropriate reader/writer for the given file.
 * </p>
 * <p>
 * It selects the appropriate reader/writer based on the file extension (case
 * ignored).
 * </p>
 * <p>
 * Here is an simple example of use:
 * </p>
 * <p>
 * <code>
 *		AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3")); //Reads the given file.<br/>
 *		int bitrate = audioFile.getBitrate(); //Retreives the bitrate of the file.<br/>
 *		String artist = audioFile.getTag().getArtist(); //Retreive the artist name.<br/>
 *		audioFile.getTag().setGenre("Progressive Rock"); //Sets the genre to Prog. Rock, note the file on disk is still unmodified.<br/>
 *		AudioFileIO.write(audioFile); //Write the modifications in the file on disk.
 *	</code>
 * </p>
 * <p>
 * You can also use the <code>commit()</code> method defined for
 * <code>AudioFile</code>s to achieve the same goal as
 * <code>AudioFileIO.write(File)</code>, like this:
 * </p>
 * <p>
 * <code>
 *		AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3"));<br/>
 *		audioFile.getTag().setGenre("Progressive Rock");<br/>
 *		audioFile.commit(); //Write the modifications in the file on disk.<br/>
 *	</code>
 * </p>
 * 
 * @author Raphael Slinckx ; Nicolas Velin
 * @version $Id: AudioFileIO.java,v 1.1.2.3 2007/11/29 23:12:17 ericnotthered
 *          Exp $
 * @since v0.01
 * @see AudioFile
 * @see Tag
 */
public class AudioFileIO {
	// !! Do not forget to also add new supported extensions to AudioFileFilter
	// !!

	/**
	 * This field contains the default instance for static use.
	 */
	private static AudioFileIO defaultInstance;

	/**
	 * This method returns the default isntance for static use.<br>
	 * 
	 * @return The default instance.
	 */
	public synchronized static AudioFileIO getDefaultAudioFileIO() {
		if (defaultInstance == null) {
			defaultInstance = new AudioFileIO();
		}
		return defaultInstance;
	}

	/**
	 * <p>
	 * Read the tag contained in the given file.
	 * </p>
	 * 
	 * @param f
	 *            The file to read.
	 * @return The AudioFile with the file tag and the file encoding infos.
	 * @exception CannotReadException
	 *                If the file could not be read, the extension wasn't
	 *                recognized, or an IO error occured during the read.
	 */
	public static AudioFile read(File f) throws CannotReadException {
		return getDefaultAudioFileIO().readFile(f);
	}

	/**
	 * This member is used to broadcast modification events to registered
	 * {@link entagged.audioformats.generic.AudioFileModificationListener}
	 */
	private final ModificationHandler modificationHandler;

	// These tables contains all the readers/writers associated with extension
	// as a key
	private Hashtable<String, AudioFileReader> readers = new Hashtable<String, AudioFileReader>();

	/**
	 * Creates an instance.
	 * 
	 */
	public AudioFileIO() {
		this.modificationHandler = new ModificationHandler();
		prepareReaders();
	}

	/**
	 * Adds an listener for all file formats.
	 * 
	 * @param listener
	 *            listener
	 */
	public void addAudioFileModificationListener(
			AudioFileModificationListener listener) {
		this.modificationHandler.addAudioFileModificationListener(listener);
	}

	/**
	 * Creates the readers and writers.
	 */
	private void prepareReaders() {
		// Tag Readers
		readers.put("mp3", new Mp3FileReader());
		readers.put("mp4", new Mp4FileReader());
		readers.put("m4a", new Mp4FileReader());
		readers.put("m4p", new Mp4FileReader());
		readers.put("aac", new Mp4FileReader());
		readers.put("ogg", new OggFileReader());
		readers.put("flac", new FlacFileReader());
		readers.put("wav", new WavFileReader());
		readers.put("mpc", new MpcFileReader());
		readers.put("mp+", readers.get("mpc"));
		readers.put("ape", new MonkeyFileReader());
		readers.put("wma", new AsfFileReader());
		readers.put("ra", new RealFileReader());
		readers.put("rm", readers.get("ra"));
	}

	/**
	 * <p>
	 * Read the tag contained in the given file.
	 * </p>
	 * 
	 * @param f
	 *            The file to read.
	 * @return The AudioFile with the file tag and the file encoding infos.
	 * @exception CannotReadException
	 *                If the file could not be read, the extension wasn't
	 *                recognized, or an IO error occured during the read.
	 */
	public AudioFile readFile(File f) throws CannotReadException {
		String ext = Utils.getExtension(f);

		AudioFileReader afr = readers.get(ext);
		if (afr == null)
			throw new CannotReadException(
					"No Reader associated to this extension: " + ext);

		return afr.read(f);
	}

	/**
	 * Removes an listener for all file formats.
	 * 
	 * @param listener
	 *            listener
	 */
	public void removeAudioFileModificationListener(
			AudioFileModificationListener listener) {
		this.modificationHandler.removeAudioFileModificationListener(listener);
	}
}
