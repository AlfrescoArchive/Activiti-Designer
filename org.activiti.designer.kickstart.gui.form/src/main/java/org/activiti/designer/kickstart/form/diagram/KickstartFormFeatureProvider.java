package org.activiti.designer.kickstart.form.diagram;

import org.activiti.designer.kickstart.form.features.AddDatePropertyFeature;
import org.activiti.designer.kickstart.form.features.AddFormGroupFeature;
import org.activiti.designer.kickstart.form.features.AddTextPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateDatePropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateFormGroupFeature;
import org.activiti.designer.kickstart.form.features.CreateTextAreaPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.DeleteFormComponentFeature;
import org.activiti.designer.kickstart.form.features.DirectEditFormComponentFeature;
import org.activiti.designer.kickstart.form.features.FormPropertyResizeFeature;
import org.activiti.designer.kickstart.form.features.MoveFormComponentFeature;
import org.activiti.designer.kickstart.form.features.UpdateDateFormPropertyFeature;
import org.activiti.designer.kickstart.form.features.UpdateFormPropertyFeature;
import org.activiti.designer.util.editor.KickstartFormIndependenceSolver;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class KickstartFormFeatureProvider extends DefaultFeatureProvider {

    protected KickstartFormLayouter formLayouter;
    
	public KickstartFormFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		// Use resolver based on the memory-model for the form
		setIndependenceSolver(new KickstartFormIndependenceSolver(dtp));
		this.formLayouter = new KickstartFormLayouter();
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
	  return new ICreateFeature[]{ 
	      new CreateTextInputPropertyFeature(this), new CreateTextAreaPropertyFeature(this),
	      new CreateDatePropertyFeature(this), new CreateFormGroupFeature(this)
	  };
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
	  IAddFeature addFeature = null;
	  if(context.getNewObject() instanceof TextPropertyDefinition) {
	    addFeature = new AddTextPropertyFeature(this);
	  } else if(context.getNewObject() instanceof DatePropertyDefinition) {
	    addFeature = new AddDatePropertyFeature(this);
	  } else if(context.getNewObject() instanceof FormPropertyGroup) {
	    addFeature = new AddFormGroupFeature(this);
	  }
	  return addFeature;
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
	  Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
	  if(bo instanceof DatePropertyDefinition) {
	    return new UpdateDateFormPropertyFeature(this);
	  } else if(bo instanceof FormPropertyDefinition) {
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
	  return new MoveFormComponentFeature(this);
	}
	
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
	  return new DirectEditFormComponentFeature(this);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
	  return new DeleteFormComponentFeature(this);
	}
	
	@Override
	public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
	  // Business-object for the diagram is ALWAYS the root form-definition
	  if(pictogramElement instanceof Diagram) {
	    KickstartFormMemoryModel model = ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(pictogramElement));
	    if(model != null) {
	      return model.getFormDefinition();
	    }
	  }
	  return super.getBusinessObjectForPictogramElement(pictogramElement);
	}
	
	public KickstartFormLayouter getFormLayouter() {
      return formLayouter;
    }
}
