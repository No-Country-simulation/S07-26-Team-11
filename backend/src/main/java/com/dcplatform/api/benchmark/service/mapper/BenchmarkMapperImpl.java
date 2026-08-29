package com.dcplatform.api.benchmark.service.mapper;

import com.dcplatform.api.benchmark.model.BenchmarkDimension;
import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.BenchmarkOption;
import com.dcplatform.api.benchmark.model.BenchmarkQuestion;
import com.dcplatform.api.benchmark.model.dto.ActiveBenchmarkResponse;
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
