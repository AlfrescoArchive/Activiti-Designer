package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.definition.form.ListPropertyEntry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public abstract class ListPropertyEntryEditingSupport extends EditingSupport {

  private final TableViewer viewer;

  public ListPropertyEntryEditingSupport(TableViewer viewer) {
    super(viewer);
    this.viewer = viewer;
  }

  @Override
  protected CellEditor getCellEditor(Object element) {
    return new TextCellEditor(viewer.getTable());
  }

  @Override
  protected boolean canEdit(Object element) {
    return true;
  }

  @Override
  protected Object getValue(Object element) {
    return getValueFromEntry((ListPropertyEntry)element);
  }
  
  @Override
  protected void setValue(Object element, Object value) {
    setValueInEntry((ListPropertyEntry) element, value);
  }

  protected abstract Object getValueFromEntry(ListPropertyEntry entry);
  protected abstract void setValueInEntry(ListPropertyEntry entry, Object value);
} 