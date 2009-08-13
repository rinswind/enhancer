package enhancer.examples.importer.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.generator.aop.Logging;

public class Activator implements BundleActivator {
  public void start(BundleContext bc) throws Exception {
    /* Get a raw object */
    ServiceReference ref = bc.getServiceReference(Goodbye.class.getName());
    Goodbye delegate = (Goodbye) bc.getService(ref);
    
    /* Wrap it in a logging proxy */
    Class<Goodbye> proxyClass = Logging.withLogging(Goodbye.class);
    Goodbye proxy = proxyClass.getConstructor(Goodbye.class).newInstance(delegate);
    
    /* Drive it */
    System.out.println(proxy.goodbie("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
