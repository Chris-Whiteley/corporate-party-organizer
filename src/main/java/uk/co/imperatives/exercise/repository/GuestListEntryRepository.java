package uk.co.imperatives.exercise.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.imperatives.exercise.model.GuestListEntry;

public interface GuestListEntryRepository extends CrudRepository<GuestListEntry, String> {
}
