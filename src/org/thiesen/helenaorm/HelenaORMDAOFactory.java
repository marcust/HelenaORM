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
	private String[] _nodes = null;
    
    private final SerializeUnknownClasses _serializationPolicy;
    private final ImmutableMap<Class<?>, TypeMapping<?>> _typeMappings;

    private HelenaORMDAOFactory( final String hostname, final int port,
            final SerializeUnknownClasses serializationPolicy, final Map<Class<?>, TypeMapping<?>> mappings ) {
        _hostname = hostname;
        _port = port;
        _serializationPolicy = serializationPolicy;
        _typeMappings = ImmutableMap.<Class<?>, TypeMapping<?>>builder().putAll( DEFAULT_TYPES ).putAll(  mappings ).build();
    }
    
    private HelenaORMDAOFactory( final String[] nodes,
            final SerializeUnknownClasses serializationPolicy, final Map<Class<?>, TypeMapping<?>> mappings ) {
    	
    	this(null, 0, serializationPolicy, mappings);
    	this._nodes = nodes;
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
    
	public static HelenaORMDAOFactory withConfig(final String[] nodes) {
        return withConfig(nodes, SerializeUnknownClasses.YES);
	}

	public static HelenaORMDAOFactory withConfig(final String[] nodes,
			SerializeUnknownClasses serializationPolicy) {
        return new HelenaORMDAOFactory(nodes, serializationPolicy, ImmutableMap.<Class<?>, TypeMapping<?>>of());
	}

	public static HelenaORMDAOFactory withConfig(final String[] nodes,
			SerializeUnknownClasses serializationPolicy, final Map<Class<?>,TypeMapping<?>> mappings) {
        return new HelenaORMDAOFactory(nodes, serializationPolicy, mappings);
	}

    public <T> HelenaDAO<T> makeDaoForClass( final Class<T> clz ) {
    	if (this._nodes == null) {
    		return new HelenaDAO<T>( clz, _hostname, _port, _serializationPolicy, _typeMappings );
    	} else {
    		return new HelenaDAO<T>( clz, _nodes, _serializationPolicy, _typeMappings );
    	}
    }

}