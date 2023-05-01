package app.weehong.financeapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.Card;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.CardRepository;
import app.weehong.financeapi.repositories.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

  @Mock
  private CardRepository cardRepository;

  @Mock
  private AmountRepository amountRepository;

  @Mock
  private BankRepository bankRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CardServiceImpl cardService;

  private User mockUser;

  private Bank mockBank;

  private Amount mockAmount;

  private Card mockCardOne;

  private Card mockCardTwo;

  private CardRequestDto cardRequestDto;

  @BeforeEach
  public void init() {
    mockUser = new User();
    mockUser.setId("random-string");
    mockUser.setName("Tester");
    mockUser.setCreatedAt(LocalDateTime.now());
    mockUser.setUpdatedAt(LocalDateTime.now());

    mockBank = new Bank();
    mockBank.setUser(mockUser);
    mockBank.setId(1L);
    mockBank.setName("DBS Bank");
    mockBank.setCreatedAt(LocalDateTime.now());
    mockBank.setUpdatedAt(LocalDateTime.now());

    mockAmount = new Amount();
    mockAmount.setUser(mockUser);
    mockAmount.setId(1L);
    mockAmount.setName("DBS Credit Amount");
    mockAmount.setInitialAmount(BigDecimal.TEN);
    mockAmount.setLeftoverAmount(BigDecimal.TEN);
    mockAmount.setCreatedAt(LocalDateTime.now());
    mockAmount.setUpdatedAt(LocalDateTime.now());

    mockCardOne = new Card();
    mockCardOne.setId(1L);
    mockCardOne.setName("DBS Live Fresh");
    mockCardOne.setStatementDate(1);
    mockCardOne.setBank(mockBank);
    mockCardOne.setAmount(mockAmount);
    mockCardOne.setCreatedAt(LocalDateTime.now());
    mockCardOne.setUpdatedAt(LocalDateTime.now());

    mockCardTwo = new Card();
    mockCardTwo.setId(2L);
    mockCardTwo.setName("POSB Everyday Card");
    mockCardTwo.setStatementDate(1);
    mockCardTwo.setBank(mockBank);
    mockCardTwo.setAmount(mockAmount);
    mockCardTwo.setCreatedAt(LocalDateTime.now());
    mockCardTwo.setUpdatedAt(LocalDateTime.now());

    cardRequestDto = new CardRequestDto();
    cardRequestDto.setName("DBS Live Fresh");
    cardRequestDto.setStatementDate(1);
    cardRequestDto.setAmountId(1L);
    cardRequestDto.setBankId(1L);
  }

  @DisplayName("Create card should create and return card")
  @Test
  void CardService_Create_ShouldCreateAndReturnCard() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockAmount));
    when(bankRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockBank));
    when(cardRepository.save(any(Card.class)))
        .thenReturn(mockCardOne);

    CardResponseDto cardResponseDto = cardService.create("random-string", cardRequestDto);

    verify(userRepository, times((1))).findById("random-string");
    verify(amountRepository, times((1))).findByUserId(1L, "random-string");
    verify(bankRepository, times((1))).findByUserId(1L, "random-string");
    verify(cardRepository, times((1))).save(any(Card.class));

    assertEquals(mockCardOne.getId(), cardResponseDto.getId());
    assertEquals(mockCardOne.getName(), cardResponseDto.getName());
    assertEquals(mockCardOne.getStatementDate(), cardResponseDto.getStatementDate());
    assertEquals(mockCardOne.getAmount().getInitialAmount(), cardResponseDto.getInitialAmount());
    assertEquals(mockCardOne.getAmount().getInitialAmount(), cardResponseDto.getLeftoverAmount());
  }

  @DisplayName("Create card should return 404 error when userId is not present")
  @Test
  void CardService_Create_create_ReturnNotFound_WhenUserNotPresent() {
    when(userRepository.findById(""))
        .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.create("", cardRequestDto);
    });

    verify(userRepository, times((1))).findById("");
    verify(amountRepository, times((0))).findByUserId(1L, "random-string");
    verify(bankRepository, times((0))).findByUserId(1L, "random-string");
    verify(cardRepository, times((0))).save(any(Card.class));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User ID doesn't exist.", exception.getReason());
  }

  @DisplayName("Create card should return 404 error when bank is not present")
  @Test
  void create_ReturnNotFound_WhenBankNotPresent() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.create("random-string", cardRequestDto);
    });

    verify(userRepository, times((1))).findById("random-string");
    verify(amountRepository, times((1))).findByUserId(1L, "random-string");
    verify(bankRepository, times((1))).findByUserId(1L, "random-string");
    verify(cardRepository, times((0))).save(any(Card.class));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Record ID doesn't exist.", exception.getReason());
  }

  @DisplayName("Get all cards should return empty card list if no cards exist")
  @Test
  void CardService_All_ShouldReturnEmptyCardList() {
    when(cardRepository.findAllByUserId(anyString()))
        .thenReturn(new ArrayList<>());

    List<CardResponseDto> cards = cardService.all("random-string");

    verify(cardRepository, times((1))).findAllByUserId("random-string");

    assert (cards.isEmpty());
  }

  @DisplayName("Get all cards should return card list if cards exist")
  @Test
  void CardService_All_ShouldReturnCardList() {
    when(cardRepository.findAllByUserId("random-string"))
        .thenReturn(Arrays.asList(mockCardOne, mockCardTwo));

    List<CardResponseDto> cards = cardService.all("random-string");

    verify(cardRepository, times((1))).findAllByUserId("random-string");

    assertFalse(cards.isEmpty());
    assertEquals(2, cards.size());
  }

  @DisplayName("Get card by id should return card if card exists")
  @Test
  void CardService_GetById_ShouldReturnCard() {
    when(cardRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockCardOne));

    CardResponseDto card = cardService.getById(1L, "random-string");

    verify(cardRepository, times((1))).findByUserId(1L, "random-string");

    assertNotNull(card);
    assertEquals(mockCardOne.getId(), card.getId());
  }

  @DisplayName("Get card by id should return error when user id doesn't exist")
  @Test
  void CardService_GetById_ShouldReturnErrorWhenUserIdNotExist() {
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.getById(1L, "");
    });

    verify(cardRepository, times((1))).findByUserId(1L, "");

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Record ID doesn't exist.", exception.getReason());
  }

  @DisplayName("Get card by id should return null if card does not exist")
  @Test
  void CardService_GetById_ShouldReturnNull() {
    when(cardRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.getById(1L, "");
    });

    verify(cardRepository, times((1))).findByUserId(1L, "");

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Record ID doesn't exist.", exception.getReason());
  }

  @DisplayName("Update card should return card if card exists and is updated")
  @Test
  void CardService_Update_ShouldUpdateAndReturnCard() {
    cardRequestDto.setName("DBS Woman Card");

    when(cardRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockCardOne));
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockAmount));
    when(bankRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockBank));
    when(cardRepository.save(any(Card.class)))
        .thenReturn(mockCardOne);

    CardResponseDto cardResponseDto = cardService.update(1L, "", cardRequestDto);

    verify(cardRepository, times((1))).save(any(Card.class));

    assertEquals(mockCardOne.getId(), cardResponseDto.getId());
    assertEquals(mockCardOne.getStatementDate(), cardResponseDto.getStatementDate());
    assertEquals(mockCardOne.getAmount().getInitialAmount(), cardResponseDto.getInitialAmount());
    assertEquals(mockCardOne.getName(), cardResponseDto.getName());
  }

  @DisplayName("Update card should return null if card does not exist")
  @Test
  void CardService_Update_ShouldFailToUpdateCard() {
    when(cardRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    when(bankRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.update(99L, "", cardRequestDto);
    });

    verify(cardRepository, times((1))).findByUserId(99L, "");
    verify(amountRepository, times((1))).findByUserId(99L, "");
    verify(bankRepository, times((1))).findByUserId(99L, "");
    verify(cardRepository, never()).save(any());

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Record ID doesn't exist.", exception.getReason());
  }

  @DisplayName("Delete card should return true if card exists and is deleted")
  @Test
  void CardService_Delete_ShouldDeleteCard() {
    when(cardRepository.findByUserId(1L, "random-string"))
        .thenReturn(Optional.of(mockCardOne))
        .thenReturn(Optional.empty());
    when(cardRepository.findById(1L))
        .thenReturn(Optional.empty());

    cardService.delete(1L, "random-string");

    verify(cardRepository, times((1))).findByUserId(1L, "random-string");
    verify(cardRepository, times((1))).findById(1L);
    verify(cardRepository, times((1))).deleteById(1L);
  }

  @DisplayName("Delete card should return false if card does not exist")
  @Test
  void CardService_Delete_ShouldFailToDeleteCard() {
    when(cardRepository.findByUserId(99L, ""))
        .thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      cardService.delete(99L, "");
    });

    verify(cardRepository, times((1))).findByUserId(99L, "");

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Record ID doesn't exist.", exception.getReason());
  }
}