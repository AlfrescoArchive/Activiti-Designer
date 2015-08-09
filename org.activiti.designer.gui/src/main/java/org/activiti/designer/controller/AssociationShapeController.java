package org.activiti.designer.controller;

import java.util.List;

import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.designer.diagram.ActivitiBPMNFeatureProvider;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link Task} objects.
 *  
 * @author Tijs Rademakers
 */
public class AssociationShapeController extends AbstractBusinessObjectShapeController {
  
  public AssociationShapeController(ActivitiBPMNFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    if (businessObject instanceof Association) {
      return true;
    } else {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public PictogramElement createShape(Object businessObject, ContainerShape layoutParent, int width, int height, IAddContext context) {
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final IGaService gaService = Graphiti.getGaService();
    
    Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
    
    IAddConnectionContext addConContext = (IAddConnectionContext) context;
    Association addedAssociation = (Association) context.getNewObject();
    
    Anchor sourceAnchor = addConContext.getSourceAnchor();
    Anchor targetAnchor = addConContext.getTargetAnchor(); 
    
    if (sourceAnchor == null) {
      final List<Shape> shapes = diagram.getChildren();
      
      for (final Shape shape : shapes) {
        final BaseElement baseElement = (BaseElement) getFeatureProvider().getBusinessObjectForPictogramElement(
            shape.getGraphicsAlgorithm().getPictogramElement());
        if (baseElement == null || baseElement.getId() == null 
                || addedAssociation.getSourceRef() == null || addedAssociation.getTargetRef() == null) {
          continue;
        }
        
        if (baseElement.getId().equals(addedAssociation.getSourceRef())) {
          final List<Anchor> anchors = ((ContainerShape) shape).getAnchors();
          for (final Anchor anchor : anchors) {
            if (anchor instanceof ChopboxAnchor) {
              sourceAnchor = anchor;
              
              break;
            }
          }
        }
        
        if (baseElement.getId().equals(addedAssociation.getTargetRef())) {
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
    
    // CONNECTION WITH POLYLINE
    final FreeFormConnection connection = peCreateService.createFreeFormConnection(diagram);
    
    connection.setStart(sourceAnchor);
    connection.setEnd(targetAnchor);
    
    sourceAnchor.getOutgoingConnections().add(connection);
    targetAnchor.getIncomingConnections().add(connection);
    
    BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    
    BaseElement sourceElement = model.getFlowElement(addedAssociation.getSourceRef());
    if (sourceElement == null) {
      sourceElement = model.getArtifact(addedAssociation.getSourceRef());
    }
    BaseElement targetElement = model.getFlowElement(addedAssociation.getTargetRef());
    if (targetElement == null) {
      targetElement = model.getArtifact(addedAssociation.getTargetRef());
    }

    final GraphicsAlgorithm sourceGraphics = getPictogramElement(sourceElement).getGraphicsAlgorithm();
    GraphicsAlgorithm targetGraphics = getPictogramElement(targetElement).getGraphicsAlgorithm();
    
    List<GraphicInfo> bendpointList = (List<GraphicInfo>) addConContext.getProperty("org.activiti.designer.bendpoints");
   
    if(bendpointList != null && bendpointList.size() >= 0) {
      for (GraphicInfo graphicInfo : bendpointList) {
        Point bendPoint = StylesFactory.eINSTANCE.createPoint();
        bendPoint.setX((int) graphicInfo.getX());
        bendPoint.setY((int) graphicInfo.getY());
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
          if (sourceElement instanceof SubProcess) {
            int middleSub = sourceGraphics.getY() + (sourceGraphics.getHeight() / 2);
            if ((middleSub + 20) < targetGraphics.getY() || (middleSub - 20) > targetGraphics.getY()) {
              subProcessWithBendPoint = true;
            }
          }
          
          if (sourceElement instanceof SubProcess == false || subProcessWithBendPoint == true) {
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
    
    Polyline polyline = gaService.createPolyline(connection);
    polyline.setLineStyle(LineStyle.DOT);
    polyline.setLineWidth(2);
    polyline.setForeground(Graphiti.getGaService().manageColor(diagram, IColorConstant.BLACK));

    return connection;
  }
  
  protected PictogramElement getPictogramElement(Object businessObject) {
    return getFeatureProvider().getPictogramElementForBusinessObject(businessObject);
  }
}
