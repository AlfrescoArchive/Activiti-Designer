package org.activiti.designer.kickstart.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
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
  private Set<HumanStepDefinition> definitionsTouched = new HashSet<HumanStepDefinition>();
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
      for (StepDefinition step : definition.getSteps()) {
        if (step instanceof HumanStepDefinition) {
          HumanStepDefinition humanStep = (HumanStepDefinition) step;

          if (humanStep.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
            String formPath = (String) humanStep.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
            IFile formFile = project.getFile(new Path(formPath));
            FormDefinition form = converter.readFormDefinition(new FileInputStream(formFile.getLocation().toFile()));
            definitions.put(humanStep.getName(), form);
          }
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Error while getting referenced forms: " + ioe);
    }
    return definitions;
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

      // Add all forms
      for (StepDefinition step : definition.getSteps()) {
        if (step instanceof HumanStepDefinition) {
          HumanStepDefinition humanStep = (HumanStepDefinition) step;

          // TODO: REMOVE
          if (humanStep.getId() == null) {
            humanStep.setId(humanStep.getName().replace(" ", "").toLowerCase());
          }

          if (humanStep.getParameters().containsKey(KickstartConstants.PARAMETER_FORM_REFERENCE)) {
            String formPath = (String) humanStep.getParameters().get(KickstartConstants.PARAMETER_FORM_REFERENCE);
            IFile formFile = project.getFile(new Path(formPath));
            FormDefinition form = converter.readFormDefinition(new FileInputStream(formFile.getLocation().toFile()));
            humanStep.setForm(form);

            definitionsTouched.add(humanStep);
          }
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Error while merging forms into workflow definition: " + ioe);
    }
  }

  public void removeFormReferences() {
    if (startFormAdded) {
      definition.setStartFormDefinition(null);
    }

    for (HumanStepDefinition step : definitionsTouched) {
      step.setForm(null);
    }
  }
}
