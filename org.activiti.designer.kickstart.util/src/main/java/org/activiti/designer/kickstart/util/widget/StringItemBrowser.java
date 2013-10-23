package org.activiti.designer.kickstart.util.widget;

import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Item browser for string selection.
 * 
 * @author Frederik Heremans
 */
public interface StringItemBrowser {

  /**
   * @param viewer listener to report selection back to
   * @param parent parent to add control to
   * @return the control that is added to the parent
   */
  public Control getBrowserControl(PropertyChangeListener listener, Composite parent);

}
