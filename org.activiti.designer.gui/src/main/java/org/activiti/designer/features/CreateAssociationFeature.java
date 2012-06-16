package org.activiti.designer.features;

import java.util.List;

import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.Artifact;
import org.activiti.designer.bpmn2.model.Association;
import org.activiti.designer.bpmn2.model.BaseElement;
import org.activiti.designer.bpmn2.model.Lane;
import org.activiti.designer.bpmn2.model.Process;
import org.activiti.designer.bpmn2.model.SubProcess;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class CreateAssociationFeature extends AbstractCreateBPMNConnectionFeature {

  public static final String FEATURE_ID_KEY = "association";
  
  public CreateAssociationFeature(final IFeatureProvider fp) {
    super(fp, "Association", "Associate information with artifacts and flow objects");
  }

  @Override
  public boolean canCreate(ICreateConnectionContext context) {
    final BaseElement sourceBo = getBaseElement(context.getSourceAnchor());
    final BaseElement targetBo = getBaseElement(context.getTargetAnchor());
    
    return sourceBo != targetBo;
  }

  @Override
  public Connection create(ICreateConnectionContext context) {
    final Anchor sourceAnchor = context.getSourceAnchor();
    final Anchor targetAnchor = context.getTargetAnchor();
    
    final BaseElement sourceBo = getBaseElement(sourceAnchor);  
    final BaseElement targetBo = getBaseElement(targetAnchor);
    
    if (sourceBo == null || targetBo == null) {
      return null;
    } else {
      // create new association
      final Association association = createAssociation(sourceBo, targetBo, context);
      
      final AddConnectionContext addContext = new AddConnectionContext(sourceAnchor
                                                                     , targetAnchor);
      addContext.setNewObject(association);
      
      return (Connection) getFeatureProvider().addIfPossible(addContext);
    }
  }
  
  private Association createAssociation(final BaseElement sourceBo, final BaseElement targetBo
                                      , final ICreateConnectionContext context) {
    
    final Association association = new Association();
    
    association.setId(getNextId());
    association.setSourceRef(sourceBo);
    association.setTargetRef(targetBo);
    
    final ContainerShape targetContainer = (ContainerShape) context.getSourcePictogramElement();
    final ContainerShape parentContainer = targetContainer.getContainer();
    
    List<Artifact> artifacts = null;
    
    if (parentContainer instanceof Diagram) {
      final Process process = ModelHandler.getModel(EcoreUtil.getURI(getDiagram())).getMainProcess();
      
      artifacts = process.getArtifacts();
    } else {
      final Object parentBo = getBusinessObjectForPictogramElement(parentContainer);
      
      if (parentBo instanceof SubProcess) {
        final SubProcess subProcess = (SubProcess) parentBo;
        
        artifacts = subProcess.getArtifacts();
      } else if (parentBo instanceof Lane) {
        final Lane lane = (Lane) parentBo;
        
        artifacts = lane.getParentProcess().getArtifacts();
      }
    }
    
    if (artifacts != null) {
      artifacts.add(association);
    }
    
    return association;
  }

  @Override
  public boolean canStartConnection(ICreateConnectionContext context) {
    return getBaseElement(context.getSourceAnchor()) != null;
  }

  @Override
  public String getCreateImageId() {
    return PluginImage.IMG_ASSOCIATION.getImageKey();
  }

  @Override
  protected String getFeatureIdKey() {
    return FEATURE_ID_KEY;
  }
  
  @Override
  protected Class< ? extends BaseElement> getFeatureClass() {
    return new Association().getClass();
  }

  private BaseElement getBaseElement(final Anchor anchor) {
    if (anchor != null) {
      final Object bo = getBusinessObjectForPictogramElement(anchor.getParent());
      
      if (bo instanceof BaseElement) {
        return (BaseElement) bo;
      }
    }
    
    return null;
  }
}
