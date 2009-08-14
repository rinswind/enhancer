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

/**
 * Dynamically generates enhancement classes. Each implementation performs a
 * single specific type of e enhancement (e.g. AOP advice) based on proxying.
 * 
 * @author rinsvind@gmail.com (Todor Boev)
 */
public interface Generator {
  /**
   * Tests if the passed class is used by the implementation of the
   * enhancements. Used to route class load requests.
   * 
   * @param className Name of tested class.
   * @return <code>true</code> if the tested class is used privately by the
   *         generated enhancements.
   */
  boolean isInternal(String className);

  /**
   * Generate the raw bytes of an enhancement. The generation is usually driven
   * from reflection. The classes to examine are loaded from the context
   * <code>ClassLoader</code>. Sometimes the generation can be driven from the
   * raw bytes of the input class. To do this convert <code>inputClass</code> to
   * a class file name and try to obtain it't content via
   * <code>ClassLoader.getResource</code>.
   * 
   * @param inputClass name of the class to enhance.
   * @param outputClass name of the resulting class.
   * @param context source of classes and resources to drive code generation.
   *          The context must provide all classes needed to link the input
   *          class.
   * @return the raw bytes of of a class. Some class loader must call
   *         <code>defineClass()</code> on these to introduce them into the JVM.
   */
  byte[] generate(String inputClass, String outputClass, ClassLoader context);
}
