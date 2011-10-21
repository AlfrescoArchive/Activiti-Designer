package org.acme.servicetasks;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;

/**
 * Defines fields for a data grid managing AccountManagers.
 * 
 * @author John Doe
 * @since 1.0.0
 * @version 1
 */
public class AccountManager {

  @Property(type = PropertyType.TEXT, displayName = "First name", required = true)
  @Help(displayHelpShort = "First name", displayHelpLong = "The person's first name")
  private String firstName;

  @Property(type = PropertyType.TEXT, displayName = "Last name", required = true)
  @Help(displayHelpShort = "Last name", displayHelpLong = "The person's last name")
  private String lastName;

  @Property(type = PropertyType.PERIOD, displayName = "Authorization period", required = true, defaultValue = "0y 2mo 0w 0d 0h 0m 0s")
  @Help(displayHelpShort = "Authorization period", displayHelpLong = "The person's authorization period")
  private String authorizationPeriod;

}
