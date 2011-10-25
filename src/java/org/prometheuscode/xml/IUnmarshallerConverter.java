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

import java.util.List;
import java.util.Map;

import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.IXMLQName;

/**
 * <p>
 * It is callback interface the client is suppose to implement if there is a
 * need to convert tree model to an object.
 * </p>
 * <p>
 * It can be registered in proper Adaptive Unmarshaller. Also class implementing
 * this interface must have no-arg constructor.
 * </p>
 * 
 * @author marta
 * 
 */
public interface IUnmarshallerConverter {

	/**
	 * Converts given Composite tag and its children to user object.
	 * 
	 * @param tag
	 * @param convertedObjs
	 * @return
	 */
	Object convert(ICompositeTag tag, Map<IXMLQName, List<Object>> convertedObjs);
}
