package uk.co.imperatives.exercise.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.imperatives.exercise.model.Guest;

public interface GuestRepository extends CrudRepository<Guest, String> {
}
