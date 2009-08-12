package edu.unseen.osgi.enhancer;

public interface Generator {
  /**
   * Tests if the passed class is used by the implementation of the
   * enhancements.
   * 
   * @param name
   * @return
   */
  boolean isImplClass(String name);

  /**
   * @param inputName
   *          name of the class to enhance.
   * @param outputName
   *          name of the resulting class.
   * @param context
   *          used to load the target class and any classes related to the
   *          generation, which need to be examined via reflection. Also can be
   *          used to obtain the raw bytes of the enhanced class via
   *          <code>ClassLoader.getResource</code>.
   * @return
   */
  byte[] generate(String inputName, String outputName, ClassLoader context);
}
