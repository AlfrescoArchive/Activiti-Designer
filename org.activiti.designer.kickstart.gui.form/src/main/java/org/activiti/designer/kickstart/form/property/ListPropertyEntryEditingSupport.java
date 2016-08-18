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