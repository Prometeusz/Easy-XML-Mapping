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

/**
 * It is a cache for tags used during unmarshalling.
 * 
 * @author marta
 * 
 */
public interface ITagsCache {

	/**
	 * Get cached simple Tag.
	 * 
	 * @param simpleTagName
	 * @return
	 */
	ISimpleTag getSimpleTag(IXMLQName simpleTagName);



	/**
	 * Get cached Composite Tag.
	 * 
	 * @param composisteTagName
	 * @return
	 */
	ICompositeTag getCompositeTag(IXMLQName composisteTagName);



	/**
	 * Updates the number of simple tags which are free in cache per tag
	 * qualified name.
	 * 
	 */
	void updateSimpleTagsFreeSlots();



	/**
	 * Updates the number of composite tags which are free in cache per tag
	 * qualified name.
	 * 
	 */
	void updateCompositeTagsFreeSlots();

}
