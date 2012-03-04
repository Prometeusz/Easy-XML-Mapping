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

import java.io.IOException;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.prometheuscode.xml.annotation.AnnotationException;
import org.prometheuscode.xml.annotation.Convertable;
import org.prometheuscode.xml.annotation.Dependee;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.IOrder;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.IXMLTag;

/**
 * This is implementation of IXMLAdaptiveMarshaller interface based on StaX api.
 * 
 * 
 * 
 * @author marta
 * 
 */
class XMLStaXAdaptiveMarshaller implements IXMLAdaptiveMarshaller {

	public final static String DEFAULT_ENCODING = "UTF-8";

	public final static String DEFAULT_VERSION = "1.0";

	/*
	 * final values give names for tag which is "container". (Map or Collection)
	 */
	public final static String LIST_TAG_NAME = "list";

	public final static String MAP_TAG_NAME = "map";

	public final static String MAP_TAG_ENTRY_NAME = "entry";

	public final static String MAP_TAG_KEY_NAME = "key";

	public final static String MAP_TAG_VALUE_NAME = "value";

	/*
	 * mapping to use during marshalling
	 */
	private IMapping<?> mapping;

	private IXMLTreeModelFactory treeModelFactory = new XMLTreeModelFactory();

	/* key is fully qualified class name, value is mapping for given class */
	private Map<String, IMapping<?>> classesNamesToMappingsCache;



	public XMLStaXAdaptiveMarshaller() {
	}



	/*
	 * Public Methods ***^_^***
	 */

	
	
	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when parameters are null or root has empty name.
	 * 
	 * @throws XMLMarshallerException
	 *             if error occured during saving tree model.
	 */
	@Override
	public void saveTreeModel(IXMLRoot root, OutputStream out) {
		if (root == null || out == null) {
			throw new IllegalArgumentException("Arguments can't be null");
		}

		if (root.getXMLRoot() == null || root.getXMLRoot().getName().getLocalPart() == "") {
			throw new IllegalArgumentException("Root element must be specified with non-empty name");
		}

		String encoding = (root.getEncoding() != null) ? root.getEncoding() : DEFAULT_ENCODING;
		String version = (root.getVersion() != null) ? root.getVersion() : DEFAULT_VERSION;

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		factory.setProperty("javax.xml.stream.isRepairingNamespaces", new Boolean(true));
		XMLStreamWriter writer = null;

		try {
			writer = factory.createXMLStreamWriter(out, encoding);
			this.writeTreeModel(writer, root.getXMLRoot(), encoding, version);
		} catch (XMLStreamException e) {
			throw new XMLMarshallerException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
					out.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	} /* end method */



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when parameters are null or mapping was not registered for
	 *             given object.
	 * 
	 * @throws XMLMarshallerException
	 *             if error occured during saving tree model.
	 */
	@Override
	public void marshal(Object objectToSave, OutputStream out) {
		if (objectToSave == null || out == null) {
			throw new IllegalArgumentException("Arguments can't be null");
		}

		this.checkRegisteredMapping(objectToSave);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		factory.setProperty("javax.xml.stream.isRepairingNamespaces", new Boolean(true));
		XMLStreamWriter writer = null;
		try {
			writer = factory.createXMLStreamWriter(out, DEFAULT_ENCODING);
			IXMLTag root = this.createTreeModelFromObject(objectToSave);
			this.writeTreeModel(writer, root, DEFAULT_ENCODING, DEFAULT_VERSION);
		} catch (Exception e) {
			throw new XMLMarshallerException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
					out.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when parameters are null or mapping was not registered for
	 *             given object.
	 * 
	 * @throws XMLMarshallerException
	 *             if error occured during converting to tree model.
	 */
	@Override
	public IXMLTag getTreeModelFromObject(Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException("Object parameter can not be null");
		}

		this.checkRegisteredMapping(obj);
		return this.createTreeModelFromObject(obj);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when parameters are null.
	 */
	@Override
	public void registerMapping(IMapping<?> mapping) {
		if (mapping == null) {
			throw new IllegalArgumentException("Mapping can not be null");
		}

		this.mapping = mapping;
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             when parameters are null or paramter is a Map or Collection.
	 * 
	 * @throws AnnotationException
	 *             if annotation has incorrect value or was put on incorrect
	 *             method.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> IMapping<T> createMapping(Class<T> rootClazz) {

		if (rootClazz == null) {
			throw new IllegalArgumentException("Root Class parameter can no be null");
		}

		if (Map.class.isAssignableFrom(rootClazz) || Collection.class.isAssignableFrom(rootClazz)) {
			throw new IllegalArgumentException("Class parameter can not represent java.util.Map or java.util.Collection");
		}

		synchronized (this) {
			if (this.classesNamesToMappingsCache == null) {
				/*
				 * key is fully qualified class name, 
				 * value is mapping for given class
				 */
				this.classesNamesToMappingsCache = new HashMap<String, IMapping<?>>();
			}
		}

		LinkedList<IMapping<?>> todoMappings = new LinkedList<IMapping<?>>();

		Class<Convertable> convertableAnnotation = Convertable.class;
		Class<Dependee> dependeeAnnotation = Dependee.class;

		Class<?> currentClass = rootClazz;
		boolean[] newCreated = new boolean[1];
		newCreated[0] = false;
		IMapping<T> rootMapping = (IMapping<T>) this.getMapping(rootClazz, newCreated);
		if (newCreated[0] == false) {
			rootMapping.setParentMapping(null);
			rootMapping.setMappedMethodName("");
			return rootMapping;
		}

		rootMapping.setMappingType(MappingType.Object);
		todoMappings.add(rootMapping);
		while (!(todoMappings.isEmpty())) {

			IMapping<Object> mapping = (IMapping<Object>) todoMappings.removeFirst();

			IMarshallerConverter<Object> converter = (IMarshallerConverter<Object>) this.getConverterFromClass(currentClass, convertableAnnotation);
			mapping.setConverter(converter);

			/* process all "dependee" objects */
			Method[] methodsToCheck = currentClass.getMethods();
			for (Method testMethod : methodsToCheck) {

				if (testMethod.getAnnotation(dependeeAnnotation) == null) {
					continue;
				}

				Object[] valueMapping = this.createDependeeMapping(testMethod, mapping);
				if (valueMapping == null) {
					/* cache version was used so there is no need to resolve it */
					continue;
				}

				/* add this mapping to list so we can set converter later */
				currentClass = (Class<?>) valueMapping[0];
				todoMappings.add((IMapping<Object>) valueMapping[1]);
			}

		} /* outer while loop */

		return rootMapping;
	}



	@Override
	public IMapping<?> getRegisteredMapping() {
		return this.mapping;
	}



	/*
	 * Private Methods ***^_^***
	 */

	
	
	/*
	 * Get mapping using cache or create a new one. Do not cache Map or
	 * Collection objects.
	 */
	synchronized private IMapping<?> getMapping(Class<?> clazz, boolean[] newCreated) {
		IMapping<?> rootMapping = null;
		if (!(this.classesNamesToMappingsCache.containsKey(clazz.getCanonicalName()))) {
			rootMapping = new Mapping<Object>();
			newCreated[0] = true;
			if (!(Map.class.isAssignableFrom(clazz)) && !(Collection.class.isAssignableFrom(clazz))) {
				this.classesNamesToMappingsCache.put(clazz.getCanonicalName(), rootMapping);
			}

		} else {
			rootMapping = this.classesNamesToMappingsCache.get(clazz.getCanonicalName());
			rootMapping = rootMapping.copy();
		}

		return rootMapping;
	}



	/*
	 * This helper function search class file for converter having Annotation
	 * which specifies converter class.
	 */
	private IMarshallerConverter<?> getConverterFromClass(Class<?> clazzToProcess, Class<Convertable> convertableAnnotation) {

		Convertable classAnnotation = clazzToProcess.getAnnotation(convertableAnnotation);

		IMarshallerConverter<?> converter = null;
		try {
			converter = classAnnotation.converter().newInstance();
		} catch (InstantiationException e) {
			throw new AnnotationException("Converter in Convertable annotation might not have no-arg constructor or is abstract", e);
		} catch (IllegalAccessException e) {
			throw new AnnotationException("Converter in Convertable annotation might not have proper access rights for no-arg constructor", e);
		}

		return converter;
	}



	/*
	 * Creates IMapping for object returned by given method. Returned array has
	 * class for mapping and mapping also : 
	 * - if object returned by this method is
	 * not Collection or Map this mapping is mapping for object
	 * 
	 * - if object is Collection or Map (Also Maps of Maps, Colections of
	 * Collections and mixed) returned mapping is for the most inner(no
	 * Collection or Map type) value mapping. 
	 * For ex. Mapping returned in Map<String, List<User>> is for User object.
	 * 
	 * - it returns null if mapping from cache were used
	 */
	private Object[] createDependeeMapping(Method testMethod, IMapping<?> parentMapping) {

		/* get proper mapping if return type is Map or Collection */
		Class<?> currentClazz = testMethod.getReturnType();
		Type currentType = testMethod.getGenericReturnType();

		boolean[] newCreated = new boolean[1];
		newCreated[0] = false;
		IMapping<?> dependeeMapping = this.getMapping(currentClazz, newCreated);

		String methodName = testMethod.getName();
		dependeeMapping.setMappedMethodName(methodName);
		dependeeMapping.setParentMapping(parentMapping);
		if (newCreated[0] == false) {
			parentMapping.addMapping(methodName, dependeeMapping);
			return null;
		}

		/* get mappings for "value object" of Collection or Map */
		IMapping<?> currentContainerMapping = dependeeMapping;
		while (Map.class.isAssignableFrom(currentClazz) || Collection.class.isAssignableFrom(currentClazz)) {

			if (!(currentType instanceof ParameterizedType)) {
				throw new AnnotationException("Dependee annotation can be put only on" + "Map or Collection type which is generic, not raw and not variable type");
			}

			ParameterizedType genericType = (ParameterizedType) currentType;
			Type[] types = genericType.getActualTypeArguments();
			Type valueType = null;
			if (Map.class.isAssignableFrom((currentClazz))) {
				/* map so get Key value generic type 1 */
				valueType = types[1];
				currentContainerMapping.setMappingType(MappingType.Map);
			} else {
				currentContainerMapping.setMappingType(MappingType.Collection);
				valueType = types[0];
			}

			if (Class.class.isAssignableFrom(valueType.getClass())) {
				currentClazz = (Class<?>) valueType;
			} else if (ParameterizedType.class.isAssignableFrom(valueType.getClass())) {
				currentClazz = (Class<?>) ((ParameterizedType) valueType).getRawType();
			} else {
				throw new AnnotationException("Annotation Depende can only be put above method" + "having return type: user object or generic collection or map with know" + "type(static, not variable). For ex. List<String> ok, List<?> or List<T> evil");
			}

			newCreated[0] = false;
			IMapping<?> valueMapping = this.getMapping(currentClazz, newCreated);
			currentContainerMapping.setContainerMapping(valueMapping);
			if (newCreated[0] == false) {
				return null;
			}

			currentContainerMapping = valueMapping;
			currentType = valueType;
		}

		currentContainerMapping.setMappingType(MappingType.Object);
		parentMapping.addMapping(methodName, dependeeMapping);

		Object[] returnValue = new Object[2];
		returnValue[0] = currentClazz;
		returnValue[1] = currentContainerMapping;
		return returnValue;
	}



	/*
	 * Get tree model from given Object.
	 * 
	 * It handles only primitives types or types with toString() method. It does
	 * not handle cycle in graph it could by timer but it is not precise or it
	 * could use some extra method or some "check" in this method.
	 */
	private IXMLTag createTreeModelFromObject(Object objectToConvert) {
		/*
		 * Temporary saved mapping for a class. Elements of it will be removed.
		 * First key is class name plus current mapping path in a tree made from
		 * methods labels, second key is method name.
		 */
		Map<String, Map<String, IMapping<?>>> savedMappingsForClass = new HashMap<String, Map<String, IMapping<?>>>();

		/*
		 * It holds all converted tags class depends on. First key is class name
		 * plus current mapping path in a tree made from methods labels, second
		 * key is method name.
		 */
		Map<String, Map<String, IXMLTag>> tmpConvertedChildrenObjs = new HashMap<String, Map<String, IXMLTag>>();

		/* mapping to use for converting an objects */
		IMapping<?> currentMapping = this.mapping;

		/*
		 * List of objects currently processed. Current object is part of
		 * "dependencies" of previous object in a list.
		 */
		LinkedList<Object> parentsList = new LinkedList<Object>();
		parentsList.addFirst(objectToConvert);

		IXMLTag[] result = new IXMLTag[1];
		/* key to use for above maps class name current mapping path */
		String currentKey = "";
		Object currentObjectToConvert = null;
		while (!(parentsList.isEmpty())) {
			currentObjectToConvert = parentsList.getFirst();
			currentKey = this.getMappingKey(currentMapping, currentObjectToConvert);

			if (!currentMapping.getMappingType().equals(MappingType.Object)) {
				boolean wasPushed = this.pushNewObjectFromContainer(currentObjectToConvert, currentMapping, parentsList, tmpConvertedChildrenObjs);
				if (wasPushed) {
					currentMapping = currentMapping.getContainerMapping();
					continue;
				} else { /* was not push because container was empty */
					this.handleEmptyContainer(currentObjectToConvert, currentMapping, parentsList, tmpConvertedChildrenObjs, result);
					currentMapping = currentMapping.getParentMapping();
					continue;
				}
			}

			/* here we have MappingType.Object */
			Map<String, IMapping<?>> methodMappings = savedMappingsForClass.get(currentKey);
			if (methodMappings == null) {
				methodMappings = currentMapping.getMappings(); /* it is a copy */
				savedMappingsForClass.put(currentKey, methodMappings);
			}

			/*
			 * if all dependent objects are done convert current object else get
			 * new object to convert
			 */
			if (!(methodMappings.isEmpty())) {
				currentMapping = this.pushNewObjectToConvert(currentObjectToConvert, methodMappings, parentsList);
			} else {
				Map<String, IXMLTag> convertedChildren = tmpConvertedChildrenObjs.get(currentKey);
				/* remove current value so it can be reused */
				savedMappingsForClass.put(currentKey, null);
				tmpConvertedChildrenObjs.put(currentKey, null);

				IXMLTag tag = this.convertObjectToTag(currentObjectToConvert, currentMapping, convertedChildren);

				/* Object was converted so it is done */
				parentsList.removeFirst();
				if (parentsList.isEmpty()) {
					/* this was last conversion so end */
					result[0] = tag;
					break;
				}

				String primitiveKey = "";
				if (currentMapping.getParentMapping().getMappingType().equals(MappingType.Map)) {
					/* remove also a key */
					primitiveKey = parentsList.removeFirst().toString();
				}
				Object parentObject = parentsList.getFirst();
				this.updateParentsConvertedChildren(tmpConvertedChildrenObjs, parentObject, currentMapping, primitiveKey, tag);
				currentMapping = currentMapping.getParentMapping();
			}

		} /* end outer while loop */

		return result[0];
	}



	/*
	 * Helper method to form method key for temporary maps
	 */
	private String getMappingKey(IMapping<?> mapping, Object objectToConvert) {
		return (this.getMappingTreeLabelsPath(mapping) + objectToConvert.getClass().getCanonicalName());
	}



	/*
	 * It pushes new object on parents "pipe line" also it returns current
	 * mapping for objects returend by registered methods.
	 */
	private IMapping<?> pushNewObjectToConvert(Object objectToConvert, Map<String, IMapping<?>> methodMappings, LinkedList<Object> parentsList) {
		Iterator<String> iter = methodMappings.keySet().iterator();
		IMapping<?> mapping = null;
		while (iter.hasNext()) {
			String methodName = iter.next();
			Object obj = this.getObjectFromMethod(objectToConvert, methodName);
			/* object can not be null because there wont be anything to convert */
			this.checkIfObjectIsNull(obj, parentsList, methodName);

			mapping = methodMappings.get(methodName);
			iter.remove();
			parentsList.addFirst(obj);
			break;
		}

		return mapping;
	}



	/*
	 * Check if object being converted is null.
	 */
	private void checkIfObjectIsNull(Object obj, List<Object> parentsList, String methodName) {
		if (obj == null) {
			Class<?> parentClass = this.getObjectParentFromList(parentsList, mapping).getClass();
			throw new XMLMarshallerException("Object returned by method: " + methodName + " in class: " + parentClass.getCanonicalName() + " or cointained in returned Collection or Map can not be null." + "User can not convert *null* objects");
		}

	}



	/*
	 * Get current object parent from parent list
	 */
	private Object getObjectParentFromList(List<Object> parentsList, IMapping<?> mapping) {

		Object parent = null;
		IMapping<?> currentMapping = mapping;
		for (Object object : parentsList) {
			if (currentMapping.getMappingType().equals(MappingType.Object)) {
				parent = object;
			}
			currentMapping = mapping.getParentMapping();
		}

		return parent;
	}



	/*
	 * Get Object from given method. It must be no arg and public.
	 */
	private Object getObjectFromMethod(Object currentObject, String methodName) {
		Method method = null;
		try {
			method = currentObject.getClass().getMethod(methodName, (Class<?>[]) null);
		} catch (NoSuchMethodException ex) {
			throw new XMLMarshallerException("Method with name: " + methodName + " and no arguments does not exist in class: " + currentObject.getClass().getCanonicalName());
		}

		Object obj = null;
		try {
			obj = method.invoke(currentObject, (Object[]) null);
		} catch (Exception e) {
			throw new XMLMarshallerException("Problems with IMapping, mapped method could" + " not be no-arg method, or not public or accessing protected data", e);
		}

		return obj;
	}



	/*
	 * Convert given Object having its mapping and partally converted children
	 * to IXMLTag
	 */
	private IXMLTag convertObjectToTag(Object objectToConvert, IMapping<?> mapping, Map<String, IXMLTag> convertedChildren) {
		IMarshallerConverter<Object> converter = (IMarshallerConverter<Object>) mapping.getConverter();
		if (converter == null) {
			throw new XMLMarshallerException("Converter in mapping for object type: " + objectToConvert.getClass().getCanonicalName() + "can not be null");
		}
		IXMLTag tag = null;
		try {
			tag = converter.convert(objectToConvert, convertedChildren);
		} catch (ClassCastException e) {
			throw new XMLMarshallerException("Object type: " + objectToConvert.getClass().getCanonicalName() + " and registered converter: " + converter.getClass().getCanonicalName() + " are not compatible." + "It might be the case that converter on Convertable annotation has is inappropriate or if mapping" + "was given by user *manually*, converter in mapping is inappropriate. Also it might be internal converter error", e);
		}

		return tag;
	}



	/*
	 * Update temporary converted children map
	 */
	private void updateParentsConvertedChildren(Map<String, Map<String, IXMLTag>> convertedChildren, Object parentObject, IMapping<?> mapping, String mapsKey, IXMLTag tag) {
		String parentKey = this.getMappingKey(mapping.getParentMapping(), parentObject);
		String methodName = mapping.getMappedMethodName();
		/*
		 * Init parent entry in a map if does not exist
		 */
		Map<String, IXMLTag> convertedTagsForParent = convertedChildren.get(parentKey);
		if (convertedTagsForParent == null) {
			convertedTagsForParent = new HashMap<String, IXMLTag>();
			convertedChildren.put(parentKey, convertedTagsForParent);
		}

		MappingType parentType = mapping.getParentMapping().getMappingType();
		if (!convertedTagsForParent.containsKey(methodName) && (parentType.equals(MappingType.Collection) || parentType.equals(MappingType.Map))) {
			String wrapperTagName = "";
			switch (parentType) {
			case Collection:
				wrapperTagName = LIST_TAG_NAME;
				break;
			case Map:
				wrapperTagName = MAP_TAG_NAME;
				break;
			}

			/* create tag for this container mapping */
			ICompositeTag wrapper = this.createWrapperTag(wrapperTagName);
			convertedTagsForParent.put(methodName, wrapper);
		}

		/*
		 * add entry to a map or object to a list or simply object method
		 * returns type
		 */
		switch (parentType) {
		case Map:
			tag = this.createMapEntry(mapsKey, tag);
		case Collection:
			ICompositeTag containerTag = (ICompositeTag) convertedTagsForParent.get(methodName);
			containerTag.addTag(tag);
			break;
		case Object:
			convertedTagsForParent.put(methodName, tag);
			break;
		}

	}



	/*
	 * Count mapping depth in a mapping tree.
	 */
	private String getMappingTreeLabelsPath(IMapping<?> mapping) {
		IMapping<?> tmpMapping = mapping;
		StringBuffer pathMethodLabel = new StringBuffer();
		while (tmpMapping != null) {
			String pathMethodName = tmpMapping.getMappedMethodName();
			pathMethodLabel.insert(0, pathMethodName + "<-->");
			tmpMapping = tmpMapping.getParentMapping();
		}

		return pathMethodLabel.toString();
	}



	/*
	 * Creates Tag Entry for a map
	 */
	private ICompositeTag createMapEntry(String key, IXMLTag value) {
		ICompositeTag entry = this.createWrapperTag(MAP_TAG_ENTRY_NAME);

		ISimpleTag keyTag = this.treeModelFactory.createSimpleTag();
		IXMLQName keyName = this.treeModelFactory.createXMLQName();
		keyName.setLocalPart(MAP_TAG_KEY_NAME);
		keyTag.setName(keyName);

		ICompositeTag valueTag = this.createWrapperTag(MAP_TAG_VALUE_NAME);

		entry.addTag(keyTag);
		entry.addTag(valueTag);

		keyTag.setValue(key);
		valueTag.addTag(value);

		return entry;
	}



	/*
	 * Helper method to create wrapper tag
	 */
	private ICompositeTag createWrapperTag(String name) {
		ICompositeTag tag = this.treeModelFactory.createCompositeTag();
		IXMLQName tagName = this.treeModelFactory.createXMLQName();
		tagName.setLocalPart(name);
		tag.setName(tagName);

		return tag;
	}



	/*
	 * 
	 * Returns true if object from container was added to parentsList.
	 */
	private boolean pushNewObjectFromContainer(Object objectToConvert, IMapping<?> mapping, LinkedList<Object> parentsList, Map<String, Map<String, IXMLTag>> tmpConvertedChildrenObjs) {
		MappingType type = mapping.getMappingType();
		Collection<?> collection = null;
		Map<?, ?> mapObj = null;
		switch (type) {
		case Collection:
			collection = ((Collection<?>) objectToConvert);
			break;
		case Map:
			mapObj = ((Map<?, ?>) objectToConvert);
			break;
		}

		boolean wasAdded = false;
		if ((mapObj != null && !(mapObj.isEmpty())) || (collection != null && !(collection.isEmpty()))) {
			this.handleNotEmptyContainer(parentsList, type, collection, mapObj);

			this.checkIfObjectIsNull(parentsList.getFirst(), parentsList, mapping.getMappedMethodName());
			wasAdded = true;
		}

		return wasAdded;
	}



	/*
	 * It takes care of situation where container is empty. Also if this was
	 * last conversion it updates the result.
	 */
	private void handleEmptyContainer(Object objectToConvert, IMapping<?> mapping, LinkedList<Object> parentsList, Map<String, Map<String, IXMLTag>> tmpConvertedChildrenObjs, IXMLTag[] result) {
		String currentMappedMethod = mapping.getMappedMethodName();
		String currentKey = this.getMappingKey(mapping, objectToConvert);
		/*
		 * Remove container if empty also there is key to handle if this mapping
		 * is Map
		 */
		parentsList.removeFirst();
		String primitiveKey = "";
		IMapping<?> parentMapping = mapping.getParentMapping();
		if (parentMapping != null && parentMapping.getMappingType().equals(MappingType.Map)) {
			/* remove also a key for value map of map */
			primitiveKey = parentsList.removeFirst().toString();
		}

		Map<String, IXMLTag> convertedTagsForContainer = tmpConvertedChildrenObjs.get(currentKey);
		IXMLTag tag = convertedTagsForContainer.get(currentMappedMethod);
		if (!(parentsList.isEmpty())) {
			this.updateParentsConvertedChildren(tmpConvertedChildrenObjs, parentsList.getFirst(), mapping, primitiveKey, tag);
			/* remove entry for reuse */
			convertedTagsForContainer.remove(currentMappedMethod);
		} else { /* this container is the last to convert */
			result[0] = tag;
		}
	}



	/*
	 * Helper method for processContainerObject method. It takes care of
	 * situation where container is not empty
	 */
	private void handleNotEmptyContainer(LinkedList<Object> parentsList, MappingType type, Collection<?> collection, Map<?, ?> mapObj) {

		Iterator<?> iter = null;
		Object value = null;
		if (type.equals(MappingType.Map)) {
			iter = mapObj.keySet().iterator();
			Object mapKey = iter.next();
			value = mapObj.get(mapKey);
			parentsList.addFirst(mapKey);
		} else {
			iter = collection.iterator();
			value = iter.next();
		}

		parentsList.addFirst(value);
		iter.remove();
	}



	/*
	 * Writes given elements to a file.
	 */
	private void writeTreeModel(XMLStreamWriter writer, IXMLTag root, String encoding, String version) throws XMLStreamException {
		writer.writeStartDocument(encoding, version);
		if (root.isSimpleTag()) {
			this.writeSimpleTag(writer, (ISimpleTag) root);
			return;
		}

		/*
		 * These maps are used for: remember current list of composite and
		 * simple tags children to write and order counter - which element to
		 * write first
		 */
		Map<IXMLQName, List<ICompositeTag>> compositeTagsToWrite = new HashMap<IXMLQName, List<ICompositeTag>>();
		Map<IXMLQName, List<ISimpleTag>> simpleTagsToWrite = new HashMap<IXMLQName, List<ISimpleTag>>();
		Map<IXMLQName, Integer> currentElementToWrite = new HashMap<IXMLQName, Integer>();

		ICompositeTag currentCompositeRoot = (ICompositeTag) root;
		while (currentCompositeRoot != null) {
			IXMLQName currentCompositeName = currentCompositeRoot.getName();
			List<ICompositeTag> currentCompositeTags = compositeTagsToWrite.get(currentCompositeName);
			List<ISimpleTag> currentSimpleTags = simpleTagsToWrite.get(currentCompositeName);

			if (currentCompositeTags == null && currentSimpleTags == null) {
				/*
				 * here we are starting so init current compositeTag "context"
				 */
				this.writeXMLTag(writer, currentCompositeRoot);

				currentCompositeTags = currentCompositeRoot.getAllCompositeTags();
				currentSimpleTags = currentCompositeRoot.getAllSimpleTags();

				compositeTagsToWrite.put(currentCompositeName, currentCompositeTags);
				simpleTagsToWrite.put(currentCompositeName, currentSimpleTags);
				currentElementToWrite.put(currentCompositeName, 0);
			}

			/*
			 * write tags according to ordering
			 */
			int elemToWrite = currentElementToWrite.get(currentCompositeName);
			IOrder order = currentCompositeRoot.getOrder();
			while (elemToWrite < order.size()) {
				IXMLTag tag = order.getTagAt(elemToWrite);
				if (tag.isSimpleTag()) {
					this.writeSimpleTag(writer, (ISimpleTag) tag);
					currentSimpleTags.remove(0);
					elemToWrite++;
				} else {
					/*
					 * save where did we stopped
					 */
					currentElementToWrite.put(currentCompositeName, elemToWrite);
					break;
				}
			}

			/*
			 * At this point we should ask if we are done with this composite
			 * tag. If yes, we should end tag, move "up the tree"(change to tag
			 * parent if not root) and move forward order counter since we have
			 * written composite element.
			 */
			if (currentCompositeTags.isEmpty()) {
				writer.writeEndElement();

				ICompositeTag parent = currentCompositeRoot.getParent();
				if (parent == null) {
					break;
				}

				compositeTagsToWrite.put(currentCompositeName, null);
				simpleTagsToWrite.put(currentCompositeName, null);

				int parentCounter = currentElementToWrite.get(parent.getName());
				currentElementToWrite.put(parent.getName(), parentCounter + 1);

				currentCompositeRoot = parent;
				continue;
			}

			/*
			 * Change to first composite tag child in current composite tag
			 * root. We are moving "down the tree".
			 * 
			 * Implementation Note:
			 * 
			 * We can do this because we know that composite tag list is not
			 * null and not empty removing ensures that it will end. Remember
			 * also that all continues(without holes) simple tags are written in
			 * "one go". That is if there only remain simple tags children at
			 * "back" of order list they will be "drained" from simple tags list
			 * before hitting this place.
			 * 
			 * For ex. S - simple tag C - composite tag R - current root
			 * composite tag
			 * 
			 * Lets say we have this ordering: R-CCCSSSSCSS-R
			 * 
			 * After writing 3rd composite tag, "control" returns to current
			 * root tag. Then all simple tags are written (4 of them) in
			 * "one go", and so on.
			 * 
			 * In other words simple tags list will be empty just after
			 * composite tags list have became empty.
			 */
			currentCompositeRoot = currentCompositeTags.remove(0);
		} /* end inner while loop */
	}



	/*
	 * Write Simple XML Tag.
	 */
	private void writeSimpleTag(XMLStreamWriter writer, ISimpleTag tag) throws XMLStreamException {

		this.writeXMLTag(writer, tag);

		writer.writeCharacters(tag.getValue());
		writer.writeEndElement();
	}



	/*
	 * Write generic XML Tag.
	 */
	private void writeXMLTag(XMLStreamWriter writer, IXMLTag tag) throws XMLStreamException {

		IXMLQName name = tag.getName();
		writer.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());

		Map<String, String> namespaces = tag.getNamespaces();
		for (String prefix : namespaces.keySet()) {
			writer.writeNamespace(prefix, namespaces.get(prefix));
		}

		Map<IXMLQName, String> attributes = tag.getAttributes();
		for (IXMLQName attrName : attributes.keySet()) {
			writer.writeAttribute(attrName.getPrefix(), attrName.getNamespaceURI(), attrName.getLocalPart(), attributes.get(attrName));
		}

	}



	/*
	 * Check if proper mapping is registered.
	 */
	private void checkRegisteredMapping(Object objectToSave) {
		if (this.mapping == null || (this.mapping.getMappingType().equals(MappingType.Object) && this.mapping.getConverter() == null)) {
			throw new IllegalArgumentException("To use this method you have to register IMapping for given object with" + " object class and converter");
		}
	}

}