package enhancer.examples.exporter.goodbye;

import enhancer.examples.exporter.hello.Hello;

public interface Goodbye extends Hello {
  GoodbyeMessage goodbie(String who);
}
