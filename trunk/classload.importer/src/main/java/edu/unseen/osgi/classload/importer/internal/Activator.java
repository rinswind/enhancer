package edu.unseen.osgi.classload.importer.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.unseen.osgi.classload.exporter.goodbie.Goodbie;
import edu.unseen.osgi.enhancer.Enhancer;
import edu.unseen.osgi.enhancer.Namers;

public class Activator implements BundleActivator {
  private final Enhancer enhancer = new Enhancer(
    getClass().getClassLoader(), Namers.suffixNamer("__proxy__"), new PassthroughGenerator());
  
  public void start(BundleContext context) throws Exception {
    ServiceReference ref = context.getServiceReference(Goodbie.class.getName());
    Goodbie delegate = (Goodbie) context.getService(ref);
    
    Class<Goodbie> proxyClass = enhancer.enhance(Goodbie.class);
    Goodbie proxy = proxyClass.getConstructor(Goodbie.class).newInstance(delegate);
    
    System.out.println(proxy.hello("importer"));
    System.out.println(proxy.goodbie("importer"));
  }

  public void stop(BundleContext context) throws Exception {
  }
}
