package org.activiti.designer.util.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

public class ModelHandler {

	private static Map<URI, Bpmn2MemoryModel> modelMap = new HashMap<URI, Bpmn2MemoryModel>();
	
	public static void addModel(URI uri, Bpmn2MemoryModel model) {
		modelMap.put(uri, model);
	}
	
	public static Bpmn2MemoryModel getModel(URI uri) {
		return modelMap.get(uri);
	}
	
	public static void removeModel(URI uri) {
		modelMap.remove(uri);
	}
	
	public static List<String> getModelURIList() {
		List<String> modelURIList = new ArrayList<String>();
		for(Bpmn2MemoryModel model : modelMap.values()) {
			modelURIList.add(model.getModelFile().getRawLocationURI().toString());
		}
		return modelURIList;
	}
}
