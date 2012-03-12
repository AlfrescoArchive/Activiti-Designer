/**
 * 
 */
package org.activiti.designer.util;

import org.activiti.designer.bpmn2.model.ComplexDataType;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.DataGrid;
import org.activiti.designer.bpmn2.model.DataGridField;
import org.activiti.designer.bpmn2.model.DataGridRow;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.InclusiveGateway;
import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.ManualTask;
import org.activiti.designer.bpmn2.model.ParallelGateway;
import org.activiti.designer.bpmn2.model.ScriptTask;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.bpmn2.model.StartEvent;
import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateInclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * 
 * TODO: copy standard lists such as properties and node-specific list contents
 * by inspecting original.get...
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public final class CloneUtil {

  private CloneUtil() {

  }

  public static FlowElement clone(final FlowElement element, final Diagram diagram) {

    if (element instanceof StartEvent) {
      return clone((StartEvent) element, diagram);
    } else if (element instanceof ServiceTask) {
      return clone((ServiceTask) element, diagram);
    } else if (element instanceof EndEvent) {
      return clone((EndEvent) element, diagram);
    } else if (element instanceof ExclusiveGateway) {
      return clone((ExclusiveGateway) element, diagram);
    } else if (element instanceof InclusiveGateway) {
      return clone((InclusiveGateway) element, diagram);
    } else if (element instanceof MailTask) {
      return clone((MailTask) element, diagram);
    } else if (element instanceof ManualTask) {
      return clone((ManualTask) element, diagram);
    } else if (element instanceof ParallelGateway) {
      return clone((ParallelGateway) element, diagram);
    } else if (element instanceof ScriptTask) {
      return clone((ScriptTask) element, diagram);
    } else if (element instanceof UserTask) {
      return clone((UserTask) element, diagram);
    }

    return null;

  }

  /**
   * Clones a {@link StartEvent}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final StartEvent clone(final StartEvent original, final Diagram diagram) {

    StartEvent result = new StartEvent();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateStartEventFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones an {@link EndEvent}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final EndEvent clone(final EndEvent original, final Diagram diagram) {

    EndEvent result = new EndEvent();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateEndEventFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones an {@link ExclusiveGateway}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ExclusiveGateway clone(final ExclusiveGateway original, final Diagram diagram) {

    ExclusiveGateway result = new ExclusiveGateway();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateExclusiveGatewayFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    
    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones an {@link InclusiveGateway}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final InclusiveGateway clone(final InclusiveGateway original, final Diagram diagram) {

    InclusiveGateway result = new InclusiveGateway();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateInclusiveGatewayFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    
    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link MailTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final MailTask clone(final MailTask original, final Diagram diagram) {

    MailTask result = new MailTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateMailTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setBcc(original.getBcc());
    result.setCc(original.getCc());
    result.setFrom(original.getFrom());
    result.setHtml(original.getHtml());
    result.setSubject(original.getSubject());
    result.setText(original.getText());
    result.setTo(original.getTo());

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link ManualTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ManualTask clone(final ManualTask original, final Diagram diagram) {

    ManualTask result = new ManualTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateMailTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link ParallelGateway}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ParallelGateway clone(final ParallelGateway original, final Diagram diagram) {

    ParallelGateway result = new ParallelGateway();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateParallelGatewayFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    
    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link ScriptTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ScriptTask clone(final ScriptTask original, final Diagram diagram) {

    ScriptTask result = new ScriptTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateScriptTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setScript(original.getScript());
    result.setScriptFormat(original.getScriptFormat());

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link UserTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final UserTask clone(final UserTask original, final Diagram diagram) {

    UserTask result = new UserTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateUserTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setAssignee(original.getAssignee());
    result.setFormKey(original.getFormKey());
    
    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link ServiceTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final ServiceTask clone(final ServiceTask original, final Diagram diagram) {

    ServiceTask result = new ServiceTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateServiceTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    result.setImplementation(original.getImplementation());

    for (CustomProperty property : original.getCustomProperties()) {
      final CustomProperty clone = clone(property, diagram);
      // Reset the id
      clone.setId(ExtensionUtil.wrapCustomPropertyId(result, ExtensionUtil.upWrapCustomPropertyId(clone.getId())));
      result.getCustomProperties().add(clone);
    }

    ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(result);

    return result;

  }

  /**
   * Clones a {@link CustomProperty}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final CustomProperty clone(final CustomProperty original, final Diagram diagram) {

    CustomProperty result = new CustomProperty();

    result.setId(original.getId());
    if (original.getComplexValue() != null) {
      result.setComplexValue(clone(original.getComplexValue()));
    }
    result.setName(original.getName());
    result.setSimpleValue(original.getSimpleValue());

    return result;

  }

  private static ComplexDataType clone(ComplexDataType complexValue) {
    if (complexValue instanceof DataGrid) {
      final DataGrid dataGrid = (DataGrid) complexValue;
      DataGrid result = new DataGrid();
      for (final DataGridRow dataGridRow : dataGrid.getRows()) {
        final DataGridRow rowClone = new DataGridRow();
        rowClone.setIndex(dataGridRow.getIndex());
        for (final DataGridField dataGridField : dataGridRow.getFields()) {
          final DataGridField fieldClone = new DataGridField();
          fieldClone.setName(dataGridField.getName());
          fieldClone.setValue(dataGridField.getValue());
          rowClone.getFields().add(fieldClone);
        }
        result.getRows().add(rowClone);
      }
      return result;
    }
    return null;
  }

}
