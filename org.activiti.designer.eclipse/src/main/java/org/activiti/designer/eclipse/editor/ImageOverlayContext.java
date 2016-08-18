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
package org.activiti.designer.eclipse.editor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.GC;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.subscribers.Subscriber;
import org.eclipse.team.core.synchronize.SyncInfo;

public class ImageOverlayContext {

  private static final String PREFIX_DATE = "Saved";
  private static final String PREFIX_PROCESS_KEY = "Key";
  private static final String PREFIX_PROCESS_NAMESPACE = "Namespace";
  private static final String PREFIX_FILE_NAME = "File";
  private static final String PREFIX_PROCESS_NAME = "Process";
  private static final String PREFIX_REVISION = "Revision";

  private GC imageGC;
  private String processName;
  private String processKey;
  private String processNamespace;
  private final String fileName;
  private final IFile modelFile;
  private final String date;
  private String revision;

  public ImageOverlayContext(final IFile modelFile) {
    super();
    this.modelFile = modelFile;
    this.fileName = modelFile.getName();
    date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public GC getImageGC() {
    return imageGC;
  }

  public void setImageGC(GC imageGC) {
    this.imageGC = imageGC;
  }

  public void setProcessKey(String processKey) {
    this.processKey = processKey;
  }

  public void setProcessNamespace(String processNamespace) {
    this.processNamespace = processNamespace;
  }

  public String getRevisionDisplay() {
    if (revision == null) {
      revision = determineRevision();
    }
    return padPrefix(PREFIX_REVISION) + revision;
  }

  public boolean isKeyEnabled() {
    return PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_KEY, ActivitiPlugin.getDefault());
  }

  public boolean isNamespaceEnabled() {
    return PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_NAMESPACE, ActivitiPlugin.getDefault());
  }

  public boolean isFilenameEnabled() {
    return PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_FILENAME, ActivitiPlugin.getDefault());
  }

  public boolean isDateEnabled() {
    return PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_DATE, ActivitiPlugin.getDefault());
  }

  public boolean isRevisionEnabled() {
    return PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_REVISION, ActivitiPlugin.getDefault());
  }

  public int getCornerPreference() {
    return Integer.parseInt(PreferencesUtil.getStringPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY_POSITION, ActivitiPlugin.getDefault()));
  }

  public String getProcessNameDisplay() {
    return padPrefix(PREFIX_PROCESS_NAME) + processName;
  }

  public String getFilenameDisplay() {
    return padPrefix(PREFIX_FILE_NAME) + fileName;
  }

  public String getProcessNamespaceDisplay() {
    return padPrefix(PREFIX_PROCESS_NAMESPACE) + processNamespace;
  }

  public String getProcessKeyDisplay() {
    return padPrefix(PREFIX_PROCESS_KEY) + processKey;
  }

  public String getDateDisplay() {
    return padPrefix(PREFIX_DATE) + date;
  }

  private String padPrefix(final String prefix) {
    int maxLength = 0;
    final List<String> prefixes = Arrays.asList(PREFIX_PROCESS_NAME, PREFIX_FILE_NAME, PREFIX_PROCESS_KEY, PREFIX_PROCESS_NAMESPACE, PREFIX_DATE,
            PREFIX_REVISION);
    for (final String currentPrefix : prefixes) {
      maxLength = Math.max(maxLength, currentPrefix.length());
    }
    return prefix + ": ";
  }

  private String determineRevision() {
    String result = "n/a";
    if (isRevisionEnabled() && modelFile != null) {

      final RepositoryProvider repositoryProvider = RepositoryProvider.getProvider(modelFile.getProject());

      if (repositoryProvider != null) {
        final IFileHistoryProvider fileHistoryProvider = repositoryProvider.getFileHistoryProvider();
        if (fileHistoryProvider != null) {
          final Subscriber subscriber = repositoryProvider.getSubscriber();
          try {
            final SyncInfo info = subscriber.getSyncInfo(modelFile);
            result = info.getRemote().getContentIdentifier();
          } catch (TeamException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return result;
  }

}
