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
package org.prometheuscode.xml.treemodel;

import java.util.Map;

/**
 * <p>
 * Interface represents generic abstraction over XML Tag.
 * </p>
 * 
 * @author marta
 * 
 */
public interface IXMLTag {

	/**
	 * Get reference of IXMLQName of XML tag.
	 * 
	 * @return IXMLQName tag name.
	 */
	IXMLQName getName();



	/**
	 * Set IXMLQName XML tag name.
	 * 
	 * @param tagName
	 *            IXMLQName tag name
	 */
	void setName(IXMLQName tagName);



	/**
	 * <p>
	 * Get a copy of Namespaces on this Tag.
	 * </p>
	 * 
	 * 
	 * <p>
	 * Key is a <b>prefix</b> of namespace URI.</br> Value is a <b>Namespace
	 * URI.</b>
	 * </p>
	 * 
	 * <p>
	 * xmlns can be seen as a <code>""</code> prefix.
	 * </p>
	 * 
	 * @return Map < prefix --> namespaceURI >
	 */
	Map<String, String> getNamespaces();



	/**
	 * Set namespaces.
	 * 
	 * @param namespaces
	 */
	void setNamespaces(Map<String, String> namespaces);



	/**
	 * <p>
	 * Get a copy of tag attributes
	 * </p>
	 * 
	 * 
	 * 
	 * @return Map < IXMLQName --> attribute value >
	 */
	Map<IXMLQName, String> getAttributes();



	/**
	 * 
	 * 
	 * @param attributes
	 */
	void setAttributes(Map<IXMLQName, String> attributes);



	/**
	 * Set Tag ICompositeTag as parent.
	 * 
	 * @param parent
	 */
	void setParent(ICompositeTag parent);



	/**
	 * Get Tag parent.
	 * 
	 * @return tag parent.
	 */
	ICompositeTag getParent();



	/**
	 * Is this tag simple tag?
	 * 
	 * @return
	 */
	boolean isSimpleTag();



	/**
	 * Put on the tag namespace with given prefix and URI.
	 * 
	 * @param prefix
	 * @param nsUri
	 */
	void putNamespace(String prefix, String nsUri);



	/**
	 * Remove Namespace.
	 * 
	 * @param prefix
	 */
	void removeNamespace(String prefix);



	/**
	 * Put on the tag attribute with given name and value.
	 * 
	 * @param prefix
	 * @param nsUri
	 */
	void putAttribute(IXMLQName attrName, String value);



	/**
	 * Remove attribute.
	 * 
	 * @param prefix
	 */
	void removeAttribute(IXMLQName attrName);

}