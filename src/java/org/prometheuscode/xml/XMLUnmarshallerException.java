/*
 * Copyright (C) 2011 Marta Spodymek
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.prometheuscode.xml;

/**
 * Exception thrown during unmarshaling an object.
 * 
 * @author marta
 * 
 */
public class XMLUnmarshallerException extends RuntimeException {

	private static final long serialVersionUID = 4670740165592084576L;



	public XMLUnmarshallerException() {
		super();
	}



	public XMLUnmarshallerException(String msg) {
		super(msg);

	}



	public XMLUnmarshallerException(Throwable exp) {
		super(exp);

	}



	public XMLUnmarshallerException(String msg, Throwable exp) {
		super(msg, exp);

	}
}
