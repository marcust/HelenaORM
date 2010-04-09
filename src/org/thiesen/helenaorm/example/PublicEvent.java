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
import java.util.UUID;

import org.thiesen.helenaorm.annotations.HelenaBean;
import org.thiesen.helenaorm.annotations.KeyProperty;

@HelenaBean( keyspace="Keyspace1", columnFamily="Standard1")
public class PublicEvent {

    private UUID _id;
    private String _name;
    private String _description;
    private String _moreDescription;
    private URI _url;
    private EventType _type;
    
    
    @KeyProperty
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
