package com.dcplatform.api.magiclink.service;

import java.util.UUID;

public interface MagicLinkService {
	String generateMagicLinkAndInform(String email, UUID subjectId, String clientIp);

	String verifyAndExchange(String tokenFromMagicLink);
}
