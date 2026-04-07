package com.biere.catalog.infrastructure.configuration

import com.biere.catalog.domain.exception.RateLimitException
import org.springframework.http.HttpStatus
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.ConsumptionProbe
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitInterceptor(
    @Value("\${rate-limit.capacity:20}") private val capacity: Long,
    @Value("\${rate-limit.refill-tokens:20}") private val refillTokens: Long,
    @Value("\${rate-limit.refill-duration-minutes:1}") private val refillDurationMinutes: Long
): HandlerInterceptor {
    private val cache: MutableMap<String, Bucket> = ConcurrentHashMap()

    private fun getBucket(requesterIdentifier: String): Bucket {
        return cache.computeIfAbsent(requesterIdentifier) { createBucket() }
    }

    private fun createBucket(): Bucket {
        val bandwidth: Bandwidth = Bandwidth.builder()
            .capacity(capacity)
            .refillGreedy(refillTokens, Duration.ofMinutes(refillDurationMinutes))
            .build()
        return Bucket.builder().addLimit(bandwidth).build()
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: kotlin.Any): Boolean {
        val ipAddress: String = if (request.getHeader("X-Forwarded-For") != null) {
            request.getHeader("X-Forwarded-For").split(",")[0]
        } else {
            request.remoteAddr
        }
        val bucket: Bucket = this.getBucket(ipAddress)
        val probe: ConsumptionProbe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            response.addHeader("X-Rate-Limit-Total", capacity.toString())
            return true
        }
        val waitForRefill = probe.nanosToWaitForRefill / 1_000_000_000
        response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
        throw RateLimitException("You have exhausted your API Request Quota", retryAfterSeconds = waitForRefill)
    }
}