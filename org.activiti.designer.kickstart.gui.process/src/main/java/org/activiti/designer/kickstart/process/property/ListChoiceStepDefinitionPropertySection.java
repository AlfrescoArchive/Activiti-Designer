package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.kickstart.process.command.KickstartProcessModelUpdater;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section containing all properties in the main {@link StepDefinition}.
 * 
 * @author Frederik Hereman
 */
public class ListChoiceStepDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Text nameControl;
  protected ChoiceConditionEditor choiceConditionEditor;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    nameControl = createTextControl(false);
    createLabel("Choice name", nameControl);
    
    createSeparator();
    createFullWidthLabel("Conditions");
    
    // Add condition builder
    choiceConditionEditor = new ChoiceConditionEditor(formComposite, new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        executeModelUpdater();
      }
    });
    
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.top = createTopFormAttachment();
    data.right = new FormAttachment(100, 0);
    choiceConditionEditor.getComposite().setLayoutData(data);
    registerControl(choiceConditionEditor.getComposite());
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ListConditionStepDefinition<?> stepDefinition = (ListConditionStepDefinition<StepDefinition>) businessObject;
    if(control == nameControl) {
      return stepDefinition.getName() != null ? stepDefinition.getName() : "";
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void storeValueInModel(Control control, Object businessObject) {
    ListConditionStepDefinition<StepDefinition> stepDefinition = (ListConditionStepDefinition<StepDefinition>) businessObject;
    if(control == nameControl) {
      stepDefinition.setName(nameControl.getText());
    }
  }
  
  @Override
  public void refresh() {
    super.refresh();
    if(getSelectedPictogramElement() != null && getDiagram() != null && choiceConditionEditor != null) {
      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
      if(model != null && model.getModelFile() != null) {
        choiceConditionEditor.setWorkflowDefinition(model.getWorkflowDefinition());
        choiceConditionEditor.setProject(model.getModelFile().getProject());
      }
    }
    
    resetModelUpdater();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected KickstartProcessModelUpdater<?> createModelUpdater() {
    KickstartProcessModelUpdater<ListConditionStepDefinition<StepDefinition>> updater = (KickstartProcessModelUpdater<ListConditionStepDefinition<StepDefinition>>) super.createModelUpdater();
    
    // When a new updater is created, hook the choice-editor up with the condition
    ListConditionStepDefinition<StepDefinition> stepDefinition = updater.getUpdatableBusinessObject();
    if(stepDefinition .getConditions()  == null || stepDefinition.getConditions().isEmpty()) {
      // Add one default condition
      stepDefinition.addCondition(null, "==", null);
    }
    choiceConditionEditor.setConditionDefinition(stepDefinition.getConditions().get(0));
    
    return updater;
  }
  

}
