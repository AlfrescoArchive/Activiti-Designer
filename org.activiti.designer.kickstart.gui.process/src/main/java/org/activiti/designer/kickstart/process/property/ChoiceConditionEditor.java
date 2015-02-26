package org.activiti.designer.kickstart.process.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.activiti.workflow.simple.definition.ConditionDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Frederik Heremans
 */
public class ChoiceConditionEditor {
  
  protected static final String EQUALS = "Equals";
  protected static final String NOT_EQUALS = "Not equal";
  protected static final String GREATER_THAN = "Greater than";
  protected static final String GREATER_THAN_OR_EQUALS = "Greater than or equals";
  protected static final String LESS_THAN = "Less than";
  protected static final String LESS_THAN_OR_EQUALS = "Less than or equals";
  
  protected static final String EQUALS_OPERATOR = "==";
  protected static final String NOT_EQUALS_OPERATOR = "!=";
  protected static final String GREATER_THAN_OPERATOR = ">";
  protected static final String GREATER_THAN_OR_EQUALS_OPERATOR = ">=";
  protected static final String LESS_THAN_OPERATOR = "<";
  protected static final String LESS_THAN_OR_EQUALS_OPERATOR = "<=";
  
  protected ConditionDefinition definition;
  protected PropertyChangeListener listener;
  
  protected Composite composite;
  protected Text leftText;
  protected Text rightText;
  protected PropertyItemBrowser leftBrowser;
  protected PropertyItemBrowser rightBrowser;
  protected Combo operatorSelection;
  
  public ChoiceConditionEditor(Composite parent, PropertyChangeListener listener) {
    this.listener = listener;
    
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(3, false));
    composite.setBackground(parent.getBackground());
    
    Composite leftComposite = new Composite(composite, SWT.NONE);
    leftComposite.setBackground(parent.getBackground());
    leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    leftComposite.setLayout(new GridLayout(2, false));
    leftText = new Text(leftComposite, SWT.BORDER);
    leftText.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if(definition != null) {
          leftText.setText(getSafeText(definition.getLeftOperand()));
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        flushLeftValue();
      }
    });
    leftText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    leftBrowser = new PropertyItemBrowser();
    leftBrowser.setItemfilter(new ConcretePropertyItemFilter());
    leftBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent changeEvent) {
        flushLeftBrowserValue(changeEvent.getNewValue());
      }
    }, leftComposite);
    
    operatorSelection = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
    operatorSelection.setItems(new String[] {EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS});
    operatorSelection.setText(EQUALS);
    
    operatorSelection.addSelectionListener(new SelectionAdapter() {
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        flushOperatorValue();
      }
    });
    
    Composite rightComposite = new Composite(composite, SWT.NONE);
    rightComposite.setBackground(parent.getBackground());
    rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    rightComposite.setLayout(new GridLayout(2, false));
    rightText = new Text(rightComposite, SWT.BORDER);
    rightText.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (definition != null) {
          rightText.setText(getSafeText(definition.getRightOperand()));
        }
      }
      
      @Override
      public void focusLost(FocusEvent e) {
        flushRightValue();
      }
    });
    rightText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    rightBrowser = new PropertyItemBrowser();
    rightBrowser.setItemfilter(new ConcretePropertyItemFilter());
    rightBrowser.getBrowserControl(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent changeEvent) {
        flushRightBrowserValue(changeEvent.getNewValue());
      }
    }, rightComposite);
  }
  
  public Composite getComposite() {
    return composite;
  }
  
  protected void flushRightBrowserValue(Object object) {
    if(object != null) {
      rightText.setText((String) object);
    } else {
      rightText.setText("");
    }
   
    flushRightValue();
   
  }
  
  protected void flushRightValue() {
    String newValue = getSafeModelValue(rightText.getText());
    if(!StringUtils.equals(newValue, definition.getRightOperand())) {
      definition.setRightOperand((String) newValue);
      notifyListener();
    }
  }

  protected void flushLeftBrowserValue(Object object) {
    if(object != null) {
      leftText.setText((String) object);
    } else {
      leftText.setText("");
    }
    flushLeftValue();
  }
  

  protected void flushLeftValue() {
    String newValue = getSafeModelValue(leftText.getText());
    if(!StringUtils.equals(newValue, definition.getLeftOperand())) {
      definition.setLeftOperand((String) newValue);
      notifyListener();
    }
  }
  
  protected void flushOperatorValue() {
    if(definition != null) {
      String operator = getOperatorFromSelection();
      if(!operator.equals(definition.getOperator())) {
        definition.setOperator(operator);
        notifyListener();
      }
    }
  }

  protected void notifyListener() {
    if(listener != null) {
      listener.propertyChange(new PropertyChangeEvent(this, "condition", definition, definition));
    }
  }

  public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
    leftBrowser.setWorkflowDefinition(workflowDefinition);
    rightBrowser.setWorkflowDefinition(workflowDefinition);
  }

  public void setProject(IProject project) {
    leftBrowser.setProject(project);
    rightBrowser.setProject(project);
  }
  
  public void setConditionDefinition(ConditionDefinition definition) {
    this.definition = definition;
    if(definition != null) {
      leftText.setText(getSafeText(definition.getLeftOperand()));
      rightText.setText(getSafeText(definition.getRightOperand()));
      operatorSelection.setText(getSafeText(getOperatorSelectionFromDefinition(definition)));
    } else {
      leftText.setText("");
      rightText.setText("");
      operatorSelection.setText(EQUALS_OPERATOR);
    }
  }

  protected String getOperatorSelectionFromDefinition(ConditionDefinition def) {
    String operand = EQUALS_OPERATOR;
    
    if(def != null && def.getOperator() != null) {
      if(NOT_EQUALS_OPERATOR.equals(def.getOperator())) {
        operand = NOT_EQUALS;
      } else if(GREATER_THAN_OPERATOR.equals(def.getOperator())) {
        operand = GREATER_THAN;
      } else if(GREATER_THAN_OR_EQUALS_OPERATOR.equals(def.getOperator())) {
        operand = GREATER_THAN_OR_EQUALS;
      } else if(LESS_THAN_OPERATOR.equals(def.getOperator())) {
        operand = LESS_THAN;
      } else  if(LESS_THAN_OR_EQUALS_OPERATOR.equals(def.getOperator())) {
        operand = LESS_THAN_OR_EQUALS;
      }
    }
    return operand;
  }
  
  protected String getOperatorFromSelection() {
    String operand = EQUALS_OPERATOR;
    String selected = operatorSelection.getText();
    if(selected != null) {
      if(NOT_EQUALS.equals(selected)) {
        operand = NOT_EQUALS_OPERATOR;
      } else if(GREATER_THAN.equals(selected)) {
        operand = GREATER_THAN_OPERATOR;
      } else if(GREATER_THAN_OR_EQUALS.equals(selected)) {
        operand = GREATER_THAN_OR_EQUALS_OPERATOR;
      } else if(LESS_THAN.equals(selected)) {
        operand = LESS_THAN_OPERATOR;
      } else  if(LESS_THAN_OR_EQUALS.equals(selected)) {
        operand = LESS_THAN_OR_EQUALS_OPERATOR;
      }
    }
    return operand;
  }
  
  protected String getSafeText(String object) {
    if(object == null) {
      return "";
    }
    return object;
  }
  
  protected String getSafeModelValue(String object) {
    if("".equals(object)) {
      return null;
    }
    return object;
  }
}