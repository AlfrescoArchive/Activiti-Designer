package org.activiti.designer.kickstart.process.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.Gateway;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.Task;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.kickstart.process.PluginImage;
import org.activiti.designer.kickstart.process.features.AbstractCreateBPMNFeature;
import org.activiti.designer.kickstart.process.features.ChangeElementTypeFeature;
import org.activiti.designer.kickstart.process.features.CreateHumanStepFeature;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

public class KickstartProcessToolBehaviorProvider extends DefaultToolBehaviorProvider {

  public KickstartProcessToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);
  }

  @Override
  public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
    /*
     * ICustomFeature customFeature = new
     * ExpandCollapseSubProcessFeature(getFeatureProvider()); if
     * (customFeature.canExecute(context)) { return customFeature; }
     */
    return super.getDoubleClickFeature(context);
  }

  @Override
  public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
    IContextButtonPadData data = super.getContextButtonPad(context);
    PictogramElement pe = context.getPictogramElement();

    setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);

    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);

    CreateConnectionContext connectionContext = new CreateConnectionContext();
    connectionContext.setSourcePictogramElement(pe);
    Anchor connectionAnchor = null;
    if (pe instanceof Anchor) {
      connectionAnchor = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {
      connectionAnchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    connectionContext.setSourceAnchor(connectionAnchor);

    if (pe.eContainer() instanceof ContainerShape == false) {
      return data;
    }

    CreateContext taskContext = new CreateContext();
    taskContext.setTargetContainer((ContainerShape) pe.eContainer());
    taskContext.putProperty("org.activiti.designer.connectionContext", connectionContext);

    if (bo instanceof StartEvent || bo instanceof Task || bo instanceof CallActivity || bo instanceof Gateway) {

      CreateHumanStepFeature userTaskfeature = new CreateHumanStepFeature(getFeatureProvider());
      ContextButtonEntry newUserTaskButton = new ContextButtonEntry(userTaskfeature, taskContext);
      newUserTaskButton.setText("new user task"); //$NON-NLS-1$
      newUserTaskButton.setDescription("Create a new task"); //$NON-NLS-1$
      newUserTaskButton.setIconId(PluginImage.IMG_USERTASK.getImageKey());
      data.getDomainSpecificContextButtons().add(newUserTaskButton);
    }

    CreateConnectionContext ccc = new CreateConnectionContext();
    ccc.setSourcePictogramElement(pe);
    Anchor anchor = null;
    if (pe instanceof Anchor) {
      anchor = (Anchor) pe;
    } else if (pe instanceof AnchorContainer) {
      anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
    }
    ccc.setSourceAnchor(anchor);

    ContextButtonEntry button = new ContextButtonEntry(null, context);
    button.setText("Create connection"); //$NON-NLS-1$
    button.setIconId(PluginImage.IMG_EREFERENCE.getImageKey());
    ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
    for (ICreateConnectionFeature feature : features) {
      if (feature.isAvailable(ccc) && feature.canStartConnection(ccc)) {
        button.addDragAndDropFeature(feature);
      }
    }

    if (button.getDragAndDropFeatures().size() > 0) {
      data.getDomainSpecificContextButtons().add(button);
    }

    if (bo instanceof StartEvent || bo instanceof Task || bo instanceof CallActivity || bo instanceof Gateway) {

      ContextButtonEntry otherElementButton = new ContextButtonEntry(null, null);
      otherElementButton.setText("new element"); //$NON-NLS-1$
      otherElementButton.setDescription("Create a new element"); //$NON-NLS-1$
      otherElementButton.setIconId(PluginImage.NEW_ICON.getImageKey());
      data.getDomainSpecificContextButtons().add(otherElementButton);

      addContextButton(otherElementButton, new CreateHumanStepFeature(getFeatureProvider()), taskContext, "Create service task", "Create a new service task",
              PluginImage.IMG_SERVICETASK);
    }

    ContextButtonEntry editElementButton = new ContextButtonEntry(null, null);
    editElementButton.setText("change element type"); //$NON-NLS-1$
    editElementButton.setDescription("Change the element type to another type"); //$NON-NLS-1$
    editElementButton.setIconId(PluginImage.EDIT_ICON.getImageKey());
    data.getDomainSpecificContextButtons().add(editElementButton);

    CustomContext customContext = new CustomContext();
    customContext.putProperty("org.activiti.designer.changetype.pictogram", pe);

    if (bo instanceof Task) {
      addTaskButtons(editElementButton, (Task) bo, customContext);
    }

    return data;
  }

  private void addTaskButtons(ContextButtonEntry otherElementButton, Task notTask, CustomContext customContext) {
    if (notTask == null || notTask instanceof UserTask == false) {
      addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "usertask"), customContext, "Change to user task",
              "Change to a user task", PluginImage.IMG_USERTASK);
    }
  }

  private void addContextButton(ContextButtonEntry button, ChangeElementTypeFeature feature, CustomContext customContext, String text, String description,
          PluginImage image) {

    ContextButtonEntry newButton = new ContextButtonEntry(feature, customContext);
    newButton.setText(text);
    newButton.setDescription(description);
    newButton.setIconId(image.getImageKey());
    button.getContextButtonMenuEntries().add(newButton);
  }

  private void addContextButton(ContextButtonEntry button, AbstractCreateBPMNFeature feature, CreateContext context, String text, String description,
          PluginImage image) {

    ContextButtonEntry newButton = new ContextButtonEntry(feature, context);
    newButton.setText(text);
    newButton.setDescription(description);
    newButton.setIconId(image.getImageKey());
    button.getContextButtonMenuEntries().add(newButton);
  }

  @Override
  public IContextMenuEntry[] getContextMenu(ICustomContext context) {
    List<IContextMenuEntry> menuList = new ArrayList<IContextMenuEntry>();

    if (context.getPictogramElements() != null) {
      for (PictogramElement pictogramElement : context.getPictogramElements()) {
        if (getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement) == null) {
          continue;
        }
        Object object = getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
      }
    }
    return menuList.toArray(new IContextMenuEntry[menuList.size()]);
  }

  @Override
  public IPaletteCompartmentEntry[] getPalette() {

    final IProject project = ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram());

    final List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

    // add compartments from super class if not disabled
    IPaletteCompartmentEntry[] superCompartments = super.getPalette();

    // create new compartments
    IPaletteCompartmentEntry connectionCompartmentEntry = new PaletteCompartmentEntry("Connection", null);
    IPaletteCompartmentEntry stepCompartmentEntry = new PaletteCompartmentEntry("Step", null);

    for (IPaletteCompartmentEntry iPaletteCompartmentEntry : superCompartments) {
      final List<IToolEntry> toolEntries = iPaletteCompartmentEntry.getToolEntries();

      for (IToolEntry toolEntry : toolEntries) {
        if ("sequenceflow".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("human step".equalsIgnoreCase(toolEntry.getLabel())) {
          stepCompartmentEntry.getToolEntries().add(toolEntry);
        }
      }
    }
    // Always add the connection compartment
    ret.add(connectionCompartmentEntry);

    if (stepCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(stepCompartmentEntry);
    }

    return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
  }

  private boolean subProcessDiagramExists(SubProcess subProcess) {
    Resource resource = getDiagramTypeProvider().getDiagram().eResource();

    URI uri = resource.getURI();
    URI uriTrimmed = uri.trimFragment();

    if (uriTrimmed.isPlatformResource()) {

      String platformString = uriTrimmed.toPlatformString(true);

      IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

      if (fileResource != null) {
        IProject project = fileResource.getProject();
        final String parentDiagramName = uriTrimmed.trimFileExtension().lastSegment();

        IFile file = project.getFile(String.format(ActivitiConstants.DIAGRAM_FOLDER + "%s.%s" + ".kickproc",
                parentDiagramName, subProcess.getId()));

        Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(file, new ResourceSetImpl());

        return diagram != null;
      }
    }
    // Safe default assumption
    return true;
  }
}
