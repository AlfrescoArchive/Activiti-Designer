package org.activiti.designer.property;

import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public abstract class BaseActivitiPropertySection extends GFPropertySection implements ITabbedPropertyConstants {
  
  /**
   * @return the business-object associated with the given element, if any.
   */
  protected Object getBusinessObject(PictogramElement element) {
    if (element == null)
      return null;
    ActivitiBPMNFeatureProvider featureProvider = getFeatureProvider(element);
    if(featureProvider != null) {
      return featureProvider.getBusinessObjectForPictogramElement(element);
    }
    return null;
  }
  
  protected ActivitiBPMNFeatureProvider getFeatureProvider(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
    if (model != null) {
      return (ActivitiBPMNFeatureProvider) model.getFeatureProvider();
    }
    return null;
  }

  protected Diagram getContainer(EObject container) {
    if(container == null) {
      return null;
    }
    if (container instanceof Diagram) {
      return (Diagram) container;
    } else {
      return getContainer(container.eContainer());
    }
  }
  
  protected BpmnMemoryModel getModel(PictogramElement element) {
    if (element == null)
      return null;
    Diagram diagram = getContainer(element);
    BpmnMemoryModel model = (ModelHandler.getModel(EcoreUtil.getURI(diagram)));
    return model;
  }

	/**
	 * Returns the transactional editing domain of the current diagram editor.
	 *
	 * @return the transactional editing domain of the diagram editor.
	 */
	protected TransactionalEditingDomain getTransactionalEditingDomain() {
	  final IDiagramContainer diagramContainer = getDiagramContainer();
	  if (diagramContainer != null) {
	    return diagramContainer.getDiagramBehavior().getEditingDomain();
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
}
