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
import java.util.Map;

/**
 * <p>
 * Implements {@link IMapping}.
 * </p>
 * 
 * 
 * @author marta
 * 
 */
public class Mapping<T> implements IMapping<T> {

	private String mappedMethodName = "";

	private MappingType type;

	private IMapping<?> parent;

	private IMarshallerConverter<T> converter;

	/*
	 * this map specifies mapping for converters per method name
	 */
	private Map<String, IMapping<?>> convertersForMethod;

	/*
	 * Mapping for value of "container"
	 */
	private IMapping<?> containerMapping;



	public Mapping() {
	}



	/*
	 * Public Methods ***^_^***
	 */

	public Mapping(IMarshallerConverter<T> converter) {
		this.converter = converter;
	}



	@Override
	public void setConverter(IMarshallerConverter<T> converter) {
		this.converter = converter;
	}



	@Override
	public IMarshallerConverter<T> getConverter() {
		return this.converter;
	}



	@Override
	public MappingType getMappingType() {
		return type;
	}



	@Override
	public void setMappingType(MappingType type) {
		this.type = type;
	}



	@Override
	public String getMappedMethodName() {

		return this.mappedMethodName;
	}



	@Override
	public void setMappedMethodName(String methodName) {
		this.mappedMethodName = methodName;
	}



	@Override
	public IMapping<?> getParentMapping() {
		return this.parent;
	}



	@Override
	public void setParentMapping(IMapping<?> mapping) {
		this.parent = mapping;
	}



	@Override
	public Map<String, IMapping<?>> getMappings() {

		if (this.convertersForMethod == null) {
			return new HashMap<String, IMapping<?>>();
		}
		return new HashMap<String, IMapping<?>>(this.convertersForMethod);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when arguments are are null or empty string.
	 */
	@Override
	public void addMapping(String methodName, IMapping<?> mapping) {

		if (methodName == null || methodName.equals("")) {
			throw new IllegalArgumentException("Method name argument can not  is empty string or null");
		}

		if (mapping == null) {
			throw new IllegalArgumentException("Mapping can not be null");
		}

		MappingType type = mapping.getMappingType();
		if (type == null) {
			throw new IllegalArgumentException("Mapping type can not be null");
		}

		if (this.convertersForMethod == null) {
			this.convertersForMethod = new HashMap<String, IMapping<?>>();
		}

		this.correctMappingTree(mapping);

		mapping.setParentMapping(this);
		mapping.setMappedMethodName(methodName);

		this.convertersForMethod.put(methodName, mapping);
	}



	@Override
	public IMapping<?> removeMapping(String methodName) {

		if (this.convertersForMethod == null) {
			return null;
		}

		IMapping<?> mapping = this.convertersForMethod.remove(methodName);
		mapping.setMappedMethodName("");
		mapping.setParentMapping(null);
		return mapping;
	}



	@Override
	public IMapping<?> getMapping(String methodName) {

		if (this.convertersForMethod == null) {
			return null;
		}

		return this.convertersForMethod.get(methodName);
	}



	@Override
	public IMapping<?> getContainerMapping() {
		return this.containerMapping;
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when arguments are null.
	 */
	@Override
	public void setContainerMapping(IMapping<?> mapping) {

		if (mapping == null) {
			throw new IllegalArgumentException("Parameter mapping can not be null");
		}

		this.correctMappingTree(mapping);
		/* method is propagated since it is mapping for the same method */
		mapping.setMappedMethodName(this.mappedMethodName);
		mapping.setParentMapping(this);
		this.containerMapping = mapping;
	}



	@Override
	public boolean areThereMappings() {

		boolean result = true;
		if (this.convertersForMethod == null || this.convertersForMethod.isEmpty()) {
			result = false;
		}

		return result;
	}



	@Override
	public IMapping<T> copy() {
		IMapping<T> newMapping = new Mapping<T>();
		newMapping.setMappedMethodName(this.mappedMethodName);
		newMapping.setMappingType(this.type);

		newMapping.setParentMapping(this.parent);
		if (this.converter != null) {
			newMapping.setConverter(this.converter);
		}

		if (this.containerMapping != null) {
			newMapping.setContainerMapping(this.containerMapping.copy());
		}

		if (this.convertersForMethod != null) {

			for (String methodName : this.convertersForMethod.keySet()) {
				newMapping.addMapping(methodName, this.convertersForMethod.get(methodName).copy());
			}

		}

		return newMapping;
	}



	/*
	 * Private Methods ***^_^***
	 */

	/*
	 * One parent for a node.
	 */
	private void correctMappingTree(IMapping<?> mapping) {
		IMapping<?> parent = mapping.getParentMapping();
		if (parent != null && parent != this) {
			String childMethodMapping = mapping.getMappedMethodName();
			parent.removeMapping(childMethodMapping);
		}
	}

}
