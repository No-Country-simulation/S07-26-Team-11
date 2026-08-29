package com.dcplatform.api.benchmark;

import com.dcplatform.api.benchmark.model.dto.*;
import com.dcplatform.api.benchmark.service.BenchmarkService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/public/benchmark")
public class BenchmarkController {

	private final BenchmarkService benchmarkService;

	public BenchmarkController(BenchmarkService benchmarkService) {
		this.benchmarkService = benchmarkService;
	}

	@GetMapping(value = "/instrument", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ActiveBenchmarkResponse> getActiveBenchmark() {
		ActiveBenchmarkResponse response = benchmarkService.getActiveInstrument();
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/responses", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StartBenchmark.Response> startBenchmark(
			@AuthenticationPrincipal String leadEmail,
			@RequestBody StartBenchmark.Request request) {
		StartBenchmark.Response response = benchmarkService.startBenchmark(leadEmail, request);
		return ResponseEntity.created(URI.create("location")).body(response);
	}

	@PatchMapping(value = "/responses/{responseId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BenchmarkProgressResponse> saveProgress(
			@AuthenticationPrincipal String leadEmail,
			@PathVariable String responseId,
			@RequestBody SubmitBenchmarkRequest request) {
		BenchmarkProgressResponse response = benchmarkService.saveProgress(leadEmail, responseId, request);
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/responses/{responseId}/complete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompleteBenchmarkResponse> completeBenchmark(
			@AuthenticationPrincipal String leadEmail,
			@PathVariable String responseId) {
		CompleteBenchmarkResponse response = benchmarkService.completeBenchmark(leadEmail, responseId);
		return ResponseEntity.accepted().body(response);
	}

	@GetMapping(value = "/responses/{responseId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompleteBenchmarkResponse> getResponse(
			@AuthenticationPrincipal String leadEmail,
			@PathVariable String responseId) {
		CompleteBenchmarkResponse response = benchmarkService.getResponse(leadEmail, responseId);
		return ResponseEntity.ok(response);
	}
}
