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
import entagged.audioformats.generic.TagTextField;


public class ApeTagTextField extends ApeTagField implements TagTextField  {
    
    private String content;

    public ApeTagTextField(String id, String content) {
        super(id, false);
        this.content = content;
    }
    
    public boolean isEmpty() {
        return this.content.equals("");
    }
    
    public String toString() {
        return this.content;
    }
    
    public void copyContent(TagField field) {
        if(field instanceof ApeTagTextField) {
            this.content = ((ApeTagTextField)field).getContent();
        }
    }
    
    public String getContent() {
        return this.content;
    }

    public String getEncoding() {
        return "UTF-8";
    }
    public void setEncoding(String s) {
        /* Not allowed */
    }

    public void setContent(String s) {
        this.content = s;
    }
    
    public byte[] getRawContent() throws UnsupportedEncodingException {
        byte[] idBytes = getBytes(getId(), "ISO-8859-1");
        byte[] contentBytes = getBytes(content, getEncoding());
		byte[] buf = new byte[4 + 4 + idBytes.length + 1 + contentBytes.length];
		byte[] flags = {0x00,0x00,0x00,0x00};
		
		int offset = 0;
		copy(getSize(contentBytes.length), buf, offset);offset += 4;
		copy(flags, buf, offset);                       offset += 4;
		copy(idBytes, buf, offset);                     offset += idBytes.length;
		buf[offset] = 0;                                offset += 1;
		copy(contentBytes, buf, offset);                offset += contentBytes.length;
		
		return buf;
    }
}