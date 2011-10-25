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

import java.util.Map;

/**
 * <p>
 * This interface allows to define mapping for a marshaller.
 * </p>
 * 
 * <p>
 * Every mapping is for some class {@code T variable}. If given class has some
 * objects on which full convertion depends on, then user can add mapping for
 * those objects classes also. It works by defining getter method with no arguments and by
 * returning "dependent" object. It must be not null. User needs also define
 * (at runtime or by annotation) mapping for those returned objects. By using
 * annotations user is able to create "mapping" for given class at runtime.
 * See {@link IXMLAdaptiveMarshaller} createMapping() method for more.
 * </p>
 * 
 * <p>
 * This approach of mapping allows user to focuse during writing converter only on 
 * one class at time. All other objects which also belong to given class will be
 * resolved by user registered mappings(per method).
 * </p>
 * 
 * <p>
 * Mappings for "containers" that is, java.util.Map and java.util.Collection,
 * can have converter xor(exluding or) cointainer mapping.</br> Having container
 * mappings means that Value of Collection or Map is suppose to be target of
 * convertion not "container" as a whole. This can be chained for Collections of
 * Collections or Maps of Maps, or mixed.
 * </p>
 * 
 * 
 * @author marta
 * 
 */
public interface IMapping<T> {

	/**
	 * Get type of mapping.
	 * 
	 * @return
	 */
	MappingType getMappingType();



	/**
	 * Set Type of Mapping.
	 * 
	 * @param type
	 */
	void setMappingType(MappingType type);



	/**
	 * Return mapping for list element or map value. Depends on current type.
	 * 
	 * @return
	 */
	IMapping<?> getContainerMapping();



	/**
	 * Set current container Mapping. It must be no MappingType.Object type mapping.
	 * 
	 * @param mapping
	 */
	void setContainerMapping(IMapping<?> mapping);



	/**
	 * Get mapped method for this mapping.
	 * 
	 * 
	 * @return
	 */
	String getMappedMethodName();



	/**
	 * Get mapped method for this mapping.
	 * 
	 * @param methodName
	 */
	void setMappedMethodName(String methodName);



	/**
	 * Get mapping for which this mapping is attached to.
	 * 
	 * @return parent mapping or null if it is root mapping
	 */
	IMapping<?> getParentMapping();



	/**
	 * Set parent mapping for this mapping.
	 * 
	 * @param mapping
	 */
	void setParentMapping(IMapping<?> mapping);



	/**
	 * Set {@link IMarshallerConverter} for this mapping.
	 * 
	 * @param converter
	 */
	void setConverter(IMarshallerConverter<T> converter);



	/**
	 * Get {@link IMarshallerConverter} for this mapping.
	 * 
	 * @return
	 */
	IMarshallerConverter<T> getConverter();



	/**
	 * Add mapping for an object returned by method with name "methodName".
	 * 
	 * It also keep "tree" constrain" one parent for a child and update method
	 * and parent for mapping added.
	 * 
	 * @param methodName
	 * @param converter
	 */
	void addMapping(String methodName, IMapping<?> mapping);



	/**
	 * Remove "dependent" mapping of class method.
	 * 
	 * @param methodName
	 * @return
	 */
	IMapping<?> removeMapping(String methodName);



	/**
	 * Get mapping for method of class.
	 * 
	 * @param methodName
	 * @return
	 */
	IMapping<?> getMapping(String methodName);



	/**
	 * Get all "dependent" mappings of this mapping.
	 * 
	 * @return
	 */
	Map<String, IMapping<?>> getMappings();



	/**
	 * Check if this mapping specifies "dependent" objects.
	 * 
	 * @return
	 */
	boolean areThereMappings();



	/**
	 * Make a copy of this mapping.
	 * 
	 * @return
	 */
	public IMapping<T> copy();
}
