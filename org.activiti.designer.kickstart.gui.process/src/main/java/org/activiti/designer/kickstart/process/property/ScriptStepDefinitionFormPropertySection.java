package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.ScriptStepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class ScriptStepDefinitionFormPropertySection extends AbstractKickstartProcessPropertySection {

  private PropertyItemBrowser itemBrowser;
  
  protected Text scriptText;
  protected Text customRunAsText;

  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    createSeparator();
    
    itemBrowser = new PropertyItemBrowser();
    itemBrowser.setBrowseLabel("Insert property...");
    
    scriptText = createTextControlWithButton(true, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(scriptText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("Script", scriptText);
    
    customRunAsText = createTextControlWithButton(false, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(customRunAsText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("Run as user", customRunAsText);
  }
  
  protected void insertIfNotEmpty(Text text, String selection) {
    if(selection != null && !selection.isEmpty()) {
      text.setFocus();
      if(text.getSelection() != null) {
        text.insert(selection);
      }
    }
  }
  
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
    
    KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
    if (model != null && model.getModelFile() != null) {
      itemBrowser.setProject(model.getModelFile().getProject());
      itemBrowser.setWorkflowDefinition(model.getWorkflowDefinition());
    }
  }
  
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ScriptStepDefinition def = (ScriptStepDefinition) businessObject;
    if(control == scriptText) {
      return def.getScript();
    } else if(control == customRunAsText) {
      return getStringParameter(def, AlfrescoConversionConstants.PARAMETER_SCRIPT_TASK_RUNAS, null);
    }
    return null;
  }
  
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ScriptStepDefinition def = (ScriptStepDefinition) businessObject;
    
    if(control == scriptText) {
      def.setScript(getSafeText(scriptText.getText()));
    } else if(control == customRunAsText) {
      if(customRunAsText.getText().isEmpty()) {
        def.getParameters().put(AlfrescoConversionConstants.PARAMETER_SCRIPT_TASK_RUNAS, null);
      } else {
        def.getParameters().put(AlfrescoConversionConstants.PARAMETER_SCRIPT_TASK_RUNAS, customRunAsText.getText());
      }
    }
  }

}
