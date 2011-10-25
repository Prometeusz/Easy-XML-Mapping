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

import java.util.Map;

import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.IXMLTag;

/**
 * <p>
 * Converter to help turning Object into tree model representation.
 * </p>
 * <p>
 * Class implementing this interface must have no-arg constructor.
 * </p>
 * 
 * @author marta
 * 
 */
public interface IMarshallerConverter<T> {

	/**
	 * Convert object to IComposite tree tag.
	 * 
	 * @param objectToConvert
	 * @param convertedObjects
	 *            map of converted objects for which
	 *            getter method and converter mapping was registered. 
	 *            Key is a method name, value is converted object as IXMLTag.
	 * 
	 * @TODO Try to change return value to simple IXMLTag
	 */
	IXMLTag convert(T objectToConvert, Map<String, IXMLTag> convertedObjects);
}
