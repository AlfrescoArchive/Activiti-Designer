package org.activiti.designer.kickstart.form.diagram;

import java.util.HashSet;
import java.util.Set;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.kickstart.form.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.form.features.CreateBooleanPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateCommentPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateContentSelectPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateDatePropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateDueDatePropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateEmailNotificationPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateFormGroupFeature;
import org.activiti.designer.kickstart.form.features.CreateGroupSelectPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateListPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateNumberInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreatePackageItemsPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreatePeopleSelectPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreatePriorityPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateReferencePropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextAreaPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateWorkflowDescriptionPropertyFeature;
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

public class KickstartFormToolBehaviorProvider extends DefaultToolBehaviorProvider {

  protected static final Set<Class<?>> contolToolClasses = new HashSet<Class<?>>(); 
  protected static final Set<Class<?>> fixedToolClasses = new HashSet<Class<?>>(); 
  protected static final Set<Class<?>> containerToolClasses = new HashSet<Class<?>>();
  protected static final Set<Class<?>> referenceToolClasses = new HashSet<Class<?>>();
  
  static {
    contolToolClasses.add(CreateTextAreaPropertyFeature.class);
    contolToolClasses.add(CreateTextInputPropertyFeature.class);
    contolToolClasses.add(CreateNumberInputPropertyFeature.class);
    contolToolClasses.add(CreateDatePropertyFeature.class);
    contolToolClasses.add(CreateBooleanPropertyFeature.class);
    contolToolClasses.add(CreateListPropertyFeature.class);
    
    referenceToolClasses.add(CreatePeopleSelectPropertyFeature.class);
    referenceToolClasses.add(CreateGroupSelectPropertyFeature.class);
    referenceToolClasses.add(CreateContentSelectPropertyFeature.class);
    
    fixedToolClasses.add(CreateDueDatePropertyFeature.class);
    fixedToolClasses.add(CreatePriorityPropertyFeature.class);
    fixedToolClasses.add(CreatePackageItemsPropertyFeature.class);
    fixedToolClasses.add(CreateWorkflowDescriptionPropertyFeature.class);
    fixedToolClasses.add(CreateCommentPropertyFeature.class);
    fixedToolClasses.add(CreateEmailNotificationPropertyFeature.class);
    fixedToolClasses.add(CreateReferencePropertyFeature.class);
    
    containerToolClasses.add(CreateFormGroupFeature.class);
  }
  
  public KickstartFormToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);
  }
  
  @Override
  public ILocationInfo getLocationInfo(PictogramElement pe, ILocationInfo locationInfo) {
    if(pe instanceof ContainerShape) {
      KickstartFormFeatureProvider provider = (KickstartFormFeatureProvider) getFeatureProvider();
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
    IPaletteCompartmentEntry inputControls = new PaletteCompartmentEntry("Controls", null);
    IPaletteCompartmentEntry fixedControls = new PaletteCompartmentEntry("Fixed controls", null);
    IPaletteCompartmentEntry referenceControls = new PaletteCompartmentEntry("Reference controls", null);
    IPaletteCompartmentEntry containerControls = new PaletteCompartmentEntry("Containers", null);
    
    IPaletteCompartmentEntry[] entries = new IPaletteCompartmentEntry[] {inputControls, referenceControls, fixedControls, containerControls};
    IPaletteCompartmentEntry[] palette = super.getPalette();
    
    // Superclass returns 2 compartments: connections and objects. Reorganize the objects
    // entries into the new compartments
    
    IPaletteCompartmentEntry objects = palette[1];
    ObjectCreationToolEntry creationTool = null;
    for(IToolEntry tool : objects.getToolEntries()) {
      if(tool instanceof ObjectCreationToolEntry) {
        creationTool = (ObjectCreationToolEntry) tool;
        if(contolToolClasses.contains(creationTool.getCreateFeature().getClass())) {
          inputControls.getToolEntries().add(tool);
        } else if(fixedToolClasses.contains(creationTool.getCreateFeature().getClass())) {
          fixedControls.getToolEntries().add(tool);
        } else if(containerToolClasses.contains(creationTool.getCreateFeature().getClass())) {
          containerControls.getToolEntries().add(tool);
        } else if(referenceToolClasses.contains(creationTool.getCreateFeature().getClass())) {
          referenceControls.getToolEntries().add(tool);
        } else {
          Logger.log(IStatus.WARNING, IStatus.OK, "Palette wil not contain entry for: " + creationTool.getLabel(), null);
        }
      }
    }
    return entries;
  }
}
