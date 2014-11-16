package org.activiti.designer.features;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.CompensateEventDefinition;
import org.activiti.designer.PluginImage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateBoundaryCompensateFeature extends AbstractCreateBPMNFeature {

	  public static final String FEATURE_ID_KEY = "boundarycompensate";

	  public CreateBoundaryCompensateFeature(IFeatureProvider fp) {
	    // set name and description of the creation feature
	    super(fp, "CompensateBoundaryEvent", "Add compensate boundary event");
	  }

	  public boolean canCreate(ICreateContext context) {
	    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
	    if (parentObject instanceof Activity == true) {
	      return true;
	    }
	    return false;
	  }

	  public Object[] create(ICreateContext context) {
	    BoundaryEvent boundaryEvent = new BoundaryEvent();
	    CompensateEventDefinition compensateEvent = new CompensateEventDefinition();
	    boundaryEvent.getEventDefinitions().add(compensateEvent);

	    Object parentObject = getBusinessObjectForPictogramElement(context.getTargetContainer());
	    ((Activity) parentObject).getBoundaryEvents().add(boundaryEvent);
	    boundaryEvent.setAttachedToRef((Activity) parentObject);
	    
	    addObjectToContainer(context, boundaryEvent, "Compensate");

	    // return newly created business object(s)
	    return new Object[] { boundaryEvent };
	  }

	  @Override
	  public String getCreateImageId() {
	    return PluginImage.IMG_EVENT_COMPENSATE.getImageKey();
	  }

	  @Override
	  protected String getFeatureIdKey() {
	    return FEATURE_ID_KEY;
	  }
}
