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
package org.activiti.designer.kickstart.eclipse.ui.wizard.form;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.common.PluginImage;
import org.activiti.designer.kickstart.eclipse.util.KickstartConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * Page that allows selecting the file-name of a new form and the destination. Doesn't allow
 * finishing in case the resource already exists. 
 *  
 * @author Frederik Heremans
 */
public class CreateKickstartFormNameWizardPage extends WizardNewFileCreationPage {

  public static final String PAGE_NAME = "createKickstartFormNameWizardPage";
  private static final String DEFAULT_FORM_NAME = "MyForm." + KickstartConstants.KICKSTART_FORM_EXTENSION;
  
  public CreateKickstartFormNameWizardPage(IStructuredSelection selection) {
    super(PAGE_NAME, selection);
    super.setFileName(DEFAULT_FORM_NAME);
    
    setTitle("New Kickstart Form");
    setImageDescriptor(KickstartPlugin.getImageDescriptor(PluginImage.ACTIVITI_LOGO_64x64));
    setDescription("Create a new Kickstart form.");
    setFileExtension(KickstartConstants.KICKSTART_FORM_EXTENSION);
    
    // Don't allow overriding existing forms when creating a new one.
    setAllowExistingResources(false);
  }
}
