package com.dcplatform.api.pdf.provider;

import java.util.Map;
import java.util.UUID;

public interface PdfTemplateDataProvider {
	/**
	 * @param sourceId El ID de origen
	 * @return Las variables listas para inyectar en Thymeleaf
	 */
	Map<String, Object> provideData(UUID sourceId);
}
