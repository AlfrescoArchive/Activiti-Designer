package org.activiti.designer.kickstart.util.widget;

import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FormPropertyDefinitionLabelProvider extends LabelProvider {

  protected Image image;
  
  public FormPropertyDefinitionLabelProvider(Image formImage) {
    this.image = formImage;
  }
  
  @Override
  public Image getImage(Object element) {
    if(element instanceof String) {
      return image;
    }
    return null;
  }

  @Override
  public String getText(Object element) {
    if(element instanceof String) {
      return (String) element;
    } else if(element instanceof FormPropertyDefinition) {
      return ((FormPropertyDefinition) element).getName();
    }
    return null;
  }
}
