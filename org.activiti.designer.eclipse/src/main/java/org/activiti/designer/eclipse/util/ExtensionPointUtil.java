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

package org.activiti.designer.eclipse.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.icon.IconProvider;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;

/**
 * Utility class for extension point references.
 * 
 * @author Tiese Barrell
 * @since 0.6.0
 * @version 1
 */
public final class ExtensionPointUtil {

  private ExtensionPointUtil() {
  }

  public static final Collection<ExportMarshaller> getActiveExportMarshallers() {

    final Collection<ExportMarshaller> result = new ArrayList<ExportMarshaller>();

    final Map<String, ExportMarshaller> marshallers = getExportMarshallersAndNames();

    for (final Entry<String, ExportMarshaller> entry : marshallers.entrySet()) {
      final boolean invokeMarshaller = PreferencesUtil.getBooleanPreference(PreferencesUtil.getPreferenceId(entry.getValue()));

      if (invokeMarshaller) {
        result.add(entry.getValue());
      }
    }
    return result;
  }

  public static final ExportMarshaller getExportMarshaller(final String marshallerName) {
    final Map<String, ExportMarshaller> marshallers = getExportMarshallersAndNames();
    if (marshallers.containsKey(marshallerName)) {
      return marshallers.get(marshallerName);
    }
    return null;
  }

  public static final Collection<ExportMarshaller> getExportMarshallers() {
    return getExportMarshallersAndNames().values();
  }

  public static final ProcessValidator getProcessValidator(final String validatorId) {
    final Map<String, ProcessValidator> validators = getProcessValidatorsAndIds();
    if (validators.containsKey(validatorId)) {
      return validators.get(validatorId);
    }
    return null;
  }

  public static final Collection<ProcessValidator> getProcessValidators() {
    return getProcessValidatorsAndIds().values();
  }

  public static final Image getIconFromIconProviders(final Object context) {

    Image result = null;

    final List<IconProvider> providers = getPrioritizedIconProviders();

    for (final IconProvider provider : providers) {
      try {
        result = provider.getIcon(context);
      } catch (RuntimeException e) {
        // no-op, provider has no support for this context
      }

      if (result != null) {
        break;
      }

    }

    return result;

  }
  private static final List<IconProvider> getPrioritizedIconProviders() {

    final List<IconProvider> result = new ArrayList<IconProvider>();

    final IConfigurationElement[] providerConfiguration = Platform.getExtensionRegistry().getConfigurationElementsFor(
            ActivitiPlugin.ICON_PROVIDER_EXTENSIONPOINT_ID);

    for (final IConfigurationElement element : providerConfiguration) {
      Object o;
      try {
        o = element.createExecutableExtension("class");
        if (o instanceof IconProvider) {
          final IconProvider iconProvider = (IconProvider) o;
          result.add(iconProvider);
        }
      } catch (CoreException e1) {
        e1.printStackTrace();
      }
    }

    Collections.sort(result, new IconProviderComparator());

    return result;
  }

  private static final Map<String, ExportMarshaller> getExportMarshallersAndNames() {

    final Map<String, ExportMarshaller> result = new HashMap<String, ExportMarshaller>();

    final IConfigurationElement[] marshallerConfiguration = Platform.getExtensionRegistry().getConfigurationElementsFor(
            ActivitiPlugin.EXPORT_MARSHALLER_EXTENSIONPOINT_ID);

    for (IConfigurationElement e : marshallerConfiguration) {
      Object o;
      try {
        o = e.createExecutableExtension("class");
        if (o instanceof ExportMarshaller) {
          final ExportMarshaller exportMarshaller = (ExportMarshaller) o;
          result.put(exportMarshaller.getMarshallerName(), exportMarshaller);
        }
      } catch (CoreException e1) {
        e1.printStackTrace();
      }
    }
    return result;
  }

  private static final Map<String, ProcessValidator> getProcessValidatorsAndIds() {
    final Map<String, ProcessValidator> result = new HashMap<String, ProcessValidator>();

    final IConfigurationElement[] validatorConfiguration = Platform.getExtensionRegistry().getConfigurationElementsFor(
            ActivitiPlugin.PROCESS_VALIDATOR_EXTENSIONPOINT_ID);

    if (validatorConfiguration.length > 0) {
      for (final IConfigurationElement e : validatorConfiguration) {

        try {
          final Object o = e.createExecutableExtension("class");
          if (o instanceof ProcessValidator) {
            final ProcessValidator processValidator = (ProcessValidator) o;
            result.put(processValidator.getValidatorId(), processValidator);
          }
        } catch (CoreException e1) {
          e1.printStackTrace();
        }

      }
    }
    return result;
  }

  private static class IconProviderComparator implements Comparator<IconProvider> {

    @Override
    public int compare(final IconProvider iconProvider1, final IconProvider iconProvider2) {
      return iconProvider2.getPriority().compareTo(iconProvider1.getPriority());
    }

  }

}
