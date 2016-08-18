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
