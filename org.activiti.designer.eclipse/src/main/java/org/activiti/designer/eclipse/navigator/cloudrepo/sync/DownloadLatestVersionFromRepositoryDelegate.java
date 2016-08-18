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
/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.eclipse.navigator.cloudrepo.sync;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.designer.eclipse.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Tijs Rademakers
 */
public class DownloadLatestVersionFromRepositoryDelegate implements IObjectActionDelegate {
	
	private Shell shell;
	private IFile selectedFile;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
		  BpmnModel model = parseBpmnXML(selectedFile.getContents());
		  Map<String, List<ExtensionAttribute>> definitionAttributeMap = model.getDefinitionsAttributes();
		  String modelId = null;
		  if (definitionAttributeMap.containsKey("modelId")) {
		    List<ExtensionAttribute> definitionAttributes = definitionAttributeMap.get("modelId");
		    if (definitionAttributes != null && definitionAttributes.size() > 0) {
		      modelId = definitionAttributes.get(0).getValue();
		    }
		  }
			
			if (modelId == null || modelId.length() == 0) {
			  showNoModelIdMessage(shell);
				
			} else {
				
				SyncUtil.startDownloadLatestVersionBackgroundJob(shell, modelId, selectedFile);
				
			}
		} catch (Exception exception) {
			Logger.logError(exception);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			selectedFile = (IFile) strucSelection.getFirstElement();
		}
	}
	
	protected BpmnModel parseBpmnXML(InputStream stream) {
	  BpmnModel model = null;
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      InputStreamReader in = new InputStreamReader(stream, "UTF-8");
      XMLStreamReader xtr = xif.createXMLStreamReader(in);
      BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
      model = xmlConverter.convertToBpmnModel(xtr);
      xtr.close();
      in.close();
    } catch (Exception e) {
      Logger.logError("Error parsing xml", e);
    }
    return model;
  }
	
	protected void showNoModelIdMessage(final Shell shell) {
    Display.getDefault().syncExec(new Runnable() {
      public void run() {
        MessageDialog.openInformation(shell, "Cannot download latest version", 
            "This model doesn't have a valid Activiti Editor identifier. Please download the model first from the repository using the Activiti Editor navigator.");
      }
    });
  }
}
