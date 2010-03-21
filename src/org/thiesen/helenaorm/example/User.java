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
package org.thiesen.helenaorm.example;

import org.thiesen.helenaorm.annotations.Key;
import org.thiesen.helenaorm.annotations.SuperColumnProperty;


public class User {

    private UserType _type;
    private String _username;
    private String _firstname;
    private String _lastname;
    
    @Key
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
