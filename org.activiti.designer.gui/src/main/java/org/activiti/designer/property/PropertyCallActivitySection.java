package org.activiti.designer.property;

import java.util.Set;

import org.activiti.designer.Activator;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.activiti.designer.util.workspace.ActivitiWorkspaceUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyCallActivitySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private Text callElementText;

  private Button callElementButton;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    Composite composite = factory.createFlatFormComposite(parent);
    FormData data;

    callElementButton = factory.createButton(composite, StringUtils.EMPTY, SWT.PUSH);
    callElementButton.setImage(Activator.getImage(PluginImage.ACTION_GO));
    data = new FormData();
    data.right = new FormAttachment(100, -HSPACE);
    callElementButton.setLayoutData(data);
    callElementButton.addSelectionListener(openListener);

    callElementText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(callElementButton, -HSPACE);
    data.top = new FormAttachment(0, VSPACE);
    callElementText.setLayoutData(data);
    callElementText.addFocusListener(listener);
    callElementText.addListener(SWT.CHANGED, new Listener() {

      @Override
      public void handleEvent(Event arg0) {
        evaluateCallElementButtonEnabled();
      }
    });

    CLabel elementLabel = factory.createCLabel(composite, "Called element:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(callElementText, -HSPACE);
    data.top = new FormAttachment(callElementText, 0, SWT.TOP);
    elementLabel.setLayoutData(data);

  }

  @Override
  public void refresh() {
    callElementText.removeFocusListener(listener);

    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
      // the filter assured, that it is a EClass
      if (bo == null)
        return;

      CallActivity callActivity = (CallActivity) bo;
      String calledElement = callActivity.getCalledElement();
      callElementText.setText(calledElement == null ? "" : calledElement);
    }
    callElementText.addFocusListener(listener);
    evaluateCallElementButtonEnabled();
  }

  private void evaluateCallElementButtonEnabled() {
    final String callElement = callElementText.getText();
    if (StringUtils.isBlank(callElement)) {
      disableCallElementButton();
    } else {
      final Set<IFile> resources = ActivitiWorkspaceUtil.getBPMNResourcesById(callElement);
      if (resources.size() > 0) {
        enableCallElementButton();
      } else {
        disableCallElementButton();
      }
    }
  }

  private void disableCallElementButton() {
    callElementButton.setEnabled(false);
  }

  private void enableCallElementButton() {
    callElementButton.setEnabled(true);
    callElementButton.setToolTipText("Click to open the called element's process diagram");
  }

  private SelectionListener openListener = new SelectionListener() {

    @Override
    public void widgetSelected(SelectionEvent event) {
      final String calledElement = callElementText.getText();

      final Set<IFile> resources = ActivitiWorkspaceUtil.getBPMNResourcesById(calledElement);

      if (resources.size() == 1) {
        // open diagram
        openDiagramForBpmnFile(resources.iterator().next());
      } else if (resources.size() > 1) {
        // TODO open selection dialog, http://jira.codehaus.org/browse/ACT-895
        MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Multiple processes found", String.format(
                "There are multiple resources in the workspace that have the call element id of '%s'. Cannot determine which file should be opened.",
                calledElement));
      } else {
        // The button should not have been enabled in the first place
        throw new IllegalStateException(String.format("Cannot open diagram for process id '%s' because it can't be found in the workspace", calledElement));
      }
    }

    private void openDiagramForBpmnFile(IFile resource) {

      boolean openBpmnFile = true;
      IFile activitiFile = null;

      // Get the Activiti file to go with the provided resource
      final IPath activitiFilePath = resource.getFullPath().removeFileExtension().removeFileExtension()
              .addFileExtension(ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION_RAW);

      final IResource activitiResource = ResourcesPlugin.getWorkspace().getRoot().findMember(activitiFilePath);

      if (activitiResource != null && activitiResource.exists() && activitiResource instanceof IFile) {
        activitiFile = (IFile) activitiResource;
        openBpmnFile = false;
      }

      try {
        if (!openBpmnFile) {
          IFileEditorInput input = new FileEditorInput(activitiFile);
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ActivitiBPMNDiagramConstants.DIAGRAM_EDITOR_ID);
        } else {
          boolean userChoice = MessageDialog
                  .openConfirm(
                          Display.getCurrent().getActiveShell(),
                          "No diagram file found",
                          String.format(
                                  "The process with id '%s' was found in the workspace, but its matching diagram file with name '%s' appears to be missing. Would you like to open the BPMN 2.0 XML Editor instead?",
                                  callElementText.getText(), activitiFilePath.lastSegment()));
          if (userChoice) {
            IFileEditorInput input = new FileEditorInput(resource);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ActivitiBPMNDiagramConstants.BPMN_EDITOR_ID);
          }
        }
      } catch (PartInitException e) {
        String error = "Error while opening editor";
        IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID(), error, e);
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), "An error occured", null, status);
        return;
      }

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      widgetSelected(event);
    }
  };

  private FocusListener listener = new FocusListener() {

    public void focusGained(final FocusEvent e) {
    }

    public void focusLost(final FocusEvent e) {
      PictogramElement pe = getSelectedPictogramElement();
      if (pe != null) {
        Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if (bo instanceof CallActivity) {
          DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
          TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
          ActivitiUiUtil.runModelChange(new Runnable() {

            public void run() {
              Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
              if (bo == null) {
                return;
              }
              String calledElement = callElementText.getText();
              CallActivity callActivity = (CallActivity) bo;
              callActivity.setCalledElement(calledElement);
            }
          }, editingDomain, "Model Update");
        }

      }
    }
  };
}
