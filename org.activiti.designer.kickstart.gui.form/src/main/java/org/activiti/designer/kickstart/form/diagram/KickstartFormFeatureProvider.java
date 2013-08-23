package org.activiti.designer.kickstart.form.diagram;

import org.activiti.designer.kickstart.form.features.AddTextInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.FormPropertyMoveFeature;
import org.activiti.designer.kickstart.form.features.FormPropertyResizeFeature;
import org.activiti.designer.kickstart.form.features.UpdateFormPropertyFeature;
import org.activiti.designer.util.editor.KickstartFormIndependenceSolver;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class KickstartFormFeatureProvider extends DefaultFeatureProvider {

	public KickstartFormFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		// Use resolver based on the memory-model for the form
		setIndependenceSolver(new KickstartFormIndependenceSolver(dtp));
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
	  return new ICreateFeature[]{new CreateTextInputPropertyFeature(this)};
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
	  IAddFeature addFeature = null;
	  if(context.getNewObject() instanceof TextPropertyDefinition) {
	    addFeature = new AddTextInputPropertyFeature(this);
	  }
	  return addFeature;
	}
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
	  Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
	  if(bo instanceof FormPropertyDefinition) {
	    return new UpdateFormPropertyFeature(this);
	  } else {
	    return super.getUpdateFeature(context);
	  }
	}
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
	  return new FormPropertyResizeFeature(this);
	}
	
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
	  return new FormPropertyMoveFeature(this);
	}
}
