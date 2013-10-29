package org.activiti.designer.kickstart.process.diagram;

import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.util.ILocationInfo;
import org.eclipse.graphiti.util.LocationInfo;

public class KickstartProcessToolBehaviorProvider extends DefaultToolBehaviorProvider {

  public KickstartProcessToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);
  }
  
  @Override
  public ILocationInfo getLocationInfo(PictogramElement pe, ILocationInfo locationInfo) {
    if(pe instanceof ContainerShape) {
      KickstartProcessFeatureProvider provider = (KickstartProcessFeatureProvider) getFeatureProvider();
      Object bo = provider.getBusinessObjectForPictogramElement(pe);
      if(bo != null && provider.hasShapeController(bo)) {
        BusinessObjectShapeController controller = provider.getShapeController(bo);
        
        // Request what GA should be used to direct edit the shape
        GraphicsAlgorithm ga = controller.getGraphicsAlgorithmForDirectEdit((ContainerShape) pe);
        if(ga != null) {
          return new LocationInfo((Shape) pe, ga);
        }
      }
    }
    return super.getLocationInfo(pe, locationInfo);
  }
}
