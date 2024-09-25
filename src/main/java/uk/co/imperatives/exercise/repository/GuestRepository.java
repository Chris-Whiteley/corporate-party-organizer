package uk.co.imperatives.exercise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.imperatives.exercise.model.Guest;

public interface GuestRepository extends JpaRepository<Guest, String> {
}
