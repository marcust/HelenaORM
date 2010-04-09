/*
 * The MIT License
 *
 * Copyright (c) 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 * This file is part of HelenaORM.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.thiesen.helenaorm;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;


class MarshalledObject {

    private byte[] _key;
    private byte[] _superColumn;
    private final Map<String, byte[]> _values = Maps.newHashMap();
    
    static MarshalledObject create() {
        return new MarshalledObject();
    }

    void setKey( final byte[] value ) {
        _key = value;
        
    }

    void addValue( final String name, final byte[] value ) {
        if ( _values.put( name, value ) != null ) {
            throw new HelenaRuntimeException("Property with name " + name + " had already" +
            		" a value, overwriting is illegal");
        }
        
    }

    byte[] getKey() {
        return _key;
    }

    Set<Map.Entry<String,byte[]>> getEntries() {
        return Collections.unmodifiableSet( _values.entrySet() );
        
    }

    public void setSuperColumn( final byte[] superColumn ) {
        _superColumn = superColumn;
    }

    public byte[] getSuperColumn() {
        return _superColumn;
    }

    public boolean isSuperColumnPresent() {
        return _superColumn != null;
    }


}
