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
package org.prometheuscode.xml.annotation;

/**
 * This exceptions is thrown when user annotation was in incorrect state or on
 * incorrect type.
 * 
 * 
 * @author marta
 * 
 */
public class AnnotationException extends RuntimeException {

	public AnnotationException() {
		super();
	}



	public AnnotationException(String msg) {
		super(msg);

	}



	public AnnotationException(Throwable exp) {
		super(exp);

	}



	public AnnotationException(String msg, Throwable exp) {
		super(msg, exp);

	}
}
