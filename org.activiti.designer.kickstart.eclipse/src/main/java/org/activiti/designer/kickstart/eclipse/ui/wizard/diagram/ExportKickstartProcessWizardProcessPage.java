/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.kickstart.eclipse.ui.wizard.diagram;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ExportKickstartProcessWizardProcessPage extends WizardPage {

  protected TableViewer processViewer;
  protected Label referenceLabel;

  private static final int PROJECT_LIST_MULTIPLIER = 15;

  public ExportKickstartProcessWizardProcessPage(String title) {
    super("select-process");

    setTitle(title);
    setDescription("Select the project the process to export is part of");
  }

  public void createControl(Composite parent) {

    Font font = parent.getFont();

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    composite.setFont(font);

    referenceLabel = new Label(composite, SWT.NONE);
    referenceLabel.setText("Select a process to export");
    referenceLabel.setFont(font);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
    referenceLabel.setLayoutData(data);
    
    processViewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER);
    processViewer.getTable().setFont(composite.getFont());
    data = new GridData(SWT.FILL, SWT.FILL, true, true);

    data.heightHint = getDefaultFontHeight(processViewer.getTable(), PROJECT_LIST_MULTIPLIER);
    processViewer.getTable().setLayoutData(data);
    processViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    processViewer.setContentProvider(new ArrayContentProvider());
    processViewer.setComparator(new ViewerComparator());

    processViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IResource selected = getSelectedProcess();
        ((ExportKickstartProcessWizard) getWizard()).setProcessResource(selected);
        
        if(selected != null) {
          referenceLabel.setText("Selected: " + selected.getProjectRelativePath().toString());
        } else {
          referenceLabel.setText("Select a process to export");
        }
        getContainer().updateButtons();
        
      }
    });
    
    setControl(composite);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);

    if (visible) {
      try {
        getContainer().run(true, true, new IRunnableWithProgress() {
          @Override
          public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            monitor.beginTask("Fetching processes in project", IProgressMonitor.UNKNOWN);
            final List<IResource> resources = gatherProcesses();

            // Finally, set the gathered resourses as input for the viewer
            getContainer().getShell().getDisplay().asyncExec(new Runnable() {
              @Override
              public void run() {
                processViewer.setInput(resources);
              }
            });
          }
        });
      } catch (InvocationTargetException e) {
        Logger.logError("Errow while searching for Processes", e);
      } catch (InterruptedException e) {
        Logger.logError("Errow while searching for Processes", e);
      }
    }
  }

  @Override
  public boolean canFlipToNextPage() {
    return processViewer.getSelection() != null && !processViewer.getSelection().isEmpty();
  }

  public IResource getSelectedProcess() {
    if (processViewer.getSelection() != null && !processViewer.getSelection().isEmpty()) {
      return (IResource) ((IStructuredSelection) processViewer.getSelection()).getFirstElement();
    }
    return null;
  }

  protected List<IResource> gatherProcesses() {
    List<IResource> processes = new ArrayList<IResource>();
    
    IProject project = ((ExportKickstartProcessWizard) getWizard()).getProject();
    if (project != null) {
      addProcesses(project, processes);
    }
    return processes;
  }

  private void addProcesses(IContainer project, List<IResource> processes) {
    try {
      IContentDescription description = null;
      for (IResource resource : project.members()) {
        if (resource instanceof IContainer && !resource.isDerived()) {
          addProcesses((IContainer) resource, processes);
        } else if (resource instanceof IFile) {
          description = ((IFile) resource).getContentDescription();
          if(description != null && description.getContentType() != null && KickstartProcessMemoryModel.KICKSTART_PROCESS_CONTENT_TYPE.equals(
              description.getContentType().getId())) {
            processes.add(resource);
          }
        }
      }
    } catch (CoreException ce) {
      Logger.logError("Error while getting processes", ce);
    }
  }

  /**
   * Get the default widget height for the supplied control.
   * 
   * @return int
   * @param control
   *          - the control being queried about fonts
   * @param lines
   *          - the number of lines to be shown on the table.
   */
  private static int getDefaultFontHeight(Control control, int lines) {
    FontData[] viewerFontData = control.getFont().getFontData();
    int fontHeight = 10;

    // If we have no font data use our guess
    if (viewerFontData.length > 0) {
      fontHeight = viewerFontData[0].getHeight();
    }
    return lines * fontHeight;

  }
}
