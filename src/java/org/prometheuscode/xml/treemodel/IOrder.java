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
 * Interface to manage the ordering of tag children.
 * 
 * @author marta
 * 
 */
public interface IOrder {

	/**
	 * Move tag at position "from" to position "to".
	 * 
	 * @param from
	 * @param to
	 */
	void move(int from, int to);



	/**
	 * Get position of tag starting from "startFrom"
	 * 
	 * @param name
	 * @param startFrom
	 * @return
	 */
	int getPosition(IXMLTag name, int startFrom);



	/**
	 * Get tag at position.
	 * 
	 * @param position
	 * @return
	 */
	IXMLTag getTagAt(int position);



	/**
	 * Get size of ordering.
	 * 
	 * @return
	 */
	int size();
}
