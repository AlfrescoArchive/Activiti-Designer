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
package org.acme.runtime.echo;

import org.activiti.engine.delegate.DelegateExecution;

public class EchoBean {

  private static final String PREFIX = "Customer name";

  private static final String ECHO_FORMAT = "%s: %s";

  public void echo(DelegateExecution execution) throws Exception {
    System.out.println(String.format(ECHO_FORMAT, PREFIX, execution.getVariable("customerName")));
  }

}
