package org.activiti.designer.property.ui;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.alfresco.AlfrescoScriptTask;
import org.activiti.bpmn.model.alfresco.AlfrescoUserTask;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

public abstract class AbstractListenerDialog extends Dialog implements ITabbedPropertyConstants {
	
	public String implementationType;
	public String implementation;
	public String eventName;
	public String script;
	public String runAs;
	public String scriptProcessor;
	public List<FieldExtension> fieldExtensionList;
	
	protected ActivitiListener savedListener;
	
	protected TableItem[] fieldList;
	
	protected Combo eventDropDown;
	protected Button classTypeButton;
	protected Button expressionTypeButton;
	protected Button delegateExpressionTypeButton;
	protected Button alfrescoTypeButton;
	protected Text classNameText;
	protected Button classSelectButton;
	protected CLabel classSelectLabel;
	protected Text expressionText;
	protected CLabel expressionLabel;
	protected Text delegateExpressionText;
	protected Text scriptText;
	protected CLabel scriptLabel;
	private Text runAsText;
	protected CLabel runAsLabel;
	private Text scriptProcessorText;
	protected CLabel scriptProcessorLabel;
	protected CLabel delegateExpressionLabel;
	protected FieldExtensionEditor fieldEditor;
	protected CLabel extensionLabel;
	
	public AbstractListenerDialog(Shell parent, TableItem[] fieldList) {
		// Pass the default styles here
		this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public AbstractListenerDialog(Shell parent, TableItem[] fieldList, ActivitiListener listener) {
    // Pass the default styles here
    this(parent, fieldList, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    this.savedListener = listener;
  }

	public AbstractListenerDialog(Shell parent, TableItem[] fieldList, int style) {
		// Let users override the default styles
		super(parent, style);
		setText("Listener configuration");
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		shell.setText(getText());
		shell.setSize(700, 400);
		Point location = getParent().getShell().getLocation();
		Point size = getParent().getShell().getSize();
		shell.setLocation((location.x + size.x - 300) / 2, (location.y + size.y - 150) / 2);
		createContents(shell);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
	  FormLayout layout = new FormLayout();
	  layout.marginHeight = 5;
	  layout.marginWidth = 5;
	  shell.setLayout(layout);
	  FormData data;
	  
	  eventDropDown = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
	  for(String event : getEventList()) {
	    eventDropDown.add(event);
	  }
	  
	  if(savedListener != null && savedListener.getEvent() != null) {
	    eventDropDown.setText(savedListener.getEvent());
	  } else {
	    eventDropDown.setText(getDefaultEvent());
	  }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(0, VSPACE);
    eventDropDown.setLayoutData(data);
    
    createLabel(shell, "Event", eventDropDown);
		
		Composite radioTypeComposite = new Composite(shell, SWT.NULL);
    radioTypeComposite.setBackground(shell.getBackground());
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(eventDropDown, VSPACE);
    radioTypeComposite.setLayoutData(data);
    radioTypeComposite.setLayout(new RowLayout());
      
    classTypeButton = new Button(radioTypeComposite, SWT.RADIO);
    classTypeButton.setText("Java class");
    classTypeButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        enableClassType();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
      
    });
    expressionTypeButton = new Button(radioTypeComposite, SWT.RADIO);
    expressionTypeButton.setText("Expression");
    expressionTypeButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent event) {
        enableExpressionType();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
      
    });
    
    delegateExpressionTypeButton = new Button(radioTypeComposite, SWT.RADIO);
    delegateExpressionTypeButton.setText("Delegate expression");
    delegateExpressionTypeButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetSelected(SelectionEvent event) {
        enableDelegateExpressionType();
      }
    
      @Override
      public void widgetDefaultSelected(SelectionEvent event) {
      }
      
    });
    
    if(PreferencesUtil.getBooleanPreference(Preferences.ALFRESCO_ENABLE)) {
      alfrescoTypeButton = new Button(radioTypeComposite, SWT.RADIO);
      alfrescoTypeButton.setText("Alfresco script");
      alfrescoTypeButton.addSelectionListener(new SelectionListener() {
        
        @Override
        public void widgetSelected(SelectionEvent event) {
          enableAlfrescoType();
        }
      
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }
        
      });
    }
    
    createLabel(shell, "Type", radioTypeComposite);

    classNameText = new Text(shell, SWT.BORDER);
    if(savedListener != null && ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(savedListener.getImplementationType())) {
      classNameText.setText(savedListener.getImplementation());
    } else {
      classNameText.setText("");
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(70, 0);
    data.top = new FormAttachment(radioTypeComposite, 10);
    classNameText.setEnabled(false);
    classNameText.setLayoutData(data);

    classSelectButton = new Button(shell, SWT.PUSH);
    classSelectButton.setText("Select class");
    data = new FormData();
    data.left = new FormAttachment(classNameText, 0);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(classNameText, -4, SWT.TOP);
    classSelectButton.setLayoutData(data);
    classSelectButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent evt) {
        Shell shell = classNameText.getShell();
        try {
          SelectionDialog dialog = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell),
              SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false);

          if (dialog.open() == SelectionDialog.OK) {
            Object[] result = dialog.getResult();
            String className = ((IType) result[0]).getFullyQualifiedName();
            
            if (className != null) {
              classNameText.setText(className);
            }
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    
    classSelectLabel = createLabel(shell, "Service class", expressionText);
    
    expressionText = new Text(shell, SWT.BORDER);
    if(savedListener != null && ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(savedListener.getImplementationType())) {
      expressionText.setText(savedListener.getImplementation());
    } else {
      expressionText.setText("");
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(radioTypeComposite, VSPACE);
    expressionText.setLayoutData(data);
    
    expressionLabel = createLabel(shell, "Expression", expressionText);
    
    delegateExpressionText = new Text(shell, SWT.BORDER);
    if(savedListener != null && ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(savedListener.getImplementationType())) {
      delegateExpressionText.setText(savedListener.getImplementation());
    } else {
      delegateExpressionText.setText("");
    }
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(radioTypeComposite, VSPACE);
    delegateExpressionText.setLayoutData(data);
        
    delegateExpressionLabel = createLabel(shell, "Delegate expression", delegateExpressionText);
    
    scriptText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    data = new FormData(SWT.DEFAULT, 100);
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(radioTypeComposite, VSPACE);
    scriptText.setLayoutData(data);
    
    scriptLabel = createLabel(shell, "Script", scriptText);
    
    runAsText = new Text(shell, SWT.BORDER);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(scriptText, VSPACE);
    runAsText.setLayoutData(data);
    runAsLabel = createLabel(shell, "Run as", runAsText);
    
    scriptProcessorText = new Text(shell, SWT.BORDER);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(runAsText, VSPACE);
    scriptProcessorText.setLayoutData(data);
    scriptProcessorLabel = createLabel(shell, "Script processor", scriptProcessorText);
    
    if(savedListener != null && (AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER.equalsIgnoreCase(savedListener.getImplementation()) ||
            AlfrescoScriptTask.ALFRESCO_SCRIPT_EXECUTION_LISTENER.equalsIgnoreCase(savedListener.getImplementation()))) {
      
      List<FieldExtension> extensionList = savedListener.getFieldExtensions();
      String script = "";
      String runAs = "";
      String scriptProcessor = "";
      for (FieldExtension fieldExtension : extensionList) {
        if ("script".equalsIgnoreCase(fieldExtension.getFieldName())) {
          if (StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
            script = fieldExtension.getStringValue();
          }
        } else if ("runAs".equalsIgnoreCase(fieldExtension.getFieldName())) {
          if (StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
            runAs = fieldExtension.getStringValue();
          }
        } else if ("scriptProcessor".equalsIgnoreCase(fieldExtension.getFieldName())) {
          if (StringUtils.isNotEmpty(fieldExtension.getStringValue())) {
            scriptProcessor = fieldExtension.getStringValue();
          }
        }
      }
      scriptText.setText(script);
      runAsText.setText(runAs);
      scriptProcessorText.setText(scriptProcessor);
      
    } else {
      scriptText.setText("");
      runAsText.setText("");
      scriptProcessorText.setText("");
    }
    
    Composite extensionsComposite = new Composite(shell, SWT.WRAP);
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(expressionText, 15);
    extensionsComposite.setLayoutData(data);
    GridLayout fieldLayout = new GridLayout();
    fieldLayout.marginTop = 0;
    fieldLayout.numColumns = 1;
    extensionsComposite.setLayout(fieldLayout);
    fieldEditor = new FieldExtensionEditor("fieldEditor", extensionsComposite);
    fieldEditor.getLabelControl(extensionsComposite).setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    if(savedListener != null && savedListener.getFieldExtensions() != null && savedListener.getFieldExtensions().size() > 0) {
      fieldEditor.initializeModel(savedListener.getFieldExtensions());
    }
    
    extensionLabel = createLabel(shell, "Fields", extensionsComposite);
    
    // Create the cancel button and add a handler
    // so that pressing it will set input to null
    Button cancel = new Button(shell, SWT.PUSH);
    cancel.setText("Cancel");
    data = new FormData();
    data.left = new FormAttachment(0, 120);
    data.right = new FormAttachment(50, 0);
    data.top = new FormAttachment(extensionsComposite, 20);
    cancel.setLayoutData(data);
    cancel.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(cancel, -HSPACE);
    data.top = new FormAttachment(cancel, 0, SWT.TOP);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(implementationType) && (StringUtils.isEmpty(classNameText.getText()))) {
					MessageDialog.openError(shell, "Validation error", "Class name must be filled.");
					return;
				}
				if(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(implementationType) && (StringUtils.isEmpty(expressionText.getText()))) {
          MessageDialog.openError(shell, "Validation error", "Expression must be filled.");
          return;
        }
				if(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equals(implementationType) && (StringUtils.isEmpty(delegateExpressionText.getText()))) {
          MessageDialog.openError(shell, "Validation error", "Delegate expression must be filled.");
          return;
        }
				eventName = eventDropDown.getText();
				if (AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER.equalsIgnoreCase(implementation) ||
		            AlfrescoScriptTask.ALFRESCO_SCRIPT_EXECUTION_LISTENER.equalsIgnoreCase(implementation)) {
				  
				  script = scriptText.getText();
          runAs = runAsText.getText();
          scriptProcessor = scriptProcessorText.getText();
          
				} else if(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(implementationType)) {
				  implementation = classNameText.getText();
				} else if(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(implementationType)){
				  implementation = delegateExpressionText.getText();
				} else {
				  implementation = expressionText.getText();
				}
				fieldExtensionList = new ArrayList<FieldExtension>();
				if(fieldEditor.getItems() != null) {
				  for (TableItem tableItem : fieldEditor.getItems()) {
				    FieldExtension fieldModel = new FieldExtension();
				    fieldModel.setFieldName(tableItem.getText(0));
				    fieldModel.setStringValue(tableItem.getText(1));
				    fieldModel.setExpression(tableItem.getText(1));
				    fieldExtensionList.add(fieldModel);
          }
				}
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
		
		if(savedListener == null || savedListener.getImplementationType() == null) {
		  classTypeButton.setSelection(true);
      enableClassType();
		} else if (PreferencesUtil.getBooleanPreference(Preferences.ALFRESCO_ENABLE) && (AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER.equalsIgnoreCase(savedListener.getImplementation()) ||
            AlfrescoScriptTask.ALFRESCO_SCRIPT_EXECUTION_LISTENER.equalsIgnoreCase(savedListener.getImplementation()))) {
		  alfrescoTypeButton.setSelection(true);
      enableAlfrescoType();
		} else if(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(savedListener.getImplementationType())) {
      classTypeButton.setSelection(true);
      enableClassType();
    } else if(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION.equals(savedListener.getImplementationType())){
      expressionTypeButton.setSelection(true);
      enableExpressionType();
    } else {
      delegateExpressionTypeButton.setSelection(true);
      enableDelegateExpressionType();
    }
	}
	
	protected abstract String[] getEventList();
	
	protected abstract String getDefaultEvent();
	
	private void setImplementationType(final String type) {
    implementationType = type;
  }
	
	private void enableClassType() {
	  setVisibleClassType(true);
    setVisibleExpressionType(false);
    setVisibleDelegateExpressionType(false);
    setVisibleAlfrescoType(false);
    setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
	}
	
	private void enableExpressionType() {
	  setVisibleClassType(false);
    setVisibleExpressionType(true);
    setVisibleDelegateExpressionType(false);
    setVisibleAlfrescoType(false);
    setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
  }
	
	private void enableDelegateExpressionType() {
	  setVisibleClassType(false);
    setVisibleExpressionType(false);
    setVisibleDelegateExpressionType(true);
    setVisibleAlfrescoType(false);
    setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
  }
	
	private void enableAlfrescoType() {
    setVisibleClassType(false);
    setVisibleExpressionType(false);
    setVisibleDelegateExpressionType(false);
    setVisibleAlfrescoType(true);
    implementation = AlfrescoUserTask.ALFRESCO_SCRIPT_TASK_LISTENER;
    setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
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
  
  private void setVisibleAlfrescoType(boolean visible) {
    if(PreferencesUtil.getBooleanPreference(Preferences.ALFRESCO_ENABLE)) {
      alfrescoTypeButton.setSelection(visible);
    }
    scriptText.setVisible(visible);
    scriptLabel.setVisible(visible);
    runAsText.setVisible(visible);
    runAsLabel.setVisible(visible);
    scriptProcessorText.setVisible(visible);
    scriptProcessorLabel.setVisible(visible);
    extensionLabel.setVisible(!visible);
    fieldEditor.setVisible(!visible);
  }
  
  private CLabel createLabel(Composite parent, String text, Control control) {
    CLabel label = new CLabel(parent, SWT.NONE);
    label.setText(text);
    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(control, -HSPACE);
    data.top = new FormAttachment(control, 0, SWT.TOP);
    label.setLayoutData(data);
    label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    return label;
  }
}
