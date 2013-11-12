package org.activiti.designer.kickstart.process.diagram;

import java.util.HashSet;
import java.util.Set;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.process.features.CreateChoiceStepFeature;
import org.activiti.designer.kickstart.process.features.CreateDelayStepFeature;
import org.activiti.designer.kickstart.process.features.CreateEmailStepFeature;
import org.activiti.designer.kickstart.process.features.CreateHumanStepFeature;
import org.activiti.designer.kickstart.process.features.CreateParallelStepFeature;
import org.activiti.designer.kickstart.process.features.CreateReviewStepFeature;
import org.activiti.designer.kickstart.process.features.CreateScriptStepFeature;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.util.ILocationInfo;
import org.eclipse.graphiti.util.LocationInfo;

public class KickstartProcessToolBehaviorProvider extends DefaultToolBehaviorProvider {

    protected static final Set<Class<?>> stepToolClasses = new HashSet<Class<?>>();
    protected static final Set<Class<?>> containerToolClasses = new HashSet<Class<?>>();
    
    static {
      containerToolClasses.add(CreateParallelStepFeature.class);
      containerToolClasses.add(CreateChoiceStepFeature.class);
      containerToolClasses.add(CreateReviewStepFeature.class);
      
      stepToolClasses.add(CreateHumanStepFeature.class);
      stepToolClasses.add(CreateEmailStepFeature.class);
      stepToolClasses.add(CreateDelayStepFeature.class);
      stepToolClasses.add(CreateScriptStepFeature.class);
    }
    
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
    
    @Override
    public IPaletteCompartmentEntry[] getPalette() {
      IPaletteCompartmentEntry stepsControls = new PaletteCompartmentEntry("Steps", null);
      IPaletteCompartmentEntry containerControls = new PaletteCompartmentEntry("Containers", null);
      
      IPaletteCompartmentEntry[] entries = new IPaletteCompartmentEntry[] {stepsControls, containerControls};
      IPaletteCompartmentEntry[] palette = super.getPalette();
      
      // Superclass returns 2 compartments: connections and objects. Reorganize the objects
      // entries into the new compartments
      
      IPaletteCompartmentEntry objects = palette[1];
      ObjectCreationToolEntry creationTool = null;
      for(IToolEntry tool : objects.getToolEntries()) {
        if(tool instanceof ObjectCreationToolEntry) {
          creationTool = (ObjectCreationToolEntry) tool;
          if(stepToolClasses.contains(creationTool.getCreateFeature().getClass())) {
            stepsControls.getToolEntries().add(tool);
          } else if(containerToolClasses.contains(creationTool.getCreateFeature().getClass())) {
            containerControls.getToolEntries().add(tool);
          } else {
            Logger.log(IStatus.WARNING, IStatus.OK, "Palette wil not contain entry for: " + creationTool.getLabel(), null);
          }
        }
      }
      return entries;
    }
}
