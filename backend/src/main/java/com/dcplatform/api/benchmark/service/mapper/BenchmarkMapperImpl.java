package com.dcplatform.api.benchmark.service.mapper;

import com.dcplatform.api.benchmark.model.*;
import com.dcplatform.api.benchmark.model.dto.ActiveBenchmarkResponse;
import com.dcplatform.api.benchmark.model.dto.StartBenchmark;
import org.springframework.stereotype.Component;

@Component
public class BenchmarkMapperImpl implements BenchmarkMapper {

	@Override
	public ActiveBenchmarkResponse toActiveBenchmarkResponse(BenchmarkInstrument entity) {
		return new ActiveBenchmarkResponse(
				entity.getId(),
				entity.getVersion(),
				entity.getDimensions().stream()
						.map(this::mapToStageDto)
						.toList()
		);
	}

	@Override
	public StartBenchmark.Response toStartBenchmarkResponse(BenchmarkResponse entity) {
		return new StartBenchmark.Response(
				entity.getId(),
				entity.getStartedAt(),
				entity.getStatus().name()
		);
	}

	private ActiveBenchmarkResponse.BenchmarkStage mapToStageDto(BenchmarkDimension entity) {
		var questions = entity.getQuestions().stream()
				.map(this::mapToQuestionDto)
				.toList();

		return new ActiveBenchmarkResponse.BenchmarkStage(
				entity.getId(),
				entity.getCode(),
				entity.getLabel(),
				questions
		);
	}

	private ActiveBenchmarkResponse.Question mapToQuestionDto(BenchmarkQuestion entity) {
		var options = entity.getOptions().stream()
				.map(this::mapToOptionDto)
				.toList();

		return new ActiveBenchmarkResponse.Question(
				entity.getId(),
				entity.getText(),
				entity.getHelpText(),
				options
		);
	}

	private ActiveBenchmarkResponse.Option mapToOptionDto(BenchmarkOption entity) {
		return new ActiveBenchmarkResponse.Option(
				entity.getId(),
				entity.getLabel()
		);
	}
}
