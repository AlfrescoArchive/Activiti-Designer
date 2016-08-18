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
package org.acme.servicetasks;

import org.activiti.designer.integration.annotation.Help;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.servicetask.PropertyType;

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
