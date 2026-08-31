package com.dcplatform.api.benchmark.repository;

import com.dcplatform.api.benchmark.model.BenchmarkAnswer;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BenchmarkAnswerRepository extends JpaRepository<BenchmarkAnswer, UUID> {
	List<BenchmarkAnswer> findByResponseId(UUID responseId);

	// UPSERT Nativo: Si no existe, inserta. Si ya existe la combinación (response_id, question_id), actualiza la opción
	@Modifying
	@Query(
			nativeQuery = true,
			value = """
					    INSERT INTO benchmark_answers (id, response_id, question_id, option_id, answered_at)
					    VALUES (gen_random_uuid(), :responseId, :questionId, :optionId, NOW())
					    ON CONFLICT (response_id, question_id)
					    DO UPDATE SET option_id = :optionId, answered_at = NOW()
					"""
	)
	void upsertAnswer(UUID responseId, UUID questionId, UUID optionId);

	int countByResponse(BenchmarkResponse response);
}
