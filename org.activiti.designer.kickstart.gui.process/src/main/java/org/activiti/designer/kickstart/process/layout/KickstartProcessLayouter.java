package org.activiti.designer.kickstart.process.layout;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.diagram.ProcessComponentLayout;
import org.activiti.designer.kickstart.process.features.DeleteStepFeature;
import org.activiti.designer.kickstart.process.features.REMOVEMEListStepDefinition;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.AbstractStepDefinitionContainer;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Main entry point for layouting components in containers.
 * 
 * @author Frederik Heremans
 * @author Tijs Rademakers
 */
public class KickstartProcessLayouter {

  private ProcessStepsVerticalLayout defaultLayout;
  protected StepDefinitionHorizontalLayout parallelLayout;
  protected StepDefinitionVerticalLayout serialLayout;

  public KickstartProcessLayouter() {
    defaultLayout = new ProcessStepsVerticalLayout();
    parallelLayout = new StepDefinitionHorizontalLayout();
    serialLayout = new StepDefinitionVerticalLayout();
  }

  /**
   * @param shapeToLayout
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container is found in the
   *         parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape, Shape shapeToLayout) {
    return this.getValidLayoutContainerShape(containerShape,
        ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram(containerShape))), shapeToLayout);
  }

  /**
   * @return the {@link ContainerShape} that can be used to add children to. An appropriate container is found in the
   *         parent hierarchy, if the given {@link ContainerShape} is not suited.
   */
  protected ContainerShape getValidLayoutContainerShape(ContainerShape containerShape,
      KickstartProcessMemoryModel model, Shape shapeToLayout) {
    if (containerShape instanceof Diagram) {
      return containerShape;
    } else {
      Object containerObject = model.getFeatureProvider().getBusinessObjectForPictogramElement(containerShape);
      if (containerObject instanceof ParallelStepsDefinition || containerObject instanceof REMOVEMEListStepDefinition) {
        // Shape represent a parallel step-definition, this has it's own layout
        return containerShape;
      } else {
        // Go one level up the hierarchy to find a container that is able to do layout
        return getValidLayoutContainerShape(containerShape.getContainer(), model, shapeToLayout);
      }
    }
  }

  /**
   * Move a shape to the target container, positioning it according to the layout applicable for that container.
   * 
   * @param targetContainer
   *          container to move shape to. In case the container is not a valid target, the first valid container in the
   *          parent hierarchy is selected.
   * @param sourceContainer
   *          container where the shape was in before
   * @param shape
   *          the shape to move
   * @param x
   *          x-coordinate relative to the provided targetContainer the shape is suggested to move to
   * @param y
   *          y-coordinate relative to the provided targetContainer the shape is suggested to move to
   * @param true, if the shape is just created before the move
   * @return the container the shape was moved to, can differ from the provided targetContainer.
   */
  public ContainerShape moveShape(KickstartProcessFeatureProvider provider, ContainerShape targetContainer,
      ContainerShape sourceContainer, Shape shape, int x, int y, boolean afterCreate) {
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer, shape);

    // Special handling for targets that need an additional wrapper
    Object targetDefinition = provider.getBusinessObjectForPictogramElement(actualTargetContainer);

    boolean wrapperCreated = false;
    
    // Special handling for parallel and choice-steps, the definitions are wrapped in a new type
    if (targetDefinition instanceof ParallelStepsDefinition) {
      Object businessObject = provider.getBusinessObjectForPictogramElement(shape);
      if (businessObject instanceof StepDefinition && !(businessObject instanceof AbstractStepDefinitionContainer<?>)) {
        actualTargetContainer.getChildren().remove(shape);

        REMOVEMEListStepDefinition wrapper = new REMOVEMEListStepDefinition();
        wrapper.addStep((StepDefinition) businessObject);
        AreaContext area = new AreaContext();
        area.setX(-1);
        area.setY(-1);
        AddContext addContext = new AddContext(area, wrapper);
        addContext.setTargetContainer(actualTargetContainer);
        PictogramElement createdElement = provider.getAddFeature(addContext).add(addContext);

        actualTargetContainer = (ContainerShape) createdElement;
        wrapperCreated = true;
        
        if(sourceContainer != actualTargetContainer) {
          sourceContainer.getChildren().remove(shape);
        }
      }
    }

    // only do the actual move in case NO new wrapper has been created
    if(!wrapperCreated) {
      if (actualTargetContainer != targetContainer) {
        // X and Y need to be recalculated using coordinates of container shapes
        // in the hierarchy between target and the actual target
        ContainerShape offsetContainer = targetContainer;
        while (offsetContainer != null && offsetContainer != actualTargetContainer) {
          x += offsetContainer.getGraphicsAlgorithm().getX();
          y += offsetContainer.getGraphicsAlgorithm().getY();
          offsetContainer = offsetContainer.getContainer();
        }
      }
      
      getLayoutForContainer(actualTargetContainer).moveShape(this, actualTargetContainer, sourceContainer, shape, x, y);
    }
    relayoutAll(actualTargetContainer, provider);
    return actualTargetContainer;
  }

  /**
   * Re-layout the target container. If the given container is not a container that can be layout, the first valid
   * container in the parent hierarchy is re-layout.
   * 
   * @param targetContainer
   *          container to re-layout
   */
  public void relayout(ContainerShape targetContainer, KickstartProcessFeatureProvider provider) {
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer, null);
    relayoutInternal(getLayoutForContainer(actualTargetContainer), actualTargetContainer, provider);
  }

  public void relayoutIfNeeded(ContainerShape targetContainer, KickstartProcessFeatureProvider provider) {
    ContainerShape actualTargetContainer = getValidLayoutContainerShape(targetContainer, null);

    if (actualTargetContainer == targetContainer) {
      relayoutInternal(getLayoutForContainer(actualTargetContainer), actualTargetContainer, provider);
    }
  }

  /**
   * @return the diagram the given container is used in.
   */
  public Diagram getDiagram(ContainerShape container) {
    if (container instanceof Diagram) {
      return (Diagram) container;
    }
    if (container.getContainer() != null) {
      return getDiagram(container.getContainer());
    }
    return null;
  }

  /**
   * Re-layouts the full diagram. All containers eligible for layouting in the hierarchy (starting from the diagram)
   * will be layout.
   * 
   * @param container
   *          any container in the diagram
   * @param provider
   * @param removeEmptyWrappers
   */
  public void relayoutAll(ContainerShape container, KickstartProcessFeatureProvider provider) {
    relayout(getDiagram(container), provider);
  }

  protected void relayoutInternal(ProcessComponentLayout layout, ContainerShape actualTargetContainer,
      KickstartProcessFeatureProvider provider) {

    layout.relayout(this, actualTargetContainer);

    // In case the source-container is a wrapper and it's empty, delete it
    Object businessObjectForSource = provider.getBusinessObjectForPictogramElement(actualTargetContainer);
    if (businessObjectForSource instanceof REMOVEMEListStepDefinition) {
      REMOVEMEListStepDefinition containerDefinition = (REMOVEMEListStepDefinition) businessObjectForSource;
      if (containerDefinition.getSteps().isEmpty()) {
        ContainerShape parent = actualTargetContainer.getContainer();
        // Delete the source container if the last child is moved from it
        DeleteContext context = new DeleteContext(actualTargetContainer);
        IDeleteFeature deleteFeature = provider.getDeleteFeature(context);
        if (deleteFeature instanceof DeleteStepFeature) {
          ((DeleteStepFeature) deleteFeature).setForceDelete(true);
        }
        deleteFeature.execute(context);
        
        // Force relayout of parent due to removal of this step
        relayoutIfNeeded(parent, provider);
      }
    }
  }

  protected ProcessComponentLayout getLayoutForContainer(ContainerShape container) {
    if (container instanceof Diagram) {
      return defaultLayout;
    } else {
      Object businessObject = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram(container)))
          .getFeatureProvider().getBusinessObjectForPictogramElement(container);
      if (businessObject instanceof ParallelStepsDefinition) {
        return parallelLayout;
      } else if (businessObject instanceof REMOVEMEListStepDefinition) {
        return serialLayout;
      }
    }
    return null;
  }
}
