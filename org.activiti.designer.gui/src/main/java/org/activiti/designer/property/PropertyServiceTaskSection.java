package org.activiti.designer.property;

import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.designer.property.ui.FieldExtensionEditor;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyServiceTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	protected Button expressionTypeButton;
	protected Button delegateExpressionTypeButton;
	protected Button classTypeButton;
	protected Text classNameText;
	protected Button classSelectButton;
	protected CLabel classSelectLabel;
	protected Text expressionText;
	protected CLabel expressionLabel;
	protected Text delegateExpressionText;
	protected CLabel delegateExpressionLabel;
	protected Text resultVariableText;
	protected FieldExtensionEditor fieldEditor;
	protected Text documentationText;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		Composite radioTypeComposite = new Composite(composite, SWT.NULL);
		radioTypeComposite.setBackground(composite.getBackground());
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		radioTypeComposite.setLayoutData(data);
		radioTypeComposite.setLayout(new RowLayout());

		classTypeButton = new Button(radioTypeComposite, SWT.RADIO);
		classTypeButton.setText("Java class");
		classTypeButton.setSelection(true);
		classTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setVisibleClassType(true);
				setVisibleExpressionType(false);
				setVisibleDelegateExpressionType(false);
				saveImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS, classNameText.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				//
			}

		});
		expressionTypeButton = new Button(radioTypeComposite, SWT.RADIO);
		expressionTypeButton.setText("Expression");
		expressionTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				setVisibleClassType(false);
				setVisibleExpressionType(true);
				setVisibleDelegateExpressionType(false);
				saveImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION, expressionText.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				//
			}

		});

		delegateExpressionTypeButton = new Button(radioTypeComposite, SWT.RADIO);
		delegateExpressionTypeButton.setText("Delegate expression");
		delegateExpressionTypeButton.addSelectionListener(new SelectionListener() {

		  @Override
		  public void widgetSelected(SelectionEvent event) {
		    setVisibleClassType(false);
		    setVisibleExpressionType(false);
		    setVisibleDelegateExpressionType(true);
		    saveImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION, delegateExpressionText.getText());
		  }

		  @Override
      public void widgetDefaultSelected(SelectionEvent event) {
        //
      }

    });

		CLabel typeLabel = factory.createCLabel(composite, "Type:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(radioTypeComposite, -HSPACE);
		data.top = new FormAttachment(radioTypeComposite, 0, SWT.TOP);
		typeLabel.setLayoutData(data);

		classNameText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(70, 0);
		data.top = new FormAttachment(radioTypeComposite, VSPACE);
		classNameText.setEnabled(true);
		classNameText.setLayoutData(data);

		classSelectButton = factory.createButton(composite, "Select class", SWT.PUSH);
		data = new FormData();
		data.left = new FormAttachment(classNameText, 0);
		data.right = new FormAttachment(78, 0);
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

						IDiagramContainer diagramContainer = getDiagramContainer();
						TransactionalEditingDomain editingDomain = diagramContainer.getDiagramBehavior().getEditingDomain();

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

		classSelectLabel = factory.createCLabel(composite, "Service class:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(classNameText, -HSPACE);
		data.top = new FormAttachment(classNameText, 0, SWT.TOP);
		classSelectLabel.setLayoutData(data);

		expressionText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(radioTypeComposite, VSPACE);
		expressionText.setVisible(false);
		expressionText.setLayoutData(data);
		expressionText.addFocusListener(listener);

		expressionLabel = factory.createCLabel(composite, "Expression:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(expressionText, -HSPACE);
		data.top = new FormAttachment(expressionText, 0, SWT.TOP);
		expressionLabel.setVisible(false);
		expressionLabel.setLayoutData(data);

		delegateExpressionText = factory.createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 160);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(radioTypeComposite, VSPACE);
		delegateExpressionText.setVisible(false);
		delegateExpressionText.setLayoutData(data);
		delegateExpressionText.addFocusListener(listener);

		delegateExpressionLabel = factory.createCLabel(composite, "Delegate expression:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(delegateExpressionText, -HSPACE);
		data.top = new FormAttachment(delegateExpressionText, 0, SWT.TOP);
		delegateExpressionLabel.setVisible(false);
		delegateExpressionLabel.setLayoutData(data);

		resultVariableText = factory.createText(composite, ""); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(expressionText, VSPACE);
    resultVariableText.setLayoutData(data);
    resultVariableText.addFocusListener(listener);

    CLabel resultVariableLabel = factory.createCLabel(composite, "Result variable:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(resultVariableText, -HSPACE);
    data.top = new FormAttachment(resultVariableText, 0, SWT.TOP);
    resultVariableLabel.setLayoutData(data);

		Composite extensionsComposite = factory.createComposite(composite, SWT.WRAP);
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

		CLabel extensionLabel = factory.createCLabel(composite, "Fields:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(extensionsComposite, -HSPACE);
		data.top = new FormAttachment(extensionsComposite, 0, SWT.TOP);
		extensionLabel.setLayoutData(data);
		
		documentationText = factory.createText(composite, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
    data = new FormData(SWT.DEFAULT, 100);
    data.left = new FormAttachment(0, 160);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(extensionsComposite, VSPACE);
    documentationText.setLayoutData(data);
    documentationText.addFocusListener(listener);
    
    CLabel documentationLabel = factory.createCLabel(composite, "Documentation:"); //$NON-NLS-1$
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(documentationText, -HSPACE);
    data.top = new FormAttachment(documentationText, 0, SWT.TOP);
    documentationLabel.setLayoutData(data);
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			expressionText.removeFocusListener(listener);
			delegateExpressionText.removeFocusListener(listener);
			resultVariableText.removeFocusListener(listener);
			documentationText.removeFocusListener(listener);
			Object bo = getBusinessObject(pe);
			if (bo == null) {
        return;
      }

			ServiceTask serviceTask = (ServiceTask) bo;
			String implementationName = serviceTask.getImplementation();
			if(StringUtils.isEmpty(serviceTask.getImplementationType()) ||
					ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(serviceTask.getImplementationType())) {
				setVisibleClassType(true);
				setVisibleExpressionType(false);
				setVisibleDelegateExpressionType(false);
				classNameText.setText(implementationName == null ? "" : implementationName);
			} else if (serviceTask.getImplementationType().equals(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION)) {
			  setVisibleClassType(false);
			  setVisibleExpressionType(false);
			  setVisibleDelegateExpressionType(true);
			  delegateExpressionText.setText(implementationName == null ? "" : implementationName);
			} else {
				setVisibleClassType(false);
				setVisibleExpressionType(true);
				setVisibleDelegateExpressionType(false);
				expressionText.setText(implementationName == null ? "" : implementationName);
			}

			if(StringUtils.isNotEmpty(serviceTask.getResultVariableName())) {
			  resultVariableText.setText(serviceTask.getResultVariableName());
			} else {
			  resultVariableText.setText("");
			}
			
			if (StringUtils.isNotEmpty(serviceTask.getDocumentation())) {
			  documentationText.setText(serviceTask.getDocumentation());
      } else {
        documentationText.setText("");
			}

			fieldEditor.pictogramElement = pe;
			fieldEditor.diagramContainer = getDiagramContainer();
			fieldEditor.diagram = getDiagram();
			fieldEditor.initialize(serviceTask.getFieldExtensions());
			
			expressionText.addFocusListener(listener);
			delegateExpressionText.addFocusListener(listener);
			resultVariableText.addFocusListener(listener);
			documentationText.addFocusListener(listener);
		}
	}

	private void saveImplementationType(final String type, final String value) {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			final Object bo = getBusinessObject(pe);
			if (bo instanceof ServiceTask) {
				IDiagramContainer diagramContainer = getDiagramContainer();
				TransactionalEditingDomain editingDomain = diagramContainer.getDiagramBehavior().getEditingDomain();
				ActivitiUiUtil.runModelChange(new Runnable() {
					@Override
          public void run() {
						ServiceTask serviceTask = (ServiceTask)  bo;
						serviceTask.setImplementationType(type);
						serviceTask.setImplementation(value);
					}
				}, editingDomain, "Model Update");
			}

		}
	}

	private void setVisibleClassType(boolean visible) {
		classTypeButton.setSelection(visible);
		classNameText.setVisible(visible);
		classSelectButton.setVisible(visible);
		classSelectLabel.setVisible(visible);
	}

	private void setVisibleExpressionType(boolean visible) {
		expressionTypeButton.setSelection(visible);
		expressionText.setVisible(visible);
		expressionLabel.setVisible(visible);
	}

	private void setVisibleDelegateExpressionType(boolean visible) {
	  delegateExpressionTypeButton.setSelection(visible);
	  delegateExpressionText.setVisible(visible);
	  delegateExpressionLabel.setVisible(visible);
	}

	private FocusListener listener = new FocusListener() {

		@Override
    public void focusGained(final FocusEvent e) {
		}

		@Override
    public void focusLost(final FocusEvent e) {
			PictogramElement pe = getSelectedPictogramElement();
			if (pe != null) {
				final Object bo = getBusinessObject(pe);
				if (bo instanceof ServiceTask) {
					updateServiceTask((ServiceTask) bo, e.getSource());
				}

			}
		}
	};
	
	protected void updateServiceTask(final ServiceTask serviceTask, final Object source) {
    String oldValue = null;
    final String newValue = ((Text) source).getText();
    if (source == expressionText) {
      oldValue = serviceTask.getImplementation();
    } else if (source == delegateExpressionText) {
      oldValue = serviceTask.getImplementation();
    } else if (source == resultVariableText) {
      oldValue = serviceTask.getResultVariableName();
    } else if (source == documentationText) {
      oldValue = serviceTask.getDocumentation();
    }
    
    if ((StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) || (StringUtils.isNotEmpty(oldValue) && newValue.equals(oldValue) == false)) {
      IFeature feature = new AbstractFeature(getDiagramTypeProvider().getFeatureProvider()) {
        
        @Override
        public void execute(IContext context) {
          if (source == expressionText) {
            serviceTask.setImplementation(newValue);
          } else if (source == delegateExpressionText) {
            serviceTask.setImplementation(newValue);
          } else if (source == resultVariableText) {
            serviceTask.setResultVariableName(newValue);
          } else if (source == documentationText) {
            serviceTask.setDocumentation(newValue);
          }
        }
        
        @Override
        public boolean canExecute(IContext context) {
          return true;
        }
      };
      CustomContext context = new CustomContext();
      execute(feature, context);
    }
  }
}