package org.prometheuscode.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.prometheuscode.xml.annotation.Convertable;
import org.prometheuscode.xml.annotation.Dependee;

@Convertable(converter = SampleMarshallerConverter.class)
public class SampleClassForMarshaller {

	private DependentSampleClassForMarshaller dependent;



	public SampleClassForMarshaller() {
	}



	public void setDependent(DependentSampleClassForMarshaller dependent) {
		this.dependent = dependent;
	}



	public DependentSampleClassForMarshaller getDependent() {
		return new DependentSampleClassForMarshaller();
	}



	@Dependee
	public Map<String, List<DependentSampleClassForMarshaller>> getMapDependee() {
		Map<String, List<DependentSampleClassForMarshaller>> map = new HashMap<String, List<DependentSampleClassForMarshaller>>();

		return map;
	}
}
