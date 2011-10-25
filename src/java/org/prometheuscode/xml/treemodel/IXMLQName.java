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

/**
 * <p>
 * <code>IXMLQName</code> is used for representation of XML specification Qualified Name
 * - QName.
 * </p>
 * <p>
 * Check link for more info: <a href="http://www.w3.org/TR/xmlschema-2/#QName">XML
 * Schema Part2: Datatypes specification</a>
 * </p>
 * 
 * <p>
 * Qualified name contains: <b>Namespace URI</b>, <b>local part</b> and
 * <b>prefix</b>.
 * </p>
 * </br>
 * <p>
 * Classes implementing this interface and using them in *some* collections or maps must
 * override <code>Object.hashCode()</code> and <code>Object.equals()</code>
 * methods.
 * </p>
 * 
 * 
 * @author marta
 * 
 */
public interface IXMLQName {

	/**
	 * Get LocalPart of XML Qualified Name.
	 * 
	 * 
	 * @return XML Qualified Name: <b>LocalPart</b>
	 */
	String getLocalPart();



	/**
	 * Get Namespace URI XML Qualified Name.
	 * 
	 * @return XML Qualified Name: <b>Namespace URI/b>
	 */
	String getNamespaceURI();



	/**
	 * Get prefix XML Qualified Name.
	 * 
	 * @return XML Qualified Name: <b>prefix</b>
	 */
	String getPrefix();



	/**
	 * Set LocalPart of XML Qualified Name.
	 * 
	 * @param localPart
	 *            XML Qualified Name local part
	 */
	void setLocalPart(String localPart);



	/**
	 * Set Namespace URI of XML Qualified Name.
	 * 
	 * @param namespaceURI
	 *            XML Qualified Name namespace URI
	 */
	void setNamespaceURI(String namespaceURI);



	/**
	 * Set prefix of XML Qualified Name.
	 * 
	 * @param prefix
	 *            XML Qualified Name prefix
	 */
	void setPrefix(String prefix);

}
