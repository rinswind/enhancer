package enhancer;

public interface Generator {
  /**
   * Tests if the passed class is used by the implementation of the
   * enhancements. Used to route class load requests.
   * 
   * @param className Name of tested class.
   * @return <code>true</code> if the tested class is used privately by the
   *         generated enhancements.
   */
  boolean isInternal(String className);

  /**
   * Generate the raw bytes of an enhancement. The generation is usually driven
   * from reflection. The classes to examine are loaded from the context
   * <code>ClassLoader</code>. Sometimes the generation can be driven from the
   * raw bytes of the input class. To do this convert <code>inputClass</code> to
   * a class file name and try to obtain it't content via
   * <code>ClassLoader.getResource</code>.
   * 
   * @param inputClass name of the class to enhance.
   * @param outputClass name of the resulting class.
   * @param context source of classes and resources to drive code generation.
   *          The context must provide all classes needed to link the input
   *          class.
   * @return
   */
  byte[] generate(String inputClass, String outputClass, ClassLoader context);
}
