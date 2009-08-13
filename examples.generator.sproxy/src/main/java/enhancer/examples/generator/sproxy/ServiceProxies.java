package enhancer.examples.generator.sproxy;

import enhancer.Enhancer;
import enhancer.Namers;
import enhancer.examples.generator.sproxy.internal.ServiceProxyGenerator;

public final class ServiceProxies {
  /* Static utility */
  private ServiceProxies() {
  }
  
  /*
   * Build an Enhancer on top of the current class space. E.g. the class space
   * of the bundle where this class is packaged. The created proxy classes will
   * have a suffix "$__service_proxy__".
   */
  private static final Enhancer enhancer = new Enhancer(
    ServiceProxies.class.getClassLoader(), 
    Namers.suffixNamer("__service_proxy__"), 
    new ServiceProxyGenerator());

  public static <T> Class<T> serviceProxy(Class<T> target) throws ClassNotFoundException {
    return enhancer.enhance(target);
  }
}
