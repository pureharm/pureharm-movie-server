/*
 * Copyright 2021 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package phms

package object test {
  val Ignore: munit.Tag = munit.Ignore
  val Only:   munit.Tag = munit.Only
  val Flaky:  munit.Tag = munit.Flaky
  val Fail:   munit.Tag = munit.Fail
  val Slow:   munit.Tag = munit.Slow

  type TestOptions = munit.TestOptions
  val TestOptions: munit.TestOptions.type = munit.TestOptions
}
