package uk.co.imperatives.exercise.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.imperatives.exercise.model.PartyTable;

public interface PartyTableRepository extends CrudRepository<PartyTable, Integer> {
}
