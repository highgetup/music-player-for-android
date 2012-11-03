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


public class ApeTagBinaryField extends ApeTagField  {
    
    private byte[] content;

    public ApeTagBinaryField(String id, byte[] content) {
        super(id, true);
        this.content = new byte[content.length];
        for(int i = 0; i<content.length; i++)
            this.content[i] = content[i];
    }
    
    public boolean isEmpty() {
        return this.content.length == 0;
    }
    
    public String toString() {
        return "Binary field";
    }
    
    public void copyContent(TagField field) {
        if(field instanceof ApeTagBinaryField) {
            this.content = ((ApeTagBinaryField)field).getContent();
        }
    }
    
    public byte[] getContent() {
        return this.content;
    }
    
    public byte[] getRawContent() throws UnsupportedEncodingException {
        byte[] idBytes = getBytes(getId(), "ISO-8859-1");
        byte[] buf = new byte[4 + 4 + idBytes.length + 1 + content.length];
		byte[] flags = {0x02,0x00,0x00,0x00};
		
		int offset = 0;
		copy(getSize(content.length), buf, offset);    offset += 4;
		copy(flags, buf, offset);                      offset += 4;
		copy(idBytes, buf, offset);                    offset += idBytes.length;
		buf[offset] = 0;                               offset += 1;
		copy(content, buf, offset);                    offset += content.length;
		
		return buf;
    }
}
