package org.activiti.designer.kickstart.process.property;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Item browser for string selection.
 * 
 * @author Frederik Heremans
 */
public interface StringItemBrowser {

  /**
   * @param viewer viewer to report selection back to
   * @param parent parent to add control to
   * @return the control that is added to the parent
   */
  public Control getBrowserControl(StringItemSelectionViewer viewer, Composite parent);

}
