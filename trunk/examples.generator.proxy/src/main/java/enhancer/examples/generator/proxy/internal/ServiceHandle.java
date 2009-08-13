package enhancer.examples.generator.proxy.internal;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.ServiceEvent.UNREGISTERING;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class ServiceHandle<T> implements ServiceListener {
  private final BundleContext bc;
  private final Class<T> type;
  private ServiceReference ref;
  private T service;

  /**
   * Called from BundleActivator.start().
   * 
   * (management control flow)
   */
  public ServiceHandle(Class<T> type, BundleContext bc) {
    this.type = type;
    this.bc = bc;

    /* Track events for services of type T */
    try {
      bc.addServiceListener(this, "(" + OBJECTCLASS + "=" + type.getName() + ")");
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException("Unexpected: filter is correct", e);
    }
  }

  /**
   * Called by the app when it needs the service.
   * 
   * (application control flow)
   */
  @SuppressWarnings("unchecked")
  public synchronized T get() {
    /* Lazy-bind to a suitable service */
    if (service == null) {
      ref = bc.getServiceReference(type.getName());

      /* Can't find a service - fail fast */
      if (ref == null) {
        throw new RuntimeException("Service " + type + " unavailable.");
      }

      service = (T) bc.getService(ref);
    }

    return service;
  }

  /**
   * Called by the container when services come and go.
   * 
   * (management control flow)
   */
  public synchronized void serviceChanged(ServiceEvent e) {
    /* Is a service going away? */
    if (UNREGISTERING == e.getType()) {
      /* Is this the service we hold? */
      if (ref == e.getServiceReference()) {
        /* Release the service */
        service = null;
        ref = null;
      }
    }
  }
}
