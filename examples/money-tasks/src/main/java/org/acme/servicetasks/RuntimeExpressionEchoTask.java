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
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;

/**
 * Example of a CustomServiceTask that uses an expression for the Runtime
 * annotation.
 */
@Runtime(expression = "${echoBean.echo(execution)}")
@Help(displayHelpShort = "Echos the customerName", displayHelpLong = "Echoes the process variable customerName")
public class RuntimeExpressionEchoTask extends AbstractCustomServiceTask {

  @Override
  public String contributeToPaletteDrawer() {
    return "Acme Corporation";
  }

  @Override
  public String getName() {
    return "Echo customer name (expression)";
  }

  @Override
  public String getSmallIconPath() {
    return "icons/coins.png";
  }

}
