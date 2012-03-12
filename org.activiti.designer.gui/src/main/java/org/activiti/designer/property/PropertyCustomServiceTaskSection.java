package org.activiti.designer.property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.designer.bpmn2.model.ComplexDataType;
import org.activiti.designer.bpmn2.model.CustomProperty;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.property.extension.FormToolTip;
import org.activiti.designer.property.extension.field.CustomPropertyBooleanChoiceField;
import org.activiti.designer.property.extension.field.CustomPropertyComboboxChoiceField;
import org.activiti.designer.property.extension.field.CustomPropertyDataGridField;
import org.activiti.designer.property.extension.field.CustomPropertyDatePickerField;
import org.activiti.designer.property.extension.field.CustomPropertyField;
import org.activiti.designer.property.extension.field.CustomPropertyMultilineTextField;
import org.activiti.designer.property.extension.field.CustomPropertyPeriodField;
import org.activiti.designer.property.extension.field.CustomPropertyRadioChoiceField;
import org.activiti.designer.property.extension.field.CustomPropertyTextField;
import org.activiti.designer.property.extension.field.FieldInfo;
import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.property.ActivitiPropertySection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class PropertyCustomServiceTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

  public static Font boldFont;
  public static Font italicFont;

  private static final String PROPERTY_REQUIRED_DISPLAY = " (*)";

  private static final int LABEL_COLUMN_WIDTH = 200;
  private static final int HELP_COLUMN_WIDTH = 40;

  private List<CustomServiceTask> customServiceTasks;
  private List<CustomPropertyField> customPropertyFields;

  private Composite parent;
  private Composite workParent;

  @Override
  public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);
    if (boldFont == null) {
      boldFont = new Font(parent.getDisplay(), new FontData("boldFontData", 10, SWT.BOLD));
    }
    if (italicFont == null) {
      italicFont = new Font(parent.getDisplay(), new FontData("italicFontData", 10, SWT.ITALIC));
    }
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
   * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {

    super.setInput(part, selection);

    FormData data;

    customPropertyFields = new ArrayList<CustomPropertyField>();

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

    if (workParent != null) {
      workParent.dispose();
    }

    workParent = factory.createFlatFormComposite(parent);

    customServiceTasks = ExtensionUtil.getCustomServiceTasks(ActivitiUiUtil.getProjectFromDiagram(getDiagram()));

    final ServiceTask serviceTask = getServiceTask();

    if (serviceTask != null) {

      CustomServiceTask targetTask = null;

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (customServiceTask.getId().equals(ExtensionUtil.getCustomServiceTaskId(serviceTask))) {
          targetTask = customServiceTask;
          break;
        }
      }

      if (targetTask != null) {

        final List<Class<CustomServiceTask>> classHierarchy = new ArrayList<Class<CustomServiceTask>>();
        final List<FieldInfo> fieldInfoObjects = new ArrayList<FieldInfo>();

        Class clazz = targetTask.getClass();
        classHierarchy.add(clazz);

        boolean hierarchyOpen = true;
        while (hierarchyOpen) {
          clazz = clazz.getSuperclass();
          if (CustomServiceTask.class.isAssignableFrom(clazz)) {
            classHierarchy.add(clazz);
          } else {
            hierarchyOpen = false;
          }
        }

        for (final Class<CustomServiceTask> currentClass : classHierarchy) {
          for (final Field field : currentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Property.class)) {
              fieldInfoObjects.add(new FieldInfo(field));
            }
          }
        }

        // Sort the list so the fields are in the correct order
        Collections.sort(fieldInfoObjects);

        Control previousAnchor = workParent;

        final CLabel labelNodeName = factory.createCLabel(workParent, targetTask.getName(), SWT.NONE);
        labelNodeName.setFont(boldFont);
        data = new FormData();
        data.top = new FormAttachment(previousAnchor, VSPACE);
        labelNodeName.setLayoutData(data);

        previousAnchor = labelNodeName;

        if (targetTask.getClass().isAnnotationPresent(Help.class)) {
          final Help helpAnnotation = targetTask.getClass().getAnnotation(Help.class);

          final CLabel labelShort = factory.createCLabel(workParent, helpAnnotation.displayHelpShort(), SWT.WRAP);

          data = new FormData();
          data.top = new FormAttachment(previousAnchor, VSPACE);
          labelShort.setLayoutData(data);
          labelShort.setFont(italicFont);

          previousAnchor = labelShort;

          final CLabel labelLong = factory.createCLabel(workParent, helpAnnotation.displayHelpLong(), SWT.WRAP);
          data = new FormData();
          data.top = new FormAttachment(previousAnchor);
          data.left = new FormAttachment(workParent, HSPACE, SWT.LEFT);
          data.right = new FormAttachment(100, -HSPACE);
          labelLong.setLayoutData(data);

          previousAnchor = labelLong;
        }

        for (final FieldInfo fieldInfo : fieldInfoObjects) {

          final Property property = fieldInfo.getPropertyAnnotation();

          Control createdControl = null;
          CustomPropertyField createdCustomPropertyField = null;

          switch (property.type()) {

          case TEXT:
            createdCustomPropertyField = new CustomPropertyTextField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case MULTILINE_TEXT:
            createdCustomPropertyField = new CustomPropertyMultilineTextField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            data.height = 80;
            createdControl.setLayoutData(data);
            break;

          case PERIOD:
            createdCustomPropertyField = new CustomPropertyPeriodField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case BOOLEAN_CHOICE:
            createdCustomPropertyField = new CustomPropertyBooleanChoiceField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case COMBOBOX_CHOICE:
            createdCustomPropertyField = new CustomPropertyComboboxChoiceField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case RADIO_CHOICE:
            createdCustomPropertyField = new CustomPropertyRadioChoiceField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case DATE_PICKER:
            createdCustomPropertyField = new CustomPropertyDatePickerField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          case DATA_GRID:
            createdCustomPropertyField = new CustomPropertyDataGridField(this, serviceTask, fieldInfo.getField());
            createdControl = createdCustomPropertyField.render(workParent, factory, listener);
            data = new FormData();
            data.top = new FormAttachment(previousAnchor, VSPACE);
            data.left = new FormAttachment(0, LABEL_COLUMN_WIDTH);
            data.right = new FormAttachment(100, -HELP_COLUMN_WIDTH);
            createdControl.setLayoutData(data);
            break;

          }

          customPropertyFields.add(createdCustomPropertyField);

          previousAnchor = createdControl;

          // Create a label for the field
          String displayName = property.displayName();
          if (StringUtils.isBlank(property.displayName())) {
            displayName = fieldInfo.getFieldName();
          }

          if (property.required()) {
            displayName += PROPERTY_REQUIRED_DISPLAY;
          }

          displayName += ": ";

          final CLabel propertyLabel = factory.createCLabel(workParent, displayName); //$NON-NLS-1$
          data = new FormData();
          data.top = new FormAttachment(createdControl, 0, SWT.TOP);
          data.left = new FormAttachment(0, 0);
          data.right = new FormAttachment(createdControl, -HSPACE);
          propertyLabel.setLayoutData(data);

          // Create a help button for the field
          final Help help = fieldInfo.getHelpAnnotation();
          if (help != null) {
            final Button propertyHelp = factory.createButton(workParent, "", SWT.BUTTON1);
            propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP));

            // create a tooltip
            final ToolTip tooltip = new FormToolTip(propertyHelp, String.format("Help for field %s",
                    property.displayName().equals("") ? fieldInfo.getFieldName() : property.displayName()), help.displayHelpShort(), help.displayHelpLong());
            tooltip.setHideOnMouseDown(false);

            data = new FormData();
            data.top = new FormAttachment(createdControl, 0, SWT.TOP);
            data.left = new FormAttachment(createdControl, 0);
            propertyHelp.setLayoutData(data);
            propertyHelp.addMouseListener(new MouseListener() {

              @Override
              public void mouseUp(MouseEvent e) {
              }

              @Override
              public void mouseDown(MouseEvent e) {
                tooltip.show(new Point(0, 0));
              }

              @Override
              public void mouseDoubleClick(MouseEvent e) {
              }
            });
          }
        }
      }
    }
    this.workParent.getParent().getParent().layout(true, true);
  }

  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo == null)
        return;
    }

    for (final CustomPropertyField field : customPropertyFields) {
      field.refresh();
    }

    // Perform validation now the fields are populated
    validateFields();
  }

  private FocusListener listener = new FocusListener() {

    @Override
    public void focusLost(FocusEvent e) {
      validateFields();
      storeFieldsToModel();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

  };

  /**
   * Validates all fields.
   */
  private void validateFields() {

    for (final CustomPropertyField field : customPropertyFields) {
      field.validate();
    }

  }

  /**
   * Stores all fields to the model so they can be persisted.
   */
  private void storeFieldsToModel() {

    final Runnable runnable = new Runnable() {

      /**
       * Stores a value to the CustomProperty with the key provided. Provide a
       * null simpleValue or complexValue to indicate which value type needs to
       * be stored.
       */
      private final void storeField(final ServiceTask task, final String key, final String simpleValue, final ComplexDataType complexValue) {

        CustomProperty property = ExtensionUtil.getCustomProperty(task, key);

        if (property == null) {
          property = new CustomProperty();
          task.getCustomProperties().add(property);
        }

        property.setId(ExtensionUtil.wrapCustomPropertyId(task, key));
        property.setName(key);
        if (simpleValue != null) {
          property.setSimpleValue(simpleValue);
        } else {
          property.setComplexValue(complexValue);
        }
      }

      public void run() {

        Object bo = getBusinessObject(getSelectedPictogramElement());
        if (bo == null) {
          return;
        }

        ServiceTask task = (ServiceTask) bo;
        for (final CustomPropertyField field : customPropertyFields) {

          if (!field.isComplex()) {
            storeField(task, field.getCustomPropertyId(), field.getSimpleValue(), null);
          } else {
            storeField(task, field.getCustomPropertyId(), null, field.getComplexValue());
          }
        }
      }
    };
    runModelChange(runnable);
  }

  public void runModelChange(final Runnable runnable) {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo instanceof ServiceTask) {
        DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
        TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
        ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
      }
    }
  }

  private ServiceTask getServiceTask() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo != null && bo instanceof ServiceTask) {
        return (ServiceTask) bo;
      }
    }
    return null;
  }

}