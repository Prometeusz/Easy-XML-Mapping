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
 * Represents unmarshaled XML document.
 * </p>
 * 
 * 
 * @author marta
 * 
 */
public interface IXMLRoot {

	/**
	 * Get XML document encoding.
	 * 
	 * @return encoding
	 */
	String getEncoding();



	/**
	 * Get XML document version.
	 * 
	 * @return version
	 */
	String getVersion();



	/**
	 * Set XML document encoding.
	 * 
	 * @param encoding
	 */
	void setEncoding(String encoding);



	/**
	 * Set XML document version.
	 * 
	 * @param version
	 */
	void setVersion(String version);



	/**
	 * Get XML document root element.
	 * 
	 * @return IComposite element.
	 */
	ICompositeTag getXMLRoot();



	/**
	 * Set XML document root element.
	 * 
	 * @param xmlRoot
	 *            root from tree model.
	 */
	void setXMLRoot(ICompositeTag xmlRoot);

}
