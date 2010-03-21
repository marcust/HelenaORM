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
package org.thiesen.helenaorm.example;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.thiesen.helenaorm.HelenaDAO;
import org.thiesen.helenaorm.HelenaORMDAOFactory;
import org.thiesen.helenaorm.SerializeUnknownClasses;

import com.google.common.collect.ImmutableList;


public class Main {

    public static void main( final String... args ) {
        
        final HelenaORMDAOFactory factory = HelenaORMDAOFactory.withConfig(
                "localhost",
                9160, 
                "Keyspace1", SerializeUnknownClasses.YES );
        
        final PublicEvent exampleEvent = new PublicEvent();
        exampleEvent.setName( "Session im Irish Rover" );
        exampleEvent.setDescription( "Gute Irische Livemusik" );
        exampleEvent.setId( UUID.randomUUID() );
        exampleEvent.setUrl( URI.create( "http://www.thiesen.org" ) );
        
        final HelenaDAO<PublicEvent> dao = factory.forClassAndColumnFamily( PublicEvent.class, "Standard1" );
        
        dao.insert( exampleEvent );
        
        System.out.println( "Stored as " + exampleEvent.getId() );

        final PublicEvent publicEvent = dao.get( exampleEvent.getId().toString() );
        
        System.out.println( publicEvent );
        
        final List<PublicEvent> events = dao.get( ImmutableList.of( exampleEvent.getId().toString() ) );
        
        System.out.println( events );
        
        dao.delete( exampleEvent );
        
        
    }
    
}
