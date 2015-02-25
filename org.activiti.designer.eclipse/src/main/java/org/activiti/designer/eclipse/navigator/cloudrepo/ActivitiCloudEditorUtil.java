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

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ActivitiCloudEditorUtil {
  
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static CloseableHttpClient getAuthenticatedClient() {
  	
    ActivitiPlugin plugin = ActivitiPlugin.getDefault();
  	// Get settings from preferences
  	String url = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin);
		String userName = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME, plugin);
		String password = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD, plugin);
		String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE, plugin);
  	
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
              PreferencesUtil.getActivitiDesignerPreferenceStore(plugin).setValue(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), 
                  byteArrayToHexString(os.toByteArray()));
              InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
	        }
	        
	      } finally {
	          response.close();
	      }
	      
	    } catch (Exception e) {
	      Logger.logError("Error authenticating " + userName, e);
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
  
  public static JsonNode getProcessModels(boolean firstTry) {
    JsonNode resultNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      CloseableHttpResponse response = client.execute(new HttpGet(PreferencesUtil.getStringPreference(
          Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + "/rest/app/rest/models"));
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          resultNode = objectMapper.readTree(responseContent);
          
        } else if (statusCode == 401 && firstTry) {
          String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE, plugin);
          if (StringUtils.isNotEmpty(cookieString)) {
            PreferencesUtil.getActivitiDesignerPreferenceStore(plugin).setValue(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), "");
            InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
            return getProcessModels(false);
          }
          
        } else {
          JsonNode exceptionNode = null;
          String exceptionString = IOUtils.toString(responseContent);
          try {
            exceptionNode = objectMapper.readTree(exceptionString);
          } catch(Exception e) {
            throw new ActivitiCloudEditorException(exceptionString);
          }
          throw new ActivitiCloudEditorException(exceptionNode);
        }
          
      } finally {
        response.close();
      }
      
    } catch (ActivitiCloudEditorException e) {
      throw e;
      
    } catch (Exception e) {
      Logger.logError("Error getting process models", e);
    
    } finally {
      try {
        client.close();
      } catch (Exception e) {}
    }
    return resultNode;
  }
  
  public static InputStream downloadProcessModel(String modelId, IFile file, boolean firstTry) {
    InputStream bpmnStream = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      CloseableHttpResponse response = client.execute(new HttpGet(PreferencesUtil.getStringPreference(
          Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/rest/app/rest/models/" + modelId + "/bpmn20"));
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        bpmnStream = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          if (file.exists()) {
            String oldBpmn = IOUtils.toString(file.getContents());
            String newBpmn = IOUtils.toString(bpmnStream);
            if (oldBpmn.equals(newBpmn)) {
              throw new ActivitiCloudEditorSameContentException("The local copy is already up to date");
            } else {
              file.setContents(IOUtils.toInputStream(newBpmn), true, true, null);
            }
          } else {
            file.create(bpmnStream, true, null);
          }
          
        } else if (statusCode == 401 && firstTry) {
          String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE, plugin);
          if (StringUtils.isNotEmpty(cookieString)) {
            PreferencesUtil.getActivitiDesignerPreferenceStore(plugin).setValue(
                Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), "");
            InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
            return downloadProcessModel(modelId, file, false);
          }
          
        } else {
          JsonNode exceptionNode = null;
          String exceptionString = IOUtils.toString(bpmnStream);
          try {
            exceptionNode = objectMapper.readTree(exceptionString);
          } catch(Exception e) {
            throw new ActivitiCloudEditorException(exceptionString);
          }
          throw new ActivitiCloudEditorException(exceptionNode);
        }
          
      } finally {
        response.close();
      }
    } catch (ActivitiCloudEditorException e) {
      throw e;
      
    } catch (Exception e) {
      Logger.logError("Error getting process models", e);
    
    } finally {
      try {
        client.close();
      } catch (Exception e) {}
    }
    return bpmnStream;
  }
  
  public static JsonNode uploadNewVersion(String modelId, String filename, byte[] content, boolean firstTry) {
    JsonNode modelNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      HttpPost post = new HttpPost(PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/rest/app/rest/models/" + modelId + "/newversion");
      HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", content, ContentType.APPLICATION_XML, filename).build();
      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          modelNode = objectMapper.readTree(responseContent);
          
        } else if (statusCode == 401 && firstTry) {
          String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE, plugin);
          if (StringUtils.isNotEmpty(cookieString)) {
            PreferencesUtil.getActivitiDesignerPreferenceStore(plugin).setValue(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), "");
            InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
            return uploadNewVersion(modelId, filename, content, false);
          }
          
        } else {
          JsonNode exceptionNode = null;
          String exceptionString = IOUtils.toString(responseContent);
          try {
            exceptionNode = objectMapper.readTree(exceptionString);
          } catch(Exception e) {
            throw new ActivitiCloudEditorException(exceptionString);
          }
          throw new ActivitiCloudEditorException(exceptionNode);
        }
          
      } finally {
        response.close();
      }
      
    } catch (ActivitiCloudEditorException e) {
      throw e;
      
    } catch (Exception e) {
      Logger.logError("Error uploading new process model version", e);
    
    } finally {
      try {
        client.close();
      } catch (Exception e) {}
    }
    return modelNode;
  }
  
  public static JsonNode importModel(String filename, byte[] content, boolean firstTry) {
    JsonNode modelNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      HttpPost post = new HttpPost(PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/rest/app/rest/import-process-model");
      HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", content, ContentType.APPLICATION_XML, filename).build();
      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          modelNode = objectMapper.readTree(responseContent);
          
        } else if (statusCode == 401 && firstTry) {
          String cookieString = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE, plugin);
          if (StringUtils.isNotEmpty(cookieString)) {
            PreferencesUtil.getActivitiDesignerPreferenceStore(plugin).setValue(Preferences.ACTIVITI_CLOUD_EDITOR_COOKIE.getPreferenceId(), "");
            InstanceScope.INSTANCE.getNode(ActivitiPlugin.PLUGIN_ID).flush();
            return importModel(filename, content, false);
          }
          
        } else {
          JsonNode exceptionNode = null;
          String exceptionString = IOUtils.toString(responseContent);
          try {
            exceptionNode = objectMapper.readTree(exceptionString);
          } catch(Exception e) {
            throw new ActivitiCloudEditorException(exceptionString);
          }
          throw new ActivitiCloudEditorException(exceptionNode);
        }
          
      } finally {
        response.close();
      }
    } catch (ActivitiCloudEditorException e) {
      throw e;
    } catch (Exception e) {
      Logger.logError("Error importing process model", e);
    
    } finally {
      try {
        client.close();
      } catch (Exception e) {}
    }
    return modelNode;
  } 
}
