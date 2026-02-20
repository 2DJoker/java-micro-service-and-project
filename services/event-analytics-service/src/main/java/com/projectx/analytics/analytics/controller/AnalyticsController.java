package com.projectx.analytics.analytics.controller;

import com.projectx.analytics.analytics.dto.FunnelReportResponse;
import com.projectx.analytics.analytics.dto.TopProductMetricResponse;
import com.projectx.analytics.analytics.service.AnalyticsQueryService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

  private final AnalyticsQueryService analyticsQueryService;

  public AnalyticsController(AnalyticsQueryService analyticsQueryService) {
    this.analyticsQueryService = analyticsQueryService;
  }

  @GetMapping("/funnel")
  public FunnelReportResponse getFunnel(
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to
  ) {
    Instant toSafe = to == null ? Instant.now() : to;
    Instant fromSafe = from == null ? toSafe.minus(7, ChronoUnit.DAYS) : from;

    if (!toSafe.isAfter(fromSafe)) {
      throw new IllegalArgumentException("to must be greater than from");
    }

    return analyticsQueryService.getFunnel(fromSafe, toSafe);
  }

  @GetMapping("/top-products")
  public List<TopProductMetricResponse> getTopProducts(
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to,
      @RequestParam(defaultValue = "20") int limit
  ) {
    Instant toSafe = to == null ? Instant.now() : to;
    Instant fromSafe = from == null ? toSafe.minus(7, ChronoUnit.DAYS) : from;
    int safeLimit = Math.max(1, Math.min(100, limit));

    if (!toSafe.isAfter(fromSafe)) {
      throw new IllegalArgumentException("to must be greater than from");
    }

    return analyticsQueryService.getTopProducts(fromSafe, toSafe, safeLimit);
  }
}
