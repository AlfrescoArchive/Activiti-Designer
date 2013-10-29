package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.command.KickstartModelUpdater;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;

/**
 * @author Frederik Heremans
 */
public class DirectEditFormComponentFeature extends AbstractDirectEditingFeature implements ICustomUndoableFeature {

  protected KickstartModelUpdater<?> updater;
  
  public DirectEditFormComponentFeature(IFeatureProvider fp) {
    super(fp);
  }

  public int getEditingType() {
    return TYPE_TEXT;
  }

  @Override
  public boolean canDirectEdit(IDirectEditingContext context) {
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (businessObject instanceof FormPropertyDefinition || businessObject instanceof FormPropertyGroup);
  }

  public String getInitialValue(IDirectEditingContext context) {
    // Return the current name of the EClass
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    if(businessObject instanceof FormPropertyDefinition) {
      return ((FormPropertyDefinition) businessObject).getName();
    } else if(businessObject instanceof FormPropertyGroup) {
      return ((FormPropertyGroup) businessObject).getTitle();
    }
    return null;
  }

  public void setValue(String value, IDirectEditingContext context) {
    Object businessObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
    updater = ((KickstartFormFeatureProvider)getFeatureProvider()).getModelUpdaterFor(businessObject, context.getPictogramElement());
    if (updater != null) {
      // Set the new value in the updateable definition
      if(businessObject instanceof FormPropertyDefinition) {
        ((FormPropertyDefinition) updater.getUpdatableBusinessObject()).setName(value);
      } else if(businessObject instanceof FormPropertyGroup) {
        ((FormPropertyGroup) updater.getUpdatableBusinessObject()).setTitle(value);
      }
      updater.doUpdate();
    }
  }

  @Override
  public void undo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    if (updater != null) {
      updater.doUndo(false);
    }
  }

  @Override
  public boolean canRedo(IContext context) {
    return true;
  }

  @Override
  public void redo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    if (updater != null) {
      updater.doUpdate(false);
    }
  }
}
