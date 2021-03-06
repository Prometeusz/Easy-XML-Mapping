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

import org.prometheuscode.xml.treemodel.CompositeTag;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.SimpleTag;
import org.prometheuscode.xml.treemodel.XMLQName;
import org.prometheuscode.xml.treemodel.XMLRoot;

/**
 * <p>
 * Simple implementation of IXMLTreeModelFactory
 * </p>
 * 
 * @author marta
 * 
 */
public class XMLTreeModelFactory implements IXMLTreeModelFactory {

	@Override
	public ICompositeTag createCompositeTag() {

		return new CompositeTag();
	}



	@Override
	public ISimpleTag createSimpleTag() {

		return new SimpleTag();
	}



	@Override
	public IXMLRoot createXMLDocument() {

		return new XMLRoot();
	}



	@Override
	public IXMLQName createXMLQName() {

		return new XMLQName();
	}

}
