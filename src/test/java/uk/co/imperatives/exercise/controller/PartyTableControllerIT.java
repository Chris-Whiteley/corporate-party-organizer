package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.imperatives.exercise.ExerciseApplication;
import uk.co.imperatives.exercise.dto.AddTableRequest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ExerciseApplication.class)
@AutoConfigureMockMvc
class PartyTableControllerIT {
    private static final String URL = "/party_tables";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AddTableRequest addTableRequest;


    @BeforeEach
    void setUp()  {
        // Clear the table before each test
        jdbcTemplate.execute("DELETE FROM party_table");
        addTableRequest = new AddTableRequest();
        addTableRequest.setTableNumber(1);
        addTableRequest.setNoOfSeats(10);
    }

    @Test
    void testAddTableWithGivenNumber() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.noOfSeats", is(10)));
    }

    @Test
    void testAddTableSystemAssignsFreeNumber() throws Exception {
        // First add some existing tables with numbers 1,3,4
        addTableRequest = new AddTableRequest();
        addTableRequest.setTableNumber(1);
        addTableRequest.setNoOfSeats(6);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated());

        addTableRequest = new AddTableRequest();
        addTableRequest.setTableNumber(3);
        addTableRequest.setNoOfSeats(6);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated());

        addTableRequest = new AddTableRequest();
        addTableRequest.setTableNumber(4);
        addTableRequest.setNoOfSeats(6);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated());

        // now add a table but don't provide number, system should do that  (should assign 2)
        addTableRequest = new AddTableRequest();
        addTableRequest.setNoOfSeats(10);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is(2)))
                .andExpect(jsonPath("$.noOfSeats", is(10)));

        // now add another table and don't provide the number, system should do that  (should assign 5)
        addTableRequest = new AddTableRequest();
        addTableRequest.setNoOfSeats(8);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is(5)))
                .andExpect(jsonPath("$.noOfSeats", is(8)));
    }

    @Test
    void testGetAllTables() throws Exception {
        // First add a table before fetching
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void testRemoveTable() throws Exception {
        // First add a table
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTableRequest)))
                .andExpect(status().isCreated());

        // Now remove the table
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }
}
