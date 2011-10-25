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

import java.util.List;

/**
 * <p>
 * This interface provides methods allowing access to Composite/Complex Tag
 * children and manage children ordering.
 * </p>
 * 
 * 
 * @author marta
 */
public interface ICompositeTag extends IXMLTag {

	/**
	 * Add Tag.
	 * 
	 * @param tag
	 * @return true if operation was successful, false otherwise
	 */
	boolean addTag(IXMLTag tag);



	/**
	 * Get current tags ordering
	 * 
	 * @return
	 */
	IOrder getOrder();



	/**
	 * Get Composite Tag with given name.
	 * 
	 * @param name
	 * @return first composite tag with given name or null if none exists
	 */
	ICompositeTag getCompositeTagByName(IXMLQName name);



	/**
	 * Get all Composite Tags with given name.
	 * 
	 * @param name
	 * @return List of composite tags with given name
	 */
	List<ICompositeTag> getAllCompositeTagsByName(IXMLQName name);



	/**
	 * Get Simple Tag with given name.
	 * 
	 * @param name
	 * @return first simple tag with given name or null if none exists
	 */
	ISimpleTag getSimpleTagByName(IXMLQName name);



	/**
	 * Get all Simple Tags with given name.
	 * 
	 * @param name
	 * @return List of simple tags with given name
	 */
	List<ISimpleTag> getAllSimpleTagsByName(IXMLQName name);



	/**
	 * Get this element all Composite Tags children.
	 * 
	 * @return list of {@link ICompositeTag} children
	 */
	List<ICompositeTag> getAllCompositeTags();



	/**
	 * Get this element all Simple Tags children.
	 * 
	 * @return list of {@link ISimpleTag} children
	 */
	List<ISimpleTag> getAllSimpleTags();



	/**
	 * Remove all children tags.
	 * 
	 */
	void removeAllTags();



	/**
	 * Remove given number of tags with given name.
	 * 
	 * @param tagName
	 * @param nrToRemove
	 *            when given zero it removes all tags else it removes number
	 *            specified from front (if nrToRemove > 0 ) or from back (if
	 *            nrToRemove < 0) but not more than max number of elements
	 * @return numbers removed
	 */
	int removeTagsByName(IXMLQName tagName, int nrToRemove);

}
