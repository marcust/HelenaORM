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
