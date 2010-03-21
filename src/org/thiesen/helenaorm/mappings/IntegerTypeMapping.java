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
package org.thiesen.helenaorm.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntegerTypeMapping extends AbstractStringBasedTypeMapping<Integer> {
    @SuppressWarnings( "unused" )
    private static final Log LOG = LogFactory.getLog( IntegerTypeMapping.class );

    @Override
    protected String asString( final Integer value ) {
        return value.toString();

    }

    @Override
    protected Integer fromString( final String string ) {
        return Integer.valueOf( string );
    }

}
