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
package org.activiti.designer.kickstart.eclipse.navigator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.FolderType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BindingType;


public class CmisUtil {
	
  private static Session currentSession;
  
  public static Session getCurrentSession() {
  	return currentSession;
  }
  
  public static void closeCurrentSession() {
  	currentSession = null;
  }

  public static Session createCmisSession(String user, String password, String url) {
    SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
    Map<String, String> parameter = new HashMap<String, String>();
    parameter.put(SessionParameter.USER, user);
    parameter.put(SessionParameter.PASSWORD, password);
    parameter.put(SessionParameter.ATOMPUB_URL, url); 
    parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

    Repository repository = sessionFactory.getRepositories(parameter).get(0);
    Session session = repository.createSession();
    
    currentSession = session;
    return currentSession;
  }
  
  public static List<CmisObject> getRootElements() {
	  Folder rootFolder = currentSession.getRootFolder();
	  ItemIterable<CmisObject> children = rootFolder.getChildren();
	  List<CmisObject> cmisObjects = new ArrayList<CmisObject>();
	  for (CmisObject cmisObject : children) {
		  cmisObjects.add(cmisObject);
	  }
	  return cmisObjects;
  }
  
  public static Folder getFolder(String folderName) {
    ObjectType type = currentSession.getTypeDefinition("cmis:folder");
    PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
    String objectIdQueryName = objectIdPropDef.getQueryName();
    
    ItemIterable<QueryResult> results = currentSession.query("SELECT * FROM cmis:folder WHERE cmis:name='" + folderName + "'", false);
    for (QueryResult qResult : results) {
    	String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
    	return (Folder) currentSession.getObject(currentSession.createObjectId(objectId));
    }
    return null;
  }
  
  public static Folder createFolder(Folder parentFolder, String folderName) {
    Map<String, Object> folderProps = new HashMap<String, Object>();
    folderProps.put(PropertyIds.NAME, folderName);
    folderProps.put(PropertyIds.OBJECT_TYPE_ID, FolderType.FOLDER_BASETYPE_ID);

    ObjectId folderObjectId = currentSession.createFolder(folderProps, parentFolder, null, null, null);
    return (Folder) currentSession.getObject(folderObjectId);
  }
  
  public static Document createDocument(Folder folder, String fileName, String mimetype, byte[] content) throws Exception {
    Map<String, Object> docProps = new HashMap<String, Object>();
    docProps.put(PropertyIds.NAME, fileName);
    docProps.put(PropertyIds.OBJECT_TYPE_ID, DocumentType.DOCUMENT_BASETYPE_ID);
    
    ByteArrayInputStream in = new ByteArrayInputStream(content);
    ContentStream contentStream = currentSession.getObjectFactory().createContentStream(fileName, content.length, mimetype, in);
    
    ObjectId documentId = currentSession.createDocument(docProps, currentSession.createObjectId((String) folder.getPropertyValue(PropertyIds.OBJECT_ID)), contentStream, null, null, null, null);
    Document document = (Document) currentSession.getObject(documentId);
    return document;
  }
  
  public static void deleteCmisObjects(Collection<CmisObject> cmisObjects) {
	  for (CmisObject cmisObject : cmisObjects) {
	  	currentSession.delete(new ObjectIdImpl(cmisObject.getId()));
	  }
  }
  
  public static InputStream downloadDocument(Document document) {
  	return currentSession.getContentStream(new ObjectIdImpl(document.getId())).getStream();
  }
  
  public static void renameCmisObject(CmisObject cmisObject, String newName) {
  	Map<String, String> properties = new HashMap<String, String>();
  	properties.put(PropertyIds.NAME, newName);
  	cmisObject.updateProperties(properties);
  }
  
}
