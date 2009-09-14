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
package enhancer;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Manages a single code {@link Generator}. Maintains a one-to-many class load
 * bridge registry where "one" is the class space used to support the generated
 * classes and "many" refers to the multiple application spaces, which own the
 * classes enhanced by the generator.
 * 
 * @author rinsvind@gmail.com (Todor Boev)
 */
public class Enhancer {
  private final ClassLoader privateSpace;
  private final Namer namer;
  private final Generator generator;
  private final Map<ClassLoader, WeakReference<ClassLoader>> cache;

  public Enhancer(ClassLoader privateSpace, Namer naming, Generator generator) {
    this.privateSpace = privateSpace;
    this.namer = naming;
    this.generator = generator;
    this.cache = new WeakHashMap<ClassLoader, WeakReference<ClassLoader>>();
  }

  @SuppressWarnings("unchecked")
  public <T> Class<T> enhance(Class<T> target) throws ClassNotFoundException {
    ClassLoader context = resolveBridge(target.getClassLoader());
    String enhancementName = namer.map(target.getName());
    return (Class<T>) context.loadClass(enhancementName);
  }

  private synchronized ClassLoader resolveBridge(ClassLoader appSpace) {
    ClassLoader bridge = null;

    WeakReference<ClassLoader> ref = cache.get(appSpace);
    if (ref != null) {
      bridge = ref.get();
    }

    if (bridge == null) {
      bridge = makeBridge(appSpace);
      cache.put(appSpace, new WeakReference<ClassLoader>(bridge));
    }

    return bridge;
  }

  private ClassLoader makeBridge(final ClassLoader appSpace) {
    return new ClassLoader(appSpace) {
      @Override
      public String toString() {
        return "BridgeClassLoader[ app: " + appSpace + ", private: " + privateSpace + " ]";
      }

      @Override
      protected Class<?> findClass(String name) throws ClassNotFoundException {
        /* Is this used privately by the enhancements? */
        if (generator.isInternal(name)) {
          return privateSpace.loadClass(name);
        }

        /* Is this a request for enhancement? */
        String unpacked = namer.unmap(name);
        if (unpacked != null) {
          byte[] raw = generator.generate(unpacked, name, this);
          return defineClass(name, raw, 0, raw.length);
        }

        /* Ask someone else */
        throw new ClassNotFoundException(name);
      }
    };
  }
}
