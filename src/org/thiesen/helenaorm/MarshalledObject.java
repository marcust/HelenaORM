/*
 * $ Id $
 * (c) Copyright 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of HelenaORM.
 *
 *  HelenaORM is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  HelenaORM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HelenaORM.  If not, see <http://www.gnu.org/licenses/>.
 *
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
