package app.weehong.financeapi.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.services.AmountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(value = AmountController.class)
public class AmountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AmountService<AmountResponseDto, AmountRequestDto> cardService;

  @DisplayName("Should returns 401 when token is not provided")
  @Test
  public void getCard_ReturnUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("Should retrieve all cards")
  @Test
  public void getCard_ReturnOkStatusWithListOfAmountResponseDto() throws Exception {
    AmountResponseDto amountResponseDto = new AmountResponseDto();
    amountResponseDto.setId(1L);
    amountResponseDto.setName("DBS Live Fresh");
    amountResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
    amountResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));

    List<AmountResponseDto> cards = List.of(amountResponseDto);

    when(cardService.all(anyString())).thenReturn(cards);

    mockMvc.perform(get("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Record has fetched successfully"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].id").value(1L))
        .andExpect(jsonPath("$.data[0].name").value("DBS Live Fresh"))
        .andExpect(jsonPath("$.data[0].initial_amount").value(BigDecimal.valueOf(1000.00)))
        .andExpect(jsonPath("$.data[0].leftover_amount").value(BigDecimal.valueOf(1000.00)));
  }

  @DisplayName("Should returns 401 when token is not provided")
  @Test
  public void getCardById_ReturnUnauthorized() throws Exception {
    when(cardService.getById(anyLong(), anyString())).thenThrow(
        new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    mockMvc.perform(get("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("Get card by ID should return card response dto when id is exists")
  @Test
  public void getCardById_ReturnOkStatusWithAmountResponseDto_WhenIdIsExists() throws Exception {
    AmountResponseDto amountResponseDto = new AmountResponseDto();
    amountResponseDto.setId(1L);
    amountResponseDto.setName("DBS Live Fresh");
    amountResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
    amountResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));

    when(cardService.getById(anyLong(), anyString())).thenReturn(amountResponseDto);

    mockMvc.perform(get("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION,
                "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("DBS Live Fresh"))
        .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
        .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)));
  }

  @DisplayName("Get card by ID should return not found when id is not exists")
  @Test
  public void getCardById_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
    mockMvc.perform(get("/api/v1/amounts/99")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound());
  }

  @DisplayName("Should returns 401 when token is not provided")
  @Test
  public void createCard_ReturnUnauthorized() throws Exception {
    mockMvc.perform(post("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("Create card controller should create card successfully and return card response dto")
  @Test
  public void createCard_ReturnCreatedStatusAndAmountResponseDto() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    AmountResponseDto amountResponseDto = new AmountResponseDto();
    amountResponseDto.setId(1L);
    amountResponseDto.setName("DBS Live Fresh");
    amountResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
    amountResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));

    when(cardService.create(anyString(), any(AmountRequestDto.class))).thenReturn(amountResponseDto);

    mockMvc.perform(post("/api/v1/amounts")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("DBS Live Fresh"))
        .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
        .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)));
  }

  @DisplayName("Create card controller should return bad request when request body is empty")
  @Test
  public void createCard_ReturnBadRequest_WhenRequestBodyIsEmpty() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    mockMvc.perform(post("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.name").value("Name is required"))
        .andExpect(jsonPath("$.errors.amount").value("Amount is required"));
  }

  @DisplayName("Create card controller should return bad request when statement date is not within range")
  @Test
  public void createCard_ReturnBadRequest_WhenStatementDateNotWithinMinRange() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    mockMvc.perform(post("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound());
  }

  @DisplayName("Create card controller should return bad request when statement date is not within range")
  @Test
  public void createCard_ReturnBadRequest_WhenStatementDateNotWithinMaxRange() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    mockMvc.perform(post("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound());
  }

  @DisplayName("Should returns 401 when token is not provided")
  @Test
  public void updateCard_ReturnUnauthorized() throws Exception {
    mockMvc.perform(put("/api/v1/amounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("Update card controller should update card successfully and return card response dto")
  @Test
  public void updateCard_ReturnAmountResponseDto_WhenIdIsExists() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    AmountResponseDto amountResponseDto = new AmountResponseDto();
    amountResponseDto.setId(1L);
    amountResponseDto.setName("POSB Everyday");
    amountResponseDto.setInitialAmount(BigDecimal.valueOf(1000.00));
    amountResponseDto.setLeftoverAmount(BigDecimal.valueOf(1000.00));

    when(cardService.update(any(), anyString(), any(AmountRequestDto.class))).thenReturn(
        amountResponseDto);

    mockMvc.perform(put("/api/v1/amounts/1")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1L))
        .andExpect(jsonPath("$.data.name").value("POSB Everyday"))
        .andExpect(jsonPath("$.data.initial_amount").value(BigDecimal.valueOf(1000.00)))
        .andExpect(jsonPath("$.data.leftover_amount").value(BigDecimal.valueOf(1000.00)));
  }

  @DisplayName("Update card controller should return not found status when id is not exists")
  @Test
  public void updateCard_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    when(cardService.update(any(), anyString(), any(AmountRequestDto.class))).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Record ID doesn't exists"));

    mockMvc.perform(put("/api/v1/amounts/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(false))
        .andExpect(
            jsonPath("$.message").value("Record ID doesn't exists"));
  }

  @DisplayName("Update card controller should return not found status when id is not exists")
  @Test
  public void updateCard_ReturnNotFoundStatus_WhenBankIdIsNotExists() throws Exception {
    AmountRequestDto amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.ONE);

    String requestBody = objectMapper.writeValueAsString(amountRequestDto);

    when(cardService.update(any(), anyString(), any(AmountRequestDto.class))).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Record ID doesn't exists"));

    mockMvc.perform(put("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(false))
        .andExpect(jsonPath("$.message").value(
            "Record ID doesn't exists"));
  }

  @DisplayName("Should returns 401 when token is not provided")
  @Test
  public void deleteCard_ReturnUnauthorized() throws Exception {
    when(cardService.delete(anyLong(), anyString())).thenThrow(
        new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    mockMvc.perform(delete("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @DisplayName("Delete card controller should delete card successfully")
  @Test
  public void deleteCard_ShouldDeleteCard_WhenIdIsExists() throws Exception {
    when(cardService.delete(any(), anyString())).thenReturn(true);

    mockMvc.perform(delete("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(true))
        .andExpect(jsonPath("$.message").value("Successfully deleted amount"))
        .andExpect(jsonPath("$.data").value(true));
  }

  @DisplayName("Delete card controller should return not found status when id is not exists")
  @Test
  public void deleteCard_ReturnNotFoundStatus_WhenIdIsNotExists() throws Exception {
    when(cardService.delete(any(), anyString())).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Record ID doesn't exists"));

    mockMvc.perform(delete("/api/v1/amounts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer mock-token")
            .with(SecurityMockMvcRequestPostProcessors.jwt()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(false))
        .andExpect(
            jsonPath("$.message").value("Record ID doesn't exists"));
  }
}