/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
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
 *  along with ecj4ant.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.thiesen.helenaorm;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;


public class MarshalledObject {

    private byte[] _id;
    private final Map<String, byte[]> _values = Maps.newHashMap();
    
    public static MarshalledObject create() {
        return new MarshalledObject();
    }

    public void setId( final byte[] value ) {
        _id = value;
        
    }

    public void addValue( final String name, final byte[] value ) {
        if ( _values.put( name, value ) != null ) {
            throw new HelenaRuntimeException("Property with name " + name + " had already" +
            		" a value, overwriting is illegal");
        }
        
    }

    public byte[] getIdAsByteArray() {
        return _id;
    }

    public Set<Map.Entry<String,byte[]>> getEntries() {
        return Collections.unmodifiableSet( _values.entrySet() );
        
    }


}