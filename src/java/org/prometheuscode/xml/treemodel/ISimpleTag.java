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
 * Interface representing XML simple tag.
 * </p>
 * <p>
 * Simple tag might have content or be empty.
 * </p>
 * 
 * <pre>
 * For ex.
 *     {@code <sometag>text</sometag> or <sometag/>}
 * </pre>
 * 
 * 
 * 
 * @author marta
 * 
 */
public interface ISimpleTag extends IXMLTag {

	/**
	 * Set Simple Tag value.
	 * 
	 * @param text
	 *            simple tag content, it can be <code>""</code>.
	 */
	void setValue(String text);



	/**
	 * Get Simple Tag Value.
	 * 
	 * @return simple tag value
	 */
	String getValue();
}
