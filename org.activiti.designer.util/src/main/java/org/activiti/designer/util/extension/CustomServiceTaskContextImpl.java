package org.activiti.designer.util.extension;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.activiti.designer.integration.servicetask.CustomServiceTask;

public class CustomServiceTaskContextImpl implements CustomServiceTaskContext {

  private static final String DEFAULT_ICON_PATH = "icons/defaultCustomServiceTask.png";
  private static final String ERROR_ICON_PATH = "icons/errorCustomServiceTask.png";

  private static final String ERROR_ICON_MESSAGE_PATTERN = "The CustomServiceTask '%s' has an incorrect icon path '%s', so the icon cannot be shown. A placeholder error icon will be shown instead.";

  private final CustomServiceTask customServiceTask;

  private final String extensionName;
  private final String extensionJarPath;
  private JarFile extensionJarFile;

  public CustomServiceTaskContextImpl(final CustomServiceTask customServiceTask, final String extensionName, final String extensionJarPath) {
    this.customServiceTask = customServiceTask;
    this.extensionName = extensionName;
    this.extensionJarPath = extensionJarPath;
    try {
      this.extensionJarFile = new JarFile(this.extensionJarPath);
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("Path '%s' is an invalid path for a JarFile"));
    }
  }

  @Override
  public InputStream getSmallIconStream() {
    InputStream result = null;

    final String path = this.customServiceTask.getSmallIconPath();
    if (path != null) {
      JarEntry imgentry = extensionJarFile.getJarEntry(path);

      try {
        result = extensionJarFile.getInputStream(imgentry);
      } catch (Exception e) {
        System.err.println(String.format(ERROR_ICON_MESSAGE_PATTERN, this.customServiceTask.getId(), path));
        result = getErrorCustomServiceTaskIconStream();
      }
    } else {
      result = getDefaultCustomServiceTaskIconStream();
    }

    return result;
  }
  @Override
  public InputStream getLargeIconStream() {
    InputStream result = null;

    final String path = this.customServiceTask.getLargeIconPath();
    if (path != null) {
      JarEntry imgentry = extensionJarFile.getJarEntry(path);

      try {
        result = extensionJarFile.getInputStream(imgentry);
      } catch (Exception e) {
        System.err.println(String.format(ERROR_ICON_MESSAGE_PATTERN, this.customServiceTask.getId(), path));
        result = getErrorCustomServiceTaskIconStream();
      }
    } else {
      result = getDefaultCustomServiceTaskIconStream();
    }

    return result;
  }

  @Override
  public InputStream getShapeIconStream() {
    InputStream result = null;

    final String path = this.customServiceTask.getShapeIconPath();
    if (path != null) {
      JarEntry imgentry = extensionJarFile.getJarEntry(path);

      try {
        result = extensionJarFile.getInputStream(imgentry);
      } catch (Exception e) {
        System.err.println(String.format(ERROR_ICON_MESSAGE_PATTERN, this.customServiceTask.getId(), path));
        result = getErrorCustomServiceTaskIconStream();
      }
    } else {
      result = getDefaultCustomServiceTaskIconStream();
    }

    return result;
  }

  @Override
  public CustomServiceTask getServiceTask() {
    return this.customServiceTask;
  }

  @Override
  public String getExtensionName() {
    return this.extensionName;
  }

  @Override
  public String getSmallImageKey() {
    return getExtensionName() + "/small/" + getServiceTask().getId();
  }

  @Override
  public String getLargeImageKey() {
    return getExtensionName() + "/large/" + getServiceTask().getId();
  }

  @Override
  public String getShapeImageKey() {
    return getExtensionName() + "/shape/" + getServiceTask().getId();
  }

  private InputStream getDefaultCustomServiceTaskIconStream() {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_ICON_PATH);
  }

  private InputStream getErrorCustomServiceTaskIconStream() {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(ERROR_ICON_PATH);
  }

  @Override
  public int compareTo(CustomServiceTaskContext otherCustomServiceTaskContext) {
    if (otherCustomServiceTaskContext instanceof CustomServiceTaskContext) {
      return getServiceTask().getOrder().compareTo(otherCustomServiceTaskContext.getServiceTask().getOrder());
    }
    return 0;
  }

}
