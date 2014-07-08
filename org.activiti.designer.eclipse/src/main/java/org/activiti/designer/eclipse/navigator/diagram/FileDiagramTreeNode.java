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

package org.activiti.designer.eclipse.navigator.diagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.designer.eclipse.navigator.TreeNode;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.eclipse.core.resources.IFile;

/**
 * @author Tiese Barrell
 */
public class FileDiagramTreeNode extends AbstractDiagramTreeNode<IFile> {

  private BpmnMemoryModel model;

  public FileDiagramTreeNode(final IFile parent) {
    super(parent, null, "Diagram Root Node");
  }

  @Override
  protected void extractChildren() {
    if (isDiagramRoot()) {

      model = buildModel((IFile) getParent());
      if (model == null || model.getBpmnModel() == null) {
        return;
      }
      if (hasPools(model)) {
        extractChildrenForPoolsOnly(model);
      } else if (hasMainProcess(model)) {
        extractChildrenForMainProcessOnly(model);
      }
    }
  }

  @Override
  protected boolean hasRootModel() {
    return true;
  }

  @Override
  protected BpmnMemoryModel getRootModel() {
    return model;
  }

  private boolean isDiagramRoot() {
    return getParent() instanceof IFile;
  }

  private boolean hasMainProcess(final BpmnMemoryModel model) {
    return model.getBpmnModel().getMainProcess() != null;
  }

  private boolean hasPools(final BpmnMemoryModel model) {
    return model.getBpmnModel().getPools() != null && !model.getBpmnModel().getPools().isEmpty();
  }

  private void extractChildrenForMainProcessOnly(final BpmnMemoryModel model) {
    extractTransparentProcess(this, model.getBpmnModel().getMainProcess());
  }

  private void extractTransparentProcess(final TreeNode parent, final Process mainProcess) {
    final TreeNode transparentProcessNode = DiagramTreeNodeFactory.createTransparentProcessNode(parent, mainProcess);
    referenceChildNodesToOwnChildren(transparentProcessNode);
  }

  private void extractChildrenForPoolsOnly(final BpmnMemoryModel model) {
    extractPools(model.getBpmnModel().getPools());
  }

  private void extractPools(final List<Pool> pools) {
    for (final Pool pool : pools) {
      addChildNode(DiagramTreeNodeFactory.createPoolNode(this, pool));
    }
  }

  private BpmnMemoryModel buildModel(final IFile modelFile) {
    final BpmnMemoryModel result = new BpmnMemoryModel(null, modelFile);

    String filePath = modelFile.getLocationURI().getPath();
    File bpmnFile = new File(filePath);
    try {
      if (bpmnFile.exists()) {
        final FileInputStream fileStream = new FileInputStream(bpmnFile);
        final XMLInputFactory xif = XMLInputFactory.newInstance();
        final InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
        final XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = converter.convertToBpmnModel(xtr);
        result.setBpmnModel(bpmnModel);
      }
    } catch (Exception e) {
      
    }

    return result;
  }

}
