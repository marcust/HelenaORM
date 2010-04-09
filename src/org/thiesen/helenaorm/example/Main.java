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
