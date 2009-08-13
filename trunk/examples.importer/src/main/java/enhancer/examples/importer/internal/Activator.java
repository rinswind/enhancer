package enhancer.examples.importer.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.generator.aop.Logging;
import enhancer.examples.generator.proxy.ServiceProxies;

public class Activator implements BundleActivator {
  public void start(BundleContext bc) throws Exception {
    /* Build a service proxy */
    Class<Goodbye> serviceProxyClass = ServiceProxies.serviceProxy(Goodbye.class);
    Goodbye serviceProxy = serviceProxyClass.getConstructor(BundleContext.class).newInstance(bc);
    
    /* Wrap it in a logging proxy */
    Class<Goodbye> logProxyClass = Logging.withLogging(Goodbye.class);
    Goodbye logProxy = logProxyClass.getConstructor(Goodbye.class).newInstance(serviceProxy);
    
    /* Drive the proxy stack */
    System.out.println(logProxy.goodbie("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
