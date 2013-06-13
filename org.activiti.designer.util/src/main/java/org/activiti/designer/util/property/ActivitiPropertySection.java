package org.activiti.designer.util.property;

import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;

public abstract class ActivitiPropertySection extends GFPropertySection {

	/**
	 * @return the {@link IDiagramContainer} diagram editor.
	 */
	@Override
  protected IDiagramContainer getDiagramEditor() {
		IWorkbenchPart part = getPart();
		if (part instanceof IContributedContentsView) {
		  IContributedContentsView contributedView = (IContributedContentsView) part
          .getAdapter(IContributedContentsView.class);
      if (contributedView != null) {
        part = contributedView.getContributingPart();
      }
		}

		if (part instanceof IDiagramContainer) {
			return (IDiagramContainer) part;
		}
		return null;
	}

	/**
	 * Returns the transactional editing domain of the current diagram editor.
	 *
	 * @return the transactional editing domain of the diagram editor.
	 */
	protected TransactionalEditingDomain getTransactionalEditingDomain() {
	  final IDiagramContainer de = getDiagramEditor();
	  if (de != null) {
	    return de.getDiagramBehavior().getEditingDomain();
	  }

	  return null;
	}

	/**
	 * Returns the default business object for the currently selected pictogram element in the
	 * diagram.
	 *
	 * @param clazz the class of the business object
	 * @return the business object or <code>null</code> if either no pictogram element is selected
	 *     or no business object is found.
	 */
	protected <T> T getDefaultBusinessObject(Class<T> clazz) {
	  final PictogramElement pe = getSelectedPictogramElement();

	  if (pe == null) {
	    return null;
	  }

	  return clazz.cast(getBusinessObject(pe));
	}

	protected Object getBusinessObject(PictogramElement element) {
		Diagram diagram = getContainer(element);
		Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));

  	if(model != null) {
  		return model.getFeatureProvider().getBusinessObjectForPictogramElement(element);
  	}
  	return null;
	}

	protected IFeatureProvider getFeatureProvider(PictogramElement element) {
    Diagram diagram = getContainer(element);
    Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));

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
      fieldExtension.setStringValue(null);
    } else {
      fieldExtension.setStringValue(fieldValue);
      fieldExtension.setExpression(null);
    }
  }

}
