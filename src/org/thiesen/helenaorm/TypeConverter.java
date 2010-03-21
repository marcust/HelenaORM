/*
 * $ Id $
 * (c) Copyright 2010 freiheit.com technologies gmbh
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies gmbh. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies gmbh.
 *
 * Initial version by Marcus Thiesen (marcus.thiesen@freiheit.com)
 */
package org.thiesen.helenaorm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

class TypeConverter {

    private static final byte[] EMPTY_BYTES = new byte[0];
    
    private final ImmutableMap<Class<?>, TypeMapping<?>> _typeMappings;
    private final SerializeUnknownClasses _serializationPolicy;

    public TypeConverter( final ImmutableMap<Class<?>, TypeMapping<?>> typeMappings,
            final SerializeUnknownClasses serializationPolicy ) {
        _serializationPolicy = serializationPolicy;
        _typeMappings = typeMappings;
    }



    byte[] convertValueObjectToByteArray( final Object propertyValue ) {
        if ( propertyValue == null ) {
            return EMPTY_BYTES;
        }
        if ( _typeMappings.containsKey( propertyValue.getClass() ) ) {
            return _typeMappings.get( propertyValue.getClass() ).toBytes( propertyValue );
        }
        if ( Enum.class.isAssignableFrom( propertyValue.getClass() ) ) {
            return stringToBytes( ((Enum<?>)propertyValue).name() );
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

    String bytesToString( final byte[] bytes ) {
        return (String)_typeMappings.get( String.class ).fromBytes( bytes );
    }

    byte[] stringToBytes( final String string ) {
        return _typeMappings.get( String.class ).toBytes( string );
    }
    
    Object convertByteArrayToValueObject( final Class<?> returnType, final byte[] value ) {
        if ( _typeMappings.containsKey( returnType ) ) {
            return returnType.cast(_typeMappings.get( returnType ).fromBytes( value ) );
        }
        if ( returnType.isEnum() ) {
            return makeEnumInstance( returnType, value );
        }
        if ( Serializable.class.isAssignableFrom( returnType ) ) {
            return returnType.cast( deserialize( value ) );
        }

        throw new HelenaRuntimeException("Can not handle type " + returnType.getClass() + ", maybe you have getters and setters with different Types? Otherwise, add a Type mapping");
    }
    
    private Enum<?> makeEnumInstance( final Class<?> returnType, final byte[] value ) {
        try {
            final Method method = returnType.getMethod( "valueOf", String.class );
            
            return (Enum<?>) method.invoke( returnType, bytesToString( value ) );
            
        } catch ( final SecurityException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final NoSuchMethodException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final IllegalArgumentException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final IllegalAccessException e ) {
            throw new HelenaRuntimeException( e );
        } catch ( final InvocationTargetException e ) {
            throw new HelenaRuntimeException( e );
        }
        
        
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



    public Function<String,byte[]> toByteArrayFunction() {
        return new Function<String, byte[]>() {

            @Override
            public byte[] apply( final String arg0 ) {
                return stringToBytes( arg0 );
            }
            
        };
        
    }



}
