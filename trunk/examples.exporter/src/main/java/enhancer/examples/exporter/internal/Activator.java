package enhancer.examples.exporter.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.exporter.goodbye.GoodbyeMessage;
import enhancer.examples.exporter.hello.HelloMessage;

public class Activator implements BundleActivator {
  @Override
  public void start(BundleContext context) throws Exception {
    context.registerService(Goodbye.class.getName(), new Goodbye() {
      @Override
      public HelloMessage hello(final String who) {
        return new HelloMessage() {
          @Override
          public String toString() {
            return "Hello " + who;
          }
        };
      }

      @Override
      public GoodbyeMessage goodbie(final String who) {
        return new GoodbyeMessage() {
          @Override
          public String toString() {
            return "Goodbie " + who;
          }
        };
      }
    }, null);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }
}
