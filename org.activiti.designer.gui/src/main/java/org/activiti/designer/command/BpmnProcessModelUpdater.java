package org.activiti.designer.command;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Class capable of updating and cloning a bpmn process model object.
 * 
 * Calls the necessary methods to notify the {@link BpmnMemoryModel} of the
 * change and optional notifies the Diagram UI.
 * 
 * @author Tijs Rademakers
 */
public abstract class BpmnProcessModelUpdater {

  protected Object businessObject;
  protected Object newBusinessObject;
  protected Object oldBusinessObject;
  protected IFeatureProvider featureProvider;
  protected PictogramElement pictogramElement;

  /**
   * @param businessObject
   *          the object to perform updates on
   * @param pictogramElement
   *          the pictogram element to be updated after update is performed. If
   *          null, no diagram update is performed.
   * @param featureProvider
   */
  public BpmnProcessModelUpdater(IFeatureProvider featureProvider) {
    this.featureProvider = featureProvider;
  }
  
  public BpmnProcessModelUpdater init(Object businessObject, PictogramElement pictogramElement) {
    BpmnProcessModelUpdater clone = createUpdater(featureProvider);
    clone.businessObject = businessObject;
    clone.newBusinessObject = cloneBusinessObject(businessObject);
    clone.oldBusinessObject = cloneBusinessObject(businessObject);
    clone.pictogramElement = pictogramElement;
    return clone;
  }

  /**
   * @return The business-object that should be used to perform the required
   *         updates, which will be applied to the actual business-object when
   *         this command is executed.
   */
  public Object getUpdatableBusinessObject() {
    return newBusinessObject;
  }

  public void doUpdate() {
    doUpdate(true);
  }

  public void doUndo() {
    doUndo(true);
  }

  public void doUpdate(boolean updatePictorgramElement) {
    performUpdates(newBusinessObject, businessObject);
    triggerBusinessObjectModelUpdated();
    if (updatePictorgramElement) {
      requestPictorgramElementUpdate();
    }
  }

  public void doUndo(boolean updatePictorgramElement) {
    performUpdates(oldBusinessObject, businessObject);
    triggerBusinessObjectModelUpdated();
    if (updatePictorgramElement) {
      requestPictorgramElementUpdate();
    }
  }

  /**
   * @return true, if this updater can update a shape for the given
   *         business-object.
   */
  public abstract boolean canControlShapeFor(Object businessObject);
  
  public abstract BpmnProcessModelUpdater createUpdater(IFeatureProvider featureProvider);

  /**
   * @return a clone of the businessObject that doesn't impact the given object
   *         when the cloned instance is updated.
   */
  protected abstract BaseElement cloneBusinessObject(Object businessObject);

  /**
   * Perform the update of the targetObject with the values in the given
   * valueObject.
   */
  protected abstract void performUpdates(Object valueObject, Object targetObject);

  protected void requestPictorgramElementUpdate() {
    if (pictogramElement != null) {
      // Request an update of the corresponding diagram element
      UpdateContext updateContext = new UpdateContext(pictogramElement);
      IUpdateFeature updateFeature = featureProvider.getUpdateFeature(updateContext);
      if (updateFeature != null) {
        updateFeature.update(updateContext);
      }
    }
  }

  protected void triggerBusinessObjectModelUpdated() {
    Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();
    if (diagram != null) {
      KickstartProcessMemoryModel model = (ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(diagram)));
      if (model != null) {
        model.modelObjectUpdated(businessObject);
      }
    }
  }
}
