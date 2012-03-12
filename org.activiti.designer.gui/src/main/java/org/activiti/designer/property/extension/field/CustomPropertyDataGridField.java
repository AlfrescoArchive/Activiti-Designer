/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.property.extension.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.designer.Activator;
import org.activiti.designer.PluginImage;
import org.activiti.designer.bpmn2.model.ComplexDataType;
import org.activiti.designer.bpmn2.model.DataGrid;
import org.activiti.designer.bpmn2.model.DataGridField;
import org.activiti.designer.bpmn2.model.DataGridRow;
import org.activiti.designer.bpmn2.model.ServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.DataGridProperty;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.validator.RequiredFieldValidator;
import org.activiti.designer.property.PropertyCustomServiceTaskSection;
import org.activiti.designer.property.custom.MultilineTextDialog;
import org.activiti.designer.property.custom.PeriodDialog;
import org.activiti.designer.property.extension.FormToolTip;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class CustomPropertyDataGridField extends AbstractCustomPropertyField {

  private static final int DEFAULT_ADDITIONAL_COLUMNS = 1;
  private static final int ORDERABLE_ADDITIONAL_COLUMNS = 3;

  private Composite parent;
  private Composite dataGridControl;
  private FocusListener sharedFocusListener;

  private DataGrid dataGrid;

  private DataGridProperty dataGridAnnotation;
  private List<FieldInfo> gridFields;

  private TabbedPropertySheetWidgetFactory factory;

  public CustomPropertyDataGridField(final PropertyCustomServiceTaskSection section, final ServiceTask serviceTask, final Field field) {
    super(section, serviceTask, field);
  }

  @Override
  public PropertyType getPrimaryPropertyType() {
    return PropertyType.DATA_GRID;
  }

  @Override
  public void refresh() {

    if (dataGrid == null) {
      final ComplexDataType value = getComplexValueFromModel();
      if (value == null) {
        dataGrid = new DataGrid();
      } else {
        dataGrid = (DataGrid) value;
      }
    }
    refreshGrid();
  }

  @Override
  public boolean isComplex() {
    return true;
  }

  @Override
  public ComplexDataType getComplexValue() {
    return dataGrid;
  }

  @Override
  public Composite render(Composite parent, TabbedPropertySheetWidgetFactory factory, final FocusListener listener) {

    Composite result = factory.createFlatFormComposite(parent);

    this.parent = result;
    this.factory = factory;
    this.sharedFocusListener = listener;

    recreateDataGridControl();

    return result;
  }

  private void recreateDataGridControl() {

    if (dataGridControl != null) {
      dataGridControl.dispose();
    }

    dataGridControl = factory.createFlatFormComposite(parent);

    if (gridFields == null && List.class.isAssignableFrom(getField().getType())) {

      gridFields = new ArrayList<FieldInfo>();

      dataGridAnnotation = getField().getAnnotation(DataGridProperty.class);
      if (dataGridAnnotation != null) {

        final Class< ? extends Object> itemClass = dataGridAnnotation.itemClass();

        final Field[] itemClassDeclaredFields = itemClass.getDeclaredFields();

        for (final Field itemClassField : itemClassDeclaredFields) {
          if (itemClassField.isAnnotationPresent(Property.class)) {
            try {
              final FieldInfo info = new FieldInfo(itemClassField);
              gridFields.add(info);
            } catch (IllegalArgumentException e) {
              // fail silently, field doesn't contain required info.
            }
          }
        }

        // Sort the list
        Collections.sort(gridFields);
      }
    }

    // Set the layout for the contents of the listParent to a
    // grid
    int additionalColumns = DEFAULT_ADDITIONAL_COLUMNS;
    if (dataGridAnnotation.orderable()) {
      additionalColumns += ORDERABLE_ADDITIONAL_COLUMNS;
    }
    final GridLayout layout = new GridLayout(gridFields.size() + additionalColumns, false);
    dataGridControl.setLayout(layout);

    FormData dataGridData = new FormData();
    dataGridData.left = new FormAttachment();
    dataGridData.right = new FormAttachment(100);
    dataGridControl.setLayoutData(dataGridData);

  }
  private void refreshGrid() {
    recreateDataGridControl();
    createHeader();
    createBody();
    createFooter();
    parent.getParent().getParent().getParent().layout(true, true);
  }

  private void createHeader() {

    if (dataGridAnnotation.orderable()) {
      // Empty label for the header of order column
      final CLabel orderColumnLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);
    }

    // Create a row with headers
    for (final FieldInfo fieldInfo : gridFields) {

      final Property propertyAnnotation = fieldInfo.getPropertyAnnotation();

      Composite headerGroup = factory.createFlatFormComposite(dataGridControl);

      final GridLayout headerLayout = new GridLayout(2, false);
      headerGroup.setLayout(headerLayout);

      final CLabel headerLabel = factory.createCLabel(headerGroup, propertyAnnotation.displayName());
      GridData headerLabelData = new GridData();
      headerLabelData.grabExcessHorizontalSpace = true;
      headerLabelData.horizontalAlignment = GridData.FILL;
      headerLabel.setLayoutData(headerLabelData);
      headerLabel.setFont(PropertyCustomServiceTaskSection.boldFont);

      final Help help = getHelpAnnotation();
      if (help != null) {
        final Button propertyHelp = factory.createButton(headerGroup, "", SWT.BUTTON1);
        propertyHelp.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP));

        GridData headerButtonData = new GridData();
        headerButtonData.horizontalAlignment = SWT.END;
        headerLabel.setLayoutData(headerButtonData);

        // create a tooltip
        final ToolTip tooltip = new FormToolTip(propertyHelp, String.format("Help for field %s",
                propertyAnnotation.displayName().equals("") ? fieldInfo.getFieldName() : propertyAnnotation.displayName()), help.displayHelpShort(),
                help.displayHelpLong());
        tooltip.setHideOnMouseDown(false);

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

      final GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      headerGroup.setLayoutData(gridData);

    }

    if (dataGridAnnotation.orderable()) {
      // Empty label for the header of the two ordering columns
      final CLabel upColumnLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);

      final CLabel downColumnLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);
    }

    // Empty label for the header of the final column
    final CLabel finalColumnLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);
  }

  private void createBody() {
    if (dataGrid.getRows().size() == 0) {
      final CLabel label = factory.createCLabel(dataGridControl, "There are no rows at the moment.", SWT.WRAP);
      //
    } else {

      // Create rows with fields
      for (final DataGridRow dataGridRow : dataGrid.getRows()) {

        if (dataGridAnnotation.orderable()) {
          final CLabel orderLabel = factory.createCLabel(dataGridControl, dataGridRow.getIndex() + ". ", SWT.WRAP);
        }

        for (final FieldInfo fieldInfo : gridFields) {

          final DataGridField field = getFieldByName(dataGridRow, fieldInfo.getFieldName());

          final Property propertyAnnotation = fieldInfo.getPropertyAnnotation();

          switch (propertyAnnotation.type()) {
          case TEXT:

            final Text propertyText = factory.createText(dataGridControl, "", SWT.BORDER_SOLID);
            propertyText.setEnabled(true);
            propertyText.setText(getSimpleValueOrDefault(field, propertyAnnotation));
            propertyText.addFocusListener(new GridFieldListener(propertyText, field));
            propertyText.addFocusListener(sharedFocusListener);

            if (propertyAnnotation.required()) {
              addFieldValidator(propertyText, RequiredFieldValidator.class);
            }

            if (propertyAnnotation.fieldValidator() != null) {
              addFieldValidator(propertyText, propertyAnnotation.fieldValidator());
            }

            final GridData textGridData = new GridData();
            textGridData.grabExcessHorizontalSpace = true;
            textGridData.horizontalAlignment = GridData.FILL;
            propertyText.setLayoutData(textGridData);

            break;
          case MULTILINE_TEXT:

            final Text multiControl = factory.createText(dataGridControl, "", SWT.BORDER_SOLID);

            multiControl.setText(getSimpleValueOrDefault(field, propertyAnnotation));
            multiControl.setToolTipText("Double-click this field to edit");
            multiControl.setEnabled(true);

            final GridData multiGridData = new GridData();
            multiGridData.grabExcessHorizontalSpace = false;
            multiGridData.horizontalAlignment = GridData.FILL;
            multiControl.setLayoutData(multiGridData);

            multiControl.addFocusListener(new GridFieldListener(multiControl, field));
            multiControl.addFocusListener(sharedFocusListener);

            if (propertyAnnotation.required()) {
              addFieldValidator(multiControl, RequiredFieldValidator.class);
            }

            if (propertyAnnotation.fieldValidator() != null) {
              addFieldValidator(multiControl, propertyAnnotation.fieldValidator());
            }

            multiControl.addMouseListener(new MouseListener() {

              @Override
              public void mouseDoubleClick(MouseEvent e) {
                MultilineTextDialog dialog = new MultilineTextDialog(dataGridControl.getDisplay().getActiveShell(), getHelpAnnotation(), multiControl.getText());
                // open dialog and wait for return status
                // code.
                if (dialog.open() == IStatus.OK) {
                  // If user clicks ok display message box
                  String value = dialog.getValue();
                  multiControl.setText(value);
                } else {

                }
              }

              @Override
              public void mouseDown(MouseEvent e) {
              }

              @Override
              public void mouseUp(MouseEvent e) {
              }

            });
            break;
          case PERIOD:
            final Text periodControl = factory.createText(dataGridControl, "", SWT.BORDER_SOLID);

            periodControl.setText(getSimpleValueOrDefault(field, propertyAnnotation));
            periodControl.setToolTipText("Double-click this field to edit");
            final String fieldName = propertyAnnotation.displayName();
            periodControl.setEnabled(true);

            final GridData periodGridData = new GridData();
            periodGridData.grabExcessHorizontalSpace = true;
            periodGridData.horizontalAlignment = GridData.FILL;
            periodControl.setLayoutData(periodGridData);

            periodControl.addFocusListener(new GridFieldListener(periodControl, field));
            periodControl.addFocusListener(sharedFocusListener);

            if (propertyAnnotation.required()) {
              addFieldValidator(periodControl, RequiredFieldValidator.class);
            }

            if (propertyAnnotation.fieldValidator() != null) {
              addFieldValidator(periodControl, propertyAnnotation.fieldValidator());
            }

            periodControl.addMouseListener(new MouseListener() {

              @Override
              public void mouseDoubleClick(MouseEvent e) {
                PeriodDialog dialog = new PeriodDialog(dataGridControl.getDisplay().getActiveShell(), getHelpAnnotation(), periodControl.getText());
                // open dialog and wait for return status code.
                if (dialog.open() == IStatus.OK) {
                  // If user clicks ok display message box
                  String value = dialog.getValue();
                  periodControl.setText(value);
                } else {

                }
              }

              @Override
              public void mouseDown(MouseEvent e) {
              }

              @Override
              public void mouseUp(MouseEvent e) {
              }

            });

            break;
          }
        }

        if (dataGridAnnotation.orderable()) {

          // if the row is not the first row
          if (dataGridRow.getIndex() > 1) {
            final Button orderUpButton = factory.createButton(dataGridControl, "", SWT.BUTTON1);
            orderUpButton.setImage(Activator.getImage(PluginImage.ACTION_UP));
            orderUpButton.addMouseListener(new MouseListener() {

              @Override
              public void mouseUp(MouseEvent e) {
                final Runnable runnable = new Runnable() {

                  public void run() {

                    final int currentPosition = dataGridRow.getIndex();

                    if (currentPosition != 1) {
                      for (final DataGridRow currentDataGridRow : dataGrid.getRows()) {
                        if (currentDataGridRow.equals(dataGridRow)) {
                          currentDataGridRow.setIndex(currentDataGridRow.getIndex() - 1);
                        } else if (currentDataGridRow.getIndex() == currentPosition - 1) {
                          currentDataGridRow.setIndex(currentDataGridRow.getIndex() + 1);
                        }
                      }
                      ECollections.sort((EList<DataGridRow>) dataGrid.getRows(), new DataGridRowComparator());
                    }
                  }
                };
                runModelChange(runnable);

                refreshGrid();
              }

              @Override
              public void mouseDown(MouseEvent e) {
              }

              @Override
              public void mouseDoubleClick(MouseEvent e) {
              }
            });
          } else {
            final CLabel placeHolderLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);
          }

          // if the row is not the last row
          if (dataGridRow.getIndex() < dataGrid.getRows().size()) {
            final Button orderDownButton = factory.createButton(dataGridControl, "", SWT.BUTTON1);
            orderDownButton.setImage(Activator.getImage(PluginImage.ACTION_DOWN));
            orderDownButton.addMouseListener(new MouseListener() {

              @Override
              public void mouseUp(MouseEvent e) {
                final Runnable runnable = new Runnable() {

                  public void run() {
                    final int currentPosition = dataGridRow.getIndex();

                    if (currentPosition != dataGrid.getRows().size()) {
                      for (final DataGridRow currentDataGridRow : dataGrid.getRows()) {
                        if (currentDataGridRow.equals(dataGridRow)) {
                          currentDataGridRow.setIndex(currentDataGridRow.getIndex() + 1);
                        } else if (currentDataGridRow.getIndex() == currentPosition + 1) {
                          currentDataGridRow.setIndex(currentDataGridRow.getIndex() - 1);
                        }
                      }
                      ECollections.sort((EList<DataGridRow>) dataGrid.getRows(), new DataGridRowComparator());
                    }
                  }
                };
                runModelChange(runnable);
                refreshGrid();
              }

              @Override
              public void mouseDown(MouseEvent e) {
              }

              @Override
              public void mouseDoubleClick(MouseEvent e) {
              }
            });
          } else {
            final CLabel placeHolderLabel = factory.createCLabel(dataGridControl, "", SWT.WRAP);
          }

        }

        final Button deleteButton = factory.createButton(dataGridControl, "", SWT.BUTTON1);
        deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
        deleteButton.addMouseListener(new MouseListener() {

          @Override
          public void mouseUp(MouseEvent e) {
            final Runnable runnable = new Runnable() {

              public void run() {

                final int currentPosition = dataGridRow.getIndex();

                boolean success = dataGrid.getRows().remove(dataGridRow);

                if (success) {
                  for (final DataGridRow currentDataGridRow : dataGrid.getRows()) {
                    if (currentDataGridRow.getIndex() > currentPosition) {
                      currentDataGridRow.setIndex(currentDataGridRow.getIndex() - 1);
                    }
                  }
                  ECollections.sort((EList<DataGridRow>) dataGrid.getRows(), new DataGridRowComparator());
                }
              }
            };
            runModelChange(runnable);
            refreshGrid();
          }

          @Override
          public void mouseDown(MouseEvent e) {
          }

          @Override
          public void mouseDoubleClick(MouseEvent e) {
          }
        });

      }
    }
  }
  private String getSimpleValueOrDefault(DataGridField field, Property propertyAnnotation) {
    String value = field.getValue();
    if (value == null) {
      if (StringUtils.isNotBlank(propertyAnnotation.defaultValue())) {
        value = propertyAnnotation.defaultValue();
      } else {
        value = "";
      }
    }

    return value;
  }

  private void createFooter() {
    final Button addButton = factory.createButton(dataGridControl, "Add item", SWT.BUTTON1);
    addButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
    final GridData gridData = new GridData();
    gridData.horizontalSpan = gridFields.size() + 1;
    addButton.setLayoutData(gridData);

    addButton.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(MouseEvent e) {
        final DataGridRow newRow = new DataGridRow();
        newRow.setIndex(dataGrid.getRows().size() + 1);

        for (final FieldInfo fieldInfo : gridFields) {
          final DataGridField newField = new DataGridField();
          newField.setName(fieldInfo.getFieldName());
          newField.setValue(null);
          newRow.getFields().add(newField);
        }

        final Runnable runnable = new Runnable() {

          public void run() {
            dataGrid.getRows().add(newRow);
          }
        };
        runModelChange(runnable);
        refreshGrid();
      }

      @Override
      public void mouseDown(MouseEvent e) {
      }

      @Override
      public void mouseDoubleClick(MouseEvent e) {
      }
    });
  }

  private DataGridField getFieldByName(final DataGridRow dataGridRow, final String name) {
    DataGridField result = null;
    final List<DataGridField> fields = dataGridRow.getFields();
    for (final DataGridField field : fields) {
      if (StringUtils.equalsIgnoreCase(name, field.getName())) {
        result = field;
        break;
      }
    }
    return result;
  }

  private class GridFieldListener implements FocusListener {

    private Text textControl;
    private DataGridField dataGridField;

    public GridFieldListener(final Text textControl, final DataGridField dataGridField) {
      this.textControl = textControl;
      this.dataGridField = dataGridField;
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {

      final Runnable runnable = new Runnable() {

        @Override
        public void run() {
          dataGridField.setValue(textControl.getText());
        }

      };
      runModelChange(runnable);
    }
  }

  private class DataGridRowComparator implements Comparator<DataGridRow> {

    @Override
    public int compare(DataGridRow thisDataGridRow, DataGridRow otherDataGridRow) {
      return new Integer(thisDataGridRow.getIndex()).compareTo(new Integer(otherDataGridRow.getIndex()));
    }

  }
}
