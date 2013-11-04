package org.activiti.designer.kickstart.util.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Wrapper around a {@link Composite} that allows displaying a single widget at a time of a list of
 * configured widgets, with the posibility to switch between them.
 * 
 * @author Frederik Heremans
 */
public class ToggleContainerViewer {

  protected Composite composite;
  protected Map<String, ToggleContainerViewer.ToggleContainerChild> children;
  protected boolean pack = true;
  
 
  public ToggleContainerViewer(Composite parent) {
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());
    composite.setBackground(parent.getBackground());
    children = new HashMap<String, ToggleContainerViewer.ToggleContainerChild>();
  }
  
  public void setPack(boolean pack) {
    this.pack = pack;
  }
  
  public void showChild(String key) {
    for(Entry<String, ToggleContainerChild> entry : children.entrySet()) {
      if(key.equals(entry.getKey())) {
        entry.getValue().showControl();
      } else {
        entry.getValue().hideControl();
      }
    }
    
    // Force the gridlayout to re-layout
    composite.layout(true);
    if(pack) {
      composite.getParent().getParent().getParent().pack(true);
    } else {
      composite.getParent().layout(true);
    }
  }
  
  public void hideAll() {
    for(Entry<String, ToggleContainerChild> entry : children.entrySet()) {
        entry.getValue().hideControl();
    }
    
    // Force the gridlayout to re-layout
    composite.layout(true);
    if(pack) {
      composite.getParent().getParent().getParent().pack(true);
    } else {
      composite.getParent().layout(true);
    }
  }
  
  public void addControl(String key, Control control) {
    ToggleContainerChild newChild = new ToggleContainerChild(control);
    children.put(key, newChild);
  }
  
  public Composite getComposite() {
    return composite;
  }
  
  private class ToggleContainerChild {
    
    private Control control;
    private GridData gridData;
    
    public ToggleContainerChild(Control control) {
      this.control = control;
      this.gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      this.gridData.exclude = true;
      control.setLayoutData(gridData);
      
      control.setVisible(false);
    }
    
    public void hideControl() {
      gridData.exclude = true;
      control.setVisible(false);
    }
    
    public void showControl() {
      gridData.exclude = false;
      gridData.widthHint = composite.getSize().y;
      control.setVisible(true);
      if(pack) {
        control.pack(true);
      }
    }
  }    

}
