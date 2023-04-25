package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.services.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService<CardResponseDto, CardRequestDto> cardService;

    @DisplayName("Should retrieve all cards")
    @Test
    public void getCard_ReturnOkStatusWithListOfCardResponseDto() throws Exception {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(1L);
        cardResponseDto.setName("DBS Live Fresh");
        cardResponseDto.setBankId(1L);
        cardResponseDto.setStatementDate(1);
        cardResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setCreatedAt(currentDate);
        cardResponseDto.setUpdatedAt(currentDate);

        List<CardResponseDto> cards = List.of(cardResponseDto);

        when(cardService.all()).thenReturn(cards);

        mockMvc.perform(get("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record has fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("DBS Live Fresh"))
                .andExpect(jsonPath("$.data[0].bank_id").value(1L))
                .andExpect(jsonPath("$.data[0].initial_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data[0].leftover_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data[0].created_at").value(currentDate.format(dateTimeFormatter)))
                .andExpect(jsonPath("$.data[0].updated_at").value(currentDate.format(dateTimeFormatter)));
    }

    @DisplayName("Get card by ID should return card response dto when id is exists")
    @Test
    public void getCardById_ReturnOkStatusWithCardResponseDto_WhenIdIsExists() throws Exception {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(1L);
        cardResponseDto.setName("DBS Live Fresh");
        cardResponseDto.setBankId(1L);
        cardResponseDto.setStatementDate(1);
        cardResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setCreatedAt(currentDate);
        cardResponseDto.setUpdatedAt(currentDate);

        when(cardService.getById(1L)).thenReturn(cardResponseDto);

        mockMvc.perform(get("/api/v1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("DBS Live Fresh"))
                .andExpect(jsonPath("$.data.bank_id").value(1L))
                .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.created_at").value(currentDate.format(dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updated_at").value(currentDate.format(dateTimeFormatter)));
    }

    @DisplayName("Get card by ID should return not found when id is not exists")
    @Test
    public void getCardById_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/cards/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Create card controller should create card successfully and return card response dto")
    @Test
    public void createCard_ReturnCreatedStatusAndCardResponseDto() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(1L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(31);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(1L);
        cardResponseDto.setName("DBS Live Fresh");
        cardResponseDto.setBankId(1L);
        cardResponseDto.setStatementDate(31);
        cardResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setCreatedAt(currentDate);
        cardResponseDto.setUpdatedAt(currentDate);

        when(cardService.create(any(CardRequestDto.class))).thenReturn(cardResponseDto);

        mockMvc.perform(post("/api/v1/cards")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("DBS Live Fresh"))
                .andExpect(jsonPath("$.data.bank_id").value(1L))
                .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.created_at").value(currentDate.format(dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updated_at").value(currentDate.format(dateTimeFormatter)));
    }

    @DisplayName("Create card controller should return bad request when request body is empty")
    @Test
    public void createCard_ReturnBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name is required"))
                .andExpect(jsonPath("$.errors.bank_id").value("Bank ID is required"))
                .andExpect(jsonPath("$.errors.amount_id").value("Amount ID is required"))
                .andExpect(jsonPath("$.errors.statement_date").value("Statement date is required"));
    }

    @DisplayName("Create card controller should return bad request when statement date is not within range")
    @Test
    public void createCard_ReturnBadRequest_WhenStatementDateNotWithinMinRange() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(1L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(-1);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.statement_date").value("Statement date must be between 1 and 31"));
    }

    @DisplayName("Create card controller should return bad request when statement date is not within range")
    @Test
    public void createCard_ReturnBadRequest_WhenStatementDateNotWithinMaxRange() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(1L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(100);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        mockMvc.perform(post("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.statement_date").value("Statement date must be between 1 and 31"));
    }

    @DisplayName("Update card controller should update card successfully and return card response dto")
    @Test
    public void updateCard_ReturnCardResponseDto_WhenIdIsExists() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(1L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(31);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

        CardResponseDto cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(1L);
        cardResponseDto.setName("POSB Everyday");
        cardResponseDto.setBankId(1L);
        cardResponseDto.setStatementDate(1);
        cardResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));
        cardResponseDto.setCreatedAt(currentDate);
        cardResponseDto.setUpdatedAt(currentDate);

        when(cardService.update(any(), any(CardRequestDto.class))).thenReturn(cardResponseDto);

        mockMvc.perform(put("/api/v1/cards/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("POSB Everyday"))
                .andExpect(jsonPath("$.data.bank_id").value(1L))
                .andExpect(jsonPath("$.data.statement_date").value(1))
                .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)))
                .andExpect(jsonPath("$.data.created_at").value(currentDate.format(dateTimeFormatter)))
                .andExpect(jsonPath("$.data.updated_at").value(currentDate.format(dateTimeFormatter)));
    }

    @DisplayName("Update card controller should return not found status when id is not exists")
    @Test
    public void updateCard_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(1L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(31);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        when(cardService.update(any(), any(CardRequestDto.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - update(): Card ID doesn't exists."));

        mockMvc.perform(put("/api/v1/cards/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("CardServiceImpl - update(): Card ID doesn't exists."));
    }

    @DisplayName("Update card controller should return not found status when id is not exists")
    @Test
    public void updateCard_ReturnNotFoundStatus_WhenBankIdIsNotExists() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setName("DBS Live Fresh");
        cardRequestDto.setBankId(99L);
        cardRequestDto.setAmountId(1L);
        cardRequestDto.setStatementDate(31);

        String requestBody = objectMapper.writeValueAsString(cardRequestDto);

        when(cardService.update(any(), any(CardRequestDto.class))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - update(): Amount/Bank ID doesn't exists."));

        mockMvc.perform(put("/api/v1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("CardServiceImpl - update(): Amount/Bank ID doesn't exists."));
    }

    @DisplayName("Delete card controller should delete card successfully")
    @Test
    public void deleteCard_ShouldDeleteCard_WhenIdIsExists() throws Exception {
        when(cardService.delete(any())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Successfully update card"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @DisplayName("Delete card controller should return not found status when id is not exists")
    @Test
    public void deleteCard_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
        when(cardService.delete(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - delete(): Card ID doesn't exists."));

        mockMvc.perform(delete("/api/v1/cards/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").value("CardServiceImpl - delete(): Card ID doesn't exists."));
    }
}
