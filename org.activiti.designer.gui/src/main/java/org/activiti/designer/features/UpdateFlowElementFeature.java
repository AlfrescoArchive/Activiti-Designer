package org.activiti.designer.features;

import java.util.Iterator;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.designer.PluginImage;
import org.activiti.designer.controller.TaskShapeController;
import org.activiti.designer.util.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class UpdateFlowElementFeature extends AbstractUpdateFeature {

	public UpdateFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canUpdate(IUpdateContext context) {
	  Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
    return (bo instanceof FlowElement);
	}

  public IReason updateNeeded(IUpdateContext context) {
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pictogramElement);
    FlowElement flowElement = (FlowElement) bo;
    boolean hasMiSequentialImage = false;
    boolean hasMiParallelImage = false;
    
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
					
				} else if (shape.getGraphicsAlgorithm() instanceof MultiText) {
				  MultiText text = (MultiText) shape.getGraphicsAlgorithm();
          pictogramName = text.getValue();
        
				} else if (shape.getGraphicsAlgorithm() instanceof Image) {
				  Image image = (Image) shape.getGraphicsAlgorithm();
				  if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey())) {
				    hasMiSequentialImage = true;
				  
				  } else if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey())) {
				    hasMiParallelImage = true;
				  }
				}
			}
		} else if (pictogramElement instanceof FreeFormConnection) {
		
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) pictogramElement).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof MultiText) {
          MultiText text = (MultiText) decorator.getGraphicsAlgorithm();
          pictogramName = text.getValue();
        }
      }
		}
		
		if (bo instanceof Activity) {
		  
      Activity activity = (Activity) bo;
      MultiInstanceLoopCharacteristics multiInstanceObject = null;
      
      if (activity.getLoopCharacteristics() != null) {
      
        if (StringUtils.isNotEmpty(activity.getLoopCharacteristics().getLoopCardinality()) ||
            StringUtils.isNotEmpty(activity.getLoopCharacteristics().getInputDataItem()) ||
            StringUtils.isNotEmpty(activity.getLoopCharacteristics().getCompletionCondition())) {
          
          multiInstanceObject = activity.getLoopCharacteristics();
        }
      }
      
      if (multiInstanceObject != null) {
        if (multiInstanceObject.isSequential() && hasMiParallelImage) {
          return Reason.createTrueReason();
        
        } else if (multiInstanceObject.isSequential() == false && hasMiSequentialImage) {
          return Reason.createTrueReason();
        }
      
      } else if (hasMiParallelImage || hasMiSequentialImage) {
        return Reason.createTrueReason();
      }
    }

		String businessName = flowElement.getName();

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || 
				(pictogramName != null && !pictogramName.equals(businessName)));
		
		if (updateNameNeeded) {
			return Reason.createTrueReason(); //$NON-NLS-1$
		} else {
			return Reason.createFalseReason();
		}
	}

	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof FlowElement) {
			FlowElement flowElement = (FlowElement) bo;
			businessName = flowElement.getName();
		}
		
		boolean updated = false;

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			Iterator<Shape> itShape = cs.getChildren().iterator();
			while (itShape.hasNext()) {
			  Shape shape = itShape.next();
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					updated = true;
					
				} else if (shape.getGraphicsAlgorithm() instanceof MultiText) {
					MultiText text = (MultiText) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					updated = true;
				
				} else if (shape.getGraphicsAlgorithm() instanceof Image) {
          Image image = (Image) shape.getGraphicsAlgorithm();
          if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey())) {
            itShape.remove();
            updated = true;
          
          } else if (image.getId().endsWith(PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey())) {
            itShape.remove();
            updated = true;
          }
        }
			}
			
			if (bo instanceof Activity) {
			  Activity activity = (Activity) bo;
			  MultiInstanceLoopCharacteristics multiInstanceObject = activity.getLoopCharacteristics();
	      if (multiInstanceObject != null) {
	      
	        if (StringUtils.isNotEmpty(multiInstanceObject.getLoopCardinality()) ||
	            StringUtils.isNotEmpty(multiInstanceObject.getInputDataItem()) ||
	            StringUtils.isNotEmpty(multiInstanceObject.getCompletionCondition())) {
	          
	          final IPeCreateService peCreateService = Graphiti.getPeCreateService();
	          final IGaService gaService = Graphiti.getGaService();
	          
	          int imageX = (cs.getGraphicsAlgorithm().getWidth() - TaskShapeController.MI_IMAGE_SIZE) / 2;
	          
	          if (multiInstanceObject.isSequential()) {
	            final Shape miShape = peCreateService.createShape(cs, false);
	            final Image miImage = gaService.createImage(miShape, PluginImage.IMG_MULTIINSTANCE_SEQUENTIAL.getImageKey());
	            gaService.setLocationAndSize(miImage, imageX, 
	                    (cs.getGraphicsAlgorithm().getHeight() - TaskShapeController.MI_IMAGE_SIZE) - 2, TaskShapeController.MI_IMAGE_SIZE, TaskShapeController.MI_IMAGE_SIZE);
	          
	          } else {
              final Shape miShape = peCreateService.createShape(cs, false);
              final Image miImage = gaService.createImage(miShape, PluginImage.IMG_MULTIINSTANCE_PARALLEL.getImageKey());
              gaService.setLocationAndSize(miImage, imageX, 
                      (cs.getGraphicsAlgorithm().getHeight() - TaskShapeController.MI_IMAGE_SIZE) - 2, TaskShapeController.MI_IMAGE_SIZE, TaskShapeController.MI_IMAGE_SIZE);
	        
	          }
	        }
	      }
			}
		
		} else if (pictogramElement instanceof FreeFormConnection) {
    
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) pictogramElement).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof MultiText) {
          MultiText text = (MultiText) decorator.getGraphicsAlgorithm();
          text.setValue(businessName);
          TextUtil.setTextSize(text);
          updated = true;
        }
      }
    }

		return updated;
	}
}
