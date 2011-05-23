package org.activiti.designer.preferences;

import java.util.Collection;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ActivitiSavePreferencesPage() {
    super(GRID);
  }

  public void createFieldEditors() {

    ExportMarshaller bpmnMarshaller = ExtensionPointUtil.getExportMarshaller(ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME);

    if (bpmnMarshaller != null) {
      Group group = new Group(getFieldEditorParent(), SWT.BORDER);
      group.setText("Activiti BPMN 2.0");
      group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

      addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(bpmnMarshaller), "Automatically save to Activiti &BPMN 2.0 format when saving diagrams",
              group));

      addField(new BooleanFieldEditor(Preferences.VALIDATE_ACTIVITI_BPMN_FORMAT.getPreferenceId(),
              "&Validate diagram before saving to Activiti BPMN 2.0 format", group));

      addField(new RadioGroupFieldEditor(Preferences.SKIP_BPMN_MARSHALLER_ON_VALIDATION_FAILURE.getPreferenceId(), "Handling validation failures", 1,
              new String[][] { { "Skip saving to BPMN 2.0 format if validation fails", ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_SKIP },
                  { "Attempt to save to BPMN 2.0 format anyway if validation fails", ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_VALIDATION_ATTEMPT } }, group));
    }

    final Collection<ExportMarshaller> marshallers = ExtensionPointUtil.getExportMarshallers();

    if (marshallers.size() > 0) {
      Group group = new Group(getFieldEditorParent(), SWT.BORDER);
      group.setText("Additional Export formats");
      group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

      for (final ExportMarshaller exportMarshaller : marshallers) {
        if (!StringUtils.equals(exportMarshaller.getMarshallerName(), ActivitiBPMNDiagramConstants.BPMN_MARSHALLER_NAME)) {
          addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(exportMarshaller), "Automatically save to " + exportMarshaller.getFormatName()
                  + " format when saving diagrams", group));
        }
      }
    }

  }
  @Override
  public void init(IWorkbench workbench) {
    IPreferenceStore prefStore = ActivitiPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(prefStore);
    setDescription("Set preferences used while saving Activiti Diagrams");
    setTitle("Activiti Designer Save Preferences");
  }

}
