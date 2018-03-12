

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Method;

/**
 *
 * @author Franck Arnulfo
 */
public class Main {

  /**
   * @param args the command line arguments
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws IOException {
    String jarPath = args[0];
    String targetClass = args[1];
    String targetMethodDeclaration = args[2];

    JarFile jarFile = new JarFile(jarPath);
    Enumeration<JarEntry> entries = jarFile.entries();
    Method targetMethod = Method.getMethod(targetMethodDeclaration);
    AppClassVisitor cv = new AppClassVisitor(targetClass, targetMethod);

    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();

      if (entry.getName().endsWith(".class")) {
        try (InputStream stream = new BufferedInputStream(jarFile.getInputStream(entry), 1024)) {
          ClassReader reader = new ClassReader(stream);

          reader.accept(cv, 0);
        }
      }
    }

    for (Callee c : cv.callees) {
      System.out.println(c.source + ":" + c.line + " " + c.className + " " + c.methodName + " " + c.methodDesc);
    }
  }

}
