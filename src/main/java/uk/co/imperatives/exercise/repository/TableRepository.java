package uk.co.imperatives.exercise.repository;

import org.springframework.data.repository.CrudRepository;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.model.Table;

public interface TableRepository extends CrudRepository<Table, Integer> {
}
