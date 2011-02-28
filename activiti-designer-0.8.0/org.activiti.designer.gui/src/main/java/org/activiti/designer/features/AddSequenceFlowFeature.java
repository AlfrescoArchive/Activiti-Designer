package org.activiti.designer.features;

import java.util.Collection;
import java.util.List;

import org.activiti.designer.util.StyleUtil;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

public class AddSequenceFlowFeature extends AbstractAddFeature {

	public AddSequenceFlowFeature(IFeatureProvider fp) {
		super(fp);
	}

	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		SequenceFlow addedSequenceFlow = (SequenceFlow) context.getNewObject();
		
		Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
		if(addConContext.getSourceAnchor() == null) {
      EList<Shape> shapeList = getDiagram().getChildren();
      for (Shape shape : shapeList) {
        FlowNode flowNode = (FlowNode) getBusinessObjectForPictogramElement(shape.getGraphicsAlgorithm().getPictogramElement());
        if(flowNode == null || flowNode.getId() == null || addedSequenceFlow.getSourceRef() == null ||
                addedSequenceFlow.getTargetRef() == null) continue;
        if(flowNode.getId().equals(addedSequenceFlow.getSourceRef().getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
              sourceAnchor = anchor;
              break;
            }
          }
        }
        
        if(flowNode.getId().equals(addedSequenceFlow.getTargetRef().getId())) {
          EList<Anchor> anchorList = ((ContainerShape) shape).getAnchors();
          for (Anchor anchor : anchorList) {
            if(anchor instanceof ChopboxAnchor) {
              targetAnchor = anchor;
              break;
            }
          }
        }
      }
		} else {
		  sourceAnchor = addConContext.getSourceAnchor();
		  targetAnchor = addConContext.getTargetAnchor();
		}
		
		if(sourceAnchor == null || targetAnchor == null) {
		  return null;
		}

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		// CONNECTION WITH POLYLINE
		FreeFormConnection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(sourceAnchor);
		connection.setEnd(targetAnchor);
		sourceAnchor.getOutgoingConnections().add(connection);
		targetAnchor.getIncomingConnections().add(connection);

		if (addedSequenceFlow.getSourceRef() instanceof Gateway) {
			GraphicsAlgorithm sourceGraphics = getPictogramElement(addedSequenceFlow.getSourceRef())
					.getGraphicsAlgorithm();
			GraphicsAlgorithm targetGraphics = getPictogramElement(addedSequenceFlow.getTargetRef())
					.getGraphicsAlgorithm();
			if (((sourceGraphics.getY() + 5) < targetGraphics.getY()
					|| (sourceGraphics.getY() - 5) > targetGraphics.getY())  && 
					(sourceGraphics.getX() + (sourceGraphics.getWidth() / 2)) < targetGraphics.getX()) {
				Point bendPoint = StylesFactory.eINSTANCE.createPoint();
				bendPoint.setX(sourceGraphics.getX() + 30);
				bendPoint.setY(targetGraphics.getY() + (targetGraphics.getHeight() / 2));
				connection.getBendpoints().add(bendPoint);
			}
		} else if (addedSequenceFlow.getTargetRef() instanceof Gateway) {
			GraphicsAlgorithm sourceGraphics = getPictogramElement(addedSequenceFlow.getSourceRef())
					.getGraphicsAlgorithm();
			GraphicsAlgorithm targetGraphics = getPictogramElement(addedSequenceFlow.getTargetRef())
					.getGraphicsAlgorithm();
			if (((sourceGraphics.getY() + 5) < targetGraphics.getY()
					|| (sourceGraphics.getY() - 5) > targetGraphics.getY()) && 
					(sourceGraphics.getX() + sourceGraphics.getWidth()) < targetGraphics.getX()) {
				Point bendPoint = StylesFactory.eINSTANCE.createPoint();
				bendPoint.setX(targetGraphics.getX() + 30);
				bendPoint.setY(sourceGraphics.getY() + (sourceGraphics.getHeight() / 2));
				connection.getBendpoints().add(bendPoint);
			}
		}

		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineStyle(LineStyle.SOLID);
		polyline.setForeground(Graphiti.getGaService().manageColor(getDiagram(), IColorConstant.BLACK));

		// create link and wire it
		link(connection, addedSequenceFlow);

		// add dynamic text decorator for the reference name
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		Text text = gaService.createDefaultText(textDecorator);
		text.setStyle(StyleUtil.getStyleForEClassText((getDiagram())));
		gaService.setLocation(text, 10, 0);

		// set reference name in the text decorator
		SequenceFlow sequenceFlow = (SequenceFlow) context.getNewObject();
		text.setValue(sequenceFlow.getName());

		// add static graphical decorators (composition and navigable)
		ConnectionDecorator cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd);

		return connection;
	}

	public boolean canAdd(IAddContext context) {
		// return true if given business object is an EReference
		// note, that the context must be an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof SequenceFlow) {
			return true;
		}
		return false;
	}

	private Polygon createArrow(GraphicsAlgorithmContainer gaContainer) {
		int xy[] = new int[] { -10, -5, 0, 0, -10, 5, -8, 0 };
		int beforeAfter[] = new int[] { 3, 3, 0, 0, 3, 3, 3, 3 };
		Polygon polyline = Graphiti.getGaCreateService().createPolygon(gaContainer, xy, beforeAfter);
		polyline.setStyle(StyleUtil.getStyleForPolygon(getDiagram()));
		return polyline;
	}

	private PictogramElement getPictogramElement(EObject businessObject) {
		Collection<PictogramLink> pictogramLinks = getDiagram().getPictogramLinks();
		for (PictogramLink pictogramLink : pictogramLinks) {
			List<EObject> businessObjects = pictogramLink.getBusinessObjects();
			for (EObject obj : businessObjects) {
				if (EcoreUtil.equals((EObject) businessObject, obj)) {
					PictogramElement pe = pictogramLink.getPictogramElement();
					if (pe != null) {
						return pe;
					}
				}
			}
		}
		return null;
	}
}
