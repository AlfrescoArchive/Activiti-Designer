package org.activiti.designer.eclipse.navigator.nodes.base;

import org.eclipse.swt.graphics.Image;

/**
 * The Interface IContainerNode.
 */
public interface IContainerNode {

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	Object getParent();

	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
	Object[] getChildren();

	/**
	 * Checks for children.
	 * 
	 * @return true, if successful
	 */
	boolean hasChildren();

	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	String getText();

	Image getImage();
}