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

import java.io.OutputStream;

import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.IXMLTag;

/**
 * Interface for writing objects to XML file.
 * 
 * @author marta
 * 
 */
public interface IXMLAdaptiveMarshaller {

	/**
	 * Write given tree model to output stream.
	 * 
	 * @param document
	 * @param out
	 */
	void saveTreeModel(IXMLRoot document, OutputStream out);



	/**
	 * Marshall given object to input stream.
	 * 
	 * 
	 * @param objectToSave
	 * @param out
	 */
	void marshal(Object objectToSave, OutputStream out);



	/**
	 * It uses registered mapping to convert given object to tree model.
	 * 
	 * @param obj
	 * @return
	 */
	IXMLTag getTreeModelFromObject(Object obj);



	/**
	 * Register mapping to use for converting to XML.
	 * 
	 * @param converters
	 */
	void registerMapping(IMapping<?> mapping);



	/**
	 * Create mapping for given class.
	 * 
	 * 
	 * @param rootClassName
	 *            class name for which build mapping.
	 */
	<T> IMapping<T> createMapping(Class<T> rootClazz);



	/**
	 * Get Registered mapping.
	 * 
	 * @return
	 */
	IMapping<?> getRegisteredMapping();

}
