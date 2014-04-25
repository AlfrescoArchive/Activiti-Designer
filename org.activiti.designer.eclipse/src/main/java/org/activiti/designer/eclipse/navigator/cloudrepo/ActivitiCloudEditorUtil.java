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
package org.activiti.designer.eclipse.navigator.cloudrepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.runtime.preferences.InstanceScope;


public class ActivitiCloudEditorUtil {
  
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static CloseableHttpClient getAuthenticatedClient() {
  	
  	// Get settings from preferences
  	String url = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL);
		String userName = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME);
		String password = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD);
		String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE);
  	
		Cookie cookie = null;
		if (StringUtils.isNotEmpty(cookieString)) {
  		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(hexStringToByteArray(cookieString));
      try {
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        cookie = (BasicClientCookie) objectInputStream.readObject();
      } catch (Exception e) {
        e.printStackTrace();
      }
		}
		
		// Build session
    BasicCookieStore cookieStore = new BasicCookieStore();
    CloseableHttpClient httpClient = HttpClients.custom()
        .setDefaultCookieStore(cookieStore).build();
		
    if (cookie == null) {
		  try {
	      HttpUriRequest login = RequestBuilder.post()
	          .setUri(new URI(url + "/rest/app/authentication"))
	          .addParameter("j_username", userName)
	          .addParameter("j_password", password)
	          .addParameter("_spring_security_remember_me", "true")
	          .build();
	      
	      CloseableHttpResponse response = httpClient.execute(login);
	      
	      try {
	        EntityUtils.consume(response.getEntity());
	        List<Cookie> cookies = cookieStore.getCookies();
	        if (cookies.isEmpty()) {
	            // nothing to do
	        } else {
	            Cookie reponseCookie = cookies.get(0);
	            ByteArrayOutputStream os = new ByteArrayOutputStream();
	            ObjectOutputStream outputStream = new ObjectOutputStream(os);
              outputStream.writeObject(reponseCookie);
              PreferencesUtil.getActivitiDesignerPreferenceStore().setValue(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), byteArrayToHexString(os.toByteArray()));
              InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
	        }
	        
	      } finally {
	          response.close();
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		
		} else {
		  // setting cookie from cache
		  cookieStore.addCookie(cookie);
		}
    
    return httpClient;
  }
  
  /**
   * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
   * large Base64 libraries. Can be overridden if you like!
   *
   * @param bytes byte array to be converted
   * @return string containing hex values
   */
  protected static String byteArrayToHexString(byte[] bytes) {
      StringBuilder sb = new StringBuilder(bytes.length * 2);
      for (byte element : bytes) {
          int v = element & 0xff;
          if (v < 16) {
              sb.append('0');
          }
          sb.append(Integer.toHexString(v));
      }
      return sb.toString().toUpperCase();
  }

  /**
   * Converts hex values from strings to byte arra
   *
   * @param hexString string of hex-encoded values
   * @return decoded byte array
   */
  protected static byte[] hexStringToByteArray(String hexString) {
      int len = hexString.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
          data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
      }
      return data;
  }
  
  public static JsonNode getProcessModels() {
    JsonNode resultNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      
      CloseableHttpResponse response = client.execute(new HttpGet(PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL) + "/rest/app/rest/process-models"));
      try {
        InputStream responseContent = response.getEntity().getContent();
        resultNode = objectMapper.readTree(responseContent);
        
      } catch (Exception e) {
        e.printStackTrace();
        
      } finally {
        response.close();
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    
    } finally {
      try {
        client.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return resultNode;
  }
  
  /*public static Document createDocument(Folder folder, String fileName, String mimetype, byte[] content) throws Exception {
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
    CmisObject persistedExtensions = ActivitiCloudEditorUtil.getFolderChild(folder, fileName);
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
    CmisObject persistedExtensions = ActivitiCloudEditorUtil.getFolderChild(folder, file.getName());
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
  } */
  
}
