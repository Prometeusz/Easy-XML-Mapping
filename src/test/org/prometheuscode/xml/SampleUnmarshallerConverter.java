package org.prometheuscode.xml;

import java.util.List;
import java.util.Map;

import org.prometheuscode.xml.IUnmarshallerConverter;
import org.prometheuscode.xml.annotation.TagToJavaConverter;
import org.prometheuscode.xml.annotation.TagToJavaConverter.XMLQualifiedName;
import org.prometheuscode.xml.treemodel.ICompositeTag;
import org.prometheuscode.xml.treemodel.IXMLQName;

/*
 * 
 * Stub class for testing
 */
@TagToJavaConverter(@XMLQualifiedName(localPart = "zlo", namespaceURI = "http://zlo.org"))
public class SampleUnmarshallerConverter implements IUnmarshallerConverter {

	@Override
	public Object convert(ICompositeTag tag, Map<IXMLQName, List<Object>> convertedObjs) {
		// TODO Auto-generated method stub
		return null;
	}

}
