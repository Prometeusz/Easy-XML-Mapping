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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.Order;
import org.prometheuscode.xml.treemodel.SimpleTag;
import org.prometheuscode.xml.treemodel.XMLQName;

public class OrderTest {

	private Order orderTest;



	@Before
	public void setUp() {
		this.orderTest = new Order();
		this.orderTest.addElement(new SimpleTag(new XMLQName("evil", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("evil", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("pink", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("evil", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("green", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("shaman", "", "")));
		this.orderTest.addElement(new SimpleTag(new XMLQName("good", "", "")));
	}



	@Test
	public void testOrderGetInterface() {

		assertEquals("Elment at position 2 should be named: pink", "pink", this.orderTest.getTagAt(2).getName().getLocalPart());

		assertEquals("Elment at position 6 should be named: pink", "good", this.orderTest.getTagAt(6).getName().getLocalPart());

		ISimpleTag tag = new SimpleTag(new XMLQName("green", "", ""));
		int greenPosition = this.orderTest.getPosition(tag, 0);
		assertEquals("Elment green should be at position: 4", 4, greenPosition);

		tag.getName().setLocalPart("evil");
		int evilPosition = this.orderTest.getPosition(tag, 2);
		assertEquals("Elment green should be at position: 3", 3, evilPosition);

		this.orderTest.move(0, 6);
		assertEquals("Elment at position 0 should be moved to 6: evil --> good ", "evil", this.orderTest.getTagAt(6).getName().getLocalPart());

		this.orderTest.addElementAt(new SimpleTag(new XMLQName("black", "", "")), 1);
		assertEquals("Elment at position 1 should be named: black", "black", this.orderTest.getTagAt(1).getName().getLocalPart());

	}



	@Test
	public void testOrderRemoveInterface() {

		IXMLQName name = new XMLQName("evil", "", "");
		this.orderTest.removeElementsByName(name, 0, 3);
		assertEquals("# of elements after remove should be: 4", 4, this.orderTest.size());

		ISimpleTag tag = new SimpleTag(name);
		assertEquals("There should be no element with name evil after remove", -1, this.orderTest.getPosition(tag, 0));

	}



	@Test
	public void testOrderRangeException() {

		IXMLQName name = new XMLQName("floor", "", "");
		ISimpleTag tag = new SimpleTag(name);
		try {
			this.orderTest.addElementAt(tag, 666);
			fail("Illegal argument exception should be throw");
		} catch (IllegalArgumentException e) {

		}

		try {
			this.orderTest.addElementAt(tag, -666);
			fail("Illegal argument exception should be throw");
		} catch (IllegalArgumentException e) {

		}

		try {
			this.orderTest.move(-666, -666);
			fail("Illegal argument exception should be throw");
		} catch (IllegalArgumentException e) {

		}

	}

}
