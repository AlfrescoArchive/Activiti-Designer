/**
 * 
 */
package org.activiti.designer.util;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.bpmn2.model.ActivitiListener;
import org.activiti.designer.bpmn2.model.Activity;
import org.activiti.designer.bpmn2.model.ComplexDataType;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.DataGrid;
import org.activiti.designer.bpmn2.model.DataGridField;
import org.activiti.designer.bpmn2.model.DataGridRow;
import org.activiti.designer.bpmn2.model.EndEvent;
import org.activiti.designer.bpmn2.model.ExclusiveGateway;
import org.activiti.designer.bpmn2.model.FieldExtension;
import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.bpmn2.model.FormProperty;
import org.activiti.designer.bpmn2.model.FormValue;
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
    } else if (element instanceof MailTask) {
    	cloneElement = clone((MailTask) element, diagram);
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
    
    if(formProperties != null) {
    	List<FormProperty> resultPropertyList = new ArrayList<FormProperty>();
  		for (FormProperty formProperty : formProperties) {
	      resultPropertyList.add(clone(formProperty));
      }
  		if(cloneElement instanceof UserTask) {
  			((UserTask) cloneElement).setFormProperties(resultPropertyList);
  		} else {
  			((StartEvent) cloneElement).setFormProperties(resultPropertyList);
  		}
  	}
    
    if(cloneElement != null) {
    	cloneElement.setName(element.getName());
    	ModelHandler.getModel(EcoreUtil.getURI(diagram)).addFlowElement(cloneElement);
    }

    return cloneElement;

  }
  
  private static ActivitiListener clone(final ActivitiListener listener) {
  	ActivitiListener result = new ActivitiListener();
  	result.setId(listener.getId());
  	result.setEvent(listener.getEvent());
  	result.setImplementation(listener.getImplementation());
  	result.setImplementationType(listener.getImplementationType());
  	result.setRunAs(listener.getRunAs());
  	result.setScriptProcessor(listener.getScriptProcessor());
  	
  	List<FieldExtension> fieldList = new ArrayList<FieldExtension>();
  	for (FieldExtension fieldExtension : listener.getFieldExtensions()) {
  		FieldExtension resultField = new FieldExtension();
  		resultField.setExpression(fieldExtension.getExpression());
  		resultField.setFieldName(fieldExtension.getFieldName());
  		fieldList.add(resultField);
  	}
  	result.setFieldExtensions(fieldList);
  	
  	return result;
  }
  
  private static FormProperty clone(final FormProperty formProperty) {
  	FormProperty result = new FormProperty();
  	result.setId(formProperty.getId());
  	result.setName(formProperty.getName());
  	result.setType(formProperty.getType());
  	result.setValue(formProperty.getValue());
  	result.setExpression(formProperty.getExpression());
  	result.setVariable(formProperty.getVariable());
  	result.setDatePattern(formProperty.getDatePattern());
  	result.setReadable(formProperty.getReadable());
  	result.setRequired(formProperty.getRequired());
  	result.setWriteable(formProperty.getWriteable());
  	
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
   * Clones a {@link MailTask}.
   * 
   * @param original
   *          the object to clone
   * @return a clone of the original object
   */
  private static final MailTask clone(final MailTask original, final Diagram diagram) {
    MailTask result = new MailTask();

    result.setId(ActivitiUiUtil.getNextId(result.getClass(), CreateMailTaskFeature.FEATURE_ID_KEY, diagram));
    result.setBcc(original.getBcc());
    result.setCc(original.getCc());
    result.setFrom(original.getFrom());
    result.setHtml(original.getHtml());
    result.setSubject(original.getSubject());
    result.setText(original.getText());
    result.setTo(original.getTo());

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

    for (CustomProperty property : original.getCustomProperties()) {
      final CustomProperty clone = clone(property, diagram);
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
