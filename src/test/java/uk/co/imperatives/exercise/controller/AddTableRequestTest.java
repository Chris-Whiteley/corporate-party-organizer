package uk.co.imperatives.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.imperatives.exercise.dto.AddTableRequest;

import static org.junit.jupiter.api.Assertions.*;

public class AddTableRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper(); // Jackson object mapper for JSON operations
    }

    @Test
    public void testSerializeToJson() throws Exception {
        // Create an AddTableRequest object
        AddTableRequest addTableRequest = new AddTableRequest(1, 10);

        // Serialize the object to JSON
        String jsonResult = objectMapper.writeValueAsString(addTableRequest);

        // Check if the JSON string contains the correct data
        assertTrue(jsonResult.contains("\"tableNumber\":1"));
        assertTrue(jsonResult.contains("\"noOfSeats\":10"));
    }

    @Test
    public void testDeserializeFromJson() throws Exception {
        // JSON string to deserialize
        String jsonInput = "{\"tableNumber\":1,\"noOfSeats\":10}";

        // Deserialize the JSON string into an AddTableRequest object
        AddTableRequest result = objectMapper.readValue(jsonInput, AddTableRequest.class);

        // Check if the object is correctly populated
        assertEquals(1, result.getTableNumber());
        assertEquals(10, result.getNoOfSeats());
    }

    @Test
    public void testSerializeWithNullValues() throws Exception {
        // Create an AddTableRequest object with null values
        AddTableRequest addTableRequest = new AddTableRequest(null, null);

        // Serialize the object to JSON
        String jsonResult = objectMapper.writeValueAsString(addTableRequest);

        // Check if the JSON string contains the null values correctly
        assertTrue(jsonResult.contains("\"tableNumber\":null"));
        assertTrue(jsonResult.contains("\"noOfSeats\":null"));
    }

    @Test
    public void testDeserializeWithNullValues() throws Exception {
        // JSON string with null values
        String jsonInput = "{\"tableNumber\":null,\"noOfSeats\":null}";

        // Deserialize the JSON string into an AddTableRequest object
        AddTableRequest result = objectMapper.readValue(jsonInput, AddTableRequest.class);

        // Check if the object is correctly populated with null values
        assertNull(result.getTableNumber());
        assertNull(result.getNoOfSeats());
    }
}

