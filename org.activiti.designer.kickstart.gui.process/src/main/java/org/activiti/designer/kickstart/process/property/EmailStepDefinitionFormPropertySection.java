package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.step.AlfrescoEmailStepDefinition;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class EmailStepDefinitionFormPropertySection extends AbstractKickstartProcessPropertySection {

  private PropertyItemBrowser itemBrowser;
  
  protected Text fromText;
  protected Text toText;
  protected Text ccText;
  protected Text subjectText;
  protected Text bodyText;

  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    itemBrowser = new PropertyItemBrowser();
    itemBrowser.setBrowseLabel("Insert property...");
    
    // From
    fromText = createTextControlWithButton(false, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(fromText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("From", fromText);
    
    // To
    toText = createTextControlWithButton(false, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(toText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("To", toText);
    
    // Cc
    ccText = createTextControlWithButton(false, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(ccText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("Cc", ccText);
    
    createSeparator();
    
    // Subject
    subjectText = createTextControlWithButton(false, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(subjectText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("Subject", subjectText);
    
    // Body
    bodyText = createTextControlWithButton(true, itemBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        insertIfNotEmpty(bodyText, (String) evt.getNewValue());
      }
    }, formComposite));
    createLabel("Body", bodyText);
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
    AlfrescoEmailStepDefinition def = (AlfrescoEmailStepDefinition) businessObject;
    if(control == fromText) {
      return def.getFrom();
    } else if(control == toText) {
      return def.getTo();
    } else if(control == ccText) {
      return def.getCc();
    } else if(control == subjectText) {
      return def.getSubject();
    } else if(control == bodyText) {
      return def.getBody();
    }
    return null;
  }
  
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    AlfrescoEmailStepDefinition def = (AlfrescoEmailStepDefinition) businessObject;
    
    if(control == fromText) {
      def.setFrom(getSafeText(fromText.getText()));
    } else if(control == toText) {
      def.setTo(getSafeText(toText.getText()));
    } else if(control == ccText) {
      def.setCc(getSafeText(ccText.getText()));
    } else if(control == subjectText) {
      def.setSubject(getSafeText(subjectText.getText()));
    } else if(control == bodyText) {
      def.setBody(getSafeText(bodyText.getText()));
    }
  }

}
