package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Validation Code enumeration.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public enum ValidationCode {

  VAL_001, VAL_002, VAL_003, VAL_004, VAL_005, VAL_006;

  private static final String REPLACE_UNDERSCORE = "_";
  private static final String REPLACEMENT_DASH = "-";

  public String getDisplayName() {
    return StringUtils.replace(name(), REPLACE_UNDERSCORE, REPLACEMENT_DASH);
  }

}
