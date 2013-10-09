package org.activiti.designer.kickstart.eclipse.navigator;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class CmisLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	
	public CmisLabelProvider() {
		System.out.println("Label provider wordt aangemaakt");
	}

	public String getText(Object element) {
		System.out.println("Getting name of " + element);
		if (element instanceof Folder) {
			return ((Folder) element).getName();
		} else if (element instanceof Document) {
			return ((Document) element).getName();
		}
		return null;
	}

	public String getDescription(Object element) {
		String text = getText(element);
		return "This is a description of " + text;
	}

	public Image getImage(Object element) {
		if (element instanceof Folder) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof Document) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}

}
