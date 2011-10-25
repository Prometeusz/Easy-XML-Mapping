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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLTag;

/**
 * Implements {@link ITagsCache}.
 * 
 * @author marta
 * 
 */
public class TagsCache implements ITagsCache {

	private Map<IXMLQName, LinkedList<IXMLTag>> simpleTagsCache = new HashMap<IXMLQName, LinkedList<IXMLTag>>();

	private Map<IXMLQName, Integer> simpleTagsFreeSlots = new HashMap<IXMLQName, Integer>();

	
	private Map<IXMLQName, LinkedList<IXMLTag>> compositeTagsCache = new HashMap<IXMLQName, LinkedList<IXMLTag>>();

	private Map<IXMLQName, Integer> compositeTagsFreeSlots = new HashMap<IXMLQName, Integer>();

	
	private IXMLTreeModelFactory treeModelFactory = new XMLTreeModelFactory();



	public TagsCache() {
	}



	@Override
	public ISimpleTag getSimpleTag(IXMLQName simpleTagName) {
		IXMLTag returnTag = this.getTag(simpleTagName, this.simpleTagsCache, this.simpleTagsFreeSlots, true);

		return ((ISimpleTag) returnTag);
	}



	@Override
	public ICompositeTag getCompositeTag(IXMLQName composisteTagName) {
		ICompositeTag returnTag = (ICompositeTag) this.getTag(composisteTagName, this.compositeTagsCache, this.compositeTagsFreeSlots, false);
		returnTag.removeAllTags();
		return returnTag;
	}



	@Override
	public void updateSimpleTagsFreeSlots() {
		this.updateCacheFreeSlots(this.simpleTagsCache, this.simpleTagsFreeSlots);
	}



	@Override
	public void updateCompositeTagsFreeSlots() {
		this.updateCacheFreeSlots(this.compositeTagsCache, this.compositeTagsFreeSlots);

	}



	/*
	 * Get tag for given name
	 */
	private IXMLTag getTag(IXMLQName tagName, Map<IXMLQName, LinkedList<IXMLTag>> tagsCache, Map<IXMLQName, Integer> tagsFreeSlots, boolean useSimpleTag) {
		IXMLTag returnTag = null;
		int currentFreeSlots = 0;
		if (tagsFreeSlots.containsKey(tagName)) {
			currentFreeSlots = tagsFreeSlots.get(tagName);
		}

		if (currentFreeSlots != 0) {
			LinkedList<IXMLTag> tagsWithTheSameName = tagsCache.get(tagName);
			returnTag = tagsWithTheSameName.removeFirst();
			tagsWithTheSameName.addLast(returnTag);
			currentFreeSlots--;
			tagsFreeSlots.put(tagName, currentFreeSlots);
		} else {
			LinkedList<IXMLTag> tagsWithTheSameName = tagsCache.get(tagName);
			if (tagsWithTheSameName == null) {
				tagsWithTheSameName = new LinkedList<IXMLTag>();
				tagsCache.put(tagName, tagsWithTheSameName);
			}

			if (useSimpleTag) {
				returnTag = this.treeModelFactory.createSimpleTag();
			} else {
				returnTag = this.treeModelFactory.createCompositeTag();
			}

			IXMLQName name = returnTag.getName();
			name.setLocalPart(tagName.getLocalPart());
			name.setNamespaceURI(tagName.getNamespaceURI());
			name.setPrefix(tagName.getPrefix());

			tagsWithTheSameName.add(returnTag);
		}

		return returnTag;
	}



	/*
	 * update cache free slots
	 */
	private void updateCacheFreeSlots(Map<IXMLQName, LinkedList<IXMLTag>> tagsCache, Map<IXMLQName, Integer> freeSlots) {
		for (IXMLQName tagName : tagsCache.keySet()) {
			LinkedList<IXMLTag> tagList = tagsCache.get(tagName);
			if (tagList != null) {
				int size = tagList.size();
				freeSlots.put(tagName, size);
			}

		}
	}

}
