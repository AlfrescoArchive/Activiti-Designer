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

package org.activiti.designer.eclipse.bpmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.bpmn2.ActivitiListener;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.UserTask;
import org.junit.Test;


/**
 * @author Tijs Rademakers
 */
public class BpmnParserTest {
  
  @Test
  public void parseListeners() {
    File bpmnFile = new File("src/test/resources/processEventListener.bpmn20.xml");
    if(bpmnFile.exists() == false) {
      fail("bpmn file not found");
    }
    BpmnParser bpmnParser = null;
    try {
      bpmnParser = getBpmnParser(bpmnFile);
    } catch(Exception e) {
      fail("Bpmn parser error: " + e.getStackTrace());
    }
    assertNotNull(bpmnParser.bpmnList);
    assertEquals(4, bpmnParser.bpmnList.size());
    assertNotNull(bpmnParser.process);
    StartEvent startEvent = (StartEvent) bpmnParser.bpmnList.get(0);
    assertEquals("theStart", startEvent.getId());
    
    assertEquals(3, bpmnParser.sequenceFlowList.size());
    SequenceFlowModel sequenceFlow = bpmnParser.sequenceFlowList.get(0);
    assertEquals("theStart", sequenceFlow.sourceRef);
    assertNotNull(sequenceFlow.listenerList);
    assertEquals(1, sequenceFlow.listenerList.size());
    ActivitiListener sequenceListener = sequenceFlow.listenerList.get(0);
    assertEquals("expressionType", sequenceListener.getImplementationType());
  }
  
  @Test
  public void parseVacationExample() {
    File bpmnFile = new File("src/test/resources/vacationRequest.bpmn20.xml");
    if(bpmnFile.exists() == false) {
      fail("bpmn file not found");
    }
    BpmnParser bpmnParser = null;
    try {
      bpmnParser = getBpmnParser(bpmnFile);
    } catch(Exception e) {
      fail("Bpmn parser error: " + e.getStackTrace());
    }
    assertNotNull(bpmnParser.bpmnList);
    assertEquals(7, bpmnParser.bpmnList.size());
    assertNull(bpmnParser.process);
    StartEvent startEvent = (StartEvent) bpmnParser.bpmnList.get(0);
    assertEquals("sid-E2CC4D54-9C99-46CB-A145-3E44807A50F8", startEvent.getId());
    
    UserTask userTask = (UserTask) bpmnParser.bpmnList.get(1);
    assertEquals("Handle vacation request", userTask.getName());
    assertEquals(0, userTask.getActivitiListeners().size());
    assertEquals(1, userTask.getCandidateGroups().size());
    
    assertEquals(8, bpmnParser.sequenceFlowList.size());
  }
  
  @Test
  public void parseSequenceFlowWithCondition() {
    File bpmnFile = new File("src/test/resources/sequenceFlowWithCondition.bpmn20.xml");
    if(bpmnFile.exists() == false) {
      fail("bpmn file not found");
    }
    BpmnParser bpmnParser = null;
    try {
      bpmnParser = getBpmnParser(bpmnFile);
    } catch(Exception e) {
      fail("Bpmn parser error: " + e.getStackTrace());
    }
    assertNotNull(bpmnParser.bpmnList);
    assertEquals(3, bpmnParser.bpmnList.size());
    assertNull(bpmnParser.process);
    StartEvent startEvent = (StartEvent) bpmnParser.bpmnList.get(0);
    assertEquals("theStart", startEvent.getId());
    
    assertEquals(2, bpmnParser.sequenceFlowList.size());
    SequenceFlowModel sequenceFlow = bpmnParser.sequenceFlowList.get(0);
    assertEquals("theStart", sequenceFlow.sourceRef);
    assertNotNull(sequenceFlow.listenerList);
    assertEquals(1, sequenceFlow.listenerList.size());
    ActivitiListener sequenceListener = sequenceFlow.listenerList.get(0);
    assertEquals("expressionType", sequenceListener.getImplementationType());
    assertNotNull(sequenceFlow.conditionExpression);
    assertEquals("test", sequenceFlow.conditionExpression.getBody());
  }
  
  private BpmnParser getBpmnParser(File filename) throws Exception {
    BpmnParser bpmnParser = new BpmnParser();
    FileInputStream inStream = new FileInputStream(filename);
    XMLInputFactory xif = XMLInputFactory.newInstance();
    InputStreamReader in = new InputStreamReader(inStream, "UTF-8");
    XMLStreamReader xtr = xif.createXMLStreamReader(in);
    bpmnParser.parseBpmn(xtr);
    return bpmnParser;
  }

}
