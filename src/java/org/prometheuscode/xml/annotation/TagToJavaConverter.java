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

import java.lang.annotation.*;

/**
 * <p>
 * This annotation can be specified on the top of IUnmarshallerConverter along
 * with XML qualified name.
 * </p>
 * 
 * <p>
 * This annotation is used for "automatic" registration in
 * IXMLAdaptiveUnmarshaller.
 * </p>
 * 
 * 
 * 
 * @author marta
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TagToJavaConverter {

	XMLQualifiedName value();

	/**
	 * Specifies XML Qualified Name.
	 * 
	 * @author marta
	 *
	 */
	public static @interface XMLQualifiedName {
		String localPart();



		String namespaceURI();
	}
}
