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
 * 
 * <p>
 * Implements IXMLQName.
 * </p>
 * 
 * <p>
 * It is important to implement <code>equals()</code> and
 * <code>hashCode()</code> using unique properties of this class:
 * <ul>
 * <li>namespace URI</li>
 * <li>XML tag local part</li>
 * </ul>
 * </p>
 * 
 * @author marta
 * @see org.prometheuscode.xml.treemodel.IXMLQName
 */

public class XMLQName implements IXMLQName {

	private String localPart = "";

	private String namespaceURI = "";

	private String prefix = "";



	public XMLQName() {

	}



	public XMLQName(String localPart, String namespaceURI, String prefix) {
		this.localPart = localPart;
		this.namespaceURI = namespaceURI;
		this.prefix = prefix;
	}



	/**
	 * Make a copy of given IXMLQname
	 * 
	 * @param qname
	 */
	public XMLQName(IXMLQName qname) {
		this.localPart = qname.getLocalPart();
		this.namespaceURI = qname.getNamespaceURI();
		this.prefix = qname.getPrefix();
	}



	@Override
	public String getLocalPart() {
		return this.localPart;
	}



	@Override
	public String getNamespaceURI() {
		return namespaceURI;
	}



	@Override
	public String getPrefix() {
		return prefix;
	}



	@Override
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}



	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}



	@Override
	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}



	/**
	 * Semantics is the same to the QName.equals()
	 * 
	 * @return boolean is object equal?
	 * 
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || !(IXMLQName.class.isAssignableFrom(obj.getClass()))) {
			return false;
		}

		IXMLQName qName = (IXMLQName) obj;

		return (this.localPart.equals(qName.getLocalPart()) && this.namespaceURI.equals(qName.getNamespaceURI()));

	}



	/**
	 * Semantics is the same to the QName.equals()
	 * 
	 * @return int hash
	 * 
	 */
	@Override
	public final int hashCode() {
		return this.namespaceURI.hashCode() ^ this.localPart.hashCode();

	}



	@Override
	public String toString() {
		return this.prefix + "{" + this.namespaceURI + "}" + this.localPart;

	}

}
