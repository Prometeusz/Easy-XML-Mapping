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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of {@link IOrder}.
 * 
 * @author marta
 * 
 */
public class Order implements IOrder {

	private List<IXMLTag> list = new LinkedList<IXMLTag>();



	public Order() {
	}



	@Override
	public void move(int from, int to) {

		this.checkRange("From", from);
		this.checkRange("To", to);

		IXMLTag elem = this.list.remove(from);
		this.list.add(to, elem);

	}



	@Override
	public int getPosition(IXMLTag tag, int startFrom) {

		this.checkRange("Start from", startFrom);

		int position = 0;
		if (startFrom == 0) {
			position = this.list.indexOf(tag);
		} else {
			/*
			 * @TODO FIXME it might be sloppy here
			 */
			position = startFrom + this.list.subList(startFrom, this.list.size()).indexOf(tag);
		}

		return position;
	}



	@Override
	public IXMLTag getTagAt(int position) {

		this.checkRange("Position", position);
		return this.list.get(position);
	}



	@Override
	public int size() {
		return this.list.size();
	}



	/**
	 * Add element to ordering.
	 * 
	 * @param tag
	 */
	void addElement(IXMLTag tag) {
		this.list.add(tag);
	}



	/**
	 * Add element at position.
	 * 
	 * @param tag
	 * @param position
	 */
	void addElementAt(IXMLTag tag, int position) {

		if (tag == null || tag.getName().getLocalPart().equals("")) {
			throw new IllegalArgumentException("Name can't be null or without local part");
		}

		this.checkRange("Position", position);
		this.list.add(position, tag);
	};



	/**
	 * Removes "howMany", given tag name, starting at "from" occurrence
	 */
	void removeElementsByName(IXMLQName tagName, int from, int howMany) {

		this.checkRange("From", from);

		if (howMany <= 0 || howMany > this.list.size()) {
			throw new IllegalArgumentException("howMany argument is out of band: " + howMany + " where size is: " + this.list.size());
		}

		int tagNameIndex = 0;
		for (IXMLTag tmpTag : this.list) {
			if (tmpTag.getName().equals(tagName)) {
				break;
			}
			tagNameIndex++;
		}
		/*
		 * it should be quicker way of doing this
		 * 
		 * Move to first occurrence of "name"
		 */
		ListIterator<IXMLTag> iter = this.list.listIterator(tagNameIndex);
		IXMLTag candidate = null;
		for (int i = 0, removed = 0; (removed < howMany) && iter.hasNext(); i++) {

			candidate = iter.next();
			if (i >= from && candidate.getName().equals(tagName)) {
				iter.remove();
				removed++;
			}

		}

	}



	/**
	 * Remove element at position
	 * 
	 * @param position
	 * @return
	 */
	IXMLTag removeElementAt(int position) {

		this.checkRange("Position", position);
		return this.list.remove(position);

	};



	/**
	 * Remove element.
	 * 
	 * @param name
	 * @return
	 */
	boolean removeElement(IXMLTag name) {
		return this.list.remove(name);
	}



	/**
	 * Remove all elements.
	 */
	void removeAll() {
		this.list.clear();
	}



	/*
	 * check if acces fall into range.
	 */
	private void checkRange(String msg, int access) {

		if (access < 0 || access >= this.list.size()) {
			throw new IllegalArgumentException(msg + " argument is out of band: " + access + " where size is: " + this.list.size());
		}

	}

}
