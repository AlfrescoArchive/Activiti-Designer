package org.activiti.designer.kickstart.form.command;

import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Class capable of updating and cloning a Kickstart form model object.
 * 
 * Calls the neccasiary methods to notify the {@link KickstartFormMemoryModel} of the change
 * and optionallt notifies the Diagram UI.
 * 
 * @author Frederik Heremans
 */
public abstract class KickstartModelUpdater<T extends Object> {

  protected T businessObject;
  protected T newBusinessObject;
  protected T oldBusinessObject;
  protected IFeatureProvider featureProvider;
  protected PictogramElement pictogramElement;
  
  /**
   * @param businessObject the object to perform updates on
   * @param pictogramElement the pictogram element to be updated after update is performed. If null, no diagram
   * update is performed.
   * @param featureProvider
   */
  public KickstartModelUpdater(T businessObject, PictogramElement pictogramElement, IFeatureProvider featureProvider) {
    this.businessObject = businessObject;
    this.newBusinessObject = cloneBusinessObject(businessObject);
    this.oldBusinessObject = cloneBusinessObject(businessObject);
    this.pictogramElement = pictogramElement;
    this.featureProvider = featureProvider;
  }
  
  /**
   * @return The business-object that should be used to perform the required updates,
   * which will be applied to the actual business-object when this command is executed.
   */
  public T getUpdatableBusinessObject() {
    return newBusinessObject;
  }
  
  public void doUpdate() {
    performUpdates(newBusinessObject, businessObject);
    triggerBusinessObjectModelUpdated();
    requestPictorgramElementUpdate();
  }
  
  public void doUndo() {
    performUpdates(oldBusinessObject, businessObject);
    triggerBusinessObjectModelUpdated();
    requestPictorgramElementUpdate();
  }
  
  /**
   * @return a clone of the businessObject that doesn't impact the given object when
   * the cloned instance is updated.
   */
   protected abstract T cloneBusinessObject(T businessObject);
  
  /**
   * Perform the update of the targetObject with the values in the given 
   * valueObject.
   */
   protected abstract void performUpdates(T valueObject, T targetObject);
   
   protected void requestPictorgramElementUpdate() {
     if(pictogramElement != null) {
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
     if(diagram != null) {
       KickstartFormMemoryModel model = (ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(diagram)));
       if (model != null) {
        model.modelObjectUpdated(businessObject);
       }
     }
   }
}
