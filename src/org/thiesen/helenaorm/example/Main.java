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
        simpleExample();
        superColumnExample();
    }

    private static void superColumnExample() {
        final User admin = new User();
        admin.setType( UserType.ADMINISTRATOR );
        admin.setFirstname( "Emil" );
        admin.setLastname("Admin");
        admin.setUsername( "admin" );
        
        final User normalUser = new User();
        normalUser.setType( UserType.USER );
        normalUser.setFirstname( "Joe" );
        normalUser.setLastname( "Example" );
        normalUser.setUsername( "joex20" );
        
        
        final User normalUseress = new User();
        normalUseress.setType( UserType.USER );
        normalUseress.setFirstname( "Jane" );
        normalUseress.setLastname( "Example" );
        normalUseress.setUsername( "jane73" );
        
        
        final HelenaORMDAOFactory factory = HelenaORMDAOFactory.withConfig(
                "localhost",
                9160, 
                SerializeUnknownClasses.NO );
        
        
        final HelenaDAO<User> userDAO = factory.makeDaoForClass( User.class );
        
        userDAO.insert( admin );
        userDAO.insert( normalUser );
        userDAO.insert( normalUseress );
        
        System.out.println( userDAO.get( UserType.USER.toString(), ImmutableList.of( "jane73", "joex20" ) ) );
    }

    private static void simpleExample() {
        final HelenaORMDAOFactory factory = HelenaORMDAOFactory.withConfig(
                "localhost",
                9160, 
                SerializeUnknownClasses.YES );
        
        final PublicEvent exampleEvent = new PublicEvent();
        exampleEvent.setName( "Session im Irish Rover" );
        exampleEvent.setDescription( "Gute Irische Livemusik" );
        exampleEvent.setId( UUID.randomUUID() );
        exampleEvent.setUrl( URI.create( "http://www.thiesen.org" ) );
        exampleEvent.setType( EventType.CONCERT );
        
        final HelenaDAO<PublicEvent> dao = factory.makeDaoForClass( PublicEvent.class );
        
        dao.insert( exampleEvent );
        
        System.out.println( "Stored as " + exampleEvent.getId() );

        final PublicEvent publicEvent = dao.get( exampleEvent.getId().toString() );
        
        System.out.println( publicEvent );
        
        final List<PublicEvent> events = dao.get( ImmutableList.of( exampleEvent.getId().toString() ) );
        
        System.out.println( events );
        
        dao.delete( exampleEvent );
    }
    
}
