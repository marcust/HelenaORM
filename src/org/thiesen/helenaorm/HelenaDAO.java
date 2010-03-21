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

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.service.Keyspace;

import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.NotFoundException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet.Builder;


public class HelenaDAO<T> {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private final String _hostname;
    private final int _port;
    private final String _keyspace;
    private final SerializeUnknownClasses _serializationPolicy;
    private final ImmutableMap<Class<?>, TypeMapping<?>> _typeMappings;
    private final String _columnFamily;
    private final PropertyDescriptor[] _propertyDescriptors;
    private final ImmutableList<byte[]> _columnNames;
    private final Class<T> _clz;
    private PropertyDescriptor _keyPropertyDescriptor;

    HelenaDAO( final Class<T> clz, final String columnFamily, final String hostname, final int port, final String keyspace, final SerializeUnknownClasses serializationPolicy,
            final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings ) {
        _clz = clz;
        _propertyDescriptors = PropertyUtils.getPropertyDescriptors( clz );
        _typeMappings = typeMappings;
        _columnFamily = columnFamily;
        _hostname = hostname;
        _port = port;
        _keyspace = keyspace;
        _serializationPolicy = serializationPolicy;

        final Builder<byte[]> setBuilder = ImmutableSet.<byte[]>builder();
        for ( final PropertyDescriptor descriptor : _propertyDescriptors ) {
            setBuilder.add( stringToBytes( descriptor.getName() ) );
            if ( isKeyProperty( descriptor  ) ) {
                _keyPropertyDescriptor = descriptor;
            }
        }
        _columnNames = ImmutableList.copyOf( setBuilder.build() );

        if ( _keyPropertyDescriptor == null ) {
            throw new HelenaRuntimeException("Could not find key of class " + clz.getName() + ", did you annotate with @Key" );
        }
    }

    public void insert( final T object ) {

        final MarshalledObject marshalledObject = MarshalledObject.create();

        for ( final PropertyDescriptor d : _propertyDescriptors ) {
            if ( isReadWrite( d ) ) {
                try {
                    final String name = d.getName();
                    final byte[] value = typeConvert( PropertyUtils.getProperty( object, name ) );
                    if ( isKeyProperty( d ) ) {
                        marshalledObject.setId( value );
                    } else {
                        marshalledObject.addValue( name, value );
                    }

                } catch ( final NoSuchMethodException e ) {
                    throw new HelenaRuntimeException( e );
                } catch ( final IllegalAccessException e ) {
                    throw new HelenaRuntimeException( e );
                } catch ( final InvocationTargetException e ) {
                    throw new HelenaRuntimeException( e );
                }
            }
        }

        store( marshalledObject );

    }

    private byte[] typeConvert( final Object propertyValue ) {
        if ( propertyValue == null ) {
            return EMPTY_BYTES;
        }
        if ( _typeMappings.containsKey( propertyValue.getClass() ) ) {
            return _typeMappings.get( propertyValue.getClass() ).toBytes( propertyValue );
        }
        if ( propertyValue instanceof Serializable && _serializationPolicy == SerializeUnknownClasses.YES ) {
            return serialize( propertyValue );
        }

        throw new HelenaRuntimeException("Can not map " + propertyValue.getClass() + " instance to byte array, either implement serializable or create a custom type mapping!");


    }

    private byte[] serialize( final Object propertyValue ) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream( out );

            oout.writeObject( propertyValue );
            oout.close();

            return out.toByteArray();
        } catch ( final IOException e ) {
            throw new HelenaRuntimeException( "Unable to Serialize object of type " + propertyValue.getClass() , e );
        }

    }

    private boolean isKeyProperty( final PropertyDescriptor d ) {
        return d.getReadMethod().getAnnotation( Key.class ) != null;
    }

    private boolean isReadWrite( final PropertyDescriptor d ) {
        return d.getReadMethod() != null && d.getWriteMethod() != null;
    }

    private void store( final MarshalledObject marshalledObject ) {
        final byte[] idColumn = marshalledObject.getIdAsByteArray();
        final List<Column> columnList = Lists.newLinkedList();
        final long timestamp = System.currentTimeMillis();
        for ( final Map.Entry<String, byte[]> property : marshalledObject.getEntries() ) {
            columnList.add( toColumn( property, timestamp ) );
        }
        final Map<String, List<Column>> columnMap = ImmutableMap.<String,List<Column>>of( _columnFamily, columnList );

        try {
            execute(new Command<Void>(){
                @Override
                public Void execute(final Keyspace ks) throws Exception {
                    ks.batchInsert( bytesToString( idColumn ), 
                            columnMap, null );

                    return null;
                }
            } );
        } catch ( final Exception e ) {
            throw new HelenaRuntimeException(e);
        }

    }
    private String bytesToString( final byte[] bytes ) {
        return (String)_typeMappings.get( String.class ).fromBytes( bytes );
    }

    private byte[] stringToBytes( final String string ) {
        return _typeMappings.get( String.class ).toBytes( string );
    }


    private Column toColumn( final Entry<String, byte[]> property, final long timestamp ) {
        return new Column( stringToBytes( property.getKey() ), property.getValue(), timestamp );

    }

    private <V> V execute(final Command<V> command) throws Exception {
        return command.execute(_hostname, _port, _keyspace);
    }

    public T get(final String key) {
        final ColumnParent parent = makeColumnParent();
        final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();

        try {
            return execute(new Command<T>(){
                @Override
                public T execute(final Keyspace ks) throws Exception {
                    try {
                        final List<Column> slice = ks.getSlice( key, parent , predicate );


                        return applyColumns( key, slice );
                    } catch (final NotFoundException e) {
                        return null;
                    }
                }
            }); 
        } catch ( final Exception e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    private T applyColumns( final String key, final List<Column> slice ) {
        try {
            final T newInstance = _clz.newInstance();

            PropertyUtils.setProperty( newInstance, _keyPropertyDescriptor.getName(),
                    convert( _keyPropertyDescriptor.getReadMethod().getReturnType(),
                            stringToBytes( key ) ) );

            for ( final Column c : slice ) {
                final String name = bytesToString( c.name );
                if ( PropertyUtils.isWriteable( newInstance, name ) ) {
                    final PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor( newInstance, name );
                    final Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();
                    PropertyUtils.setProperty( newInstance, name, convert( returnType, c.value ) );
                }
            }

            return newInstance;

        } catch ( final InstantiationException e ) {
            throw new HelenaRuntimeException("Could not instanciate " + _clz.getName(), e );
        } catch ( final IllegalAccessException e ) {
            throw new HelenaRuntimeException("Could not instanciate " + _clz.getName(), e );
        } catch ( final InvocationTargetException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final NoSuchMethodException e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    private Object convert( final Class<?> returnType, final byte[] value ) {
        if ( _typeMappings.containsKey( returnType ) ) {
            return returnType.cast(_typeMappings.get( returnType ).fromBytes( value ) );
        }
        if ( Serializable.class.isAssignableFrom( returnType ) ) {
            return returnType.cast( deserialize( value ) );
        }

        throw new HelenaRuntimeException("Can not handle type " + returnType.getClass() + ", maybe you have getters and setters with different Types? Otherwise, add a Type mapping");
    }

    private Object deserialize( final byte[] value ) {
        final ByteArrayInputStream in = new ByteArrayInputStream( value );
        try {
            final ObjectInputStream oin = new ObjectInputStream( in );

            final Object retval = oin.readObject();

            oin.close();

            return retval;
        } catch ( final IOException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final ClassNotFoundException e ) {
            throw new HelenaRuntimeException( e );
        }


    }

    public void delete( final T object ) {
        delete( getKeyFrom( object ) );
    }

    private String getKeyFrom( final T object ) {
        try {
            return bytesToString( typeConvert( PropertyUtils.getProperty( object, _keyPropertyDescriptor.getName() ) ) );
        } catch ( final IllegalAccessException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final InvocationTargetException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final NoSuchMethodException e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    public void delete( final String key ) {
        try {
            execute(new Command<Void>(){
                @Override
                public Void execute(final Keyspace ks) throws Exception {
                    ks.remove( key, new ColumnPath( _columnFamily, null, null ) );
                    return null;
                }
            });
        } catch ( final Exception e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    public List<T> get( final List<String> keys ) {
        final ColumnParent parent = makeColumnParent();
        final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();
        try {
            return execute(new Command<List<T>>(){
                @Override
                public List<T> execute(final Keyspace ks) throws Exception {

                    final Map<String,List<Column>> slice = ks.multigetSlice( keys, parent , predicate );

                    return convertToList( slice );

                }
            }); 
        } catch ( final Exception e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    public List<T> getRange( final String keyStart, final String keyEnd, final int amount ) {
        final ColumnParent parent = makeColumnParent();
        final SlicePredicate predicate = makeSlicePredicateWithAllPropertyColumns();
        try {
            return execute(new Command<List<T>>(){
                @Override
                public List<T> execute(final Keyspace ks) throws Exception {

                    final Map<String,List<Column>> slice = ks.getRangeSlice( parent, predicate, keyStart, keyEnd , amount );

                    return convertToList( slice );

                }
            }); 
        } catch ( final Exception e ) {
            throw new HelenaRuntimeException( e );
        }
    }

    private SlicePredicate makeSlicePredicateWithAllPropertyColumns() {
        final SlicePredicate predicate = new SlicePredicate();
        predicate.setColumn_names( _columnNames );
        return predicate;
    }

    private ColumnParent makeColumnParent() {
        final ColumnParent parent = new ColumnParent();
        parent.setColumn_family( _columnFamily );
        return parent;
    }

    
    
    private List<T> convertToList( final Map<String, List<Column>> slice ) {
        final ImmutableList.Builder<T> listBuilder = ImmutableList.<T>builder();
        for ( final Map.Entry<String, List<Column>> entry : slice.entrySet() ) {
            listBuilder.add( applyColumns( entry.getKey(), entry.getValue() ) ); 
        }
        return listBuilder.build();
    }



}
