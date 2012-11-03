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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import entagged.audioformats.Tag;
import entagged.audioformats.ape.ApeTag;
import entagged.audioformats.generic.AbstractTagCreator;

public class ApeTagCreator extends AbstractTagCreator {
		
	public void create(Tag tag, ByteBuffer buf, List fields, int tagSize, int paddingSize) throws UnsupportedEncodingException {
		byte[] b;

		//APETAGEX------------------
		buf.put( "APETAGEX".getBytes() );  //APETAGEX
		
		//Version 2.0 (aka 2000)
		buf.put( new byte[] {(byte)0xD0,0x07,0x00,0x00} );
		
		//Tag size
		int size = tagSize - 32;
		b = new byte[4];
		b[3] = (byte) ( ( size & 0xFF000000 ) >>> 24 );
		b[2] = (byte) ( ( size & 0x00FF0000 ) >>> 16 );
		b[1] = (byte) ( ( size & 0x0000FF00 ) >>> 8 );
		b[0] = (byte) ( size & 0x000000FF );
		buf.put(b);
		
		//Number of fields
		int listLength = fields.size();
		b = new byte[4];
		b[3] = (byte) ( ( listLength & 0xFF000000 ) >>> 24 );
		b[2] = (byte) ( ( listLength & 0x00FF0000 ) >>> 16 );
		b[1] = (byte) ( ( listLength & 0x0000FF00 ) >>> 8 );
		b[0] = (byte) ( listLength & 0x000000FF );
		buf.put( b );
		
		//Flags
		buf.put( new byte[] {0x00,0x00,0x00,(byte)0xA0} );
		//means: We have a header and a footer, this is the header
		
		//Reserved 8-bytes 0x00
		buf.put( new byte[] {0,0,0,0,0,0,0,0} );
		
		//Now each field is saved:
		Iterator it = fields.iterator();
		while(it.hasNext()) {
			buf.put((byte[]) it.next());
		}
		
		//APETAGEX------------------
		buf.put( "APETAGEX".getBytes() );  //APETAGEX
		
		//Version 2.0 (aka 2000)
		buf.put( new byte[] {(byte)0xD0,0x07,0x00,0x00} );
		
		//Tag size
		b = new byte[4];
		b[3] = (byte) ( ( size & 0xFF000000 ) >>> 24 );
		b[2] = (byte) ( ( size & 0x00FF0000 ) >>> 16 );
		b[1] = (byte) ( ( size & 0x0000FF00 ) >>> 8 );
		b[0] = (byte) ( size & 0x000000FF );
		buf.put(b);
		
		//Number of fields
		b = new byte[4];
		b[3] = (byte) ( ( listLength & 0xFF000000 ) >>> 24 );
		b[2] = (byte) ( ( listLength & 0x00FF0000 ) >>> 16 );
		b[1] = (byte) ( ( listLength & 0x0000FF00 ) >>> 8 );
		b[0] = (byte) ( listLength & 0x000000FF );
		buf.put( b );
		
		//Flags
		buf.put( new byte[] {0x00,0x00,0x00,(byte)0x80} );
		//means: We have a header and a footer, this is the footer
		
		//Reserved 8-bytes 0x00
		buf.put( new byte[] {0,0,0,0,0,0,0,0} );
		
		//----------------------------------------------------------------------------
	}
	
	protected Tag getCompatibleTag(Tag tag) {
	    if(! (tag instanceof ApeTag)) {
		    ApeTag apeTag = new ApeTag();
		    apeTag.merge(tag);
		    return apeTag;
		}
	    return tag;
	}
	
	protected int getFixedTagLength(Tag tag) {
	    return 64;
	}
}
