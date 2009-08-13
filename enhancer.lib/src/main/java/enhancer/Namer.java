package enhancer;

public interface Namer {
  /**
   * Map a target class name to an enhancement class name.
   * 
   * @param target name of the class to enhance.
   * @return
   */
  String map(String target);

  /**
   * Try to extract the target class name out of an enhancement class name.
   * 
   * @param name class name on which to try the extraction.
   * @return the extracted original class name. In case the passed name does not
   *         denote an enhancement class returns <code>null</code>
   */
  String unmap(String name);
}
