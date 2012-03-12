package org.activiti.designer.util.editor;

import java.util.HashMap;
import java.util.Map;

import org.activiti.designer.bpmn2.model.BaseElement;
import org.eclipse.graphiti.features.impl.IIndependenceSolver;

/**
 * @author Nikolai Raitsev
 *
 */
public class POJOIndependenceSolver implements IIndependenceSolver {
	
	private Map<String, Object> objectMap = new HashMap<String, Object>();

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.IIndependenceSolver#getKeyForBusinessObject(java.lang.Object)
	 */
	@Override
	public String getKeyForBusinessObject(Object bo) {
		String result = null;
		if(bo != null && bo instanceof BaseElement ) {
			result = String.valueOf(bo.hashCode());
			
			if(!objectMap.containsKey(result))
				objectMap.put(result, bo);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.IIndependenceSolver#getBusinessObjectForKey(java.lang.String)
	 */
	@Override
	public Object getBusinessObjectForKey(String key) {
		return objectMap.get(key);
	}

	public Map<String, Object> getObjectMap() {
		return objectMap;
	}

	public void setObjectMap(Map<String, Object> objectMap) {
		this.objectMap = objectMap;
	}
	
	

}
