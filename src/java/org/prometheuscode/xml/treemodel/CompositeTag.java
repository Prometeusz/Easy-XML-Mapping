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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.prometheuscode.xml.XMLTreeModelConstrainException;

/**
 * <p>
 * Implementation of ICompositeTag
 * </p>
 * 
 * 
 * @author marta
 * 
 */
public class CompositeTag extends XMLTag implements ICompositeTag {

	/*
	 * These maps holds list of elements with given name or one element if there
	 * is only one element with given name.
	 */

	private Map<IXMLQName, List<ICompositeTag>> compositeTags = new LinkedHashMap<IXMLQName, List<ICompositeTag>>();

	private Map<IXMLQName, List<ISimpleTag>> simpleTags = new LinkedHashMap<IXMLQName, List<ISimpleTag>>();

	private Order order = new Order();



	public CompositeTag() {
		super();
	}



	public CompositeTag(IXMLQName name) {
		super(name);
	}



	/*
	 * Public Methods ***^_^***
	 */

	@Override
	public List<ICompositeTag> getAllCompositeTags() {

		return this.<ICompositeTag> getTags(this.compositeTags);
	}



	@Override
	public List<ISimpleTag> getAllSimpleTags() {

		return this.<ISimpleTag> getTags(this.simpleTags);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if given argument is null.
	 * 
	 * @throws XMLTreeModelConstrainException
	 *             if simple tag and composite tag with the same XML qualified
	 *             name is going to be added.
	 */
	@Override
	public boolean addTag(IXMLTag tag) {

		if (tag == null) {
			throw new IllegalArgumentException("Tag should be given. Null argument");
		}

		this.checkNamingConstrain(tag);

		ICompositeTag parent = tag.getParent();
		IXMLQName tagName = tag.getName();

		/*
		 * if it has a parent keep "tree" constrain, only one parent
		 */
		if (parent != null && parent != this) {
			parent.removeTagsByName(tagName, 1);
		}

		tag.setParent(this);
		this.order.addElement(tag);

		boolean wasAdded = false;
		if (tag.isSimpleTag()) {
			wasAdded = this.<ISimpleTag> addTagWithType(tag, this.simpleTags);
		} else {
			wasAdded = this.<ICompositeTag> addTagWithType(tag, this.compositeTags);
		}

		return wasAdded;
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if given argument is null.
	 */
	@Override
	public int removeTagsByName(IXMLQName tagName, int nrToRemove) {

		if (tagName == null) {
			throw new IllegalArgumentException("A Name should be given. Null argument");
		}

		List<? extends IXMLTag> tagsWithName = this.getTagsListByName(tagName);

		if (tagsWithName == null || tagsWithName.isEmpty()) {
			return 0;
		}

		int tagsSize = tagsWithName.size();
		int correctNrToRemove = this.getCorrectNumber(tagsSize, nrToRemove);
		int start = 0;
		int end = tagsSize;
		if (nrToRemove < 0) {
			start = correctNrToRemove;
		} else {
			end = correctNrToRemove;
		}

		/*
		 * NOTE: start and end are are applicable to ordering
		 */
		this.order.removeElementsByName(tagName, start, end - start);

		/*
		 * it might be not efficient
		 */
		for (int i = start; i < end; i++) {
			IXMLTag tag = tagsWithName.remove(i);
			tag.setParent(null);
		}

		return correctNrToRemove;
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if name is null or empty string.
	 */
	@Override
	public ICompositeTag getCompositeTagByName(IXMLQName name) {

		if (name == null || name.getLocalPart().equals("")) {
			throw new IllegalArgumentException("A Name should be given with none empty local part");
		}

		List<ICompositeTag> tags = this.compositeTags.get(name);
		if (tags == null || tags.isEmpty()) {
			return null;
		}
		return tags.get(0);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if name is null.
	 */
	@Override
	public List<ICompositeTag> getAllCompositeTagsByName(IXMLQName name) {

		return this.<ICompositeTag> getTagsByName(name, this.compositeTags, 0);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if name is null.
	 */
	@Override
	public ISimpleTag getSimpleTagByName(IXMLQName name) {

		if (name == null || name.getLocalPart().equals("")) {
			throw new IllegalArgumentException("A Name should be given with none empty local part");
		}

		List<ISimpleTag> tags = this.simpleTags.get(name);
		if (tags == null || tags.isEmpty()) {
			return null;
		}
		return tags.get(0);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if name is null.
	 */
	@Override
	public List<ISimpleTag> getAllSimpleTagsByName(IXMLQName name) {

		return this.getTagsByName(name, this.simpleTags, 0);
	}



	@Override
	public IOrder getOrder() {
		return this.order;
	}



	@Override
	public boolean isSimpleTag() {
		return false;
	}



	@Override
	public void removeAllTags() {
		this.compositeTags.clear();
		this.simpleTags.clear();
		this.order.removeAll();
	}



	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		List<ICompositeTag> compositeTags = this.getAllCompositeTags();
		List<ISimpleTag> simpleTags = this.getAllSimpleTags();

		result.append(super.toString());
		result.append("Composite tags children(" + compositeTags.size() + "):\n" + compositeTags.toString() + "\n");
		result.append("Simple tags children(" + simpleTags.size() + "):\n" + simpleTags.toString() + "\n");

		return result.toString();
	}



	/*
	 * Private Methods ***^_^***
	 */

	/*
	 * Helper method to get list of tags ICompositeTag or ISimpleTag. List
	 * created depends on argument "tags".
	 * 
	 * @param tagsMap
	 * 
	 * @return
	 */
	private <T> List<T> getTags(Map<IXMLQName, List<T>> tags) {

		List<T> tagsToReturn = new ArrayList<T>();

		if (!(tags.isEmpty())) {

			for (List<T> elem : tags.values()) {
				tagsToReturn.addAll(elem);
			}
		}

		return tagsToReturn;
	}



	/*
	 * Add given Tag.
	 */
	@SuppressWarnings("unchecked")
	private <T> boolean addTagWithType(IXMLTag tag, Map<IXMLQName, List<T>> tags) {

		boolean result = false;
		List<T> tagsList = tags.get(tag.getName());
		if (tagsList == null) {
			tagsList = new ArrayList<T>();
			tags.put(tag.getName(), tagsList);
		}

		result = tagsList.add((T) tag);
		return result;
	}



	/*
	 * Get given number of tags with given name.
	 */
	private <T> List<T> getTagsByName(IXMLQName tagName, Map<IXMLQName, List<T>> tags, int nrToGet) {

		if (tagName == null || tagName.getLocalPart().equals("")) {
			throw new IllegalArgumentException("A Name should be given with none empty local part");
		}

		List<T> tagsWithName = tags.get(tagName);
		List<T> tagsToReturn = new ArrayList<T>();
		if (tagsWithName == null || tagsWithName.isEmpty()) {
			return tagsToReturn;
		}

		int tagsWithNameSize = tagsWithName.size();
		int correctNrToGet = this.getCorrectNumber(tagsWithNameSize, nrToGet);
		int start = 0;
		int end = tagsWithNameSize;

		if (nrToGet < 0) {
			start = correctNrToGet;
		} else {
			end = correctNrToGet;
		}

		for (int i = start; i < end; i++) {
			tagsToReturn.add(tagsWithName.get(i));
		}

		return tagsToReturn;
	}



	/*
	 * Corrects a number to be in limit:
	 * 
	 * size if nrGiven == 0 || |nrGiven| > size or nrGiven if nrGiven < size or
	 */
	private int getCorrectNumber(int size, int nrGiven) {
		int correctNumber = 0;
		if (nrGiven == 0) {
			/*
			 * get all
			 */
			correctNumber = size;
		} else {
			nrGiven = Math.abs(nrGiven);
			correctNumber = (nrGiven < size) ? nrGiven : size;
		}
		return correctNumber;
	}



	/*
	 * Get tag list with given tag name.
	 */
	private List<? extends IXMLTag> getTagsListByName(IXMLQName tagName) {

		List<? extends IXMLTag> list = null;
		if (this.compositeTags.containsKey(tagName)) {

			list = this.compositeTags.get(tagName);
		} else if (this.simpleTags.containsKey(tagName)) {

			list = this.simpleTags.get(tagName);
		}

		return list;

	}



	/*
	 * Check if XML schema naming constrain holds
	 */
	private void checkNamingConstrain(IXMLTag tag) {

		if ((!(tag.isSimpleTag()) && this.simpleTags.containsKey(tag.getName())) || (tag.isSimpleTag() && this.compositeTags.containsKey(tag.getName()))) {
			throw new XMLTreeModelConstrainException("Constrain invalidation. One can't have simple and composite tag with the same qualified name.");
		}

	}

}
