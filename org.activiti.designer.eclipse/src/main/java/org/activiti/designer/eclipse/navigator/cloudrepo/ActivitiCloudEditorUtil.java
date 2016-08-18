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
package org.activiti.designer.eclipse.navigator.cloudrepo;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.core.resources.IFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ActivitiCloudEditorUtil {
  
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static CloseableHttpClient getAuthenticatedClient() {
  	
    ActivitiPlugin plugin = ActivitiPlugin.getDefault();
  	// Get settings from preferences
  	String userName = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_USERNAME, plugin);
		String password = PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_PASSWORD, plugin);
		
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

    SSLContext sslContext = null;
    try {
      sslContext = SSLContexts.custom()
          .loadTrustMaterial(null, new TrustStrategy() {
    
              @Override
              public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                  return true;
              }
          })
          .useTLS()
          .build();
      
    } catch (Exception e) {
      Logger.logError("Could not configure HTTP client to use SSL" , e);
    }
    
    if (sslContext != null) {
      httpClientBuilder.setSslcontext(sslContext);
    }

    return httpClientBuilder.build();
  }
  
  public static JsonNode getProcessModels() {
    JsonNode resultNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      HttpGet httpGet = new HttpGet(PreferencesUtil.getStringPreference(
          Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + "/api/enterprise/models");
      CloseableHttpResponse response = client.execute(httpGet);
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          resultNode = objectMapper.readTree(responseContent);
          
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
  
  public static InputStream downloadProcessModel(String modelId, IFile file) {
    InputStream bpmnStream = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      CloseableHttpResponse response = client.execute(new HttpGet(PreferencesUtil.getStringPreference(
          Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/api/enterprise/models/" + modelId + "/bpmn20"));
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
  
  public static JsonNode uploadNewVersion(String modelId, String filename, byte[] content) {
    JsonNode modelNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      HttpPost post = new HttpPost(PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/api/enterprise/models/" + modelId + "/newversion");
      HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", content, ContentType.APPLICATION_XML, filename).build();
      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          modelNode = objectMapper.readTree(responseContent);
          
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
  
  public static JsonNode importModel(String filename, byte[] content) {
    JsonNode modelNode = null;
    CloseableHttpClient client = getAuthenticatedClient();
    try {
      ActivitiPlugin plugin = ActivitiPlugin.getDefault();
      HttpPost post = new HttpPost(PreferencesUtil.getStringPreference(Preferences.ACTIVITI_CLOUD_EDITOR_URL, plugin) + 
          "/api/enterprise/process-models/import");
      HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", content, ContentType.APPLICATION_XML, filename).build();
      post.setEntity(entity);
      CloseableHttpResponse response = client.execute(post);
      try {
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream responseContent = response.getEntity().getContent();
        if (statusCode >= 200 && statusCode < 300) {
          modelNode = objectMapper.readTree(responseContent);
          
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
