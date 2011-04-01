package org.activiti.designer.property.extension;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.activiti.designer.integration.Activator;

/**
 * Loads classes from jar files in the path provided.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class JarClassLoader extends ClassLoader {

  private String jarFilePath;

  public JarClassLoader(final String jarFilePath) {
    if (jarFilePath == null)
      throw new IllegalArgumentException("Null jarFilePath");
    this.jarFilePath = jarFilePath;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

    // Since all support classes of loaded class use same class loader
    // must check subclass cache of classes for things like Object
    Class c = null;

    // First check with the parent class loader
    try {
      c = super.getParent().loadClass(name);
    } catch (ClassNotFoundException e) {
      // fail silently
    }

    c = findLoadedClass(name);

    if (c == null) {
      // first, delegate to the integration bundle's class loader
      try {
        c = Activator.class.getClassLoader().loadClass(name);
      } catch (ClassNotFoundException e) {
        // fail silently
      }
    }

    if (c == null) {
      // Convert class name argument to filename
      // Convert package names into subdirectories
      String className = name.replace('.', '/') + ".class";

      try {
        // Load class data from file and save in byte array
        byte data[] = loadClassData(jarFilePath, className);

        // Convert byte array to Class
        c = defineClass(name, data, 0, data.length);

        // If failed, throw exception
        if (c == null)
          throw new ClassNotFoundException(name);

      } catch (IOException e) {
        throw new ClassNotFoundException("Error reading file: " + className);
      }
    }

    // Resolve class definition if appropriate
    if (resolve)
      resolveClass(c);

    // Return class just created
    return c;
  }

  private byte[] loadClassData(String jarFilePath, String className) throws IOException {

    if (className == null) {
      throw new IOException("Unable to load classes with null className");
    }

    // Get jar file from the provided path
    JarFile file = new JarFile(jarFilePath);
    JarEntry entry = file.getJarEntry(className);
    InputStream is = file.getInputStream(entry);

    if (is == null) {
      throw new IOException("Unable to load class with name " + className + " because the inputstream was null");
    }

    // Get size of class file
    int size = (int) entry.getSize();

    // Reserve space to read
    byte buff[] = new byte[size];

    // Get stream to read from
    DataInputStream dis = new DataInputStream(is);

    // Read in data
    dis.readFully(buff);

    // close stream
    dis.close();

    // return data
    return buff;
  }
}