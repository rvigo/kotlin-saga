package com.rvigo.saga.infra.repositories

import com.rvigo.saga.domain.Participant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ParticipantRepository : JpaRepository<Participant, UUID>
