package org.activiti.designer.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.designer.ActivitiImageProvider;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.extension.AbstractDiagramWorker;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.features.ChangeElementTypeFeature;
import org.activiti.designer.features.CreateBoundaryErrorFeature;
import org.activiti.designer.features.CreateBoundaryTimerFeature;
import org.activiti.designer.features.CreateBusinessRuleTaskFeature;
import org.activiti.designer.features.CreateCallActivityFeature;
import org.activiti.designer.features.CreateCustomServiceTaskFeature;
import org.activiti.designer.features.CreateEmbeddedSubProcessFeature;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateErrorEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateManualTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateReceiveTaskFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateTimerStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.features.DeleteSequenceFlowFeature;
import org.activiti.designer.features.ExpandCollapseSubProcessFeature;
import org.activiti.designer.features.SaveBpmnModelFeature;
import org.activiti.designer.integration.palette.PaletteEntry;
import org.activiti.designer.property.extension.CustomServiceTaskContext;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.features.AbstractCreateBPMNFeature;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
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
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.alfresco.designer.gui.features.CreateAlfrescoMailTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoScriptTaskFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoStartEventFeature;
import com.alfresco.designer.gui.features.CreateAlfrescoUserTaskFeature;

public class ActivitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

  private static final Map<Class< ? extends ICreateFeature>, PaletteEntry> toolMapping = new HashMap<Class< ? extends ICreateFeature>, PaletteEntry>();

  public ActivitiToolBehaviorProvider(IDiagramTypeProvider dtp) {
    super(dtp);

    // Setup tool mappings to palette entries
    toolMapping.put(CreateStartEventFeature.class, PaletteEntry.START_EVENT);
    toolMapping.put(CreateTimerStartEventFeature.class, PaletteEntry.TIMER_START_EVENT);
    toolMapping.put(CreateAlfrescoStartEventFeature.class, PaletteEntry.ALFRESCO_START_EVENT);
    toolMapping.put(CreateEndEventFeature.class, PaletteEntry.END_EVENT);
    toolMapping.put(CreateErrorEndEventFeature.class, PaletteEntry.ERROR_END_EVENT);
    toolMapping.put(CreateExclusiveGatewayFeature.class, PaletteEntry.EXCLUSIVE_GATEWAY);
    toolMapping.put(CreateMailTaskFeature.class, PaletteEntry.MAIL_TASK);
    toolMapping.put(CreateManualTaskFeature.class, PaletteEntry.MANUAL_TASK);
    toolMapping.put(CreateReceiveTaskFeature.class, PaletteEntry.RECEIVE_TASK);
    toolMapping.put(CreateParallelGatewayFeature.class, PaletteEntry.PARALLEL_GATEWAY);
    toolMapping.put(CreateScriptTaskFeature.class, PaletteEntry.SCRIPT_TASK);
    toolMapping.put(CreateServiceTaskFeature.class, PaletteEntry.SERVICE_TASK);
    toolMapping.put(CreateCallActivityFeature.class, PaletteEntry.CALL_ACTIVITY);
    toolMapping.put(CreateEmbeddedSubProcessFeature.class, PaletteEntry.SUBPROCESS);
    toolMapping.put(CreateUserTaskFeature.class, PaletteEntry.USER_TASK);
    toolMapping.put(CreateAlfrescoUserTaskFeature.class, PaletteEntry.ALFRESCO_USER_TASK);
    toolMapping.put(CreateBoundaryTimerFeature.class, PaletteEntry.BOUNDARY_TIMER);
    toolMapping.put(CreateBoundaryErrorFeature.class, PaletteEntry.ERROR_END_EVENT);
    toolMapping.put(CreateBusinessRuleTaskFeature.class, PaletteEntry.BUSINESSRULE_TASK);
    toolMapping.put(CreateAlfrescoScriptTaskFeature.class, PaletteEntry.ALFRESCO_SCRIPT_TASK);
    toolMapping.put(CreateAlfrescoMailTaskFeature.class, PaletteEntry.ALFRESCO_MAIL_TASK);
  }

  @Override
  public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
    /*ICustomFeature customFeature = new ExpandCollapseSubProcessFeature(getFeatureProvider());
    if (customFeature.canExecute(context)) {
      return customFeature;
    }*/
    return super.getDoubleClickFeature(context);
  }

  @Override
  public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
    IContextButtonPadData data = super.getContextButtonPad(context);
    PictogramElement pe = context.getPictogramElement();

    setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);

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
    button.setIconId(ActivitiImageProvider.IMG_EREFERENCE);
    ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
    for (ICreateConnectionFeature feature : features) {
      if (feature.isAvailable(ccc) && feature.canStartConnection(ccc))
        button.addDragAndDropFeature(feature);
    }

    Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
    if (bo instanceof StartEvent || bo instanceof Task || bo instanceof CallActivity || bo instanceof Gateway) {

    	CreateConnectionContext connectionContext = new CreateConnectionContext();
    	connectionContext.setSourcePictogramElement(pe);
      Anchor connectionAnchor = null;
      if (pe instanceof Anchor) {
      	connectionAnchor = (Anchor) pe;
      } else if (pe instanceof AnchorContainer) {
      	connectionAnchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
      }
      connectionContext.setSourceAnchor(connectionAnchor);
    	
    	CreateContext taskContext = new CreateContext();
    	taskContext.setTargetContainer((ContainerShape) pe.eContainer());
    	taskContext.putProperty("org.activiti.designer.connectionContext", connectionContext);
    	
    	CreateUserTaskFeature userTaskfeature = new CreateUserTaskFeature(getFeatureProvider());
      ContextButtonEntry newUserTaskButton = new ContextButtonEntry(userTaskfeature, taskContext);
      newUserTaskButton.setText("new user task"); //$NON-NLS-1$
      newUserTaskButton.setDescription("Create a new task"); //$NON-NLS-1$
      newUserTaskButton.setIconId(ActivitiImageProvider.IMG_USERTASK);
      data.getDomainSpecificContextButtons().add(newUserTaskButton);
      
      CreateExclusiveGatewayFeature exclusiveGatewayFeature = new CreateExclusiveGatewayFeature(getFeatureProvider());
      ContextButtonEntry newExclusiveGatewayButton = new ContextButtonEntry(exclusiveGatewayFeature, taskContext);
      newExclusiveGatewayButton.setText("new exclusive gateway"); //$NON-NLS-1$
      newExclusiveGatewayButton.setDescription("Create a new exclusive gateway"); //$NON-NLS-1$
      newExclusiveGatewayButton.setIconId(ActivitiImageProvider.IMG_GATEWAY_EXCLUSIVE);
      data.getDomainSpecificContextButtons().add(newExclusiveGatewayButton);
      
      CreateEndEventFeature endFeature = new CreateEndEventFeature(getFeatureProvider());
      ContextButtonEntry newEndButton = new ContextButtonEntry(endFeature, taskContext);
      newEndButton.setText("new end event"); //$NON-NLS-1$
      newEndButton.setDescription("Create a new end event"); //$NON-NLS-1$
      newEndButton.setIconId(ActivitiImageProvider.IMG_ENDEVENT_NONE);
      data.getDomainSpecificContextButtons().add(newEndButton);
      
      ContextButtonEntry otherElementButton = new ContextButtonEntry(null, null);
      otherElementButton.setText("new element"); //$NON-NLS-1$
      otherElementButton.setDescription("Create a new element"); //$NON-NLS-1$
      otherElementButton.setIconId(ActivitiImageProvider.NEW_ICON);
      data.getDomainSpecificContextButtons().add(otherElementButton);
      
      addContextButton(otherElementButton, new CreateServiceTaskFeature(getFeatureProvider()), taskContext, 
      		"Create service task", "Create a new service task", ActivitiImageProvider.IMG_SERVICETASK);
      addContextButton(otherElementButton, new CreateScriptTaskFeature(getFeatureProvider()), taskContext, 
      		"Create script task", "Create a new script task", ActivitiImageProvider.IMG_SCRIPTTASK);
      addContextButton(otherElementButton, new CreateUserTaskFeature(getFeatureProvider()), taskContext, 
      		"Create user task", "Create a new user task", ActivitiImageProvider.IMG_USERTASK);
      addContextButton(otherElementButton, new CreateMailTaskFeature(getFeatureProvider()), taskContext, 
      		"Create mail task", "Create a new mail task", ActivitiImageProvider.IMG_MAILTASK);
      addContextButton(otherElementButton, new CreateBusinessRuleTaskFeature(getFeatureProvider()), taskContext, 
      		"Create business rule task", "Create a new business rule task", ActivitiImageProvider.IMG_BUSINESSRULETASK);
      addContextButton(otherElementButton, new CreateManualTaskFeature(getFeatureProvider()), taskContext, 
      		"Create manual task", "Create a new manual task", ActivitiImageProvider.IMG_MANUALTASK);
      addContextButton(otherElementButton, new CreateReceiveTaskFeature(getFeatureProvider()), taskContext, 
      		"Create receive task", "Create a new receive task", ActivitiImageProvider.IMG_RECEIVETASK);
      addContextButton(otherElementButton, new CreateCallActivityFeature(getFeatureProvider()), taskContext, 
      		"Create call activity", "Create a new call activiti", ActivitiImageProvider.IMG_CALLACTIVITY);
      addContextButton(otherElementButton, new CreateExclusiveGatewayFeature(getFeatureProvider()), taskContext, 
      		"Create exclusive gateway", "Create a new exclusive gateway", ActivitiImageProvider.IMG_GATEWAY_EXCLUSIVE);
      addContextButton(otherElementButton, new CreateParallelGatewayFeature(getFeatureProvider()), taskContext, 
      		"Create parallel gateway", "Create a new parallel gateway", ActivitiImageProvider.IMG_GATEWAY_PARALLEL);
      addContextButton(otherElementButton, new CreateEndEventFeature(getFeatureProvider()), taskContext, 
      		"Create end event", "Create a new end event", ActivitiImageProvider.IMG_ENDEVENT_NONE);
      addContextButton(otherElementButton, new CreateErrorEndEventFeature(getFeatureProvider()), taskContext, 
      		"Create error end event", "Create a new error end event", ActivitiImageProvider.IMG_ENDEVENT_ERROR);
      addContextButton(otherElementButton, new CreateAlfrescoScriptTaskFeature(getFeatureProvider()), taskContext, 
      		"Create alfresco script task", "Create a new alfresco script task", ActivitiImageProvider.IMG_SERVICETASK);
      addContextButton(otherElementButton, new CreateAlfrescoUserTaskFeature(getFeatureProvider()), taskContext, 
      		"Create alfresco user task", "Create a new alfresco user task", ActivitiImageProvider.IMG_USERTASK);
      addContextButton(otherElementButton, new CreateAlfrescoMailTaskFeature(getFeatureProvider()), taskContext, 
      		"Create alfresco mail task", "Create a new alfresco mail task", ActivitiImageProvider.IMG_MAILTASK);
      
      ContextButtonEntry editElementButton = new ContextButtonEntry(null, null);
      editElementButton.setText("change element type"); //$NON-NLS-1$
      editElementButton.setDescription("Change the element type to another type"); //$NON-NLS-1$
      editElementButton.setIconId(ActivitiImageProvider.EDIT_ICON);
      data.getDomainSpecificContextButtons().add(editElementButton);
      
      CustomContext customContext = new CustomContext();
      customContext.putProperty("org.activiti.designer.changetype.pictogram", pe);
      
      if(bo instanceof Task) {
      	addTaskButtons(editElementButton, (Task) bo, customContext);
      
      } else if(bo instanceof Gateway) {
      	addGatewayButtons(editElementButton, (Gateway) bo, customContext);
      }
      
    }

    if (button.getDragAndDropFeatures().size() > 0) {
      data.getDomainSpecificContextButtons().add(button);
    }

    return data;
  }
  
  private void addGatewayButtons(ContextButtonEntry otherElementButton, Gateway notGateway, CustomContext customContext) {
  	if(notGateway == null || notGateway instanceof ExclusiveGateway == false) {
	  	addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "exclusivegateway"), customContext, 
	    		"Change to exclusive gateway", "Change to a exclusive gateway", ActivitiImageProvider.IMG_GATEWAY_EXCLUSIVE);
  	}
  	if(notGateway == null || notGateway instanceof ParallelGateway == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "parallelgateway"), customContext, 
	    		"Change to parallel gateway", "Change to a parallel gateway", ActivitiImageProvider.IMG_GATEWAY_PARALLEL);
  	}
  }
  
  private void addTaskButtons(ContextButtonEntry otherElementButton, Task notTask, CustomContext customContext) {
  	if(notTask == null || notTask instanceof ServiceTask == false) {
	  	addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "servicetask"), customContext, 
	    		"Change to service task", "Change to a service task", ActivitiImageProvider.IMG_SERVICETASK);
  	}
  	if(notTask == null || notTask instanceof ScriptTask == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "scripttask"), customContext, 
	    		"Change to script task", "Change to a script task", ActivitiImageProvider.IMG_SCRIPTTASK);
  	}
    if(notTask == null || notTask instanceof UserTask == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "usertask"), customContext, 
	    		"Change to user task", "Change to a user task", ActivitiImageProvider.IMG_USERTASK);
    }
    if(notTask == null || notTask instanceof MailTask == false) {;
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "mailtask"), customContext, 
	    		"Change to mail task", "Change to a mail task", ActivitiImageProvider.IMG_MAILTASK);
    }
    if(notTask == null || notTask instanceof BusinessRuleTask == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "businessruletask"), customContext, 
	    		"Change to business rule task", "Change to a business rule task", ActivitiImageProvider.IMG_BUSINESSRULETASK);
    }
    if(notTask == null || notTask instanceof ManualTask == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "manualtask"), customContext, 
	    		"Change to manual task", "Change to a manual task", ActivitiImageProvider.IMG_MANUALTASK);
    }
    if(notTask == null || notTask instanceof ReceiveTask == false) {
	    addContextButton(otherElementButton, new ChangeElementTypeFeature(getFeatureProvider(), "receivetask"), customContext, 
	    		"Change to receive task", "Change to a receive task", ActivitiImageProvider.IMG_RECEIVETASK);
    }
  }
  
  private void addContextButton(ContextButtonEntry button, ChangeElementTypeFeature feature, 
  		CustomContext customContext, String text, String description, String image) {
  	
  	ContextButtonEntry newButton = new ContextButtonEntry(feature, customContext);
  	newButton.setText(text); //$NON-NLS-1$
  	newButton.setDescription(description); //$NON-NLS-1$
  	newButton.setIconId(image);
  	button.getContextButtonMenuEntries().add(newButton);
  }
  
  private void addContextButton(ContextButtonEntry button, AbstractCreateBPMNFeature feature, 
  		CreateContext context, String text, String description, String image) {
  	
  	ContextButtonEntry newButton = new ContextButtonEntry(feature, context);
  	newButton.setText(text); //$NON-NLS-1$
  	newButton.setDescription(description); //$NON-NLS-1$
  	newButton.setIconId(image);
  	button.getContextButtonMenuEntries().add(newButton);
  }

  @Override
  public IContextMenuEntry[] getContextMenu(ICustomContext context) {
    List<IContextMenuEntry> menuList = new ArrayList<IContextMenuEntry>();
    
    if(context.getPictogramElements() != null) {
      for (PictogramElement pictogramElement : context.getPictogramElements()) {
        if(pictogramElement.getLink() == null) continue;
        EList<EObject> boList = pictogramElement.getLink().getBusinessObjects();
        if(boList != null) {
          for (EObject bObject : boList) {
            if(bObject instanceof SequenceFlow) {
              ContextMenuEntry subMenuDelete = new ContextMenuEntry(new DeleteSequenceFlowFeature(getFeatureProvider()), context);
              subMenuDelete.setText("Delete sequence flow"); //$NON-NLS-1$
              subMenuDelete.setSubmenu(false);
              menuList.add(subMenuDelete);
            }
          }
        }
      }
    }

    ContextMenuEntry subMenuExport = new ContextMenuEntry(new SaveBpmnModelFeature(getFeatureProvider()), context);
    subMenuExport.setText("Export to BPMN 2.0 XML"); //$NON-NLS-1$
    subMenuExport.setSubmenu(false);
    menuList.add(subMenuExport);

    ContextMenuEntry subMenuExpandOrCollapse = new ContextMenuEntry(new ExpandCollapseSubProcessFeature(getFeatureProvider()), context);
    subMenuExpandOrCollapse.setText("Expand/Collapse Subprocess"); //$NON-NLS-1$
    subMenuExpandOrCollapse.setSubmenu(false);

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
    IPaletteCompartmentEntry eventCompartmentEntry = new PaletteCompartmentEntry("Event", null);
    IPaletteCompartmentEntry taskCompartmentEntry = new PaletteCompartmentEntry("Task", null);
    IPaletteCompartmentEntry gatewayCompartmentEntry = new PaletteCompartmentEntry("Gateway", null);
    IPaletteCompartmentEntry boundaryEventCompartmentEntry = new PaletteCompartmentEntry("Boundary event", null);
    IPaletteCompartmentEntry alfrescoCompartmentEntry = new PaletteCompartmentEntry("Alfresco", ActivitiImageProvider.IMG_ALFRESCO_LOGO);

    for (int i = 0; i < superCompartments.length; i++) {

      final IPaletteCompartmentEntry entry = superCompartments[i];

      // Prune any disabled palette entries in the Objects compartment
      if ("Objects".equals(entry.getLabel())) {
        pruneDisabledPaletteEntries(project, entry);
      }
    }

    for (IPaletteCompartmentEntry iPaletteCompartmentEntry : superCompartments) {
      for (IToolEntry toolEntry : iPaletteCompartmentEntry.getToolEntries()) {
        if ("sequenceflow".equalsIgnoreCase(toolEntry.getLabel())) {
          connectionCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("startevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timerstartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("endevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorendevent".equalsIgnoreCase(toolEntry.getLabel())) {
          eventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("usertask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("scripttask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("servicetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("mailtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("manualtask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("receivetask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("businessruletask".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("timerboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("errorboundaryevent".equalsIgnoreCase(toolEntry.getLabel())) {
          boundaryEventCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("parallelgateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("exclusivegateway".equalsIgnoreCase(toolEntry.getLabel())) {
          gatewayCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("subprocess".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("callactivity".equalsIgnoreCase(toolEntry.getLabel())) {
          taskCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescousertask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescostartevent".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescoscripttask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        } else if ("alfrescomailtask".equalsIgnoreCase(toolEntry.getLabel())) {
          alfrescoCompartmentEntry.getToolEntries().add(toolEntry);
        }
      }
    }
    // Always add the connection compartment
    ret.add(connectionCompartmentEntry);

    if (eventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(eventCompartmentEntry);
    }
    if (taskCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(taskCompartmentEntry);
    }
    if (gatewayCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(gatewayCompartmentEntry);
    }
    if (boundaryEventCompartmentEntry.getToolEntries().size() > 0) {
      ret.add(boundaryEventCompartmentEntry);
    }
    if (PreferencesUtil.getBooleanPreference(Preferences.ALFRESCO_ENABLE) && 
            alfrescoCompartmentEntry.getToolEntries().size() > 0) {
      
      ret.add(alfrescoCompartmentEntry);
    }

    final Map<String, List<CustomServiceTaskContext>> tasksInDrawers = new HashMap<String, List<CustomServiceTaskContext>>();

    final List<CustomServiceTaskContext> customServiceTaskContexts = ExtensionUtil.getCustomServiceTaskContexts(project);

    final ImageRegistry reg = GraphitiUIPlugin.getDefault().getImageRegistry();
    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      if (reg.get(taskContext.getSmallImageKey()) == null) {
        reg.put(taskContext.getSmallImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getSmallIconStream()));
      }
      if (reg.get(taskContext.getLargeImageKey()) == null) {
        reg.put(taskContext.getLargeImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getLargeIconStream()));
      }
      if (reg.get(taskContext.getShapeImageKey()) == null) {
        reg.put(taskContext.getShapeImageKey(),
                new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), taskContext.getShapeIconStream()));
      }
    }

    for (final CustomServiceTaskContext taskContext : customServiceTaskContexts) {
      if (!tasksInDrawers.containsKey(taskContext.getServiceTask().contributeToPaletteDrawer())) {
        tasksInDrawers.put(taskContext.getServiceTask().contributeToPaletteDrawer(), new ArrayList<CustomServiceTaskContext>());
      }
      tasksInDrawers.get(taskContext.getServiceTask().contributeToPaletteDrawer()).add(taskContext);
    }

    for (final Entry<String, List<CustomServiceTaskContext>> drawer : tasksInDrawers.entrySet()) {

      // Sort the list
      Collections.sort(drawer.getValue());

      final IPaletteCompartmentEntry paletteCompartmentEntry = new PaletteCompartmentEntry(drawer.getKey(), null);

      for (final CustomServiceTaskContext currentDrawerItem : drawer.getValue()) {
        final CreateCustomServiceTaskFeature feature = new CreateCustomServiceTaskFeature(getFeatureProvider(), currentDrawerItem.getServiceTask().getName(),
                currentDrawerItem.getServiceTask().getDescription(), currentDrawerItem.getServiceTask().getClass().getCanonicalName());
        final IToolEntry entry = new ObjectCreationToolEntry(currentDrawerItem.getServiceTask().getName(), currentDrawerItem.getServiceTask().getDescription(),
                currentDrawerItem.getSmallImageKey(), null, feature);
        paletteCompartmentEntry.getToolEntries().add(entry);
      }
      ret.add(paletteCompartmentEntry);
    }

    return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
  }

  /**
   * Prunes the disabled palette entries from the
   * {@link IPaletteCompartmentEntry}.
   * 
   * @param entry
   *          the compartment being pruned
   */
  private void pruneDisabledPaletteEntries(final IProject project, final IPaletteCompartmentEntry entry) {

    final Set<PaletteEntry> disabledPaletteEntries = ExtensionUtil.getDisabledPaletteEntries(project);

    if (!disabledPaletteEntries.isEmpty()) {

      final Iterator<IToolEntry> entryIterator = entry.getToolEntries().iterator();

      while (entryIterator.hasNext()) {

        final IToolEntry toolEntry = entryIterator.next();

        if (disabledPaletteEntries.contains(PaletteEntry.ALL)) {
          entryIterator.remove();
        } else {
          if (toolEntry instanceof ObjectCreationToolEntry) {
            final ObjectCreationToolEntry objToolEntry = (ObjectCreationToolEntry) toolEntry;
            if (toolMapping.containsKey(objToolEntry.getCreateFeature().getClass())) {
              if (disabledPaletteEntries.contains(toolMapping.get(objToolEntry.getCreateFeature().getClass()))) {
                entryIterator.remove();
              }
            }
          }
        }
      }
    }
  }

  @Override
  public IDecorator[] getDecorators(PictogramElement pe) {
    IFeatureProvider featureProvider = getFeatureProvider();
    Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
    if (bo instanceof StartEvent) {
      StartEvent startEvent = (StartEvent) bo;
      if (startEvent.getOutgoing().size() != 1) {
        IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
        imageRenderingDecorator.setMessage("A start event should have exactly one outgoing sequence flow"); //$NON-NLS-1$
        return new IDecorator[] { imageRenderingDecorator };
      }
    /*} else if (bo instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) bo;

      if (!subProcessDiagramExists(subProcess)) {
        IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
        imageRenderingDecorator.setMessage("This subprocess does not have a diagram model yet");//$NON-NLS-1$
        return new IDecorator[] { imageRenderingDecorator };
      }*/
    } else if (bo instanceof ServiceTask && bo instanceof EObject && ExtensionUtil.isCustomServiceTask((EObject) bo)) {

      final Resource resource = pe.getLink().eResource();
      final IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(resource.getURI().toPlatformString(true));

      final List<IMarker> markers = getMarkers(res, (ServiceTask) bo);

      if (markers.size() > 0) {

        int maximumSeverity = 0;

        try {
          for (final IMarker marker : markers) {
            final Object severity = marker.getAttribute(IMarker.SEVERITY);
            if (severity != null && severity instanceof Integer) {
              maximumSeverity = Math.max(maximumSeverity, (Integer) severity);
            }
          }
        } catch (CoreException e) {
          e.printStackTrace();
        }

        String decoratorImagePath = null;

        switch (maximumSeverity) {
        case IMarker.SEVERITY_INFO:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK;
          break;
        case IMarker.SEVERITY_WARNING:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK;
          break;
        default:
          decoratorImagePath = IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK;
        }

        final ImageDecorator imageRenderingDecorator = new ImageDecorator(decoratorImagePath);
        imageRenderingDecorator.setMessage("There are validation markers for the properties of this node");//$NON-NLS-1$ 
        imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() / 2 - 10);
        imageRenderingDecorator.setY(4);

        return new IDecorator[] { imageRenderingDecorator };

      } else {
        return new IDecorator[] {};
      }
    }
    return super.getDecorators(pe);
  }
  protected List<IMarker> getMarkers(IResource resource, ServiceTask serviceTask) {

    final List<IMarker> result = new ArrayList<IMarker>();

    try {
      final IMarker[] markers = resource.findMarkers(ProcessValidator.MARKER_ID, true, IResource.DEPTH_INFINITE);
      for (final IMarker marker : markers) {
        Object attribute = marker.getAttribute(AbstractDiagramWorker.ATTRIBUTE_NODE_ID);
        if (attribute != null) {
          if (StringUtils.equals((String) attribute, serviceTask.getId())) {
            result.add(marker);
          }
        }

      }
    } catch (CoreException e) {
      e.printStackTrace();
    }

    return result;
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

        IFile file = project.getFile(String.format(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER + "%s.%s" + ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION,
                parentDiagramName, subProcess.getId()));

        Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(file, new ResourceSetImpl());

        return diagram != null;
      }
    }
    // Safe default assumption
    return true;
  }
}
