/**
 * 
 */
package org.activiti.designer.util;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.ComplexDataType;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.DataGrid;
import org.activiti.bpmn.model.DataGridField;
import org.activiti.bpmn.model.DataGridRow;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.ManualTask;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.ScriptTask;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.features.CreateEndEventFeature;
import org.activiti.designer.features.CreateExclusiveGatewayFeature;
import org.activiti.designer.features.CreateInclusiveGatewayFeature;
import org.activiti.designer.features.CreateMailTaskFeature;
import org.activiti.designer.features.CreateParallelGatewayFeature;
import org.activiti.designer.features.CreateScriptTaskFeature;
import org.activiti.designer.features.CreateServiceTaskFeature;
import org.activiti.designer.features.CreateStartEventFeature;
import org.activiti.designer.features.CreateUserTaskFeature;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.extension.ExtensionUtil;
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

    FlowElement cloneElement = null;
    List<FormProperty> formProperties = null;

    if (element instanceof StartEvent) {
      cloneElement = clone((StartEvent) element, diagram);
      formProperties = ((StartEvent) element).getFormProperties();
    } else if (element instanceof ServiceTask) {
      cloneElement = clone((ServiceTask) element, diagram);
    } else if (element instanceof EndEvent) {
      cloneElement = clone((EndEvent) element, diagram);
    } else if (element instanceof ExclusiveGateway) {
      cloneElement = clone((ExclusiveGateway) element, diagram);
    } else if (element instanceof InclusiveGateway) {
      cloneElement = clone((InclusiveGateway) element, diagram);
    } else if (element instanceof ManualTask) {
      cloneElement = clone((ManualTask) element, diagram);
    } else if (element instanceof ParallelGateway) {
      cloneElement = clone((ParallelGateway) element, diagram);
    } else if (element instanceof ScriptTask) {
      cloneElement = clone((ScriptTask) element, diagram);
    } else if (element instanceof UserTask) {
      cloneElement = clone((UserTask) element, diagram);
      formProperties = ((UserTask) element).getFormProperties();

      List<ActivitiListener> resultListenerList = new ArrayList<ActivitiListener>();
      for (ActivitiListener listener : ((UserTask) element).getTaskListeners()) {
        resultListenerList.add(clone(listener));
      }
      ((UserTask) cloneElement).setTaskListeners(resultListenerList);
    }

    if (element instanceof Activity && element instanceof UserTask == false) {
      List<ActivitiListener> resultListenerList = new ArrayList<ActivitiListener>();
      for (ActivitiListener listener : ((Activity) element).getExecutionListeners()) {
        resultListenerList.add(clone(listener));
      }
      ((Activity) cloneElement).setExecutionListeners(resultListenerList);
    }

    if (element instanceof Activity) {
      ((Activity) cloneElement).setAsynchronous(((Activity) element).isAsynchronous());
      ((Activity) cloneElement).setDefaultFlow(((Activity) element).getDefaultFlow());
    }

    if (formProperties != null) {
      List<FormProperty> resultPropertyList = new ArrayList<FormProperty>();
      for (FormProperty formProperty : formProperties) {
        resultPropertyList.add(clone(formProperty));
      }
      if (cloneElement instanceof UserTask) {
        ((UserTask) cloneElement).setFormProperties(resultPropertyList);
      } else {
        ((StartEvent) cloneElement).setFormProperties(resultPropertyList);
      }
    }

    if (cloneElement != null) {
      cloneElement.setName(element.getName());
      ModelHandler.getModel(EcoreUtil.getURI(diagram)).getBpmnModel().getMainProcess().addFlowElement(cloneElement);
    }

    return cloneElement;

  }

  private static ActivitiListener clone(final ActivitiListener listener) {
    ActivitiListener result = new ActivitiListener();
    result.setId(listener.getId());
    result.setEvent(listener.getEvent());
    result.setImplementation(listener.getImplementation());
    result.setImplementationType(listener.getImplementationType());
    for (FieldExtension extension : listener.getFieldExtensions()) {
      result.getFieldExtensions().add(clone(extension));
    }
    return result;
  }

  private static FormProperty clone(final FormProperty formProperty) {
    FormProperty result = new FormProperty();
    result.setId(formProperty.getId());
    result.setName(formProperty.getName());
    result.setType(formProperty.getType());
    result.setExpression(formProperty.getExpression());
    result.setVariable(formProperty.getVariable());
    result.setDefaultExpression(formProperty.getDefaultExpression());
    result.setDatePattern(formProperty.getDatePattern());
    result.setReadable(formProperty.isReadable());
    result.setRequired(formProperty.isRequired());
    result.setWriteable(formProperty.isWriteable());

    List<FormValue> resultValueList = new ArrayList<FormValue>();
    for (FormValue formValue : formProperty.getFormValues()) {
      FormValue resultValue = new FormValue();
      resultValue.setId(formValue.getId());
      resultValue.setName(formValue.getName());
      resultValueList.add(resultValue);
    }
    result.setFormValues(resultValueList);

    return result;
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
    result.setScript(original.getScript());
    result.setScriptFormat(original.getScriptFormat());

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
    result.setAssignee(original.getAssignee());
    result.setFormKey(original.getFormKey());
    result.setDueDate(original.getDueDate());
    result.setPriority(original.getPriority());

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
    result.setImplementation(original.getImplementation());
    result.setExtensionId(original.getExtensionId());

    for (FieldExtension extension : original.getFieldExtensions()) {
      result.getFieldExtensions().add(clone(extension));
    }
    
    for (CustomProperty property : original.getCustomProperties()) {
      final CustomProperty clone = clone(property);
      // Reset the id
      clone.setId(ExtensionUtil.wrapCustomPropertyId(result, ExtensionUtil.upWrapCustomPropertyId(clone.getId())));
      result.getCustomProperties().add(clone);
    }

    return result;
  }

  /**
   * Clones a {@link CustomProperty}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final CustomProperty clone(final CustomProperty original) {
    CustomProperty result = new CustomProperty();
    result.setId(original.getId());
    if (original.getComplexValue() != null) {
      result.setComplexValue(clone(original.getComplexValue()));
    }
    result.setName(original.getName());
    result.setSimpleValue(original.getSimpleValue());
    return result;
  }
  
  private static final FieldExtension clone(final FieldExtension original) {
    FieldExtension result = new FieldExtension();
    result.setFieldName(original.getFieldName());
    result.setExpression(original.getExpression());
    result.setStringValue(original.getStringValue());
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
