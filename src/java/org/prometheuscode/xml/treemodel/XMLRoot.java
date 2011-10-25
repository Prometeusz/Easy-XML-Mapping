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
 * Implementation of IXMLRoot.
 * </p>
 * 
 * @author marta
 * 
 */
public class XMLRoot implements IXMLRoot {

	private ICompositeTag xmlRoot;

	private String encoding = "UTF-8";

	private String version = "1.0";



	public XMLRoot() {
	}



	public XMLRoot(ICompositeTag xmlRoot) {

		this.xmlRoot = xmlRoot;
	}



	@Override
	public String getEncoding() {

		return this.encoding;
	}



	@Override
	public String getVersion() {

		return this.version;
	}



	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	@Override
	public void setVersion(String version) {
		this.version = version;
	}



	@Override
	public ICompositeTag getXMLRoot() {
		return this.xmlRoot;
	}



	@Override
	public void setXMLRoot(ICompositeTag xmlRoot) {
		this.xmlRoot = xmlRoot;

	}

}
