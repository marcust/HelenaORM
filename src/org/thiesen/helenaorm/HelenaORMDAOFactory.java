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

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thiesen.helenaorm.mappings.IntegerTypeMapping;
import org.thiesen.helenaorm.mappings.LongTypeMapping;
import org.thiesen.helenaorm.mappings.StringTypeMapping;
import org.thiesen.helenaorm.mappings.URITypeMapping;
import org.thiesen.helenaorm.mappings.UUIDTypeMapping;

import com.google.common.collect.ImmutableMap;

public class HelenaORMDAOFactory {
    @SuppressWarnings( "unused" )
    private static final Log LOG = LogFactory.getLog( HelenaORMDAOFactory.class );
    
    private static final Map<Class<?>, TypeMapping<?>> DEFAULT_TYPES = ImmutableMap.<Class<?>, TypeMapping<?>>of(
            String.class, new StringTypeMapping(),
            UUID.class, new UUIDTypeMapping(),
            Long.class, new LongTypeMapping(),
            Integer.class, new IntegerTypeMapping(),
            URI.class, new URITypeMapping()
    );
    
    private final String _hostname;
    private final int _port;
    private final SerializeUnknownClasses _serializationPolicy;
    private final ImmutableMap<Class<?>, TypeMapping<?>> _typeMappings;

    private HelenaORMDAOFactory( final String hostname, final int port,
            final SerializeUnknownClasses serializationPolicy, final Map<Class<?>, TypeMapping<?>> mappings ) {
        _hostname = hostname;
        _port = port;
        _serializationPolicy = serializationPolicy;
        _typeMappings = ImmutableMap.<Class<?>, TypeMapping<?>>builder().putAll( DEFAULT_TYPES ).putAll(  mappings ).build();
    }
    
    public static HelenaORMDAOFactory withConfig( final String hostname, final int port ) {
        return withConfig( hostname, port, SerializeUnknownClasses.YES );
    }
    
    public static HelenaORMDAOFactory withConfig( final String hostname, final int port,
            final SerializeUnknownClasses serializationPolicy ) {
        return new HelenaORMDAOFactory( hostname, port, serializationPolicy, ImmutableMap.<Class<?>, TypeMapping<?>>of() );
    }
    
    public static HelenaORMDAOFactory withConfig( final String hostname, final int port,
            final SerializeUnknownClasses serializationPolicy, final Map<Class<?>,TypeMapping<?>> mappings ) {
        return new HelenaORMDAOFactory( hostname, port, serializationPolicy, mappings );
    }
    
    public <T> HelenaDAO<T> makeDaoForClass( final Class<T> clz ) {
        return new HelenaDAO<T>( clz,
                _hostname, _port, _serializationPolicy, _typeMappings );
    }




}
