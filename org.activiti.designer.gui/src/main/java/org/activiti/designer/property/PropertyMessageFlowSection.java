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

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.designer.util.TextUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyMessageFlowSection extends ActivitiPropertySection implements ITabbedPropertyConstants {
	
  protected Text flowLabelWidthText;
  protected Text conditionExpressionText;
  
  @Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    flowLabelWidthText = createTextControl(false);
    createLabel("Label width (50-500)", flowLabelWidthText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    SequenceFlow sequenceFlow = (SequenceFlow) businessObject;
    if (control == flowLabelWidthText) {
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
          org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
          return "" + text.getWidth();
        }
      }
      
    } else if (control == conditionExpressionText) {
      return sequenceFlow.getConditionExpression();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    if (control == flowLabelWidthText) {
      EList<ConnectionDecorator> decoratorList = ((FreeFormConnection) getSelectedPictogramElement()).getConnectionDecorators();
      for (ConnectionDecorator decorator : decoratorList) {
        if (decorator.getGraphicsAlgorithm() instanceof org.eclipse.graphiti.mm.algorithms.MultiText) {
          final org.eclipse.graphiti.mm.algorithms.MultiText text = (org.eclipse.graphiti.mm.algorithms.MultiText) decorator.getGraphicsAlgorithm();
          final String widthText = flowLabelWidthText.getText();
          
          if (NumberUtils.isNumber(widthText)) {
            final long width = Long.valueOf(widthText);
            if (width >= 50 || width <= 500) {
              
              final Runnable runnable = new Runnable() {

                public void run() {

                  TextUtil.setTextSize((int) width, text);
                }
              };
              
              TransactionalEditingDomain editingDomain = getDiagramContainer().getDiagramBehavior().getEditingDomain();
              ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
            }
          }
        }
      } 
    }
  }
}
