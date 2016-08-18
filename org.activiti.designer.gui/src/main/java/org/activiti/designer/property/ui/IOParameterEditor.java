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
package org.activiti.designer.property.ui;

import java.util.List;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.IOParameter;
import org.activiti.designer.property.ModelUpdater;
import org.apache.commons.lang.StringUtils;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;


public class IOParameterEditor extends TableFieldEditor {
  
  protected Composite parent;
  protected ModelUpdater modelUpdater;
  
  public PictogramElement pictogramElement;
  public Diagram diagram;
  public boolean isInputParameters = false;
	
  public IOParameterEditor(String key, Composite parent, ModelUpdater modelUpdater) {
    super(key, "", new String[] {"Source", "Source expression", "Target", "Target expression"},
        new int[] {150, 150, 150, 150}, parent);
    this.parent = parent;
    this.modelUpdater = modelUpdater;
  }

  public void initialize(List<IOParameter> parameterList) {
    removeTableItems();
    if(parameterList == null || parameterList.size() == 0) return;
    for (IOParameter parameter : parameterList) {
      addTableItem(parameter);
    }
  }
  
  @Override
  protected boolean isTableChangeEnabled() {
    return false;
  }

  @Override
  protected String createList(String[][] items) {
    return null;
  }

  @Override
  protected String[][] parseString(String string) {
    return null;
  }
  
  protected void addTableItem(IOParameter parameter) {
    
    if(table != null) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      String source = parameter.getSource() != null ? parameter.getSource() : "";
      tableItem.setText(0, source);
      String sourceExpression = parameter.getSourceExpression() != null ? parameter.getSourceExpression() : "";
      tableItem.setText(1, sourceExpression);
      String target = parameter.getTarget() != null ? parameter.getTarget() : "";
      tableItem.setText(2, target);
      String targetExpression = parameter.getTargetExpression() != null ? parameter.getTargetExpression() : "";
      tableItem.setText(3, targetExpression);
    }
  }

  @Override
  protected String[] getNewInputObject() {
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems());
    dialog.open();
    if((StringUtils.isNotEmpty(dialog.source) || StringUtils.isNotEmpty(dialog.sourceExpression)) &&
            (StringUtils.isNotEmpty(dialog.target) || StringUtils.isNotEmpty(dialog.targetExpression))) {
      createNewIOParameter(dialog);
      return new String[] { dialog.source, dialog.sourceExpression, dialog.target, dialog.targetExpression};
    } else {
      return null;
    }
  }
  
  @Override
  protected String[] getChangedInputObject(TableItem item) {
    int index = table.getSelectionIndex();
    IOParameterDialog dialog = new IOParameterDialog(parent.getShell(), getItems(), 
            item.getText(0), item.getText(1), item.getText(2), item.getText(3));
    dialog.open();
    if((StringUtils.isNotEmpty(dialog.source) || StringUtils.isNotEmpty(dialog.sourceExpression)) &&
        (StringUtils.isNotEmpty(dialog.target) || StringUtils.isNotEmpty(dialog.targetExpression))) {
      saveIOParameter(dialog, index);
    	return new String[] { dialog.source, dialog.sourceExpression, dialog.target, dialog.targetExpression};
    } else {
      return null;
    }
  }
  
  @Override
  protected void removedItem(int index) {
    saveRemovedObject(index);
  }
  
  @Override
  protected void upPressed() {
    final int index = table.getSelectionIndex();
    CallActivity updatableBo = (CallActivity) modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
    List<IOParameter> parameterList = getParameters(updatableBo);
    IOParameter parameter = parameterList.remove(index);
    parameterList.add(index - 1, parameter);
    modelUpdater.executeModelUpdater();
    super.upPressed();
  }

  @Override
  protected void downPressed() {
    final int index = table.getSelectionIndex();
    CallActivity updatableBo = (CallActivity) modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
    List<IOParameter> parameterList = getParameters(updatableBo);
    IOParameter parameter = parameterList.remove(index);
    parameterList.add(index + 1, parameter);
    modelUpdater.executeModelUpdater();
    super.downPressed();
  }
  
  protected void createNewIOParameter(IOParameterDialog dialog) {
    if (pictogramElement != null) {
      CallActivity updatableBo = (CallActivity) modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
      List<IOParameter> parameterList = getParameters(updatableBo);
      
      IOParameter parameter = new IOParameter();
      copyValuesToIOParameter(dialog, parameter);
      parameterList.add(parameter);
      modelUpdater.executeModelUpdater();
    }
  }
  
  private void saveIOParameter(IOParameterDialog dialog, int index) {
    if (pictogramElement != null) {
      CallActivity updatableBo = (CallActivity) modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
      List<IOParameter> parameterList = getParameters(updatableBo);
      
    	copyValuesToIOParameter(dialog, parameterList.get(index));
    	modelUpdater.executeModelUpdater();
    }
  }
  
  protected void saveRemovedObject(int index) {
    if (pictogramElement != null) {
      CallActivity updatableBo = (CallActivity) modelUpdater.getProcessModelUpdater().getUpdatableBusinessObject();
      List<IOParameter> parameterList = getParameters(updatableBo);
      
      parameterList.remove(index);
      
      modelUpdater.executeModelUpdater();
    }
  }
  
  protected void copyValuesToIOParameter(IOParameterDialog dialog, IOParameter parameter) {
    parameter.setSource(dialog.source);
    parameter.setSourceExpression(dialog.sourceExpression);
    parameter.setTarget(dialog.target);
    parameter.setTargetExpression(dialog.targetExpression);
  }
  
  protected List<IOParameter> getParameters(CallActivity callActivity) {
    List<IOParameter> parameterList = null;
    if(isInputParameters == true) {
      parameterList = callActivity.getInParameters();
    } else {
      parameterList = callActivity.getOutParameters();
    }
    return parameterList;
  }
}
