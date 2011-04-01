package org.activiti.designer.eclipse.common;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class ActivitiEclipseImageProvider extends AbstractImageProvider {

	private static final String ROOT_FOLDER_FOR_IMG = "icons/"; //$NON-NLS-1$

	@Override
	protected void addAvailableImages() {
		// attributes
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_A_PUBLIC, ROOT_FOLDER_FOR_IMG + "modifier/attribute_public.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_A_PROTECTED, ROOT_FOLDER_FOR_IMG + "modifier/attribute_protected.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_A_PRIVATE, ROOT_FOLDER_FOR_IMG + "modifier/attribute_private.gif"); //$NON-NLS-1$

		// references
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_R_PROTECTED, ROOT_FOLDER_FOR_IMG + "modifier/reference_public.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_R_PUBLIC, ROOT_FOLDER_FOR_IMG + "modifier/reference_protected.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_R_PRIVATE, ROOT_FOLDER_FOR_IMG + "modifier/reference_private.gif"); //$NON-NLS-1$

		// operations
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_O_PRIVATE, ROOT_FOLDER_FOR_IMG + "modifier/operation_public.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_O_PROTECTED, ROOT_FOLDER_FOR_IMG + "modifier/operation_protected.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MODIFIER_O_PUBLIC, ROOT_FOLDER_FOR_IMG + "modifier/operation_private.gif"); //$NON-NLS-1$

		// mof
		addImageFilePath(ISampleImageConstants.IMG_CLASS, ROOT_FOLDER_FOR_IMG + "mof/class.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_NEW_CLASS, ROOT_FOLDER_FOR_IMG + "mof/newclass.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_PACKAGE, ROOT_FOLDER_FOR_IMG + "mof/package.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_MOF, ROOT_FOLDER_FOR_IMG + "mof/mof.gif"); //$NON-NLS-1$

		// tree
		addImageFilePath(ISampleImageConstants.IMG_TREE_DOWN, ROOT_FOLDER_FOR_IMG + "tree/tree_down.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_TREE_LEFT, ROOT_FOLDER_FOR_IMG + "tree/tree_left.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_TREE_RIGHT, ROOT_FOLDER_FOR_IMG + "tree/tree_right.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_TREE_UP, ROOT_FOLDER_FOR_IMG + "tree/tree_up.gif"); //$NON-NLS-1$

		// outline
		addImageFilePath(ISampleImageConstants.IMG_OUTLINE_TREE, ROOT_FOLDER_FOR_IMG + "outline/tree.gif"); //$NON-NLS-1$
		addImageFilePath(ISampleImageConstants.IMG_OUTLINE_THUMBNAIL, ROOT_FOLDER_FOR_IMG + "outline/thumbnail.gif"); //$NON-NLS-1$
	}
}
