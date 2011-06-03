/**
 * 
 */
package org.activiti.designer.util;

import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ComplexDataType;
import org.eclipse.bpmn2.CustomProperty;
import org.eclipse.bpmn2.DataGrid;
import org.eclipse.bpmn2.DataGridField;
import org.eclipse.bpmn2.DataGridRow;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.MailTask;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
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

    StartEvent result = Bpmn2Factory.eINSTANCE.createStartEvent();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateStartEventFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    diagram.eResource().getContents().add(result);

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

    EndEvent result = Bpmn2Factory.eINSTANCE.createEndEvent();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateEndEventFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    diagram.eResource().getContents().add(result);

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

    ExclusiveGateway result = Bpmn2Factory.eINSTANCE.createExclusiveGateway();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateExclusiveGatewayFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setGatewayDirection(original.getGatewayDirection());

    diagram.eResource().getContents().add(result);

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

    MailTask result = Bpmn2Factory.eINSTANCE.createMailTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateMailTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setBcc(original.getBcc());
    result.setCc(original.getCc());
    result.setFrom(original.getFrom());
    result.setHtml(original.getHtml());
    result.setSubject(original.getSubject());
    result.setText(original.getText());
    result.setTo(original.getTo());

    diagram.eResource().getContents().add(result);

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

    ManualTask result = Bpmn2Factory.eINSTANCE.createManualTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateMailTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    diagram.eResource().getContents().add(result);

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

    ParallelGateway result = Bpmn2Factory.eINSTANCE.createParallelGateway();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateParallelGatewayFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setGatewayDirection(original.getGatewayDirection());

    diagram.eResource().getContents().add(result);

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

    ScriptTask result = Bpmn2Factory.eINSTANCE.createScriptTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateScriptTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setScript(original.getScript());
    result.setScriptFormat(original.getScriptFormat());

    diagram.eResource().getContents().add(result);

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

    UserTask result = Bpmn2Factory.eINSTANCE.createUserTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateUserTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());
    result.setAssignee(original.getAssignee());
    result.setFormKey(original.getFormKey());
    result.setImplementation(original.getImplementation());

    diagram.eResource().getContents().add(result);

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

    ServiceTask result = Bpmn2Factory.eINSTANCE.createServiceTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateServiceTaskFeature.FEATURE_ID_KEY, diagram));
    result.setName(original.getName());

    result.setImplementation(original.getImplementation());

    for (CustomProperty property : original.getCustomProperties()) {
      final CustomProperty clone = clone(property, diagram);
      // Reset the id
      clone.setId(ExtensionUtil.wrapCustomPropertyId(result, ExtensionUtil.upWrapCustomPropertyId(clone.getId())));
      diagram.eResource().getContents().add(clone);
      result.getCustomProperties().add(clone);
    }

    diagram.eResource().getContents().add(result);

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

    CustomProperty result = Bpmn2Factory.eINSTANCE.createCustomProperty();

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
      DataGrid result = Bpmn2Factory.eINSTANCE.createDataGrid();
      for (final DataGridRow dataGridRow : dataGrid.getRow()) {
        final DataGridRow rowClone = Bpmn2Factory.eINSTANCE.createDataGridRow();
        rowClone.setIndex(dataGridRow.getIndex());
        for (final DataGridField dataGridField : dataGridRow.getField()) {
          final DataGridField fieldClone = Bpmn2Factory.eINSTANCE.createDataGridField();
          fieldClone.setName(dataGridField.getName());
          fieldClone.setSimpleValue(dataGridField.getSimpleValue());
          rowClone.getField().add(fieldClone);
        }
        result.getRow().add(rowClone);
      }
      return result;
    }
    return null;
  }

}
