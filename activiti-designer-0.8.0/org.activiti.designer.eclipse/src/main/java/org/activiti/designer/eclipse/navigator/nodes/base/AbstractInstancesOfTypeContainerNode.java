package org.activiti.designer.eclipse.navigator.nodes.base;

import org.eclipse.core.resources.IProject;

/**
 * The Class AbstractInstancesOfTypeContainerNode.
 */
public abstract class AbstractInstancesOfTypeContainerNode extends AbstractContainerNode {

	private Object parent;
	IProject project;

	/**
	 * The Constructor.
	 * 
	 * @param parent
	 *            the parent
	 */
	public AbstractInstancesOfTypeContainerNode(Object parent, IProject project) {
		super();
		this.parent = parent;
		this.project = project;
	}

	public Object getParent() {
		return parent;
	}

	@Override
	public boolean hasChildren() {
		return super.hasChildren(); // getChildren().length > 0;
	}

	public IProject getProject() {
		return project;
	}
}
