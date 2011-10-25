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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Abstract implementation of IXMLTag.
 * </p>
 * 
 * @author marta
 * 
 */
public abstract class XMLTag implements IXMLTag {

	private IXMLQName name = new XMLQName();

	private Map<String, String> namespaces;

	private Map<IXMLQName, String> attributes;

	private ICompositeTag parent;



	public XMLTag() {
	}



	public XMLTag(IXMLQName name) {
		this.name = name;
	}



	@Override
	public ICompositeTag getParent() {
		return parent;
	}



	@Override
	public void setParent(ICompositeTag parent) {
		this.parent = parent;
	}



	@Override
	public void setName(IXMLQName name) {
		this.name = name;
	}



	@Override
	public IXMLQName getName() {
		return this.name;
	}



	@Override
	public void putNamespace(String prefix, String nsUri) {
		this.namespaces.put(prefix, nsUri);
	}



	@Override
	public void removeNamespace(String prefix) {
		this.namespaces.remove(prefix);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if argument is null.
	 */
	@Override
	public void setNamespaces(Map<String, String> namespaces) {
		if (namespaces == null) {
			throw new IllegalArgumentException("Argument attributes can not be null");
		}

		if (this.namespaces != null) {
			this.namespaces.clear();
			this.namespaces.putAll(namespaces);
		} else {
			this.namespaces = new HashMap<String, String>(namespaces);
		}

	}



	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * It returns null if there are no namespaces.
	 * </p>
	 * 
	 * 
	 */
	@Override
	public Map<String, String> getNamespaces() {
		if (this.namespaces == null) {
			return new HashMap<String, String>();
		}
		return new HashMap<String, String>(this.namespaces);

	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if argument is null.
	 */
	@Override
	public void putAttribute(IXMLQName attrName, String value) {
		if (attrName == null) {
			throw new IllegalArgumentException("Attribute name can not be null");
		}
		this.attributes.put(attrName, value);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if argument is null.
	 */
	@Override
	public void removeAttribute(IXMLQName attrName) {
		if (attrName == null) {
			throw new IllegalArgumentException("Attribute name can not be null");
		}
		this.attributes.remove(attrName);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if argument is null.
	 */
	@Override
	public void setAttributes(Map<IXMLQName, String> attributes) {
		if (attributes == null) {
			throw new IllegalArgumentException("Argument attributes can not be null");
		}

		if (this.attributes != null) {
			this.attributes.clear();
			this.attributes.putAll(attributes);
		} else {
			this.attributes = new HashMap<IXMLQName, String>(attributes);
		}

	}



	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * It returns empty map if there are no attributes.
	 * </p>
	 */
	@Override
	public Map<IXMLQName, String> getAttributes() {
		if (this.attributes == null) {
			return new HashMap<IXMLQName, String>();
		}
		return new HashMap<IXMLQName, String>(this.attributes);
	};



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

		if (obj == null || !(IXMLTag.class.isAssignableFrom(obj.getClass()))) {
			return false;
		}

		return this.name.equals(((IXMLTag) obj).getName());
	}



	/**
	 * Semantics is the same to the QName.equals()
	 * 
	 * @return int hash
	 * 
	 */
	@Override
	public final int hashCode() {
		return this.name.getNamespaceURI().hashCode() ^ this.name.getLocalPart().hashCode();

	}



	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Tag Name: " + this.name.toString() + "\n");
		String attr = (this.attributes != null) ? this.attributes.toString() : "";
		result.append("Tag Attributes: " + attr + "\n");

		String ns = (this.namespaces != null) ? this.namespaces.toString() : "";
		result.append("Tag Namespaces: " + ns + "\n");

		String parent = (this.parent != null) ? this.parent.getName().toString() : "";
		result.append("Tag Parent: " + parent + "\n");

		return result.toString();
	}



	@Override
	public abstract boolean isSimpleTag();

}
