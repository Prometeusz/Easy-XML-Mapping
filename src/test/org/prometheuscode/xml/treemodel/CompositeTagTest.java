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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.prometheuscode.xml.XMLTreeModelConstrainException;
import org.prometheuscode.xml.treemodel.CompositeTag;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.SimpleTag;
import org.prometheuscode.xml.treemodel.XMLQName;

public class CompositeTagTest {

	private ICompositeTag tagToTest;



	@Before
	public void setUp() {
		this.tagToTest = new CompositeTag(new XMLQName("rootTag", "", ""));
		/*
		 * add some tags
		 */
		this.tagToTest.addTag(new CompositeTag(new XMLQName("collisionTag", "", "")));
		this.tagToTest.addTag(new CompositeTag(new XMLQName("collisionTag", "", "")));
		this.tagToTest.addTag(new CompositeTag(new XMLQName("someTag", "", "")));
		this.tagToTest.addTag(new SimpleTag(new XMLQName("simpleTag", "", "")));
		this.tagToTest.addTag(new SimpleTag(new XMLQName("simpleTag", "", "")));
		this.tagToTest.addTag(new SimpleTag(new XMLQName("myTag", "", "")));
	}



	@Test
	public void testCompositeTagGetInterface() {

		IXMLQName testName = new XMLQName("collisionTag", "", "");
		assertEquals("Tag should have composite tag with name: collisionTag", "collisionTag", this.tagToTest.getCompositeTagByName(testName).getName().getLocalPart());

		assertEquals("Tag should have 2 composite tags with name: collisionTag", 2, this.tagToTest.getAllCompositeTagsByName(testName).size());

		assertEquals("There should be 3 composite tags", 3, this.tagToTest.getAllCompositeTags().size());

		assertEquals("Order should have 6 elements", 6, this.tagToTest.getOrder().size());
	}



	@Test
	public void testCompositeTagRemovetInterface() {

		IXMLQName testName = new XMLQName("collisionTag", "", "");
		this.tagToTest.removeTagsByName(testName, 1);

		assertEquals("Tag should have 1 composite tags with name: collisionTag", 1, this.tagToTest.getAllCompositeTagsByName(testName).size());

		/*
		 * remove from back
		 */
		testName.setLocalPart("simpleTag");
		this.tagToTest.removeTagsByName(testName, -1);
		assertEquals("Tag should have 1 simple tag with name: simpleTag", 1, this.tagToTest.getAllSimpleTagsByName(testName).size());

	}



	@Test
	public void testCompositeTagAddInterface() {

		ICompositeTag compositeTag = new CompositeTag(new XMLQName("constrainTag", "", ""));
		ISimpleTag simpleTag = new SimpleTag(new XMLQName("invalidateTag", "", ""));
		compositeTag.addTag(simpleTag);

		assertEquals("Tag should have a simple tag child with name: invalidateTag", "invalidateTag", compositeTag.getSimpleTagByName(simpleTag.getName()).getName().getLocalPart());

		/*
		 * constrain of the "tree model" should hold. One parent for a child.
		 */
		this.tagToTest.addTag(simpleTag);

		assertEquals("Tag # of elements in IOrder should be 0", 0, compositeTag.getOrder().size());

		assertEquals("Tag should have a simple tag child with name: invalidateTag", "invalidateTag", this.tagToTest.getSimpleTagByName(simpleTag.getName()).getName().getLocalPart());

		assertEquals("Tag should not have a simple tag child with name: invalidateTag", null, compositeTag.getSimpleTagByName(simpleTag.getName()));

		assertEquals("Tag parent name should be: rootTag", "rootTag", simpleTag.getParent().getName().getLocalPart());

	}



	@Test(expected = XMLTreeModelConstrainException.class)
	public void testTreeConstrainExceptionCheck() {
		this.tagToTest.addTag(new CompositeTag(new XMLQName("simpleTag", "", "")));
	}
}
