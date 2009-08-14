/**
 * Copyright (C) 2009 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            return "Goodbye " + who;
          }
        };
      }
    }, null);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }
}
