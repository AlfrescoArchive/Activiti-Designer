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
package org.activiti.designer.kickstart.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.AbstractConditionStepListContainer;
import org.activiti.workflow.simple.definition.AbstractStepDefinitionContainer;
import org.activiti.workflow.simple.definition.AbstractStepListContainer;
import org.activiti.workflow.simple.definition.FormStepDefinition;
import org.activiti.workflow.simple.definition.ListConditionStepDefinition;
import org.activiti.workflow.simple.definition.ListStepDefinition;
import org.activiti.workflow.simple.definition.NamedStepDefinition;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

/**
 * Class that (temporarily) adds referenced form-definitions to a {@link WorkflowDefinition}, if present.
 * 
 * @author Frederik Heremans
 */
public class FormReferenceReader {

  private boolean startFormAdded = false;
  private Set<StepDefinition> definitionsTouched = new HashSet<StepDefinition>();
  private WorkflowDefinition definition;
  private IProject project;
  private AlfrescoSimpleWorkflowJsonConverter converter;

  public FormReferenceReader(WorkflowDefinition definition, IProject project) {
    this.definition = definition;
    this.project = project;

    converter = new AlfrescoSimpleWorkflowJsonConverter();
  }

  public Map<String, FormDefinition> getReferencedForms() {
    Map<String, FormDefinition> definitions = new HashMap<String, FormDefinition>();
    try {
      addReferencedForms(definition.getSteps(), definitions, false);
    } catch (IOException ioe) {
      throw new RuntimeException("Error while getting referenced forms: " + ioe);
    }
    return definitions;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addReferencedForms(List<StepDefinition> steps, Map<String, FormDefinition> definitions, boolean applyToModel) throws FileNotFoundException {
    for (StepDefinition step : steps) {
      if (step instanceof FormStepDefinition) {
        FormStepDefinition formStep = (FormStepDefinition) step;
        if (formStep.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
          String formPath = (String) formStep.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
          IFile formFile = project.getFile(new Path(formPath));
          FormDefinition form = converter.readFormDefinition(new FileInputStream(formFile.getLocation().toFile()));

          // Add to result map, if needed
          if(definitions != null) {
            definitions.put(((NamedStepDefinition) formStep).getName(), form);
          }
          
          if(applyToModel) {
            definitionsTouched.add(step);
            formStep.setForm(form);
          }
        }
      } else if(step instanceof AbstractStepListContainer<?>) {
        List<ListStepDefinition<?>> stepList = ((AbstractStepListContainer) step).getStepList();
        for(ListStepDefinition<?> list : stepList) {
          addReferencedForms(list.getSteps(), definitions, applyToModel);
        }
      } else if(step instanceof AbstractConditionStepListContainer<?>) {
        List<ListConditionStepDefinition<?>> stepList = ((AbstractConditionStepListContainer) step).getStepList();
        for(ListConditionStepDefinition<?> list : stepList) {
          addReferencedForms(list.getSteps(), definitions, applyToModel);
        }
      } else if(step instanceof AbstractStepDefinitionContainer<?>) {
        addReferencedForms(((AbstractStepDefinitionContainer) step).getSteps(), definitions, applyToModel);
      }
    }
  }
  
  public FormDefinition getReferenceStartForm() {
    try {
      if (definition.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
        String startFormPath = (String) definition.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
        IFile startFormFile = project.getFile(new Path(startFormPath));
        return converter.readFormDefinition(new FileInputStream(startFormFile.getLocation().toFile()));
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Error while getting referenced start-form: " + ioe);
    }
    return null;
  }

  /**
   * Merges all referenced form-definitions into the workflow-definition. To undo, these changes (to
   * prevent the changes from leaking into the saved model), call {@link #removeFormReferences()}.
   */
  public void mergeFormDefinition() {
    try {
      // Add start-form, if any
      if (definition.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
        definition.setStartFormDefinition(getReferenceStartForm());
      }

      addReferencedForms(definition.getSteps(), null, true);
    } catch (IOException ioe) {
      throw new RuntimeException("Error while merging forms into workflow definition: " + ioe);
    }
  }

  public void removeFormReferences() {
    if (startFormAdded) {
      definition.setStartFormDefinition(null);
    }

    for (StepDefinition step : definitionsTouched) {
      if(step instanceof FormStepDefinition) {
        ((FormStepDefinition) step).setForm(null);
      }
    }
  }
}
