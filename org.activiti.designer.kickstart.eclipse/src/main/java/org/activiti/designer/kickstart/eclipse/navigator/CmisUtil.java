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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.designer.kickstart.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.preferences.Preferences;
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
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;


public class CmisUtil {
	
  private static Session currentSession;
  
  public static Session getCurrentSession() {
  	if (currentSession == null) {
  		createCmisSession();
  	}
  	return currentSession;
  }
  
  public static void closeCurrentSession() {
  	currentSession = null;
  }
  
  public static void clearCaches() {
  	closeCurrentSession();
  }

  public static Session createCmisSession() {
  	
  	// Get settings from preferences
  	String url = PreferencesUtil.getStringPreference(Preferences.CMIS_URL);
		String userName = PreferencesUtil.getStringPreference(Preferences.CMIS_USERNAME);
		String password = PreferencesUtil.getStringPreference(Preferences.CMIS_PASSWORD);
  	
		// Build session
    SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
    Map<String, String> parameter = new HashMap<String, String>();
    parameter.put(SessionParameter.USER, userName);
    parameter.put(SessionParameter.PASSWORD, password);
    parameter.put(SessionParameter.ATOMPUB_URL, url); 
    parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

    Repository repository = sessionFactory.getRepositories(parameter).get(0);
    Session session = repository.createSession();
    
    CmisUtil.currentSession = session;
    return CmisUtil.currentSession;
  }
  
  public static List<CmisObject> getRootElements() {
	  Folder rootFolder = getCurrentSession().getRootFolder();
	  ItemIterable<CmisObject> children = rootFolder.getChildren();
	  List<CmisObject> cmisObjects = new ArrayList<CmisObject>();
	  for (CmisObject cmisObject : children) {
		  cmisObjects.add(cmisObject);
	  }
	  return cmisObjects;
  }
  
  public static Folder getFolder(String folderName) {
    ObjectType type = getCurrentSession().getTypeDefinition("cmis:folder");
    PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
    String objectIdQueryName = objectIdPropDef.getQueryName();
    
    ItemIterable<QueryResult> results = getCurrentSession().query("SELECT * FROM cmis:folder WHERE cmis:name='" + folderName + "'", false);
    for (QueryResult qResult : results) {
    	String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
    	return (Folder) getCurrentSession().getObject(getCurrentSession().createObjectId(objectId));
    }
    return null;
  }
  
  public static CmisObject getCmisObject(String nodeId) {
  	try {
  		return getCurrentSession().getObject(new ObjectIdImpl(nodeId));
  	} catch (CmisObjectNotFoundException confe) {
  		return null;
  	}
  }
  
  public static Folder createFolder(Folder parentFolder, String folderName) {
    Map<String, Object> folderProps = new HashMap<String, Object>();
    folderProps.put(PropertyIds.NAME, folderName);
    folderProps.put(PropertyIds.OBJECT_TYPE_ID, FolderType.FOLDER_BASETYPE_ID);

    ObjectId folderObjectId = getCurrentSession().createFolder(folderProps, parentFolder, null, null, null);
    return (Folder) getCurrentSession().getObject(folderObjectId);
  }
  
  public static Document createDocument(Folder folder, String fileName, String mimetype, byte[] content) throws Exception {
    Map<String, Object> docProps = new HashMap<String, Object>();
    docProps.put(PropertyIds.NAME, fileName);
    docProps.put(PropertyIds.OBJECT_TYPE_ID, DocumentType.DOCUMENT_BASETYPE_ID);
    
    ByteArrayInputStream in = new ByteArrayInputStream(content);
    ContentStream contentStream = getCurrentSession().getObjectFactory().createContentStream(fileName, content.length, mimetype, in);
    
    ObjectId documentId = getCurrentSession().createDocument(docProps, getCurrentSession().createObjectId((String) folder.getPropertyValue(PropertyIds.OBJECT_ID)), contentStream, null, null, null, null);
    Document document = (Document) getCurrentSession().getObject(documentId);
    return document;
  }
  
  public static void deleteCmisObjects(Collection<CmisObject> cmisObjects) {
	  for (CmisObject cmisObject : cmisObjects) {
	  	getCurrentSession().delete(new ObjectIdImpl(cmisObject.getId()));
	  }
  }
  
  public static void deleteCmisObjects(CmisObject... cmisObjects) {
    for (CmisObject cmisObject : cmisObjects) {
      getCurrentSession().delete(new ObjectIdImpl(cmisObject.getId()));
    }
}
  
  public static InputStream downloadDocument(Document document) {
  	return getCurrentSession().getContentStream(new ObjectIdImpl(document.getId())).getStream();
  }
  
  public static String uploadDocumentToFolder(Folder folder, String fileName, byte[] content) {
  	Map<String, Object> properties = new HashMap<String, Object>();
  	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
  	properties.put(PropertyIds.NAME, fileName);

  	// content
  	InputStream stream = new ByteArrayInputStream(content);
  	ContentStream contentStream = new ContentStreamImpl(fileName, BigInteger.valueOf(content.length), "application/zip", stream);

  	// create document
  	return folder.createDocument(properties, contentStream, VersioningState.MAJOR).getId();
  }
  
  public static String uploadModel(Folder folder, File file, CmisObject existingModel) throws IOException {
    if(existingModel != null) {
      Document pwc = (Document) getCurrentSession().getObject(((Document) existingModel).checkOut());
      InputStream stream = new FileInputStream(file);
      ContentStream contentStream = getCurrentSession().getObjectFactory().createContentStream(
          file.getName(), -1, "application/xml", stream);
      try {
          pwc.checkIn(false, null, contentStream, "minor version").getId();
      } catch (Exception e) {
          pwc.cancelCheckOut();
          throw new IOException("Error while checking in new version of model", e);
      }
      return existingModel.getId();
    } else {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(PropertyIds.OBJECT_TYPE_ID,  "D:cm:dictionaryModel");
      properties.put(PropertyIds.NAME, file.getName());
      properties.put("cm:modelActive", Boolean.TRUE);
      
      InputStream stream = new FileInputStream(file);
      ContentStream contentStream = new ContentStreamImpl(file.getName(), new BigInteger("-1"), "application/xml", stream);
      
      // create document
      return folder.createDocument(properties, contentStream, VersioningState.MAJOR).getId();
    }
  }
  
  public static String uploadProcess(Folder folder, File file) throws IOException {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(PropertyIds.OBJECT_TYPE_ID,  "D:bpm:workflowDefinition");
    properties.put(PropertyIds.NAME, file.getName());
    properties.put("bpm:definitionDeployed", Boolean.TRUE);
    properties.put("bpm:engineId", "activiti");

    InputStream stream = new FileInputStream(file);
    ContentStream contentStream = new ContentStreamImpl(file.getName(), new BigInteger("-1"), "application/xml", stream);

    // create document
    return folder.createDocument(properties, contentStream, VersioningState.MAJOR).getId();
  }
  
  public static void uploadPersistedExtensions(Folder folder, File file) throws Exception {
    String fileName = "default-persisted-extension.xml";
    CmisObject persistedExtensions = CmisUtil.getFolderChild(folder, fileName);
    if(persistedExtensions != null) {
      Document pwc = (Document) getCurrentSession().getObject(((Document) persistedExtensions).checkOut());
      InputStream stream = new FileInputStream(file);
      ContentStream contentStream = getCurrentSession().getObjectFactory().createContentStream(
          fileName, -1, "application/xml", stream);
      try {
          pwc.checkIn(false, null, contentStream, "minor version").getId();
      } catch (Exception e) {
          pwc.cancelCheckOut();
      }
    } else {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(PropertyIds.OBJECT_TYPE_ID,  "cmis:document");
      properties.put(PropertyIds.NAME, fileName);
      properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "application/xml");
      
      InputStream stream = new FileInputStream(file);
      ContentStreamImpl contentStream = new ContentStreamImpl(file.getName(), new BigInteger("-1"), "application/xml", stream);
      folder.createDocument(properties, contentStream, VersioningState.MAJOR).getId();
    }
  }
  
  public static void uploadModuleDeployment(Folder folder, File file) throws Exception {
    CmisObject persistedExtensions = CmisUtil.getFolderChild(folder, file.getName());
    if(persistedExtensions != null) {
      Document pwc = (Document) getCurrentSession().getObject(((Document) persistedExtensions).checkOut());
      InputStream stream = new FileInputStream(file);
      ContentStream contentStream = getCurrentSession().getObjectFactory().createContentStream(
          file.getName(), -1, "application/xml", stream);
      try {
          pwc.checkIn(false, null, contentStream, "minor version").getId();
      } catch (Exception e) {
          pwc.cancelCheckOut();
      }
    } else {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put(PropertyIds.OBJECT_TYPE_ID,  "cmis:document");
      properties.put(PropertyIds.NAME, file.getName());
      properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "application/xml");
      
      InputStream stream = new FileInputStream(file);
      ContentStreamImpl contentStream = new ContentStreamImpl(file.getName(), new BigInteger("-1"), "application/xml", stream);
      folder.createDocument(properties, contentStream, VersioningState.MAJOR).getId();
    }
  }
  
  public static CmisObject getFolderChild(Folder folder, String fileName) {
    String path = folder.getPath() + "/" + fileName;
    CmisObject child = null;
    try {
      child = getCurrentSession().getObjectByPath(path);
    } catch(CmisObjectNotFoundException onfe) {
      // Ignore, return null
    }
    return child;
  }
  
  public static void overwriteDocumentContent(Document document, byte[] content, String mimetype) {
  	InputStream stream = new ByteArrayInputStream(content);
  	ContentStream contentStream = new ContentStreamImpl(document.getName(), BigInteger.valueOf(content.length), "application/zip", stream);
  	document.setContentStream(contentStream, true);
  }
  
  public static String uploadNewVersion(Document document, byte[] content, String mimetype) {
  	 Document pwc = (Document) getCurrentSession().getObject(document.checkOut());
     InputStream stream = new ByteArrayInputStream(content);
     ContentStream contentStream = getCurrentSession().getObjectFactory().createContentStream(
             document.getName(), content.length, mimetype, stream);
     try {
         return pwc.checkIn(false, null, contentStream, "minor version").getId();
     } catch (Exception e) {
         pwc.cancelCheckOut();
     }
     return null;
  }
  
  public static void renameCmisObject(CmisObject cmisObject, String newName) {
  	Map<String, String> properties = new HashMap<String, String>();
  	properties.put(PropertyIds.NAME, newName);
  	cmisObject.updateProperties(properties);
  }

  public static Folder getFolderByPath(String cmisModelsPath) {
    CmisObject objectByPath = getCurrentSession().getObjectByPath(cmisModelsPath);
    if(objectByPath instanceof Folder) {
      return (Folder) objectByPath;
    }
    return null;
  }

  public static Folder getOrCreateChildFolder(Folder folder, String folderName) {
    String path = folder.getPath() + "/" + folderName;
    CmisObject child = null;
    try {
      child = getCurrentSession().getObjectByPath(path);
    } catch(CmisObjectNotFoundException onfe) {
      // Create the folder
      Map<String, Object> folderProps = new HashMap<String, Object>();
      folderProps.put(PropertyIds.NAME, folderName);
      folderProps.put(PropertyIds.OBJECT_TYPE_ID, FolderType.FOLDER_BASETYPE_ID);

      ObjectId folderObjectId = getCurrentSession().createFolder(folderProps, folder, null, null, null);
      child = getCurrentSession().getObject(folderObjectId);
    }
    return (Folder) child;
  }
  
}
