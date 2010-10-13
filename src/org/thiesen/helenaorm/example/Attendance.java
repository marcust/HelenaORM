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

import org.thiesen.helenaorm.annotations.ColumnProperty;
import org.thiesen.helenaorm.annotations.HelenaColumnBean;
import org.thiesen.helenaorm.annotations.KeyProperty;
import org.thiesen.helenaorm.annotations.ValueProperty;

@HelenaColumnBean(keyspace = "Keyspace1", columnFamily = "Standard1")
public class Attendance {

	@KeyProperty
	private String user;

	@ColumnProperty
	private String event;

	@ValueProperty
	private Long time;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Attendance [user=" + user + ", event=" + event + ", time=" + time + "]";
	}
	
}
