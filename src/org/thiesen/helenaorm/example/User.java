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

import org.thiesen.helenaorm.annotations.HelenaBean;
import org.thiesen.helenaorm.annotations.KeyProperty;
import org.thiesen.helenaorm.annotations.SuperColumnProperty;

@HelenaBean( keyspace="Keyspace1", columnFamily="Super1")
public class User {

    private UserType _type;
    private String _username;
    private String _firstname;
    private String _lastname;
    
    @KeyProperty
    public UserType getType() {
        return _type;
    }
    public void setType( final UserType type ) {
        _type = type;
    }
    
    @SuperColumnProperty
    public String getUsername() {
        return _username;
    }
    public void setUsername( final String username ) {
        _username = username;
    }
    public String getFirstname() {
        return _firstname;
    }
    public void setFirstname( final String firstname ) {
        _firstname = firstname;
    }
    public String getLastname() {
        return _lastname;
    }
    public void setLastname( final String lastname ) {
        _lastname = lastname;
    }
    
    @Override
    public String toString() {
        return "User [_firstname=" + _firstname + ", _lastname=" + _lastname + ", _type=" + _type + ", _username=" + _username + "]";
    }
    
    
    
    
}
