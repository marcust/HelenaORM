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
import java.util.UUID;

import org.thiesen.helenaorm.annotations.Key;

public class PublicEvent {

    private UUID _id;
    private String _name;
    private String _description;
    private String _moreDescription;
    private URI _url;
    private EventType _type;
    
    
    @Key
    public UUID getId() {
        return _id;
    }
    public void setId( final UUID id ) {
        _id = id;
    }
    public String getName() {
        return _name;
    }
    public void setName( final String name ) {
        _name = name;
    }
    public String getDescription() {
        return _description;
    }
    public void setDescription( final String description ) {
        _description = description;
    }
    
    @Override
    public String toString() {
        return "PublicEvent [_description=" + _description + ", _id=" + _id + ", _moreDescription=" + _moreDescription + ", _name=" + _name + ", _type="
                + _type + ", _url=" + _url + "]";
    }
    
    public void setMoreDescription( final String moreDescription ) {
        _moreDescription = moreDescription;
    }
    public String getMoreDescription() {
        return _moreDescription;
    }
    public void setUrl( final URI url ) {
        _url = url;
    }
    public URI getUrl() {
        return _url;
    }
    public void setType( final EventType type ) {
        _type = type;
    }
    public EventType getType() {
        return _type;
    }
    
    
    
}
