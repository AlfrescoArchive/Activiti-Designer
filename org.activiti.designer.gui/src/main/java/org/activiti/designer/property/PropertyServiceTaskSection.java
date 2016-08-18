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

import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.property.ui.FieldExtensionEditor;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyServiceTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	protected Combo taskTypeButton;
	protected CLabel classNameLabel;
	protected Text classNameText;
	protected Button classSelectButton;
	protected Text expressionText;
	protected CLabel expressionLabel;
	protected Text delegateExpressionText;
	protected CLabel delegateExpressionLabel;
	protected Text resultVariableText;
	protected Text skipExpressionText;
	protected FieldExtensionEditor fieldEditor;
	protected Text documentationText;
	protected String[] typeValues = new String[] {"Java class", "Expression", "Delegate expression"};
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  taskTypeButton = createCombobox(typeValues, 0);
    createLabel("Task type", taskTypeButton);
    
    classNameText = getWidgetFactory().createText(formComposite, "", SWT.NONE);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(70, 0);
    data.top = createTopFormAttachment();
    classNameText.setLayoutData(data);
    registerControl(classNameText);
    
    classNameLabel = createLabel("Class name", classNameText);
    classSelectButton = getWidgetFactory().createButton(formComposite, "Select class", SWT.PUSH);
    data = new FormData();
    data.left = new FormAttachment(classNameText, 0);
    data.right = new FormAttachment(90, 0);
    data.top = new FormAttachment(classNameText, -2, SWT.TOP);
    classSelectButton.setLayoutData(data);
    classSelectButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = classNameText.getShell();
        try {
          SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell),
              SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false);

          if (dialog.open() == SelectionDialog.OK) {
            Object[] result = dialog.getResult();
            String className = ((IType) result[0]).getFullyQualifiedName();
            IJavaProject containerProject = ((IType) result[0]).getJavaProject();

            if (className != null) {
              classNameText.setText(className);
            }

            TransactionalEditingDomain editingDomain = getTransactionalEditingDomain();

            ActivitiUiUtil.runModelChange(new Runnable() {
              @Override
              public void run() {
                Object bo = getBusinessObject(getSelectedPictogramElement());
                if (bo == null) {
                  return;
                }
                String implementationName = classNameText.getText();
                if (implementationName != null) {
                  if (bo instanceof ServiceTask) {
                    ServiceTask serviceTask = (ServiceTask) bo;
                    serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
                    serviceTask.setImplementation(implementationName);
                  }
                }
              }
            }, editingDomain, "Model Update");

            IProject currentProject = ActivitiUiUtil.getProjectFromDiagram(getDiagram());

            ActivitiUiUtil.doProjectReferenceChange(currentProject, containerProject, className);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    
    expressionText = getWidgetFactory().createText(formComposite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(taskTypeButton, VSPACE);
    expressionText.setVisible(false);
    expressionText.setLayoutData(data);
    registerControl(expressionText);

    expressionLabel = createLabel("Expression", expressionText);

    delegateExpressionText = getWidgetFactory().createText(formComposite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 200);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(taskTypeButton, VSPACE);
    delegateExpressionText.setVisible(false);
    delegateExpressionText.setLayoutData(data);
    registerControl(delegateExpressionText);
    
    delegateExpressionLabel = createLabel("Delegate Expression", delegateExpressionText);
    
    resultVariableText = createTextControl(false);
    createLabel("Result variable", resultVariableText);
    
    skipExpressionText = createTextControl(false);
    createLabel("Skip expression", skipExpressionText);
    
    Composite extensionsComposite = getWidgetFactory().createComposite(formComposite, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(resultVariableText, VSPACE);
    extensionsComposite.setLayoutData(data);
    GridLayout layout = new GridLayout();
    layout.marginTop = 0;
    layout.numColumns = 1;
    extensionsComposite.setLayout(layout);
    fieldEditor = new FieldExtensionEditor("fieldEditor", extensionsComposite);
    fieldEditor.getLabelControl(extensionsComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

    CLabel extensionLabel = getWidgetFactory().createCLabel(formComposite, "Fields:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(extensionsComposite, -HSPACE);
    data.top = new FormAttachment(extensionsComposite, 0, SWT.TOP);
    extensionLabel.setLayoutData(data);
    
    documentationText = createTextControl(true);
    createLabel("Documentation", documentationText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == taskTypeButton) {
      
      fieldEditor.pictogramElement = getSelectedPictogramElement();
      fieldEditor.diagramBehavior = getDiagramContainer().getDiagramBehavior();
      fieldEditor.diagram = getDiagram();
      fieldEditor.initialize(task.getFieldExtensions());
      
      if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(task.getImplementationType())) {
        setVisibleClassType(false);
        setVisibleExpressionType(true);
        setVisibleDelegateExpressionType(false);
        return "Expression";
      } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(task.getImplementationType())) {
        setVisibleClassType(false);
        setVisibleExpressionType(false);
        setVisibleDelegateExpressionType(true);
        return "Delegate expression";
      } else {
        setVisibleClassType(true);
        setVisibleExpressionType(false);
        setVisibleDelegateExpressionType(false);
        return "Java class";
      }
      
    } else if (control == classNameText) {
      if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(task.getImplementationType())) {
        return task.getImplementation();
      }
    } else if (control == expressionText) {
      if (ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(task.getImplementationType())) {
        return task.getImplementation();
      }
    } else if (control == delegateExpressionText) {
      if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(task.getImplementationType())) {
        return task.getImplementation();
      }
    } else if (control == resultVariableText) {
      return task.getResultVariableName();
      
    } else if (control == skipExpressionText) {
      return task.getSkipExpression();
      
    } else if (control == documentationText) {
      return task.getDocumentation();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == taskTypeButton) {
      if (taskTypeButton.getSelectionIndex() == 0) {
        task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        setVisibleClassType(true);
        setVisibleExpressionType(false);
        setVisibleDelegateExpressionType(false);
      
      } else if (taskTypeButton.getSelectionIndex() == 1) {
        task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        setVisibleClassType(false);
        setVisibleExpressionType(true);
        setVisibleDelegateExpressionType(false);
      
      } else if (taskTypeButton.getSelectionIndex() == 2) {
        task.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        setVisibleClassType(false);
        setVisibleExpressionType(false);
        setVisibleDelegateExpressionType(true);
      } 
    } else if (control == classNameText) {
      task.setImplementation(classNameText.getText());
    } else if (control == expressionText) {
      task.setImplementation(expressionText.getText());
    } else if (control == delegateExpressionText) {
      task.setImplementation(delegateExpressionText.getText());
    } else if (control == resultVariableText) {
      task.setResultVariableName(resultVariableText.getText());
    } else if (control == skipExpressionText) {
      task.setSkipExpression(skipExpressionText.getText());
    } else if (control == documentationText) {
      task.setDocumentation(documentationText.getText());
    }
  }

	private void setVisibleClassType(boolean visible) {
		classNameText.setVisible(visible);
		classNameLabel.setVisible(visible);
		classSelectButton.setVisible(visible);
	}

	private void setVisibleExpressionType(boolean visible) {
		expressionText.setVisible(visible);
		expressionLabel.setVisible(visible);
	}

	private void setVisibleDelegateExpressionType(boolean visible) {
	  delegateExpressionText.setVisible(visible);
	  delegateExpressionLabel.setVisible(visible);
	}
}