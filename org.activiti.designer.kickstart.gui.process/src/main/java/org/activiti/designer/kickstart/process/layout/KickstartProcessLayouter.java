/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.process.layout;

import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.designer.kickstart.process.diagram.ProcessComponentLayout;
import org.activiti.designer.kickstart.process.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.process.diagram.shape.WrappingChildShapeController;
import org.activiti.designer.kickstart.process.features.DeleteStepFeature;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.ListStepDefinition;
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
  protected StepDefinitionVerticalLayout serialLayoutWithLabel;
  protected StepDefinitionVerticalLayout serialLayoutReview;

  public KickstartProcessLayouter() {
    defaultLayout = new ProcessStepsVerticalLayout();
    parallelLayout = new StepDefinitionHorizontalLayout();
    serialLayout = new StepDefinitionVerticalLayout();
    
    serialLayoutWithLabel = new StepDefinitionVerticalLayout();
    serialLayoutWithLabel.setTopPadding(20);
    serialLayoutWithLabel.setSkipFirstShape(true);
    
    serialLayoutReview = new StepDefinitionVerticalLayout();
    serialLayoutReview.setTopPadding(50);
    serialLayoutReview.setBottomPadding(10);
    serialLayoutReview.setSkipFirstShape(true);
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
      if (containerObject instanceof ParallelStepsDefinition || containerObject instanceof ListStepDefinition<?> 
        || containerObject instanceof ChoiceStepsDefinition || containerObject instanceof ListConditionStepDefinition<?>
        || containerObject instanceof AlfrescoReviewStepDefinition) {
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

    Object targetDefinition = provider.getBusinessObjectForPictogramElement(actualTargetContainer);
    Object businessObject = provider.getBusinessObjectForPictogramElement(shape);

    // Special handling for targets that need an additional wrapper
    boolean wrapperCreated = false;
    if(targetDefinition instanceof StepDefinition) {
      BusinessObjectShapeController targetController = provider.getShapeController(targetDefinition);
      if(businessObject instanceof StepDefinition && targetController instanceof WrappingChildShapeController) {
        WrappingChildShapeController wrappingController = (WrappingChildShapeController) targetController;
        if(wrappingController.shouldWrapChild((StepDefinition) businessObject)) {
          actualTargetContainer.getChildren().remove(shape);
          StepDefinition wrapper = wrappingController.wrapChild((StepDefinition) businessObject);
          AreaContext area = new AreaContext();
          area.setX(-1);
          area.setY(-1);
          AddContext addContext = new AddContext(area, wrapper);
          addContext.setTargetContainer(actualTargetContainer);
          
          // Actually add the wrapped element instead of the moved element
          PictogramElement createdElement = provider.getAddFeature(addContext).add(addContext);
          
          actualTargetContainer = (ContainerShape) createdElement;
          wrapperCreated = true;
          
          if(sourceContainer != actualTargetContainer) {
            sourceContainer.getChildren().remove(shape);
          }
        }
      }
    }

    // Only do the actual move in case NO new wrapper has been created
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
    if(actualTargetContainer.getContainer() != null) {
      Object parentDefinition = provider.getBusinessObjectForPictogramElement(actualTargetContainer.getContainer());
      if(parentDefinition != null && parentDefinition instanceof StepDefinition) {
        BusinessObjectShapeController parentController = provider.getShapeController(parentDefinition);
        if(businessObjectForSource instanceof StepDefinition && parentController instanceof WrappingChildShapeController) {
          WrappingChildShapeController wrappingController = (WrappingChildShapeController) parentController;
          if(wrappingController.shouldDeleteWrapper((StepDefinition) businessObjectForSource)) {
            ContainerShape parent = actualTargetContainer.getContainer();
            
            // Wrapper shape should be removed
            DeleteContext context = new DeleteContext(actualTargetContainer);
            IDeleteFeature deleteFeature = provider.getDeleteFeature(context);
            if (deleteFeature instanceof DeleteStepFeature) {
              ((DeleteStepFeature) deleteFeature).setForceDelete(true);
            }
            deleteFeature.execute(context);
            
            // Force relayout of parent due to removal of this step
            relayoutAll(parent, provider);
          }
        }
      }
    }
  }

  protected ProcessComponentLayout getLayoutForContainer(ContainerShape container) {
    if (container instanceof Diagram) {
      return defaultLayout;
    } else {
      Object businessObject = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram(container)))
          .getFeatureProvider().getBusinessObjectForPictogramElement(container);
      if (businessObject instanceof ParallelStepsDefinition || businessObject instanceof ChoiceStepsDefinition) {
        return parallelLayout;
      } else if (businessObject instanceof ListStepDefinition<?>) {
        return serialLayout;
      } else if (businessObject instanceof ListConditionStepDefinition<?>) {
        return serialLayoutWithLabel;
      } else if(businessObject instanceof AlfrescoReviewStepDefinition) {
        return serialLayoutReview;
      }
    }
    return null;
  }
}
