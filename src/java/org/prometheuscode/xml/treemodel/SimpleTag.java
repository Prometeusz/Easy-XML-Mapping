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
 * Implements {@link ISimpleTag}.
 * </p>
 * 
 * 
 * @author marta
 * @see org.prometheuscode.xml.treemodel.ISimpleTag
 * 
 */
public class SimpleTag extends XMLTag implements ISimpleTag {

	private String value = "";



	public SimpleTag() {
		super();
	}



	public SimpleTag(IXMLQName name) {
		super(name);
	}



	@Override
	public String getValue() {

		return this.value;
	}



	@Override
	public void setValue(String text) {
		this.value = text;

	}



	@Override
	public boolean isSimpleTag() {

		return true;
	}



	@Override
	public String toString() {
		String partialString = super.toString();
		String result = partialString + this.value + "\n";
		return result;
	}
}
