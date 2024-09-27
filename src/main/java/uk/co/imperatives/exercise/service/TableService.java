package uk.co.imperatives.exercise.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TableService implements TableServiceInterface {

    @Override
    public int getTableWithAvailableSeating(int noOfSeats) {
        System.out.println("Real method called for getTableWithAvailableSeating");
       log.info("Real method called for getTableWithAvailableSeating");
        return 0;
    }

    @Override
    public void decreaseOccupancy(int table, int noOfSeats) {
        System.out.println("Real method called for decreaseOccupancy");
        log.info("Real method called for decreaseOccupancy");
    }

    @Override
    public void increaseOccupancy(int table, int noOfSeats) {
        System.out.println("Real method called for increaseOccupancy");
        log.info("Real method called for increaseOccupancy");
    }

    @Override
    public boolean hasAvailability(int table, int noOfSeats) {
        System.out.println("Real method called for hasAvailability");
        log.info("Real method called for hasAvailability");
        return false;
    }
}
