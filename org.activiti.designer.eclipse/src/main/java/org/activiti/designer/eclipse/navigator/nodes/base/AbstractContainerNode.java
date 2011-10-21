package org.activiti.designer.eclipse.navigator.nodes.base;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * The Class AbstractContainerNode.
 */
public abstract class AbstractContainerNode implements IContainerNode {

	protected AbstractContainerNode() {
		super();
	}

	@Override
	public String getText() {
		String ret = getContainerName();
		return ret;
	}

	/**
	 * Gets the container name.
	 * 
	 * @return the container name
	 */
	abstract protected String getContainerName();

	@Override
	public boolean hasChildren() {
			return true;
	}

	@Override
	public Image getImage() {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
