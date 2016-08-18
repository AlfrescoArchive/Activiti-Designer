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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ExportKickstartProcessWizardProjectPage extends WizardPage {

  private TableViewer projectViewer;

  private static final int PROJECT_LIST_MULTIPLIER = 15;

  public ExportKickstartProcessWizardProjectPage(String title) {
    super("select-project");
    
    setTitle(title);
    setDescription("Select the process to export");
  }

  public void createControl(Composite parent) {

    Font font = parent.getFont();

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    composite.setFont(font);

    Label referenceLabel = new Label(composite, SWT.NONE);
    referenceLabel.setText("Select project");
    referenceLabel.setFont(font);

    projectViewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER);
    projectViewer.getTable().setFont(composite.getFont());
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);

    data.heightHint = getDefaultFontHeight(projectViewer.getTable(), PROJECT_LIST_MULTIPLIER);
    projectViewer.getTable().setLayoutData(data);
    projectViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
    projectViewer.setContentProvider(getContentProvider());
    projectViewer.setComparator(new ViewerComparator());
    projectViewer.setInput(ResourcesPlugin.getWorkspace());
    
    projectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ((ExportKickstartProcessWizard) getWizard()).setProject(getSelectedProject());
        getContainer().updateButtons();
      }
    });

    setControl(composite);
  }
  
  @Override
  public boolean canFlipToNextPage() {
    return projectViewer.getSelection() != null && !projectViewer.getSelection().isEmpty();
  }

  /**
   * Returns a content provider for the reference project viewer. It will return all projects in the workspace.
   * 
   * @return the content provider
   */
  protected IStructuredContentProvider getContentProvider() {
    return new WorkbenchContentProvider() {
      public Object[] getChildren(Object element) {
        if (!(element instanceof IWorkspace)) {
          return new Object[0];
        }
        IProject[] projects = ((IWorkspace) element).getRoot().getProjects();
        return projects == null ? new Object[0] : projects;
      }
    };
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

  public IProject getSelectedProject() {
    if(projectViewer.getSelection() != null && !projectViewer.getSelection().isEmpty()) {
      return (IProject) ((IStructuredSelection)projectViewer.getSelection()).getFirstElement();
    }
    return null;
  }

}
