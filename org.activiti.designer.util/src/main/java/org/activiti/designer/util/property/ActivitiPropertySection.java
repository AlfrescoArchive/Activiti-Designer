package org.activiti.designer.util.property;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;

public abstract class ActivitiPropertySection extends GFPropertySection {
	
	/**
	 * @return the {@link IDiagramEditor} diagram editor.
	 */
	protected IDiagramEditor getDiagramEditor() {
		IWorkbenchPart part = getPart();
		if (part instanceof IContributedContentsView) {
		  IContributedContentsView contributedView = (IContributedContentsView) part
          .getAdapter(IContributedContentsView.class);
      if (contributedView != null) {
        part = contributedView.getContributingPart();
      }
		}
		
		if (part instanceof IDiagramEditor) {
			return (IDiagramEditor) part;
		}
		return null;
	}
	
	protected Object getBusinessObject(PictogramElement element) {
		Diagram diagram = getContainer(element);
		Bpmn2MemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
  	
  	if(model != null) {
  		return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
  	}
  	return null;
	}
	
	protected IFeatureProvider getFeatureProvider(PictogramElement element) {
    Diagram diagram = getContainer(element);
    Bpmn2MemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
    
    if(model != null) {
      return model.getFeatureProvider();
    }
    return null;
  }
	
	private Diagram getContainer(EObject container) {
		if(container instanceof Diagram) {
			return (Diagram) container;
		} else {
			return getContainer(container.eContainer());
		}
	}
	
	protected String getFieldString(String fieldname, ServiceTask mailTask) {
    String result = null;
    for(FieldExtension extension : mailTask.getFieldExtensions()) {
      if (fieldname.equalsIgnoreCase(extension.getFieldName())) {
        if (StringUtils.isNotEmpty(extension.getExpression())) {
          result = extension.getExpression();
        } else {
          result = extension.getStringValue();
        }
      }
    }
    if (result == null) {
      result = "";
    }
    return result;
  }
  
  protected void setFieldString(String fieldname, String fieldValue, ServiceTask mailTask) {
    FieldExtension fieldExtension = null;
    for(FieldExtension extension : mailTask.getFieldExtensions()) {
      if (fieldname.equalsIgnoreCase(extension.getFieldName())) {
        fieldExtension = extension;
      }
    }
    if (fieldExtension == null) {
      fieldExtension = new FieldExtension();
      fieldExtension.setFieldName(fieldname);
      mailTask.getFieldExtensions().add(fieldExtension);
    }
    
    if (fieldValue != null && fieldValue.contains("${") || fieldValue.contains("#{")) {
      fieldExtension.setExpression(fieldValue);
    } else {
      fieldExtension.setStringValue(fieldValue);
    }
  }

}
