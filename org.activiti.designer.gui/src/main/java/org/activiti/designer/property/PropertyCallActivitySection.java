package org.activiti.designer.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.designer.Activator;
import org.activiti.designer.PluginImage;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.dialog.ActivitiResourceSelectionDialog;
import org.activiti.designer.util.workspace.ActivitiWorkspaceUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Adds the called element of the call activity to the "Main Config" properties sheet. Provides a
 * possibility to lookup a process with the given element ID in the currently open workspace
 * projects as well as a choice of all available projects.
 *
 * @since 5.12
 */
public class PropertyCallActivitySection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  /** The text field of the process ID to call */
  private Text calledElementText;

  /**
   * A button that becomes active in case a process is found in the current workspace with this
   * the ID
   */
  private Button openCalledElementButton;

  /**
   * A button that allows to choose a called element among all currently found processes.
   */
  private Button chooseCalledElementButton;
  
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    openCalledElementButton = getWidgetFactory().createButton(formComposite, StringUtils.EMPTY, SWT.PUSH);
    openCalledElementButton.setImage(Activator.getImage(PluginImage.ACTION_GO));
    FormData data = new FormData();
    data.right = new FormAttachment(100, -HSPACE);
    openCalledElementButton.setLayoutData(data);
    openCalledElementButton.addSelectionListener(openCalledElementSelected);

    chooseCalledElementButton = getWidgetFactory().createButton(formComposite, "\u2026", SWT.PUSH);
    chooseCalledElementButton.setToolTipText(
            "Click to open a dialog to choose from all found processes.");

    data = new FormData();
    data.right = new FormAttachment(openCalledElementButton, -HSPACE);
    chooseCalledElementButton.setLayoutData(data);
    chooseCalledElementButton.addSelectionListener(chooseCalledElementSelected);
    
    calledElementText = createTextControl(false);
    FormData formData = (FormData) calledElementText.getLayoutData();
    formData.right.offset = -80;
    createLabel("Called element", calledElementText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    CallActivity activity = (CallActivity) businessObject;
    if (control == calledElementText) {
      return activity.getCalledElement();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    CallActivity activity = (CallActivity) businessObject;
    
    if (control == calledElementText) {
      evaluateOpenCalledElementButtonEnabledStatus();
      activity.setCalledElement(calledElementText.getText());
    }
  }

  /**
   * Checks, whether the given process ID refers to a process this call activity might lead to.
   *
   * @param calledElement the process ID to check
   * @return <code>true</code> in case such a process ID exists, <code>false</code> otherwise.
   */
  private boolean isCalledElementExisting(final String calledElement) {
    final Set<IFile> resources = ActivitiWorkspaceUtil.getDiagramDataFilesByProcessId(calledElement);

    return !resources.isEmpty();
  }

  /**
   * Evaluates the current state of the button to open the corresponding process diagram of the
   * given process ID. In case the called element is empty or a not found, the button will be
   * disabled, otherwise enabled.
   */
  private void evaluateOpenCalledElementButtonEnabledStatus() {
    final String calledElement = calledElementText.getText();

    if (StringUtils.isBlank(calledElement) || !isCalledElementExisting(calledElement)) {
      openCalledElementButton.setEnabled(false);
      openCalledElementButton.setToolTipText(null);
    } else {
      openCalledElementButton.setEnabled(true);
      openCalledElementButton.setToolTipText("Click to open the called element's process diagram");
    }
  }

  /**
   * This listener is called in case the user presses the button to choose a process from one of
   * the existing processes in any open project.
   */
  private SelectionListener chooseCalledElementSelected = new SelectionListener() {

    @Override
    public void widgetSelected(SelectionEvent event) {
      final Map<IFile, Set<String>> processIdsByDataFiles
        = ActivitiWorkspaceUtil.getAllProcessIdsByDiagramDataFile();

      // we now need to make a list out of this, as the TwoPaneElementSelector wants it this way
      final List<Object[]> selectorInput = new ArrayList<Object[]>();

      for (final Entry<IFile, Set<String>> entry : processIdsByDataFiles.entrySet()) {
        for (final String processId : entry.getValue()) {
          entry.getKey().getFullPath();


          selectorInput.add(new Object[] { entry.getKey(), processId });
        }
      }

      final TwoPaneElementSelector dialog
        = new TwoPaneElementSelector(Display.getCurrent().getActiveShell()
                                   , new DiagramLabelProvider()
                                   , new ProcessIdLabelProvider());

      dialog.setTitle("Choose Called Element for Call Activity");
      dialog.setMessage("Choose a diagram and a process");
      dialog.setBlockOnOpen(true);
      dialog.setElements(selectorInput.toArray());
      dialog.setUpperListLabel("Diagrams");
      dialog.setLowerListLabel("Processes");

      if (dialog.open() == Window.OK) {
        final Object[] data = (Object[]) dialog.getFirstResult();

        calledElementText.setText((String) data[1]);
      }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      // intentionally left blank
    }
  };

  /**
   * This listener is called in case the user clicks the button to open the selected process
   * model.
   */
  private SelectionListener openCalledElementSelected = new SelectionListener() {

    @Override
    public void widgetSelected(SelectionEvent event) {
      final String calledElement = calledElementText.getText();

      final Set<IFile> resources
        = ActivitiWorkspaceUtil.getDiagramDataFilesByProcessId(calledElement);

      if (resources.size() == 1) {
        // open diagram
        openDiagramForBpmnFile(resources.iterator().next());
      } else if (resources.size() > 1) {
        final ActivitiResourceSelectionDialog dialog = new ActivitiResourceSelectionDialog(
                Display.getCurrent().getActiveShell(), resources.toArray(new IResource[] {}));

        dialog.setTitle("Multiple Processes Found");
        dialog.setMessage("Select a Process Model to use (* = any string, ? = any char):");
        dialog.setBlockOnOpen(true);
        dialog.setInitialPattern("*");

        if (dialog.open() == Window.OK) {
          final Object[] result = dialog.getResult();

          openDiagramForBpmnFile((IFile) result[0]);
        }
      }
    }

    /**
     * Opens the given diagram specified by the given data file in a new editor. In case an error
     * occurs while doing so, opens an error dialog.
     *
     * @param dataFile the data file to use for the new editor to open
     */
    private void openDiagramForBpmnFile(IFile dataFile) {

      if (dataFile.exists())
      {
        final IWorkbenchPage activePage
          = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        try {
          IDE.openEditor(activePage, dataFile, ActivitiConstants.DIAGRAM_EDITOR_ID, true);
        } catch (PartInitException exception) {
          final IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID()
                                          , "Error while opening new editor.", exception);

          ErrorDialog.openError(Display.getCurrent().getActiveShell()
                              , "Error Opening Activiti Diagram", null, status);
        }
      }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      widgetSelected(event);
    }
  };
  
  /**
   * A private process ID label provider that simply returns the second element of the pair built
   * from process ID and data file. This is used in the selection list for an available process.
   */
  private static class ProcessIdLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {

      return (String) ((Object[]) element)[1];
    }

  }

  /**
   * This label provider is used in the selection list for a diagram in the lower list. It utilizes
   * a workbench label provider to retrieve the appropriate image for the diagram.
   */
  private static class DiagramLabelProvider extends LabelProvider {

    private WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();

    @Override
    public Image getImage(Object element) {
      final IResource resource = (IResource) ((Object[]) element)[0];

      return labelProvider.getImage(resource);
    }

    @Override
    public String getText(Object element) {
      final IResource resource = (IResource) ((Object[]) element)[0];

      return resource.getFullPath().makeRelative().toString();
    }

  }
}
