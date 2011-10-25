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

import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;

/**
 * <p>
 * Interface for factory generating tree model classes.
 * </p>
 * 
 * @author marta
 * 
 */
public interface IXMLTreeModelFactory {

	/**
	 * Create composite tag
	 * 
	 * @return Implementation of ICompositeTag
	 */
	ICompositeTag createCompositeTag();



	/**
	 * Create simple tag
	 * 
	 * @return Implementation of ISimpleTag
	 */
	ISimpleTag createSimpleTag();



	/**
	 * Create XML document abstraction
	 * 
	 * @return Implementation of IXMLRoot
	 */
	IXMLRoot createXMLDocument();



	/**
	 * Create XML qualified name
	 * 
	 * @return Implementation of IXMLQName
	 */
	IXMLQName createXMLQName();

}
