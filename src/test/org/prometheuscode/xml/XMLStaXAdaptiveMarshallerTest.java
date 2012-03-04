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

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.prometheuscode.xml.IMapping;
import org.prometheuscode.xml.IMarshallerConverter;
import org.prometheuscode.xml.IXMLAdaptiveMarshaller;
import org.prometheuscode.xml.Mapping;
import org.prometheuscode.xml.MappingType;
import org.prometheuscode.xml.XMLStaXAdaptiveMarshaller;
import org.prometheuscode.xml.treemodel.CompositeTag;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.IXMLTag;
import org.prometheuscode.xml.treemodel.SimpleTag;
import org.prometheuscode.xml.treemodel.XMLQName;
import org.prometheuscode.xml.treemodel.XMLRoot;

import static org.prometheuscode.xml.XMLStaXAdaptiveMarshaller.*;

public class XMLStaXAdaptiveMarshallerTest {

	private static IXMLRoot treeModelToSave;

	private static String outputSaveTreeModelFileName = "files/output-save-tree-model.xml";

	private static String absoluteOutputSaveTreeModelPathFileName;



	@BeforeClass
	public static void setUpClass() {

		/*
		 * get system dependent path relative to the build/classes which are on
		 * class path
		 */
		String path = "/" + XMLStaXAdaptiveMarshallerTest.class.getName().replace(".", "/") + ".class";

		String absolutePath = XMLStaXAdaptiveMarshallerTest.class.getResource(path).getPath();
		absoluteOutputSaveTreeModelPathFileName = absolutePath.substring(0, absolutePath.lastIndexOf("/")) + "/" + outputSaveTreeModelFileName;

		treeModelToSave = new XMLRoot();
		Map<IXMLQName, String> sampleAttr = new HashMap<IXMLQName, String>();
		sampleAttr.put(new XMLQName("sample-attr", "", ""), "evil");
		Map<String, String> sampleNS = new HashMap<String, String>();
		sampleNS.put("insane", "http://evil.org");

		ICompositeTag compositeTag = new CompositeTag(new XMLQName("root", "", ""));
		compositeTag.setAttributes(sampleAttr);
		compositeTag.setNamespaces(sampleNS);

		treeModelToSave.setXMLRoot(compositeTag);

		compositeTag.addTag(new SimpleTag(new XMLQName("simpleChild", "", "")));
		compositeTag.addTag(new CompositeTag(new XMLQName("compositeChild", "", "")));
		compositeTag.addTag(new SimpleTag(new XMLQName("simpleChild", "", "")));
		compositeTag.addTag(new SimpleTag(new XMLQName("simpleChild", "", "")));

		compositeTag.addTag(new CompositeTag(new XMLQName("compositeChild", "", "")));
		compositeTag.addTag(new CompositeTag(new XMLQName("compositeChild", "", "")));
		compositeTag.addTag(new CompositeTag(new XMLQName("compositeChild", "", "")));
	}



	@Test
	@Ignore
	public void testSaveTreeModel() {

		OutputStream outputStreamToWrite = null;
		try {
			outputStreamToWrite = new FileOutputStream(absoluteOutputSaveTreeModelPathFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IXMLAdaptiveMarshaller testMarshaller = new XMLStaXAdaptiveMarshaller();
		testMarshaller.saveTreeModel(treeModelToSave, outputStreamToWrite);

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = null;
		InputStream in = null;
		try {
			in = new FileInputStream(absoluteOutputSaveTreeModelPathFileName);
			reader = factory.createXMLStreamReader(in);
			reader.nextTag(); /* root */
			assertEquals("Root tag name of written file should be named: root", "root", reader.getName().getLocalPart());
			assertEquals("Root tag attr of written file should be named: sample-attr", "sample-attr", reader.getAttributeLocalName(0));
			assertEquals("Root tag namespace URI of written file should be named: http://evil.org", "http://evil.org", reader.getNamespaceURI("insane"));

			reader.nextTag(); /* first simpleChild */
			reader.nextTag();
			assertEquals("First child should be named: simpleChild", "simpleChild", reader.getName().getLocalPart());
			/*
			 * nextTag() return start element or end element so to pass to next
			 * start tag element use nextTag twice
			 */
			reader.nextTag(); /* first composite child */
			reader.nextTag();
			assertEquals("First child should be named: compositeChild", "compositeChild", reader.getName().getLocalPart());

			reader.nextTag(); /* second simpleChild */
			reader.nextTag();
			reader.nextTag(); /* third simpleChild */
			reader.nextTag();

			reader.nextTag();
			reader.nextTag();

			reader.nextTag();
			reader.nextTag();

			reader.nextTag();
			reader.nextTag();

			reader.nextTag();
			assertEquals("This should be root end tag", "root", reader.getName().getLocalPart());

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}



	@Test
	@Ignore
	public void testGetTreeModelFromObject() {

		/*
		 * create Converters
		 */
		IMarshallerConverter<SampleClassForMarshaller> simpleConverter = new IMarshallerConverter<SampleClassForMarshaller>() {

			@Override
			public IXMLTag convert(SampleClassForMarshaller objectToConvert, Map<String, IXMLTag> convertedObjects) {

				DependentSampleClassForMarshaller obj = objectToConvert.getDependent();
				ICompositeTag compositeTag = new CompositeTag(new XMLQName("BaseClass", "", ""));
				IXMLTag dependent = convertedObjects.get("getDependent");
				compositeTag.addTag(dependent);

				return compositeTag;
			}
		};

		IMarshallerConverter<DependentSampleClassForMarshaller> dependentConverter = new IMarshallerConverter<DependentSampleClassForMarshaller>() {

			@Override
			public IXMLTag convert(DependentSampleClassForMarshaller objectToConvert, Map<String, IXMLTag> convertedObjects) {

				return new SimpleTag(new XMLQName("DependentTag", "", ""));
			}
		};

		/*
		 * set mappings
		 */
		IMapping<List> listMapping = new Mapping<List>();
		listMapping.setMappingType(MappingType.Collection);
		listMapping.setMappedMethodName("listMapping");

		IMapping<Map> outerMapMapping = new Mapping<Map>(); /* outer map */
		outerMapMapping.setMappingType(MappingType.Map);
		outerMapMapping.setMappedMethodName("mapMapping");

		listMapping.setContainerMapping(outerMapMapping);

		IMapping<Map> innerMapMapping = new Mapping<Map>(); /* inner map */
		innerMapMapping.setMappingType(MappingType.Map);
		innerMapMapping.setMappedMethodName("mapMapping");

		outerMapMapping.setContainerMapping(innerMapMapping);

		IMapping<SampleClassForMarshaller> rootMapping = new Mapping<SampleClassForMarshaller>(simpleConverter);
		rootMapping.setMappingType(MappingType.Object);

		innerMapMapping.setContainerMapping(rootMapping);

		IMapping<DependentSampleClassForMarshaller> dependent = new Mapping<DependentSampleClassForMarshaller>(dependentConverter);
		dependent.setMappingType(MappingType.Object);

		rootMapping.addMapping("getDependent", dependent);

		/*
		 * set up marshaller
		 */
		IXMLAdaptiveMarshaller testMarshaller = new XMLStaXAdaptiveMarshaller();
		testMarshaller.registerMapping(listMapping);

		List<Map<Integer, Map<Integer, SampleClassForMarshaller>>> listOfMaps = new ArrayList<Map<Integer, Map<Integer, SampleClassForMarshaller>>>();

		for (int j = 0; j < 5; j++) {
			Map<Integer, Map<Integer, SampleClassForMarshaller>> mapToConvert = new HashMap<Integer, Map<Integer, SampleClassForMarshaller>>();
			for (int i = 0; i < 10; i++) {
				Map<Integer, SampleClassForMarshaller> tmpMap = new HashMap<Integer, SampleClassForMarshaller>();
				tmpMap.put(i, new SampleClassForMarshaller());
				mapToConvert.put(i, tmpMap);
			}
			listOfMaps.add(mapToConvert);
		}

		/*
		 * test
		 */
		ICompositeTag tag = (ICompositeTag) testMarshaller.getTreeModelFromObject(listOfMaps);

		// System.out.print(tag);

		assertNotNull("Converted tag should not be null", tag);

		assertEquals("Tag name should be: list", LIST_TAG_NAME, tag.getName().getLocalPart());
		List<ICompositeTag> tagListOFMaps = tag.getAllCompositeTags();

		assertEquals("Tag should have 5 children", 5, tagListOFMaps.size());

		/*
		 * First element is a map of map
		 */
		ICompositeTag firstMap = tagListOFMaps.get(0);
		assertEquals("Tag name should be: map", MAP_TAG_NAME, firstMap.getName().getLocalPart());

		List<ICompositeTag> firstMapChildren = firstMap.getAllCompositeTags();
		assertEquals("Outer tag map should have 10 children", 10, firstMapChildren.size());

		/*
		 * Map of Map value. Get Outer map Entry.
		 */
		ICompositeTag firstMapEntry = firstMapChildren.get(0);
		assertEquals("Tag name should be: entry", MAP_TAG_ENTRY_NAME, firstMapEntry.getName().getLocalPart());

		IXMLQName searchName = new XMLQName();
		searchName.setLocalPart(MAP_TAG_KEY_NAME);
		ISimpleTag key = firstMapEntry.getSimpleTagByName(searchName);
		searchName.setLocalPart(MAP_TAG_VALUE_NAME);
		ICompositeTag value = firstMapEntry.getCompositeTagByName(searchName);

		assertNotNull("First map Entry should have SimpleTag with name: key", key);
		assertNotNull("First map Entry should have CompositeTag with name: value", value);

		assertEquals("First entry key value should be: 0", "0", key.getValue());

		/*
		 * Test value map.
		 * 
		 * Entry in map is: <entry> <key> <value> </entry>
		 */
		searchName.setLocalPart(MAP_TAG_NAME);
		ICompositeTag innerMapTag = value.getCompositeTagByName(searchName);
		assertNotNull("The Value of Entry should be a map: entry-->value-->map", innerMapTag);

		List<ICompositeTag> innerMapChildren = innerMapTag.getAllCompositeTags();

		assertEquals("Value map should have 1 entry", 1, innerMapChildren.size());

		ICompositeTag entry = innerMapChildren.get(0);
		assertEquals("Composite Tag children should be named: entry", MAP_TAG_ENTRY_NAME, entry.getName().getLocalPart());

		searchName.setLocalPart(MAP_TAG_KEY_NAME);
		key = entry.getSimpleTagByName(searchName);
		searchName.setLocalPart(MAP_TAG_VALUE_NAME);
		value = entry.getCompositeTagByName(searchName);

		assertNotNull("Entry should have SimpleTag with name: key", key);
		assertNotNull("Entry should have CompositeTag with name: value", value);

		assertEquals("First entry key value should be: 0", "0", key.getValue());

		List<ICompositeTag> valueTags = value.getAllCompositeTags();
		assertEquals("There should be only one tag for value tag", 1, valueTags.size());

		ICompositeTag firstValue = valueTags.get(0);
		assertEquals("First entry value should be tag named: BaseClass", "BaseClass", firstValue.getName().getLocalPart());

		searchName.setLocalPart("DependentTag");
		ISimpleTag dependentTag = firstValue.getSimpleTagByName(searchName);
		assertNotNull("There should be child of BaseClass named: DependentTag", dependentTag);
	}



	@Test
	// @Ignore
	public void testAutomaticRegistration() {

		String dependeeMethodName = "getMapDependee";

		Class<SampleClassForMarshaller> rootClass = SampleClassForMarshaller.class;

		IXMLAdaptiveMarshaller testMarshaller = new XMLStaXAdaptiveMarshaller();

		//long before = System.currentTimeMillis();
		IMapping<SampleClassForMarshaller> testMapping = (IMapping<SampleClassForMarshaller>) testMarshaller.createMapping(rootClass);
		//System.out.print("time for normal: " + (System.currentTimeMillis() - before) + "\n");

		assertNotNull("There should be mapping registered", testMapping);

		assertEquals("Main mapping type should be MappingType.Object", MappingType.Object, testMapping.getMappingType());

		IMapping<?> dependeeMapping = testMapping.getMapping(dependeeMethodName);

		assertNotNull("There should be mapping for *getMapDependee* method", dependeeMapping);

		assertEquals("Dependee mapping type should be MappingType.Map", MappingType.Map, dependeeMapping.getMappingType());

		IMapping<?> valueListMapping = dependeeMapping.getContainerMapping();

		assertEquals("Map Value mapping type should be MappingType.Collection", MappingType.Collection, valueListMapping.getMappingType());

		IMapping<DependentSampleClassForMarshaller> elementListMapValue = (IMapping<DependentSampleClassForMarshaller>) valueListMapping.getContainerMapping();

		assertEquals("Map Value Element List mapping type should be MappingType.Object", MappingType.Object, elementListMapValue.getMappingType());

		assertNotNull("Object mapping should have not null converter", elementListMapValue.getConverter());

		/* test cache */
		Class<DependentSampleClassForMarshaller> depClazz = DependentSampleClassForMarshaller.class;
		IMapping<DependentSampleClassForMarshaller> fastDependee = (IMapping<DependentSampleClassForMarshaller>) testMarshaller.createMapping(depClazz);

		assertNotSame("This object should be copy, not equal to previously retrieved", elementListMapValue, fastDependee);

		assertEquals("Dependee method retrived should be empty string", "", fastDependee.getMappedMethodName());

		assertEquals("Parent of this dependee should be null", null, fastDependee.getParentMapping());

		/*
		 * TODO remove
		 */
		Class<SampleClassForMarshaller> speedClazz = SampleClassForMarshaller.class;
		//before = System.currentTimeMillis();
		IMapping<SampleClassForMarshaller> mappingDependee = (IMapping<SampleClassForMarshaller>) testMarshaller.createMapping(speedClazz);
		//System.out.print("time for fast: " + (System.currentTimeMillis() - before) + "\n");

	}

}
