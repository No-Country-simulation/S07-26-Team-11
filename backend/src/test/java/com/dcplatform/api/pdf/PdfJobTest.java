package com.dcplatform.api.pdf;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class PdfJobTest {

    @Test
    void shouldCreatePendingJobWithResponseId() {
        // given: creo un job nuevo
        UUID responseId = UUID.randomUUID();
        PdfJob job = new PdfJob(responseId);

        // then: verifica que nace en PENDING y con el responseId correcto
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.PENDING);
        assertThat(job.getResponseId()).isEqualTo(responseId);
        assertThat(job.getAttempts()).isEqualTo(0);
        assertThat(job.getCreatedAt()).isNull(); // aún no se persiste, JPA no ejecutó @PrePersist
    }

    @Test
    void markProcessingShouldSetStatusAndIncrementAttempts() {
        // given: un job recién creado
        PdfJob job = new PdfJob(UUID.randomUUID());

        // when: lo marco como procesando
        job.markProcessing();

        // then
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.PROCESSING);
        assertThat(job.getAttempts()).isEqualTo(1);
        assertThat(job.getStartedAt()).isNotNull();
        assertThat(job.getFailureReason()).isNull(); // se limpia el error anterior si lo hubiera
    }

    @Test
    void markDoneShouldSetStatusAndFinishedAt() {
        // given: un job en procesamiento
        PdfJob job = new PdfJob(UUID.randomUUID());
        job.markProcessing();

        // when: termina exitosamente
        job.markDone();

        // then
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.DONE);
        assertThat(job.getFinishedAt()).isNotNull();
        assertThat(job.getFailureReason()).isNull();
    }

    @Test
    void markFailedShouldReturnToPendingIfAttemptsBelowMax() {
        // given: job con 1 intento, máximo 3
        PdfJob job = new PdfJob(UUID.randomUUID());
        job.markProcessing(); // attempts = 1

        // when: falla
        job.markFailed("Error de red", 3);

        // then: como 1 < 3, vuelve a PENDING para reintentar
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.PENDING);
        assertThat(job.getStartedAt()).isNull(); // se resetea
        assertThat(job.getFailureReason()).isEqualTo("Error de red");
    }

    @Test
    void markFailedShouldSetFailedWhenAttemptsReachMax() {
        // given: job con 3 intentos (ya llegó al máximo)
        PdfJob job = new PdfJob(UUID.randomUUID());
        job.markProcessing(); // 1
        job.markFailed("error1", 3);
        job.markProcessing(); // 2
        job.markFailed("error2", 3);
        job.markProcessing(); // 3

        // when: falla por tercera vez
        job.markFailed("Error definitivo", 3);

        // then: 3 intentos = máximo alcanzado, queda FAILED
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.FAILED);
        assertThat(job.getAttempts()).isEqualTo(3);
        assertThat(job.getFinishedAt()).isNotNull();
    }

    @Test
    void markFailedShouldSetFailedImmediatelyIfMaxIsOne() {
        // given: máximo de 1 intento
        PdfJob job = new PdfJob(UUID.randomUUID());
        job.markProcessing(); // attempts = 1

        // when
        job.markFailed("Error", 1);

        // then: ya alcanzó el máximo
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.FAILED);
    }

    @Test
    void markProcessingTwiceShouldIncrementAttemptsToTwo() {
        // given
        PdfJob job = new PdfJob(UUID.randomUUID());
        job.markProcessing(); // attempts = 1
        job.markFailed("fallo temporal", 3); // vuelve a PENDING

        // when: se retoma
        job.markProcessing();

        // then
        assertThat(job.getAttempts()).isEqualTo(2);
        assertThat(job.getStatus()).isEqualTo(PdfJob.JobStatus.PROCESSING);
    }
}