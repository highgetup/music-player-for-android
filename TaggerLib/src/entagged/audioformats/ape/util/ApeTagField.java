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

import entagged.audioformats.generic.TagField;

public abstract class ApeTagField implements TagField {
    
    private String id;
    private boolean binary;
    
    public ApeTagField(String id, boolean binary) {
        if (id.length() == 0) {
            this.id = id;
        } else if (id.length() == 1) {
            this.id = id.toUpperCase();
        } else {
            this.id = id.substring(0, 1).toUpperCase() + id.substring(1).toLowerCase();
        }
        this.binary = binary;
    }

    public String getId() {
        return this.id;
    }

    public boolean isBinary() {
        return binary;
    }

    public void isBinary(boolean b) {
        this.binary = b;
    }

    public boolean isCommon() {
        return id.equals("Title") ||
		  id.equals("Album") ||
		  id.equals("Artist") ||
		  id.equals("Genre") ||
		  id.equals("Track") ||
		  id.equals("Year") ||
		  id.equals("Comment");
    }

    protected void copy(byte[] src, byte[] dst, int dstOffset) {
		for(int i = 0; i<src.length; i++)
			dst[i+dstOffset] = src[i];
	}
	
	protected byte[] getSize(int size) {
		byte[] b = new byte[4];
		b[3] = (byte) ( ( size & 0xFF000000 ) >> 24 );
		b[2] = (byte) ( ( size & 0x00FF0000 ) >> 16 );
		b[1] = (byte) ( ( size & 0x0000FF00 ) >> 8 );
		b[0] = (byte) (   size & 0x000000FF );
		return b;
	}
	
	protected byte[] getBytes(String s, String encoding) throws UnsupportedEncodingException{
		return s.getBytes(encoding);
	}
	
    public abstract boolean isEmpty();
    public abstract String toString();
    public abstract void copyContent(TagField field);
    public abstract byte[] getRawContent() throws UnsupportedEncodingException;
}
