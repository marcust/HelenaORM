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
package org.thiesen.helenaorm.mappings;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class URITypeMapping extends AbstractStringBasedTypeMapping<URI> {
    @SuppressWarnings( "unused" )
    private static final Log LOG = LogFactory.getLog( URITypeMapping.class );

    @Override
    protected String asString( final URI value ) {
        return value.toString();
    }

    @Override
    protected URI fromString( final String string ) {
        return URI.create( string );
    }

}
