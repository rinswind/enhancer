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
 * A naming protocol for enhancement classes.
 * 
 * @author rinsvind@gmail.com (Todor Boev)
 */
public interface Namer {
  /**
   * Map a target class name to an enhancement class name.
   * 
   * @param target name of the class to enhance.
   * @return
   */
  String map(String target);

  /**
   * Try to extract the target class name out of an enhancement class name.
   * 
   * @param name class name on which to try the extraction.
   * @return the extracted original class name. In case the passed name does not
   *         denote an enhancement class returns <code>null</code>
   */
  String unmap(String name);
}
