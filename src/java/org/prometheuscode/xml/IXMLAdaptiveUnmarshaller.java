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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;

/**
 * Interface for converting XML files to user objects or tree model.
 * 
 * @author marta
 * 
 */
public interface IXMLAdaptiveUnmarshaller {

	/**
	 * Convert given file to intermediate representation.
	 * 
	 * @param xmlPath
	 *            full path to XML file
	 * @return IXMLRoot
	 */
	IXMLRoot getTreeModel(String xmlPath);



	IXMLRoot getTreeModel(InputStream fileStream);



	/**
	 * Convert given XML file to user objects.
	 * 
	 * @param xmlPath
	 *            full path to xml file
	 * @return
	 */
	Map<IXMLQName, List<Object>> unmarshal(String xmlPath);



	Map<IXMLQName, List<Object>> unmarshal(InputStream fileStream);



	/**
	 * Register converters.
	 * 
	 * @param converters
	 */
	void registerMapping(Map<IXMLQName, IUnmarshallerConverter> converters);



	/**
	 * Create converters mapping.
	 * 
	 * <p>
	 * User supplies list of package names which have annoted classes, then
	 * mappings are generated. User {@link IUnmarshallerConverter} class should
	 * have no-arg constructor.
	 * </p>
	 * 
	 * @param converters
	 */
	Map<IXMLQName, IUnmarshallerConverter> createMapping(Set<String> packageNames);



	/**
	 * Get registered converters.
	 * 
	 * @return
	 */
	Map<IXMLQName, IUnmarshallerConverter> getRegisteredMapping();

}
