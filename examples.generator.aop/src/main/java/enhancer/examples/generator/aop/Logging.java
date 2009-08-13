package enhancer.examples.generator.aop;

import enhancer.Enhancer;
import enhancer.Namers;

public final class Logging {
  /* Static utility */
  private Logging() {
  }
  
  /*
   * Build an Enhancer on top of the current class space. E.g. the class space
   * of the bundle where this class is packaged. The created proxy classes will
   * have a suffix "$__logging__".
   */
  private static final Enhancer enhancer = new Enhancer(
    Logging.class.getClassLoader(), 
    Namers.suffixNamer("__logging__"), 
    new LoggingGenerator());

  public static <T> Class<T> withLogging(Class<T> target) throws ClassNotFoundException {
    return enhancer.enhance(target);
  }
}
