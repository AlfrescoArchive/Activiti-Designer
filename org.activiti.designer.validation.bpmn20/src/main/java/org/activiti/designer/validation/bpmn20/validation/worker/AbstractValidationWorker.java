package org.activiti.designer.validation.bpmn20.validation.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Abstract validator with helper methods and attributes
 * 
 * @author Jurosh
 *
 */
public abstract class AbstractValidationWorker implements ProcessValidationWorker {

	/**
	 * Active diagram
	 */
	protected Diagram diagram;
	
	/**
	 * Active set of nodes
	 */
	protected Map<String, List<Object>> nodes;
	
	/**
	 * Results
	 */
	protected Collection<ProcessValidationWorkerMarker> results = new ArrayList<ProcessValidationWorkerMarker>();
	
	/**
	 * Validate method, needs to be implemented.
	 * 
	 * Have access to `diagram`, `nodes` for verification
	 * and `results` for creating result object 
	 */
	public abstract void validate();
	
	@Override
	public Collection<ProcessValidationWorkerMarker> validate(Diagram diagram, Map<String, List<Object>> processNodes) {
		
		// set object attributes
		this.diagram = diagram;
		this.nodes = processNodes;
		
		// call validate function
		validate();
		
		// return result
		return results;
	}
	
	/**
	 * Get nodes defined by class, Pool.class, Activity.class, etc.
	 * @param clas class of node
	 * @return
	 */
	protected <T extends BaseElement> List<T> getNodes(Class<T> clas) {
		List<? extends Object> listing = nodes.get(clas.getCanonicalName());
		return listing == null ? new ArrayList<T>() : (List<T>) listing;
	}

}
