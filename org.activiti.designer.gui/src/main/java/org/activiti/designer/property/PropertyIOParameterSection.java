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
package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.designer.property.ui.IOParameterEditor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyIOParameterSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  private IOParameterEditor inParameterEditor;
  private IOParameterEditor outParameterEditor;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    Composite inParametersComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    inParametersComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    inParametersComposite.setLayout(layout);
    inParameterEditor = new IOParameterEditor("inputParameterEditor", inParametersComposite, (ModelUpdater) this);
    inParameterEditor.getLabelControl(inParametersComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel inParametersLabel = getWidgetFactory().createCLabel(formComposite, "Input parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(inParametersComposite, -HSPACE);
    data.top = new FormAttachment(inParametersComposite, 0, SWT.TOP);
    inParametersLabel.setLayoutData(data);
    
    Composite outParametersComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 150);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(inParametersComposite, VSPACE);
    outParametersComposite.setLayoutData(data);
    GridLayout outLayout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    outParametersComposite.setLayout(outLayout);
    outParameterEditor = new IOParameterEditor("outputParameterEditor", outParametersComposite, (ModelUpdater) this);
    outParameterEditor.getLabelControl(outParametersComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    
    CLabel outParametersLabel = getWidgetFactory().createCLabel(formComposite, "Output parameters:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(outParametersComposite, -HSPACE);
    data.top = new FormAttachment(outParametersComposite, 0, SWT.TOP);
    outParametersLabel.setLayoutData(data);
  }
  
  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      PictogramElement element = getSelectedPictogramElement();
      CallActivity bo = (CallActivity) getBusinessObject(element);
      resetModelUpdater();

      List<IOParameter> inParameterList = bo.getInParameters();
      
      inParameterEditor.pictogramElement = getSelectedPictogramElement();
      inParameterEditor.diagram = getDiagram();
      inParameterEditor.isInputParameters = true;
      inParameterEditor.initialize(inParameterList);
      
      List<IOParameter> outParameterList = bo.getOutParameters();
      
      outParameterEditor.pictogramElement = getSelectedPictogramElement();
      outParameterEditor.diagram = getDiagram();
      outParameterEditor.isInputParameters = false;
      outParameterEditor.initialize(outParameterList);
    }
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    // nothing to do
  }
}
