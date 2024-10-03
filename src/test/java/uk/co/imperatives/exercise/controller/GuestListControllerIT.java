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
import uk.co.imperatives.exercise.dto.AddGuestRequestDto;
import uk.co.imperatives.exercise.service.AddGuestRequest;
import uk.co.imperatives.exercise.service.GuestListServiceInterface;
import uk.co.imperatives.exercise.service.PartyTableServiceInterface;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GuestListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GuestListServiceInterface guestListService;

    @Autowired
    private PartyTableServiceInterface partyTableService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private AddGuestRequestDto addGuestRequestDto;

    @BeforeEach
    public void setup() {
        // Clear the tables before each test
        jdbcTemplate.execute("DELETE FROM party_table");
        jdbcTemplate.execute("DELETE FROM guest_list_entry");

        // Set up test guest request DTO
        addGuestRequestDto = AddGuestRequestDto.builder()
                .name("Mickey Mouse")
                .table(1)
                .accompanyingGuests(2)
                .build();

        // Prepare database by adding tables and initial guests
        partyTableService.addTable(1, 10);  // Add a table with 10 seats
        partyTableService.addTable(2, 8);   // Add a table with 8 seats
        guestListService.addGuest(AddGuestRequest.builder()
                .name("Betty Boop")
                .table(1)
                .accompanyingGuests(1)
                .build());  // Add initial guest
    }

    @Test
    public void addGuestShouldReturnCreated() throws Exception {
        // Perform the POST request to add a new guest
        mockMvc.perform(post("/guest_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mickey Mouse"))
                .andExpect(jsonPath("$.tableNumber").value(1))
                .andExpect(jsonPath("$.accompanyingGuests").value(2));

        // Perform GET request to check available seats at the tables
        mockMvc.perform(get("/seats_empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats_empty").value(13));
    }

    @Test
    public void getAllGuestsShouldReturnOk() throws Exception {
        // Perform the GET request to retrieve all guests
        mockMvc.perform(get("/guest_list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Betty Boop"))
                .andExpect(jsonPath("$[0].tableNumber").value(1))
                .andExpect(jsonPath("$[0].accompanyingGuests").value(1));
    }

    @Test
    public void deleteGuestShouldReturnNoContent() throws Exception {
        // Perform the DELETE request to remove a guest
        mockMvc.perform(delete("/guest_list/Betty Boop"))
                .andExpect(status().isNoContent());

        // Perform GET request to check available seats at the tables
        mockMvc.perform(get("/seats_empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats_empty").value(18));
    }

    @Test
    public void recordGuestArrivalShouldReturnOk() throws Exception {
        // First perform POST request to add guest to the guest list
        mockMvc.perform(post("/guest_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mickey Mouse"))
                .andExpect(jsonPath("$.tableNumber").value(1))
                .andExpect(jsonPath("$.accompanyingGuests").value(2));

        // arrival with more guests
        addGuestRequestDto.setAccompanyingGuests(3);

        // Perform the PUT request to record guest arrival
        mockMvc.perform(put("/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mickey Mouse"))
                .andExpect(jsonPath("$.tableNumber").value(1))
                .andExpect(jsonPath("$.accompanyingGuests").value(3))
                .andExpect(jsonPath("$.timeArrived").isNotEmpty());

        // Perform GET request to check available seats at the tables
        mockMvc.perform(get("/seats_empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats_empty").value(12));
    }

    @Test
    public void getEmptySeats_ShouldReturnOk() throws Exception {
        // Perform the GET request to check available seats at the tables
        mockMvc.perform(get("/seats_empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seats_empty").value(16));  // Initially 18, now 16 after guests added
    }

    // New tests based on your request

    @Test
    public void updateGuestNameShouldReturnOk() throws Exception {
        // Update "Betty Boop" to "Daffy Duck"
        mockMvc.perform(put("/guest_list/Betty Boop/name")
                        .param("newName", "Daffy Duck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Daffy Duck"));
    }

    @Test
    public void recordGuestLeavingShouldReturnOk() throws Exception {
        // First perform POST request to add guest to the guest list
        mockMvc.perform(post("/guest_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mickey Mouse"));

        // Perform the DELETE request to record guest leaving
        mockMvc.perform(delete("/guests/Mickey Mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mickey Mouse"))
                .andExpect(jsonPath("$.timeLeft").isNotEmpty());
    }

    @Test
    public void getGuestsAtAllTablesShouldReturnOk() throws Exception {
        // Perform the GET request to retrieve guests at all tables
        mockMvc.perform(get("/guests_at_table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableNumber").value(1))
                .andExpect(jsonPath("$[0].guests[0]").value("Betty Boop"))
                .andExpect(jsonPath("$[1].tableNumber").value(2))
                .andExpect(jsonPath("$[1].guests").isEmpty());
    }

    @Test
    public void getArrivedGuestsShouldReturnOk() throws Exception {
        // First perform POST request to add guest to the guest list and mark arrival
        mockMvc.perform(post("/guest_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addGuestRequestDto)))
                .andExpect(status().isOk());

        // Perform the GET request to retrieve only arrived guests
        mockMvc.perform(get("/guests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mickey Mouse"))
                .andExpect(jsonPath("$[0].timeArrived").isNotEmpty());
    }
}
