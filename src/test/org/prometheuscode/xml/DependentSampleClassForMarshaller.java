package org.prometheuscode.xml;

import org.prometheuscode.xml.annotation.Convertable;

@Convertable(converter = SampleMarshallerConverter.class)
public class DependentSampleClassForMarshaller {

	public String getHello() {
		return "HELLO!";
	}
}
