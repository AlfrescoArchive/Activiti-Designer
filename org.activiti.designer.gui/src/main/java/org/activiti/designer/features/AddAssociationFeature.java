package org.activiti.designer.features;

import java.util.List;

import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class AddAssociationFeature extends AbstractAddFeature {
 
  public AddAssociationFeature(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(final IAddContext context) {
    return context instanceof IAddConnectionContext 
            && context.getNewObject() instanceof Association;
  }

  @Override
	public PictogramElement add(final IAddContext context) {	  
    final IAddConnectionContext addConnectionContext = (IAddConnectionContext) context;
    final Association association = (Association) context.getNewObject();
    
    Anchor sourceAnchor = addConnectionContext.getSourceAnchor();
    Anchor targetAnchor = addConnectionContext.getTargetAnchor(); 
    
    if (sourceAnchor == null) {
      final List<Shape> shapes = getDiagram().getChildren();
      
      for (final Shape shape : shapes) {
        final BaseElement baseElement 
          = (BaseElement) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm()
                                                                  .getPictogramElement());
        if (baseElement == null || baseElement.getId() == null 
                || association.getSourceRef() == null || association.getTargetRef() == null) {
          continue;
        }
        
        if (baseElement.getId().equals(association.getSourceRef())) {
          final List<Anchor> anchors = ((ContainerShape) shape).getAnchors();
          for (final Anchor anchor : anchors) {
            if (anchor instanceof ChopboxAnchor) {
              sourceAnchor = anchor;
              
              break;
            }
          }
        }
        
        if (baseElement.getId().equals(association.getTargetRef())) {
          final List<Anchor> anchors = ((ContainerShape) shape).getAnchors();
          for (final Anchor anchor : anchors) {
            if (anchor instanceof ChopboxAnchor) {
              targetAnchor = anchor;
              
              break;
            }
          }
        }
      }
    }
    
    if (sourceAnchor == null || targetAnchor == null) {
      return null;
    }
    
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    
    // CONNECTION WITH POLYLINE
    final FreeFormConnection connection = peCreateService.createFreeFormConnection(getDiagram());
    
    connection.setStart(sourceAnchor);
    connection.setEnd(targetAnchor);
    
    sourceAnchor.getOutgoingConnections().add(connection);
    targetAnchor.getIncomingConnections().add(connection);
    
    Bpmn2MemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagram()));
    
    BaseElement sourceElement = model.getFlowElement(association.getSourceRef());
    if (sourceElement == null) {
      sourceElement = model.getArtifact(association.getSourceRef());
    }
    BaseElement targetElement = model.getFlowElement(association.getTargetRef());
    if (targetElement == null) {
      targetElement = model.getArtifact(association.getTargetRef());
    }

    final GraphicsAlgorithm sourceGraphics = getPictogramElement(sourceElement).getGraphicsAlgorithm();
    GraphicsAlgorithm targetGraphics = getPictogramElement(targetElement).getGraphicsAlgorithm();
    
    @SuppressWarnings("unchecked")
    List<GraphicInfo> bendpointList = (List<GraphicInfo>) addConnectionContext.getProperty("org.activiti.designer.bendpoints");
   
    if(bendpointList != null && bendpointList.size() >= 0) {
      for (GraphicInfo graphicInfo : bendpointList) {
        Point bendPoint = StylesFactory.eINSTANCE.createPoint();
        bendPoint.setX((int)graphicInfo.x);
        bendPoint.setY((int)graphicInfo.y);
        connection.getBendpoints().add(bendPoint);
      }
      
    } else {
      
      Shape sourceShape = (Shape) getPictogramElement(sourceElement);
      ILocation sourceShapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(sourceShape);
      int sourceX = sourceShapeLocation.getX();
      int sourceY = sourceShapeLocation.getY();
      
      Shape targetShape = (Shape) getPictogramElement(targetElement);
      ILocation targetShapeLocation = Graphiti.getLayoutService().getLocationRelativeToDiagram(targetShape);
      int targetX = targetShapeLocation.getX();
      int targetY = targetShapeLocation.getY();
      
      if (sourceElement instanceof Gateway && targetElement instanceof Gateway == false) {
        if (((sourceGraphics.getY() + 10) < targetGraphics.getY()
            || (sourceGraphics.getY() - 10) > targetGraphics.getY())  && 
            (sourceGraphics.getX() + (sourceGraphics.getWidth() / 2)) < targetGraphics.getX()) {
          
          boolean subProcessWithBendPoint = false;
          if(targetElement instanceof SubProcess) {
            int middleSub = targetGraphics.getY() + (targetGraphics.getHeight() / 2);
            if((sourceGraphics.getY() + 20) < middleSub || (sourceGraphics.getY() - 20) > middleSub) {
              subProcessWithBendPoint = true;
            }
          }
          
          if(targetElement instanceof SubProcess == false || subProcessWithBendPoint == true) {
            Point bendPoint = StylesFactory.eINSTANCE.createPoint();
            bendPoint.setX(sourceX + 20);
            bendPoint.setY(targetY + (targetGraphics.getHeight() / 2));
            connection.getBendpoints().add(bendPoint);
          }
        }
      } else if (targetElement instanceof Gateway) {
        if (((sourceGraphics.getY() + 10) < targetGraphics.getY()
            || (sourceGraphics.getY() - 10) > targetGraphics.getY()) && 
            (sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX()) {
          
          boolean subProcessWithBendPoint = false;
          if(sourceElement instanceof SubProcess) {
            int middleSub = sourceGraphics.getY() + (sourceGraphics.getHeight() / 2);
            if((middleSub + 20) < targetGraphics.getY() || (middleSub - 20) > targetGraphics.getY()) {
              subProcessWithBendPoint = true;
            }
          }
          
          if(sourceElement instanceof SubProcess == false || subProcessWithBendPoint == true) {
            Point bendPoint = StylesFactory.eINSTANCE.createPoint();
            bendPoint.setX(targetX + 20);
            bendPoint.setY(sourceY + (sourceGraphics.getHeight() / 2));
            connection.getBendpoints().add(bendPoint);
          }
        }
      } else if (targetElement instanceof EndEvent) {
        int middleSource = sourceGraphics.getY() + (sourceGraphics.getHeight() / 2);
        int middleTarget = targetGraphics.getY() + (targetGraphics.getHeight() / 2);
        if (((middleSource + 10) < middleTarget && 
            (sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX()) ||
            
            ((middleSource - 10) > middleTarget && 
            (sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX())) {
          
          Point bendPoint = StylesFactory.eINSTANCE.createPoint();
          bendPoint.setX(targetX + (targetGraphics.getWidth() / 2));
          bendPoint.setY(sourceY + (sourceGraphics.getHeight() / 2));
          connection.getBendpoints().add(bendPoint);
        }
      }
    }
    
    IGaService gaService = Graphiti.getGaService();
    Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineStyle(LineStyle.DOT);
    polyline.setLineWidth(2);
    polyline.setForeground(Graphiti.getGaService().manageColor(getDiagram(), IColorConstant.BLACK));

    // create link and wire it
    link(connection, association);

    return connection;
	}

  private PictogramElement getPictogramElement(final Object businessObject) {
    return getFeatureProvider().getPictogramElementForBusinessObject(businessObject);
  }
}
