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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.prometheuscode.xml.IUnmarshallerConverter;
import org.prometheuscode.xml.IXMLAdaptiveUnmarshaller;
import org.prometheuscode.xml.XMLStaXAdaptiveUnmarshaller;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.ISimpleTag;
import org.prometheuscode.xml.treemodel.IXMLQName;
import org.prometheuscode.xml.treemodel.IXMLRoot;
import org.prometheuscode.xml.treemodel.XMLQName;

public class XMLStaXAdaptiveUnmarshallerTest {

	private IXMLAdaptiveUnmarshaller testAdaptiveUnmarshaller;

	/**
	 * File to test is put into following directory, which is relative to this
	 * test class directory.
	 */
	private static String fullPathToXMLTestFile;

	private static String testFilePath = "files/sample.xml";



	@BeforeClass
	public static void setUpBeforeClass() {
		/*
		 * get absolute path to testing XML file it is placed relatively to this
		 * class directory
		 * 
		 * /org/mysite/xmlutils/files/sample.xml
		 */
		fullPathToXMLTestFile = "/" + XMLStaXAdaptiveUnmarshallerTest.class.getPackage().getName().replace(".", "/") + "/" + testFilePath;
	}



	@Before
	public void setUp() {
		this.testAdaptiveUnmarshaller = new XMLStaXAdaptiveUnmarshaller();
	}



	@Test
	@Ignore
	public void testGetTreeModell() {

		InputStream testInputStream = XMLStaXAdaptiveUnmarshallerTest.class.getResourceAsStream(fullPathToXMLTestFile);
		// long before = System.currentTimeMillis();
		IXMLRoot doc = this.testAdaptiveUnmarshaller.getTreeModel(testInputStream);
		// System.out.print("Speed of cache: BUUUUUUUUUUUUUUUM: " +
		// (System.currentTimeMillis() - before) + "\n");

		/*
		 * test some tags properties
		 */
		ICompositeTag compositeTag_EmployeeList = doc.getXMLRoot();

		assertNotNull("Root is null!", compositeTag_EmployeeList);

		assertEquals("Encoding should be: UTF-8", "UTF-8", doc.getEncoding());
		assertEquals("Root tag name should be: employeesList ", "employeesList", compositeTag_EmployeeList.getName().getLocalPart());

		assertEquals("Parent property on Root Element should be: null ", null, compositeTag_EmployeeList.getParent());

		List<ICompositeTag> compositeTags_EmployeeList_Children = compositeTag_EmployeeList.getAllCompositeTags();

		assertEquals("Number of composite child elements of Root should be: 5", 5, compositeTags_EmployeeList_Children.size());

		ICompositeTag compositeTag_Employee_firstRootChild = compositeTags_EmployeeList_Children.get(0);
		ICompositeTag compositeTag_Employee_lastRootChild = compositeTags_EmployeeList_Children.get(4);

		assertEquals("First child of Root, name should be: employee ", "employee", compositeTag_Employee_firstRootChild.getName().getLocalPart());
		assertEquals("First child of Root, namespace should be: http://zlo.org/evil", "http://zlo.org/evil", compositeTag_Employee_firstRootChild.getNamespaces().get(""));

		/*
		 * test if tag has one position child
		 */
		IXMLQName testName = new XMLQName("position", "", "");

		ICompositeTag compositeTag_Employee_Position = compositeTag_Employee_lastRootChild.getCompositeTagByName(testName);

		assertEquals("The composite tag child of Employee tag, should have name: position", "position", compositeTag_Employee_Position.getName().getLocalPart());

		IXMLQName attributeNameForId = new XMLQName("type", "", "");

		testName.setLocalPart("id");
		ISimpleTag simpleTag_Employee_Position_Id = compositeTag_Employee_Position.getSimpleTagByName(testName);

		assertEquals("Id attribute called 'type' should have value: String", "String", simpleTag_Employee_Position_Id.getAttributes().get(attributeNameForId));
	}



	@Test
	// @Ignore
	public void testUmarshal() {

		/*
		 * define converters
		 */
		IUnmarshallerConverter employeeConverter = new IUnmarshallerConverter() {

			@Override
			public Object convert(ICompositeTag tag, Map<IXMLQName, List<Object>> convertedObjs) {

				IXMLQName name = new XMLQName("position", "", "");

				return "PUPPY!" + convertedObjs.get(name).get(0);
			}
		};

		IUnmarshallerConverter positionConverter = new IUnmarshallerConverter() {

			@Override
			public Object convert(ICompositeTag tag, Map<IXMLQName, List<Object>> convertedObjs) {

				return "PUPPY MASTER!";
			}
		};

		/*
		 * register converters
		 */
		Map<IXMLQName, IUnmarshallerConverter> converters = new HashMap<IXMLQName, IUnmarshallerConverter>();
		IXMLQName employeeTagName = new XMLQName("employee", "", "");
		IXMLQName positionTagName = new XMLQName("position", "", "");

		converters.put(employeeTagName, employeeConverter);
		converters.put(positionTagName, positionConverter);

		testAdaptiveUnmarshaller.registerMapping(converters);

		/*
		 * Test
		 */
		InputStream testInputStream = XMLStaXAdaptiveUnmarshallerTest.class.getResourceAsStream(fullPathToXMLTestFile);
		//long before = System.currentTimeMillis();
		Map<IXMLQName, List<Object>> convertedObjs = testAdaptiveUnmarshaller.unmarshal(testInputStream);
		//System.out.print("Speed of cache: BUUUUUUUUUUUUUUUM: " + (System.currentTimeMillis() - before) + "\n");

		assertEquals("Map should have only one entry", 1, convertedObjs.size());

		List<Object> employeeObjs = convertedObjs.get(employeeTagName);

		assertNotNull("Returned list of converted objects should not be null", employeeObjs);

		assertEquals("Number of converted employee objects should be: 4", 4, employeeObjs.size());

		assertEquals("Object Should be string: PUPPY!PUPPY MASTER!", "PUPPY!PUPPY MASTER!", employeeObjs.get(0));

		/*
		 * test for employee tag having namespace
		 */
		employeeTagName = new XMLQName("employee", "http://zlo.org/evil", "");
		positionTagName = new XMLQName("position", "http://zlo.org/evil", "");

		employeeConverter = new IUnmarshallerConverter() {

			@Override
			public Object convert(ICompositeTag tag, Map<IXMLQName, List<Object>> convertedObjs) {

				IXMLQName name = new XMLQName("position", "http://zlo.org/evil", "");

				return "PUPPY!" + convertedObjs.get(name).get(0);
			}
		};

		converters.clear();
		converters.put(employeeTagName, employeeConverter);
		converters.put(positionTagName, positionConverter);

		testAdaptiveUnmarshaller.registerMapping(converters);

		testInputStream = XMLStaXAdaptiveUnmarshallerTest.class.getResourceAsStream(fullPathToXMLTestFile);
		convertedObjs = testAdaptiveUnmarshaller.unmarshal(testInputStream);

		//System.out.print(convertedObjs);
		assertEquals("Map should have only one entry", 1, convertedObjs.size());

		employeeObjs = convertedObjs.get(employeeTagName);

		assertNotNull("Returned list of converted objects should not be null", employeeObjs);

		assertEquals("Number of converted employee objects should be: 1", 1, employeeObjs.size());

	}



	@Test
	@Ignore
	public void testAutomaticRegistration() {

		Set<String> testPackageList = new HashSet<String>();
		String packageName = "org.prometheuscode.xml";
		testPackageList.add(packageName);

		Map<IXMLQName, IUnmarshallerConverter> converters = this.testAdaptiveUnmarshaller.createMapping(testPackageList);

		assertNotNull("There should be registered converters", converters);
		assertEquals("Only one converter should be registered", 1, converters.size());

		/* test converter tag name registered for */
		String localPart = "zlo";
		String namespaceURI = "http://zlo.org";

		IXMLQName testName = new XMLQName(localPart, namespaceURI, "");

		IUnmarshallerConverter conv = converters.get(testName);
		assertNotNull("No converter for given XMLQName", conv);

	}

}
