package com.dcplatform.api.magiclink.repository;

import com.dcplatform.api.magiclink.model.MagicLinkAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MagicLinkAccessTokenRepository extends JpaRepository<MagicLinkAccessToken, UUID> {
	Optional<MagicLinkAccessToken> findByTokenHash(String hash);
}
