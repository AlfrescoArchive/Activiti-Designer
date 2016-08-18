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
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;

/**
 * Example of a CustomServiceTask that uses a Java Delegate Expression for the
 * Runtime annotation.
 */
@Runtime(javaDelegateExpression = "${echoJavaDelegateExpressionBean}")
@Help(displayHelpShort = "Echos the customerName", displayHelpLong = "Echoes the process variable customerName")
public class RuntimeJavaDelegateExpressionEchoTask extends AbstractCustomServiceTask {

  @Property(type = PropertyType.TEXT, displayName = "Prefix", required = true, defaultValue = "Customer name")
  @Help(displayHelpShort = "Provide a prefix for the echo")
  private String echoPrefix;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.integration.servicetask.AbstractCustomServiceTask
   * #contributeToPaletteDrawer()
   */
  @Override
  public String contributeToPaletteDrawer() {
    return "Acme Corporation";
  }

  @Override
  public String getName() {
    return "Echo customer name (javaDelegateExpression)";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.integration.servicetask.AbstractCustomServiceTask
   * #getSmallIconPath()
   */
  @Override
  public String getSmallIconPath() {
    return "icons/coins.png";
  }

}
