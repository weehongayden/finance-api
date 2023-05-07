package app.weehong.financeapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.repositories.UserRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AmountServiceImplTest {

  @Mock
  private AmountRepository amountRepository;

  @Mock
  private InstallmentRepository installmentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private InstallmentCalculator installmentCalculator;

  @InjectMocks
  private AmountServiceImpl amountService;

  private User mockUser;

  private Amount mockAmount;

  private AmountRequestDto amountRequestDto;

  @BeforeEach
  public void init() {
    mockUser = new User();
    mockUser.setId("random-string");
    mockUser.setName("Tester");
    mockUser.setCreatedAt(LocalDateTime.now());
    mockUser.setUpdatedAt(LocalDateTime.now());

    mockAmount = new Amount();
    mockAmount.setUser(mockUser);
    mockAmount.setId(1L);
    mockAmount.setName("DBS Credit Amount");
    mockAmount.setInitialAmount(BigDecimal.TEN);
    mockAmount.setLeftoverAmount(BigDecimal.TEN);
    mockAmount.setCreatedAt(LocalDateTime.now());
    mockAmount.setUpdatedAt(LocalDateTime.now());

    amountRequestDto = new AmountRequestDto();
    amountRequestDto.setName("DBS Live Fresh");
    amountRequestDto.setAmount(BigDecimal.TEN);
  }

  @DisplayName("Should create the record and return the record details")
  @Test
  void create_ShouldReturnBank_WhenSuccess() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(amountRepository.save(any(Amount.class))).thenReturn(mockAmount);

    AmountResponseDto bankResponseDto = amountService.create("random-string",
        amountRequestDto);

    verify(userRepository, times((1))).findById("random-string");
    verify(amountRepository, times((1))).save(any(Amount.class));

    assertEquals(mockAmount.getId(), bankResponseDto.getId());
    assertEquals(mockAmount.getName(), bankResponseDto.getName());
  }

  @DisplayName("Should fetch all records based on the user ID")
  @Test
  void all_ShouldReturnAllBanks_WhenSuccess() {
    when(amountRepository.findAllByUserId(anyString()))
        .thenReturn(Collections.singletonList(mockAmount));

    List<AmountResponseDto> banks = amountService.all("random-string");

    verify(amountRepository, times((1))).findAllByUserId("random-string");

    assertFalse(banks.isEmpty());
    assertEquals(1, banks.size());
  }

  @DisplayName("Should fetch single record based on the record ID and user ID")
  @Test
  void getById_ShouldReturnBank_WhenSuccess() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockAmount));

    AmountResponseDto amount = amountService.getById(1L, "random-string");

    verify(amountRepository, times((1))).findByUserId(1L, "random-string");

    assertNotNull(amount);
    assertEquals(mockAmount.getId(), amount.getId());
  }

  @DisplayName("Should update the record based on the record ID and user ID")
  @Test
  void update_ShouldUpdateAndReturnBank_WhenSuccess() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(amountRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockAmount));
    when(amountRepository.save(any(Amount.class)))
        .thenReturn(mockAmount);

    AmountResponseDto amount = amountService.update(1L, "random-string", amountRequestDto);

    verify(userRepository, times((1))).findById("random-string");
    verify(amountRepository, times((1))).findByUserId(1L, "random-string");
    verify(amountRepository, times((1))).save(any(Amount.class));

    assertNotNull(amount);
    assertEquals(mockAmount.getId(), amount.getId());
  }

  @DisplayName("Should delete the record based on the record ID and user ID")
  @Test
  void update_ShouldDeleteAndReturnTrue_WhenSuccess() {
    when(amountRepository.findByUserId(1L, "random-string"))
        .thenReturn(Optional.of(mockAmount))
        .thenReturn(Optional.empty());
    when(amountRepository.findById(1L))
        .thenReturn(Optional.empty());

    amountService.delete(1L, "random-string");

    verify(amountRepository, times((1))).findByUserId(1L, "random-string");
    verify(amountRepository, times((1))).findById(1L);
    verify(amountRepository, times((1))).deleteById(1L);
  }
}
