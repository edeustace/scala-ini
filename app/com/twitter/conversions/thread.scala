/*
 * Copyright 2010 Twitter Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter
package conversions

import java.util.concurrent.Callable

/**
 * Implicits for turning a block of code into a Runnable or Callable.
 */
object thread {
  implicit def makeRunnable(f: => Unit): Runnable = new Runnable() { def run() = f }

  implicit def makeCallable[T](f: => T): Callable[T] = new Callable[T]() { def call() = f }
}
