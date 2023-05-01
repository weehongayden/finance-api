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

import app.weehong.financeapi.dtos.request.BankRequestDto;
import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.UserRepository;
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
public class BankServiceImplTest {

  @Mock
  private BankRepository bankRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BankServiceImpl bankService;

  private User mockUser;

  private Bank mockBank;

  private BankRequestDto bankRequestDto;

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

    bankRequestDto = new BankRequestDto();
    bankRequestDto.setName("DBS Bank");
    bankRequestDto.setUserId("random-string");
  }

  @DisplayName("Should create the record and return the record details")
  @Test
  void create_ShouldReturnBank_WhenSuccess() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(bankRepository.save(any(Bank.class))).thenReturn(mockBank);

    BankResponseDto bankResponseDto = bankService.create("random-string",
        bankRequestDto);

    verify(userRepository, times((1))).findById("random-string");
    verify(bankRepository, times((1))).save(any(Bank.class));

    assertEquals(mockBank.getId(), bankResponseDto.getId());
    assertEquals(mockBank.getName(), bankResponseDto.getName());
  }

  @DisplayName("Should fetch all records based on the user ID")
  @Test
  void all_ShouldReturnAllBanks_WhenSuccess() {
    when(bankRepository.findAllByUserId(anyString()))
        .thenReturn(Collections.singletonList(mockBank));

    List<BankResponseDto> banks = bankService.all("random-string");

    verify(bankRepository, times((1))).findAllByUserId("random-string");

    assertFalse(banks.isEmpty());
    assertEquals(1, banks.size());
  }

  @DisplayName("Should fetch single record based on the record ID and user ID")
  @Test
  void getById_ShouldReturnBank_WhenSuccess() {
    when(bankRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockBank));

    BankResponseDto bank = bankService.getById(1L, "random-string");

    verify(bankRepository, times((1))).findByUserId(1L, "random-string");

    assertNotNull(bank);
    assertEquals(mockBank.getId(), bank.getId());
  }

  @DisplayName("Should update the record based on the record ID and user ID")
  @Test
  void update_ShouldUpdateAndReturnBank_WhenSuccess() {
    when(bankRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockBank));
    when(bankRepository.save(any(Bank.class)))
        .thenReturn(mockBank);

    BankResponseDto bank = bankService.update(1L, "random-string", bankRequestDto);

    verify(bankRepository, times((1))).findByUserId(1L, "random-string");
    verify(bankRepository, times((1))).save(any(Bank.class));

    assertNotNull(bank);
    assertEquals(mockBank.getId(), bank.getId());
  }

  @DisplayName("Should delete the record based on the record ID and user ID")
  @Test
  void update_ShouldDeleteAndReturnTrue_WhenSuccess() {
    when(bankRepository.findByUserId(1L, "random-string"))
        .thenReturn(Optional.of(mockBank))
        .thenReturn(Optional.empty());
    when(bankRepository.findById(1L))
        .thenReturn(Optional.empty());

    bankService.delete(1L, "random-string");

    verify(bankRepository, times((1))).findByUserId(1L, "random-string");
    verify(bankRepository, times((1))).findById(1L);
    verify(bankRepository, times((1))).deleteById(1L);
  }
}
