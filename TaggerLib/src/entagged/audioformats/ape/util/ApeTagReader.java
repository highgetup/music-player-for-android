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
package entagged.audioformats.ape.util;

import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.Tag;
import entagged.audioformats.ape.ApeTag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.Utils;

public class ApeTagReader {

	public Tag read(RandomAccessFile raf) throws CannotReadException, IOException {
		
		//Check wether the file contains an APE tag--------------------------------
		raf.seek( raf.length() - 32 );
		
		byte[] b = new byte[8];
		raf.read(b);
		
		String tagS = new String( b );
		if(!tagS.equals( "APETAGEX" )){
			throw new CannotReadException("There is no APE Tag in this file");
		}
		//Parse the tag -)------------------------------------------------
		//Version
		b = new byte[4];
		raf.read( b );
		int version = Utils.getNumber(b, 0,3);
		if(version != 2000) {
			throw new CannotReadException("APE Tag other than version 2.0 are not supported");
		}
		
		//Size
		b = new byte[4];
		raf.read( b );
		long tagSize = Utils.getLongNumber(b, 0,3);

		//Number of items
		b = new byte[4];
		raf.read( b );
		int itemNumber = Utils.getNumber(b, 0,3);
		
		//Tag Flags
		b = new byte[4];
		raf.read( b );
		//TODO handle these
		
		raf.seek(raf.length() - tagSize);
		
		ApeTag tag = new ApeTag();
		for(int i = 0; i<itemNumber; i++) {
			//Content length
			b = new byte[4];
			raf.read( b );
			int contentLength = Utils.getNumber(b, 0,3);
			if(contentLength > 500000)
				throw new CannotReadException("Item size is much too large: "+contentLength+" bytes");
			
			//Item flags
			b = new byte[4];
			raf.read( b );
			//TODO handle these
			boolean binary = ((b[0]&0x06) >> 1) == 1;
			
			int j = 0;
			while(raf.readByte() != 0)
				j++;
			raf.seek(raf.getFilePointer() - j -1);
			int fieldSize = j;
			
			//Read Item key
			b = new byte[fieldSize];
			raf.read( b );
			raf.skipBytes(1);
			String field = new String(b);
			
			//Read Item content
			b = new byte[contentLength];
			raf.read( b );
			if(!binary)
			    tag.add(new ApeTagTextField(field, new String(b, "UTF-8")));
			else
			    tag.add(new ApeTagBinaryField(field, b));
		}
		
		return tag;
	} 
}
