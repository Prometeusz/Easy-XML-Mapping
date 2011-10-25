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

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;

import java.io.InputStream;

import java.util.HashMap;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.xml.namespace.QName;

import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.prometheuscode.utils.Utils;
import org.prometheuscode.xml.annotation.AnnotationException;
import org.prometheuscode.xml.annotation.TagToJavaConverter;
import org.prometheuscode.xml.annotation.TagToJavaConverter.XMLQualifiedName;
import org.prometheuscode.xml.treemodel.CompositeTag;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.IXMLTag;
import org.prometheuscode.xml.treemodel.XMLQName;

/**
 * <p>
 * XML parser based on StaX woodstox implementation.
 * </p>
 * 
 * <p>
 * Mixed XML Complex Types are not supported. Any data between complex type and
 * child element will be ignored.
 * </p>
 * 
 * 
 * <p>
 * If there is only one simple tag in XML file it will be returned as a child of
 * composite tag:
 * 
 * <pre>
 * {@code <root></root>}
 * </pre>
 * 
 * </p>
 * 
 * @author marta
 * @see org.prometheuscode.xml.IXMLAdaptiveUnmarshaller
 * 
 */
class XMLStaXAdaptiveUnmarshaller implements IXMLAdaptiveUnmarshaller {

	private IXMLTreeModelFactory treeModelFactory;

	private Map<IXMLQName, IUnmarshallerConverter> converters = new HashMap<IXMLQName, IUnmarshallerConverter>();



	public XMLStaXAdaptiveUnmarshaller() {
		this.treeModelFactory = new XMLTreeModelFactory();
	}



	public XMLStaXAdaptiveUnmarshaller(IXMLTreeModelFactory factory) {
		this.treeModelFactory = factory;
	}



	/*
	 * Public Methods ***^_^***
	 */



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if file input stream is null.
	 * 
	 * @throws XMLUnmarshallerException
	 *             if error occured during unmarshalling.
	 * 
	 */
	@Override
	public IXMLRoot getTreeModel(InputStream fileStream) {

		boolean getCompleteTreeModel = true;

		ParsingResult result = this.unmarshalXML(fileStream, getCompleteTreeModel);
		return result.getTreeModel();
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if file could not be found.
	 * 
	 */
	@Override
	public IXMLRoot getTreeModel(String xmlFilePath) {

		InputStream in = null;
		try {
			in = new FileInputStream(xmlFilePath);
		} catch (FileNotFoundException exp) {
			throw new IllegalArgumentException(exp);
		}

		boolean getCompleteTreeModel = true;

		ParsingResult result = this.unmarshalXML(in, getCompleteTreeModel);
		return result.getTreeModel();
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if file input stream is null.
	 * 
	 * @throws XMLUnmarshallerException
	 *             if error occured during unmarshalling.
	 * 
	 */
	@Override
	public Map<IXMLQName, List<Object>> unmarshal(InputStream xmlFileStream) {

		boolean getCompleteTreeModel = false;

		ParsingResult result = this.unmarshalXML(xmlFileStream, getCompleteTreeModel);
		return result.getConvertedObjs();
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if file could not be found.
	 * 
	 */
	@Override
	public Map<IXMLQName, List<Object>> unmarshal(String xmlFilePath) {

		InputStream in = null;
		try {
			in = new FileInputStream(xmlFilePath);
		} catch (FileNotFoundException exp) {
			throw new IllegalArgumentException(exp);
		}

		boolean getCompleteTreeModel = false;

		ParsingResult result = this.unmarshalXML(in, getCompleteTreeModel);
		return result.getConvertedObjs();
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             argument is null.
	 */
	@Override
	public void registerMapping(Map<IXMLQName, IUnmarshallerConverter> converters) {

		if (converters == null) {
			throw new IllegalArgumentException("Converts can't be null");
		}

		this.converters = new HashMap<IXMLQName, IUnmarshallerConverter>(converters);
	}



	/**
	 * 
	 * @throws IllegalArgumentException
	 *             argument is null.
	 * 
	 * @throws AnnotationException
	 *             if annotation has incorrect state.
	 */
	@Override
	public Map<IXMLQName, IUnmarshallerConverter> createMapping(Set<String> packages) {

		if (packages == null) {
			throw new IllegalArgumentException("Argument can not be null");
		}
		/* TODO cache */
		Map<IXMLQName, IUnmarshallerConverter> newConverters = new HashMap<IXMLQName, IUnmarshallerConverter>();
		Class<TagToJavaConverter> annotationClass = TagToJavaConverter.class;
		Class<?> converterInterface = IUnmarshallerConverter.class;

		ClassPool pool = ClassPool.getDefault();
		for (String packageName : packages) {
			File[] packageFiles = Utils.getClassFilesFromPackage(packageName);

			// System.out.print("# of files in package: " + packageFiles.length
			// + "\n");
			/* inspect class files */
			for (File file : packageFiles) {
				if (file.isDirectory()) {
					continue;
				}

				CtClass clazzToCheck = null;
				try {
					clazzToCheck = pool.makeClass(new FileInputStream(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// System.out.print("class name after javassist: " +
				// clazzToCheck.getName() + "\n");

				/* check if it implements interface */
				if (!(this.isImplementingInterface(clazzToCheck, converterInterface))) {
					continue;
				}

				/* process annotations */
				if (clazzToCheck.hasAnnotation(annotationClass)) {
					this.processAnnotations(clazzToCheck, annotationClass, newConverters);
				}

			}

		}

		return newConverters;
	}



	@Override
	public Map<IXMLQName, IUnmarshallerConverter> getRegisteredMapping() {
		Map<IXMLQName, IUnmarshallerConverter> converters = new HashMap<IXMLQName, IUnmarshallerConverter>();
		if (this.converters != null) {
			converters.putAll(this.converters);
		}

		return converters;
	};

	
	
	/*
	 * Private Clasess ***^_^***
	 */

	
	
	/*
	 * Class helps in handling multi-return from parsing.
	 */
	private static class ParsingResult {

		private IXMLRoot treeModelRoot;

		private Map<IXMLQName, List<Object>> convertedObjs;



		ParsingResult() {
		}



		ParsingResult(IXMLRoot treeModelRoot, Map<IXMLQName, List<Object>> convertedObjs) {
			this.treeModelRoot = treeModelRoot;
			this.convertedObjs = convertedObjs;
		}



		IXMLRoot getTreeModel() {
			return this.treeModelRoot;
		}



		Map<IXMLQName, List<Object>> getConvertedObjs() {
			return this.convertedObjs;
		}

	}



	/*
	 * Private Methods ***^_^***
	 */

	
	
	/*
	 * Does class implements specified interface?
	 */
	private boolean isImplementingInterface(CtClass clazzToCheck, Class<?> interfeceClazz) {
		CtClass[] interfaces = null;
		try {
			interfaces = clazzToCheck.getInterfaces();
		} catch (NotFoundException e1) {
			/* interfaces on class could not be found? */
			e1.printStackTrace();
		}

		String unmarshallerInterfaceName = interfeceClazz.getCanonicalName();
		boolean found = false;
		for (CtClass interf : interfaces) {
			if (interf.getName().equals(unmarshallerInterfaceName)) {
				found = true;
				break;
			}
		}
		return found;
	}



	/*
	 * Process user specified annotations to register mappings.
	 */
	private void processAnnotations(CtClass clazzToCheck, Class<?> annotationType, Map<IXMLQName, IUnmarshallerConverter> newConverters) {

		TagToJavaConverter annot = null;
		try {
			annot = (TagToJavaConverter) clazzToCheck.getAnnotation(annotationType);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		/* get IXMLQName from annotation */
		IXMLQName tagToBindName = this.treeModelFactory.createXMLQName();
		XMLQualifiedName name = annot.value();
		tagToBindName.setLocalPart(name.localPart());
		tagToBindName.setNamespaceURI(name.namespaceURI());

		IUnmarshallerConverter userConverter = null;
		Class<?> userClazz = null;
		/* get converter from annotation */
		try {
			userClazz = this.getClass().getClassLoader().loadClass(clazzToCheck.getName());
			userConverter = (IUnmarshallerConverter) userClazz.newInstance();
		} catch (InstantiationException e) {
			throw new AnnotationException("Annotation was put on: " + userClazz.getCanonicalName() + " which might not have no-arg constructor", e);
		} catch (IllegalAccessException e) {
			throw new AnnotationException("Annotation was put on: " + userClazz.getCanonicalName() + " might not have proper access rights for no-arg constructor", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		 /* update current mappings */
		newConverters.put(tagToBindName, userConverter); 
	}



	/*
	 * Helps in creating StaX parser.
	 */
	private ParsingResult unmarshalXML(InputStream fileStream, boolean getCompleteTreeModel) {

		if (fileStream == null) {
			throw new IllegalArgumentException("Null InputStream is not a valid argument");
		}

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = null;
		try {
			reader = factory.createXMLStreamReader(fileStream);
			return this.parseElements(reader, getCompleteTreeModel);
		} catch (XMLStreamException e) {
			throw new XMLUnmarshallerException(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				fileStream.close();
			} catch (Exception e) {
				/*
				 * usually this should be omitted
				 */
				e.printStackTrace();
			}
		}
	}



	/*
	 * This function process XML file and creates XML tree model. Also if
	 * converters was registered and parameter getAllModelWithoutConversion is
	 * false, it does conversion of XML tree model tags.
	 * 
	 * It does not create tree model if it is not necessary.
	 * 
	 */
	private ParsingResult parseElements(XMLStreamReader reader, boolean getAllModelWithoutConversion) throws XMLStreamException {

		/* cache for tags */
		ITagsCache tagsCache = new TagsCache();

		/*
		 * This temporary map holds converted children of composite tags for
		 * which converter was registered.
		 */
		Map<IXMLQName, Map<IXMLQName, List<Object>>> tmpConvertedChildren = new HashMap<IXMLQName, Map<IXMLQName, List<Object>>>();

		/*
		 * Map holds highest in tree converted composite tags. Composite Tags
		 * for which there does not exist composite tag parent having registered
		 * converter.
		 */
		Map<IXMLQName, List<Object>> rootConvertedTags = new HashMap<IXMLQName, List<Object>>();

		/*
		 * Vars for XML tree tag depth. They indicate if we should build tree
		 * model objects for composite tag for which converter was registered.
		 */
		boolean shouldCreateTreeModel = false;
		boolean useCache = true;
		/* relative to current tag for which converter is registered */
		int relativeCurrentDepth = 0; 
		if (getAllModelWithoutConversion) {
			shouldCreateTreeModel = true;
			useCache = false;
		}

		/*
		 * NOTE: tmp root simplifies code and handle one simple tag case
		 */
		ICompositeTag currentParent = new CompositeTag(new XMLQName("root", "", ""));

		IXMLQName tmpName = this.treeModelFactory.createXMLQName();
		Map<IXMLQName, String> tmpAttributes = new HashMap<IXMLQName, String>();
		Map<String, String> tmpNamespaces = new HashMap<String, String>();
		while (!(this.moveToNextTag(reader) == END_DOCUMENT)) {
			// System.out.print("Event type AFTER loop begin " +
			// reader.getEventType() + "\n");
			/*
			 * If processing of current composite tag is done change current
			 * parent and update the depth. Move "up the tree".
			 * 
			 * for ex. 
			 * <composite-parent> 
			 * 		<simple></simple> 
			 * 		...
			 * 		<composite-child> ---> here currentParent == <composite-child>
			 * 			<simple></simple> 
			 * 			... 
			 * 			</composite-child> ---> here change
			 * 			currentParent to <composite-parent> 
			 * 		... 
			 * </composite-parent>
			 */
			if (this.isThisEndTagOfCurrentParent(reader, currentParent)) {
				// System.out.print("I have changed parent to: " +
				// currentParent.getParent().getName() + "\n");

				relativeCurrentDepth--;
				if (!(getAllModelWithoutConversion)) {
					this.processConvertableTag(currentParent, rootConvertedTags, tmpConvertedChildren, relativeCurrentDepth);
				}

				/*
				 * Here we check if all tree model objects were created for
				 * converter at highest deep(for the most top composite tag). If
				 * yes we can stop creating tree model.
				 * 
				 * Also we can update cache free slots.
				 */
				// System.out.print("Current depth " + relativeCurrentDepth +
				// "\n");
				if (relativeCurrentDepth == 0) {
					shouldCreateTreeModel = false;
					tagsCache.updateCompositeTagsFreeSlots();
					tagsCache.updateSimpleTagsFreeSlots();
				}

				currentParent = currentParent.getParent();
			}

			if (reader.isEndElement()) {
				reader.next();
				continue;
			}

			/*
			 * here we should have Start Element
			 */

			IXMLQName tagName = this.convertQName(reader.getName(), tmpName);

			/*
			 * Don't create tree model for a tag if there is no converter for it
			 * unless composite tag ancestor has converter.
			 */
			if (!(shouldCreateTreeModel) && !(this.converters.containsKey(tagName))) {
				reader.next();
				continue;
			} else if (!(shouldCreateTreeModel)) {
				shouldCreateTreeModel = true; /* set if there is converter */
			}

			IXMLTag tagCreated = this.createTreeNodeTag(reader, tmpName, tmpAttributes, tmpNamespaces, tagsCache, useCache);
			currentParent.addTag(tagCreated);
			if (!(tagCreated.isSimpleTag())) {
				/* move "down the tree" */
				currentParent = (ICompositeTag) tagCreated;
				relativeCurrentDepth++;
			}

		} /* end while loop */

		return this.createParsingResult(currentParent, reader, rootConvertedTags);
	}



	/*
	 * Helper method to check if given composite tag can be converted and update
	 * state of parent.
	 */
	private void processConvertableTag(ICompositeTag compositeTag, Map<IXMLQName, List<Object>> rootConvertedTags, Map<IXMLQName, Map<IXMLQName, List<Object>>> tmpConvertedChildren, int currentDepth) {
		IXMLQName compositeTagName = compositeTag.getName();
		IUnmarshallerConverter converter = this.converters.get(compositeTagName);
		Object convertedObj = null;
		if (converter != null) {
			Map<IXMLQName, List<Object>> convertedObjs = tmpConvertedChildren.get(compositeTagName);
			// System.out.print("Map passed to CONVERTER: " + convertedObjs +
			// "\n");
			convertedObj = converter.convert(compositeTag, convertedObjs);

			/*
			 * prevents reuse, there can be more composite tag with the same
			 * name but with different children, don't reuse already converted
			 * children, it is temporary after all.
			 */
			if (convertedObjs != null) {
				tmpConvertedChildren.put(compositeTagName, null);
			}

			/*
			 * if we have just converted top most composite tag for which
			 * converter was registered, update "root" map
			 */
			if (currentDepth == 0) {
				List<Object> list = rootConvertedTags.get(compositeTagName);
				if (list == null) {
					list = new ArrayList<Object>();
					rootConvertedTags.put(compositeTagName, list);
				}

				list.add(convertedObj);
				return; /* we don't have anything more to do */
			}

			/*
			 * Add converted obj to the converted objs list of parent of current
			 * composite tag.
			 * 
			 * Ignore otherwise. NOTE: here it might appropriate to generate and
			 * exception since user did not specify parent converter in a chain.
			 * 
			 * For ex. 
			 * <composite-tag> ---> we have converter for it
			 * 		<childcomposite-tag> --> ---> we do not have converter for it
			 * 				<another-child-composite-tag> ---> we have converter for it
			 * 				</another-child-composite-tag>
			 * 		 </childcomposite-tag>
			 * </composite-tag>
			 * 
			 * Converter can return null obj.
			 */
			IXMLQName parentName = compositeTag.getParent().getName();
			if (convertedObj != null && this.converters.containsKey(parentName)) {
				Map<IXMLQName, List<Object>> map = tmpConvertedChildren.get(parentName);
				if (map == null) {
					map = new HashMap<IXMLQName, List<Object>>();
					tmpConvertedChildren.put(parentName, map);
				}

				List<Object> objs = map.get(compositeTagName);
				if (objs == null) {
					objs = new ArrayList<Object>();
					map.put(compositeTagName, objs);
				}

				objs.add(convertedObj);
			}

		}
	}



	/*
	 * Create node of the tree.
	 * 
	 * PRECONDITION: - current element is START_ELEMENT
	 * 
	 * POSTCONDITION: - SimpleTag or ComposisteTag was created and attached to
	 * the tree model - next() event can be: - START_ELEMENT - if current tag is
	 * composite tag - END_ELEMENT - if current tag is simple tag - CHARACTERS -
	 * if current tag is simple tag
	 * 
	 * 
	 * 
	 * @return tag created
	 */
	private IXMLTag createTreeNodeTag(XMLStreamReader reader, IXMLQName tagName, Map<IXMLQName, String> attributes, Map<String, String> namespaces, ITagsCache tagsCache, boolean useCache) throws XMLStreamException {

		IXMLTag tagToReturn = null;

		this.processTagProperites(reader, attributes, namespaces);
		/*
		 * System.out.print(reader.getName() + "\n");
		 * System.out.print("attributes  " + attributes + "\n");
		 * System.out.print("namespaces  " + namespaces + "\n");
		 */

		if (this.isThisEventSimpleTag(reader)) {
			/*
			 * here reader.getEventType() should be
			 * XMLStreamConstants.CHARACTERS or XMLStreamConstants.END_ELEMENT
			 * if SimpleTag is empty
			 */
			ISimpleTag simpleTag = null;
			if (useCache) {
				simpleTag = tagsCache.getSimpleTag(tagName);
			} else {
				simpleTag = this.treeModelFactory.createSimpleTag();

			}

			this.initXMLTag(simpleTag, tagName, attributes, namespaces);
			simpleTag.setValue(this.getSimpleTagContent(reader));

			tagToReturn = simpleTag;
		} else {
			ICompositeTag compositeTag = null;
			if (useCache) {
				compositeTag = tagsCache.getCompositeTag(tagName);
			} else {
				compositeTag = this.treeModelFactory.createCompositeTag();
			}

			this.initXMLTag(compositeTag, tagName, attributes, namespaces);
			tagToReturn = compositeTag;
		}

		return tagToReturn;
	}



	/*
	 * Init IXMLTag ( common ) properties.
	 */
	private void initXMLTag(IXMLTag tag, IXMLQName tagName, Map<IXMLQName, String> attributes, Map<String, String> namespaces) {
		IXMLQName name = tag.getName();
		name.setLocalPart(tagName.getLocalPart());
		name.setNamespaceURI(tagName.getNamespaceURI());
		name.setPrefix(tagName.getPrefix());

		if (!(attributes.isEmpty())) {
			tag.setAttributes(attributes);
			attributes.clear(); /* clear them for reuse */
		}

		if (!(namespaces.isEmpty())) {
			tag.setNamespaces(namespaces);
			namespaces.clear(); /* clear them for reuse */
		}

	}



	/*
	 * <p> Helper function to simplify EndElement Checking. </p> <p> Ekhm, is
	 * this EndElement the end of current Composite Tag? </p>
	 * 
	 * 
	 * @throws XMLStreamException
	 */
	private boolean isThisEndTagOfCurrentParent(XMLStreamReader reader, ICompositeTag currentParent) throws XMLStreamException {
		if (reader.isEndElement()) {
			QName qname = reader.getName();
			IXMLQName currentParentName = currentParent.getName();

			if (qname.getLocalPart().equals(currentParentName.getLocalPart()) && qname.getNamespaceURI().equals(currentParentName.getNamespaceURI())) {
				return true;
			}
		}
		return false;
	}



	/*
	 * Converts <code>javax.xml.namespace.Qname</code> to <code>IXMLQName</code>
	 */
	private IXMLQName convertQName(QName qname, IXMLQName tagName) {
		tagName.setLocalPart(qname.getLocalPart());
		tagName.setNamespaceURI(qname.getNamespaceURI());
		tagName.setPrefix(qname.getPrefix());
		return tagName;
	}



	/*
	 * 
	 * <p> Helper function for SimpleTag Content. </p>SimpleTag might be
	 * empty.</br>
	 * 
	 * @param event XMLEvent
	 * 
	 * @return
	 */
	private String getSimpleTagContent(XMLStreamReader reader) {
		String simpleTagContent = "";
		if (reader.isCharacters()) {
			simpleTagContent = reader.getText();
		}
		return simpleTagContent;
	}



	/*
	 * Check if Start Element is Simple tag.
	 * 
	 * PRECONDITION: - current event is XMLStreamConstants.START_ELEMENT
	 * 
	 * POSTCONDITION: - element XMLStreamReader.next() returns:
	 * XMLStreamConstants.CHARACTERS(data) or ---> if current tag is simple tag
	 * XMLStreamConstants.END_ELEMENT or ---> if current tag is simple tag
	 * XMLStreamConstants.START_ELEMENT ---> if current tag is composite tag
	 * 
	 * 
	 * 
	 * @throws XMLStreamException , XMLMarshallerException
	 */
	private boolean isThisEventSimpleTag(XMLStreamReader reader) throws XMLStreamException {

		/*
		 * Avoid anything which is not: XMLStreamConstants.START_ELEMENT
		 * XMLStreamConstants.CHARACTERS XMLStreamConstants.END_ELEMENT
		 */
		while (reader.hasNext()) {
			reader.next();

			/*
			 * Skip useless events, for ex. : white spaces, comments processed
			 * attributes and namespaces... after composite tag to children tag.
			 * 
			 * For ex.
			 * 
			 * <composite-tag> <--- from here to here
			 * ---><simple-tag>content</simple-tag> ... </composite-tag>
			 * 
			 * 
			 * Also if this spaces being skipped are "content" of simple Tag
			 * they can be "catch" in later check. Skipping them here will make
			 * them compressed: from "n" spaces to none. ( perfect compression
			 * ;) For ex. Content of this tag:
			 * 
			 * <simple-tag> </simple-tag>
			 * 
			 * will be turned into
			 * 
			 * <simple-tag></simple-tag>
			 * 
			 * This should be still correct.
			 */

			if (reader.isStartElement()) {
				return false;
			}

			/*
			 * Simple Tag can be empty or have characters data
			 */
			if ((reader.isCharacters() && !(reader.isWhiteSpace()) || reader.isEndElement())) {
				return true;
			}

		}

		/*
		 * It should not happen
		 */
		throw new XMLMarshallerException("It should stop on START_ELEMENT, END_ELEMENT or CHARACTERS(Data or CDATA), unexpected EndDocument Event");
	}



	/*
	 * <p> Moves cursor to next tag(start or end element) if it doesn't point to
	 * one. </p>
	 * 
	 * <pre> It may return: XMLStreamConstants.START_ELEMENT or
	 * XMLStreamConstants.END_ELEMENT or XMLStreamConstants.END_DOCUMENT </pre>
	 * 
	 * @param reader
	 * 
	 * @return integer indicating Event type: Tag(Start or End) or End Document
	 * if EOF
	 * 
	 * @throws XMLStreamException
	 */
	private int moveToNextTag(XMLStreamReader reader) throws XMLStreamException {

		while (reader.hasNext()) {
			if (reader.isStartElement() || reader.isEndElement()) {
				break;
			}
			reader.next();
		}

		return reader.getEventType();
	}



	/*
	 * Helper function to init attributes and namespaces on this current
	 * <code>XMLStreamConstants.START_ELEMENT</code>.
	 * 
	 * <pre> PRECONDITION: - current element is XMLStreamConstants.START_ELEMENT
	 * </pre>
	 */
	private void processTagProperites(XMLStreamReader reader, Map<IXMLQName, String> attributes, Map<String, String> namespaces) {
		/*
		 * get Attributes
		 */
		int attrsNr = reader.getAttributeCount();
		for (int i = 0; i < attrsNr; i++) {
			IXMLQName qname = this.treeModelFactory.createXMLQName();
			qname.setNamespaceURI(reader.getAttributeNamespace(i));
			qname.setLocalPart(reader.getAttributeLocalName(i));
			qname.setPrefix(reader.getAttributePrefix(i));

			attributes.put(qname, reader.getAttributeValue(i));
		}

		/*
		 * get Namespaces
		 */
		int namespacesNr = reader.getNamespaceCount();
		for (int i = 0; i < namespacesNr; i++) {
			namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
		}

	} /* end of method */



	/*
	 * Returns proper Parsing result. It also handles situation when there is
	 * only one simple tag in XML file.
	 */
	private ParsingResult createParsingResult(ICompositeTag currentRoot, XMLStreamReader reader, Map<IXMLQName, List<Object>> rootConvertedTags) {
		/*
		 * to simplify code we have used tmp root so clean up at the end and get
		 * proper root
		 */
		List<? extends ICompositeTag> compTags = currentRoot.getAllCompositeTags();
		List<? extends ISimpleTag> simpTags = currentRoot.getAllSimpleTags();
		ICompositeTag root = null;
		if (!(compTags.isEmpty())) {
			root = compTags.get(0);
			root.setParent(null);
		} else if (!(simpTags.isEmpty())) {
			/*
			 * here XML has only one simple tag, however interface requires
			 * ICompositeTag, so use tmp root as return value instead.
			 */
			root = currentRoot;
		}

		IXMLRoot documentRoot = this.treeModelFactory.createXMLDocument();

		documentRoot.setEncoding(reader.getCharacterEncodingScheme());
		documentRoot.setVersion(reader.getVersion());
		documentRoot.setXMLRoot(root);

		return new ParsingResult(documentRoot, rootConvertedTags);
	}

}
