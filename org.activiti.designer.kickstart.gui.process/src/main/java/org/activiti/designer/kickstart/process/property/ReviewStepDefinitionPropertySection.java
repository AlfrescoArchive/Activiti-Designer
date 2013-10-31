package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.util.KickstartProcessConstants;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.step.AlfrescoReviewStepDefinition;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Frederik Heremans
 */
public class ReviewStepDefinitionPropertySection extends AbstractKickstartProcessPropertySection {

  protected Button endProcessButton;
  protected FormReferenceViewer formReferenceViewer;
  
  @Override
  protected void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    
    formReferenceViewer = new FormReferenceViewer(formComposite, new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        String referencePath = formReferenceViewer.getReferencedFormPath();
        AlfrescoReviewStepDefinition workflowDefinition =  (AlfrescoReviewStepDefinition) getModelUpdater().getUpdatableBusinessObject();
        workflowDefinition.getParameters().put(KickstartProcessConstants.PARAMETER_FORM_REFERENCE, referencePath);
        executeModelUpdater();
      }
    }, null);
    
    registerControl(formReferenceViewer.getComposite());
    
    FormData formData = new FormData();
    formData.top = new FormAttachment(0, 0);
    formData.left = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, 0);
    formReferenceViewer.getComposite().setLayoutData(formData);
    
    endProcessButton = createCheckboxControl("End process when rejected");
    createSeparator();
  }
  
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
    if(formReferenceViewer != null) {
      IProject project = null;
      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagram()));
      if (model != null && model.getModelFile() != null) {
        project = model.getModelFile().getProject();
      }
      formReferenceViewer.setProject(project);
    }
  }
  
  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    AlfrescoReviewStepDefinition def = (AlfrescoReviewStepDefinition) businessObject;
    if(control == endProcessButton) {
      return def.isEndProcessOnReject();
    }
    return null;
  }
  
  @Override
  protected void populateControl(Control control, Object businessObject) {
    if(control == formReferenceViewer.getComposite()) {
      AlfrescoReviewStepDefinition definition = (AlfrescoReviewStepDefinition) businessObject;
      formReferenceViewer.setReferencedFormPath((String) definition.getParameters()
          .get(KickstartProcessConstants.PARAMETER_FORM_REFERENCE));
    } else {
      super.populateControl(control, businessObject);
    }
  }
  
  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
      AlfrescoReviewStepDefinition def = (AlfrescoReviewStepDefinition) businessObject;
    if(control == endProcessButton) {
      def.setEndProcessOnReject(endProcessButton.getSelection());
    }
  }
}
