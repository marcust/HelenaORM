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
package org.thiesen.helenaorm.mappings;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thiesen.helenaorm.TypeMapping;

public class StringTypeMapping implements TypeMapping<String> {
    @SuppressWarnings( "unused" )
    private static final Log LOG = LogFactory.getLog( StringTypeMapping.class );

    private static final Charset DEFAULT_CHARSET = Charset.forName( "UTF-8");

    @Override
    public String fromBytes( final byte[] value ) {
        if ( value.length == 0 ) {
            return null;
        }
        return new String( value, DEFAULT_CHARSET );
    }

    @Override
    public byte[] toBytes( final Object value ) {
        if ( value == null ) {
            return new byte[0];
        }
        return ((String)value).getBytes( DEFAULT_CHARSET );
    }

}
