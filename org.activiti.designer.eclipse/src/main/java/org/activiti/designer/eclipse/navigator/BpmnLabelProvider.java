package org.activiti.designer.eclipse.navigator;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.navigator.nodes.BpmnElementsNode;
import org.activiti.designer.eclipse.navigator.nodes.base.IContainerNode;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class BpmnLabelProvider extends LabelProvider {
	private static final String IMPL = "Impl";

	@Override
	public String getText(Object element) {
		String ret = "";
		if (element instanceof IContainerNode) {
			IContainerNode icn = (IContainerNode) element;
			ret = icn.getText();
		}
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			return file.getName();
		}
		if (element instanceof Diagram) {
			Diagram diagram = (Diagram) element;
			if (diagram != null) {
				ret = createTextForDiagram(diagram);
			}
		}
		if (element instanceof BaseElement) {
			String name = ((BaseElement) element).getId();
			if (name == null) {
				name = "name not available";
			}
			return name;
		}
		if (element instanceof EObject && ret.length() <= 0) {
			EObject eObject = (EObject) element;
			ret = ret + eObject.getClass().getSimpleName();
			if (ret.endsWith(IMPL)) {
				ret = ret.substring(0, ret.length() - (IMPL.length()));
			}
		}
		if (element instanceof GraphicsAlgorithm && ret.length() > 0) {
			ret = ret + "   -   ";
			ret = ret + super.getText(element);
		}
		return ret;
	}

	private String createTextForDiagram(Diagram diagram) {
		return "Diagram";
	}

	//	private String createTextForDiagramFile(Diagram diagram) {
	//		return diagram.getName() + " (" + diagram.getDiagramTypeId() + ")";
	//	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof BpmnElementsNode) {
			return getEClassesNodeImage();
		}
		if (element instanceof IContainerNode) {
			IContainerNode icn = (IContainerNode) element;
			return icn.getImage();
		}
		if (element instanceof IFile) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		if (element instanceof PictogramElement) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		if (element instanceof BaseElement) {
			return getEClassImage();
		}
		if (element instanceof EObject) {
			return getEObjectImage();
		}
		return super.getImage(element);
	}

	private Image getEClassImage() {
		ImageRegistry registry = ActivitiPlugin.getDefault().getImageRegistry();
		String key = "icons/full/obj16/EClass.gif"; //$NON-NLS-1$
		Image image = registry.get(key);
		if (image == null) {
			ImageDescriptor desc = ActivitiPlugin.imageDescriptorFromPlugin("org.eclipse.emf.ecore.edit", key);
			registry.put(key, desc);
			image = registry.get(key);
		}
		return image;
	}

	private Image getEObjectImage() {
		ImageRegistry registry = ActivitiPlugin.getDefault().getImageRegistry();
		String key = "icons/full/obj16/EObject.gif"; //$NON-NLS-1$
		Image image = registry.get(key);
		if (image == null) {
			ImageDescriptor desc = ActivitiPlugin.imageDescriptorFromPlugin("org.eclipse.emf.ecore.edit", key);
			registry.put(key, desc);
			image = registry.get(key);
		}
		return image;
	}

	private Image getEClassesNodeImage() {
		ImageRegistry registry = ActivitiPlugin.getDefault().getImageRegistry();
		String key = "icons/full/obj16/EPackage.gif"; //$NON-NLS-1$
		Image image = registry.get(key);
		if (image == null) {
			ImageDescriptor desc = ActivitiPlugin.imageDescriptorFromPlugin("org.eclipse.emf.ecore.edit", key);
			registry.put(key, desc);
			image = registry.get(key);
		}
		return image;
	}
}
