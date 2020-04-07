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
package wvlet.airframe.control

case class RateLimiter(
    name: String = "rate-limiter",
    maxCapacity: Long,
    // Refill rate per second
    refillRate: Double
) {
  def withName(newName: String): RateLimiter             = this.copy(name = newName)
  def withMaxCapacity(newMaxCapacity: Long)              = this.copy(maxCapacity = maxCapacity)
  def withRefillRate(newRefillRate: Double): RateLimiter = this.copy(refillRate = newRefillRate)
}

/**
  *
  */
object RateLimiter {

  case class RateLimitedException(rateLimiter: RateLimiter)
      extends Exception(
        f"Request rate is limited. capacity:${rateLimiter.maxCapacity}, refill rate:${rateLimiter.refillRate}%.2f"
      )

  /**
    * Define basic rate limiter configurations.
    * Based on https://docs.aws.amazon.com/AWSEC2/latest/APIReference/throttling.html#throttling-limits-rate-based
    */
  object standards {
    val nonMutatingRequestLimit = RateLimiter(name = "non-mutating requests", maxCapacity = 100, refillRate = 20)
    val nonMutatingRequestLimitForConsole =
      RateLimiter(name = "console non-mutating requests", maxCapacity = 100, refillRate = 10)
    val mutatingRequestLimit = RateLimiter(name = "mutating requests", maxCapacity = 200, refillRate = 5)

    val resourceIntensiveOperationLimit =
      RateLimiter(name = "resource-intensive operations", maxCapacity = 50, refillRate = 5)

    val lightOperationLimit      = RateLimiter(name = "light operations", maxCapacity = 10, refillRate = 1)
    val heavyOperationLimit      = RateLimiter(name = "heavy operations", maxCapacity = 1, refillRate = 1)
    val superHeavyOperationLimit = RateLimiter(name = "super-heavy operations", maxCapacity = 1, refillRate = 0.1)

    val resourceRequestLimit       = RateLimiter(name = "resource requests", maxCapacity = 1000, refillRate = 2)
    val deleteResourceRequestLimit = RateLimiter(name = "delete resource requests", maxCapacity = 1000, refillRate = 20)
  }
  ///def tokenBucket: RateLimiter = RateLimiter()
  //def leakyBucket: RateLimitter =

}
