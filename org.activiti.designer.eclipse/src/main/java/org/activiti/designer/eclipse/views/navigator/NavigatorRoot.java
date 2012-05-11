package org.activiti.designer.eclipse.views.navigator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.PlatformObject;

public class NavigatorRoot extends PlatformObject {

  private static NavigatorRoot instance;

  public Set<ParentBean> getParentBeans() {
    return new HashSet<ParentBean>();
  }

  public static final NavigatorRoot getInstance() {
    if (instance == null) {
      instance = new NavigatorRoot();
    }
    return instance;
  }

}
