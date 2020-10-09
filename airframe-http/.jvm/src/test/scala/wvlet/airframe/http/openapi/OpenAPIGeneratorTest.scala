/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wvlet.airframe.http.openapi
import wvlet.airframe.metrics.{Count, DataSize, ElapsedTime}
import wvlet.airframe.surface.Surface
import wvlet.airspec.AirSpec

/**
  */
object OpenAPIGeneratorTest extends AirSpec {
  test("Support DataType") {
    val schema = OpenAPIGenerator.getOpenAPISchema(Surface.of[ElapsedTime], false, Set.empty)
    info(schema)
  }

  case class Resp(code: Int, message: String)

  test("Seq[Object]") {
    val schema = OpenAPIGenerator.getOpenAPISchema(Surface.of[Seq[Resp]], false, Set.empty)
    info(schema)
  }

}
