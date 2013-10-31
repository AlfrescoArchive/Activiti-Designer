package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.activiti.workflow.simple.definition.HumanStepAssignment.HumanStepAssignmentType;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class ReviewStepDefinitionAssignmentPropertySection extends AbstractKickstartProcessPropertySection {

  protected PropertyItemBrowser userItemBrowser;
  protected PropertyItemBrowser numberItemBrowser;
  protected Text requiredCountText;
  protected Text assigneeText;
  
  protected Combo assignmentTypeCombo;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    assignmentTypeCombo = createCombobox(new String[] {"Single user", "Multiple users"}, 0);
    createLabel("Assignment type", assignmentTypeCombo);
    
    userItemBrowser = new PropertyItemBrowser();
    userItemBrowser.setItemfilter(new UserPropertyItemFilter());
    userItemBrowser.setBrowseLabel("Select people property...");
    
    assigneeText = createTextControlWithButton(false, userItemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getNewValue() != null) {
          assigneeText.setText((String) evt.getNewValue());
          flushControlValue(assigneeText);
        }
      }
    }, formComposite));
    createLabel("Assignee", assigneeText);
    
    numberItemBrowser = new PropertyItemBrowser();
    numberItemBrowser.setItemfilter(new TypedPropertyItemFilter(NumberPropertyDefinition.class));
    numberItemBrowser.setBrowseLabel("Select number property...");
    
    requiredCountText = createTextControlWithButton(false, numberItemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getNewValue() != null) {
          requiredCountText.setText((String) evt.getNewValue());
          flushControlValue(requiredCountText);
        }
      }
    }, formComposite));
    createLabel("Approval count", requiredCountText);
  }
  
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
    
    KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
    if (model != null && model.getModelFile() != null) {
      userItemBrowser.setProject(model.getModelFile().getProject());
      userItemBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
      numberItemBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
      numberItemBrowser.setProject(model.getModelFile().getProject());
    }
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    AlfrescoReviewStepDefinition def = (AlfrescoReviewStepDefinition) businessObject;
    if(control == assigneeText) {
      return def.getAssignmentPropertyName();
    } else if(control == assignmentTypeCombo) {
      if(def.getAssignmentType() == HumanStepAssignmentType.USER) {
        return assignmentTypeCombo.getItem(0);
      } else {
        return assignmentTypeCombo.getItem(1);
      }
    } else if(control == requiredCountText) {
      return def.getRequiredApprovalCount();
    }
    return null;
  }
  
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
      AlfrescoReviewStepDefinition def = (AlfrescoReviewStepDefinition) businessObject;
    if(control == assigneeText) {
      def.setAssignmentPropertyName(assigneeText.getText());
    } else if(control == assignmentTypeCombo) {
      if(assignmentTypeCombo.getSelectionIndex() == 0) {
         def.setAssignmentType(HumanStepAssignmentType.USER);
      } else {
        def.setAssignmentType(HumanStepAssignmentType.USERS);
      }
    } else if(control == requiredCountText) {
      def.setRequiredApprovalCount(requiredCountText.getText());
    }
  }

}
