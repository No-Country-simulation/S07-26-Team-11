package com.dcplatform.api.documents;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, UUID> {

    /** Del mas reciente al mas antiguo, como espera el listado de la API. */
    List<UserDocument> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserDocument> findByUserIdAndName(UUID userId, String name);

    List<UserDocument> findAllByOrderByCreatedAtDesc();
}
