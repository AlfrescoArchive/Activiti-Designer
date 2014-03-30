package org.activiti.designer.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ActivitiSavePreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  private Group overlayGroup;

  private BooleanFieldEditor overlayCheckbox;

  private List<FieldEditor> overlayComponents;

  public ActivitiSavePreferencesPage() {
    super(GRID);
  }

  @Override
  public void createFieldEditors() {

    addField(new BooleanFieldEditor(Preferences.SAVE_IMAGE.getPreferenceId(), "&Create process definition image when saving the diagram",
            getFieldEditorParent()));

    overlayComponents = new ArrayList<FieldEditor>();

    overlayGroup = new Group(getFieldEditorParent(), SWT.BORDER);
    overlayGroup.setText("Image overlay options");

    // this ensures the ColorEditorFields' 2 columns which are forced upwards
    // onto the layout are spanned by the group
    GridDataFactory.defaultsFor(overlayGroup).grab(true, false).span(2, 1).applyTo(overlayGroup);

    overlayCheckbox = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY.getPreferenceId(), "&Overlay process information on the process image",
            overlayGroup);
    addField(overlayCheckbox);

    final String[][] labelsAndValues = new String[][] { { "Top left", "1" }, { "Top right", "2" }, { "Bottom left", "4" }, { "Bottom right", "3" } };

    FieldEditor overlayEditor = new RadioGroupFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_POSITION.getPreferenceId(), "Position the overlay in corner", 2,
            labelsAndValues, overlayGroup, true);
    addOverlayField(overlayEditor);

    overlayEditor = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_KEY.getPreferenceId(), "Include the process' key (BPMN ID)", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_NAMESPACE.getPreferenceId(), "Include the process' namespace", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_FILENAME.getPreferenceId(), "Include the diagram's filename", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_DATE.getPreferenceId(), "Include the date/time of saving the diagram",
            overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new BooleanFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_REVISION.getPreferenceId(),
            "Include the revision from the version control system if available", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new ColorFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_TEXT_COLOR.getPreferenceId(), "Text color", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new ColorFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_BORDER_COLOR.getPreferenceId(), "Border color", overlayGroup);
    addOverlayField(overlayEditor);

    overlayEditor = new ColorFieldEditor(Preferences.SAVE_IMAGE_ADD_OVERLAY_BACKGROUND_COLOR.getPreferenceId(), "Background color", overlayGroup);
    addOverlayField(overlayEditor);

    final Label exportMarshallersLabel = new Label(getFieldEditorParent(), SWT.HORIZONTAL);
    exportMarshallersLabel.setText("When saving diagrams, also save to the following formats:");

    final Collection<ExportMarshaller> marshallers = ExtensionPointUtil.getExportMarshallers();

    if (marshallers.size() > 0) {
      for (final ExportMarshaller exportMarshaller : marshallers) {
        addField(new BooleanFieldEditor(PreferencesUtil.getPreferenceId(exportMarshaller), exportMarshaller.getFormatName(), getFieldEditorParent()));
      }
    }

  }

  private void addOverlayField(FieldEditor overlayEditor) {
    addField(overlayEditor);
    overlayComponents.add(overlayEditor);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    final String property = event.getProperty();
    final Object source = event.getSource();

    if (FieldEditor.VALUE.equals(property)) {
      if (overlayCheckbox == source) {
        processOverlayCheckboxValueChange();
      }
    }
  }

  private void processOverlayCheckboxValueChange() {
    if (overlayCheckbox.getBooleanValue()) {
      enableOverlayComponents();
    } else {
      disableOverlayComponents();
    }

  }

  private void disableOverlayComponents() {
    for (final FieldEditor fieldEditor : overlayComponents) {
      fieldEditor.setEnabled(false, overlayGroup);
    }

  }

  private void enableOverlayComponents() {
    for (final FieldEditor fieldEditor : overlayComponents) {
      fieldEditor.setEnabled(true, overlayGroup);
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
