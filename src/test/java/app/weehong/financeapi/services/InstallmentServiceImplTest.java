package app.weehong.financeapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.*;
import app.weehong.financeapi.projections.installments.AllValidInstallmentsProjection;
import app.weehong.financeapi.projections.installments.SumInstallmentByBank;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.CardRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.repositories.UserRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class InstallmentServiceImplTest {

  @Mock
  private InstallmentRepository installmentRepository;

  @Mock
  private AmountRepository amountRepository;

  @Mock
  private CardRepository cardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private InstallmentCalculator installmentCalculator;

  @InjectMocks
  private InstallmentServiceImpl installmentService;

  private User mockUser;

  private Bank mockBank;

  private Amount mockAmount;

  private Card mockCard;

  private Installment mockInstallmentOne;

  private InstallmentRequestDto installmentRequestDto;

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

    mockCard = new Card();
    mockCard.setId(1L);
    mockCard.setName("DBS Live Fresh");
    mockCard.setStatementDate(1);
    mockCard.setBank(mockBank);
    mockCard.setAmount(mockAmount);
    mockCard.setCreatedAt(LocalDateTime.now());
    mockCard.setUpdatedAt(LocalDateTime.now());

    mockInstallmentOne = new Installment();
    mockInstallmentOne.setId(1L);
    mockInstallmentOne.setName("Installment - 1");
    mockInstallmentOne.setCard(mockCard);
    mockInstallmentOne.setTotalAmount(BigDecimal.ONE);
    mockInstallmentOne.setActive(true);
    mockInstallmentOne.setLeftoverTenure(1);
    mockInstallmentOne.setEndDate(LocalDate.now());
    mockInstallmentOne.setCreatedAt(LocalDateTime.now());
    mockInstallmentOne.setUpdatedAt(LocalDateTime.now());

    installmentRequestDto = new InstallmentRequestDto();
    installmentRequestDto.setName("DBS Live Fresh");
    installmentRequestDto.setCardId(1L);
    installmentRequestDto.setTenure(1);
    installmentRequestDto.setTotalAmount(BigDecimal.ONE);
    installmentRequestDto.setStartDate(LocalDate.now());
  }

  @DisplayName("Should create the record and return the record details")
  @Test
  void create_ShouldReturnInstallment_WhenSuccess() {
    when(userRepository.findById(anyString()))
        .thenReturn(Optional.of(mockUser));
    when(cardRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockCard));
    when(installmentCalculator.calculateLeftoverAmount(any(), any()))
        .thenReturn(BigDecimal.TEN);
    when(installmentRepository.save(any(Installment.class)))
        .thenReturn(mockInstallmentOne);

    InstallmentResponseDto installmentResponseDto = installmentService.create("random-string",
        installmentRequestDto);

    verify(userRepository, times((1))).findById("random-string");
    verify(cardRepository, times((1))).findByUserId(1L, "random-string");
    verify(installmentRepository, times((1))).save(any(Installment.class));

    assertEquals(mockInstallmentOne.getId(), installmentResponseDto.getId());
    assertEquals(mockInstallmentOne.getName(), installmentResponseDto.getName());
    assertEquals(mockInstallmentOne.getTotalAmount(), installmentResponseDto.getTotalAmount());
    assertEquals(mockInstallmentOne.getTenure(), installmentResponseDto.getTenure());
    assertEquals(mockInstallmentOne.getLeftoverTenure(),
        installmentResponseDto.getLeftoverTenure());
    assertEquals(mockInstallmentOne.getPricePerMonth(), installmentResponseDto.getPricePerMonth());
    assertEquals(mockInstallmentOne.getStartDate(), installmentResponseDto.getStartDate());
    assertEquals(mockInstallmentOne.getEndDate(), installmentResponseDto.getEndDate());
    assertEquals(mockInstallmentOne.isActive(), installmentResponseDto.getIsActive());
    assertEquals(mockInstallmentOne.getCard(), mockCard);
  }

  @DisplayName("Should fetch all records based on the user ID")
  @Test
  void all_ShouldReturnAllInstallments_WhenSuccess() {
    when(installmentRepository.findAllByUserId(anyString()))
        .thenReturn(Collections.singletonList(mockInstallmentOne));

    List<InstallmentResponseDto> installments = installmentService.all("random-string");

    verify(installmentRepository, times((1))).findAllByUserId("random-string");

    assertFalse(installments.isEmpty());
    assertEquals(1, installments.size());
  }

  @DisplayName("Should fetch single record based on the record ID and user ID")
  @Test
  void getById_ShouldReturnInstallment_WhenSuccess() {
    when(installmentRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockInstallmentOne));

    InstallmentResponseDto installment = installmentService.getById(1L, "random-string");

    verify(installmentRepository, times((1))).findByUserId(1L, "random-string");

    assertNotNull(installment);
    assertEquals(mockInstallmentOne.getId(), installment.getId());
  }

  @DisplayName("Should update the record based on the record ID and user ID")
  @Test
  void update_ShouldUpdateAndReturnInstallment_WhenSuccess() {
    when(installmentRepository.findByUserId(anyLong(), anyString()))
        .thenReturn(Optional.of(mockInstallmentOne));
    when(installmentRepository.save(any(Installment.class)))
        .thenReturn(mockInstallmentOne);
    doNothing().when(amountRepository).updateLeftoverAmountById(anyLong(), any());

    InstallmentResponseDto installment = installmentService.update(1L, "random-string", installmentRequestDto);

    verify(installmentRepository, times((1))).findByUserId(1L, "random-string");
    verify(installmentRepository, times((1))).save(any(Installment.class));

    assertNotNull(installment);
    assertEquals(mockInstallmentOne.getId(), installment.getId());
  }

  @DisplayName("Should delete the record based on the record ID and user ID")
  @Test
  void update_ShouldDeleteAndReturnTrue_WhenSuccess() {
    when(installmentRepository.findByUserId(1L, "random-string"))
        .thenReturn(Optional.of(mockInstallmentOne))
        .thenReturn(Optional.empty());
    when(installmentRepository.findById(1L))
        .thenReturn(Optional.empty());

    installmentService.delete(1L, "random-string");

    verify(installmentRepository, times((1))).findByUserId(1L, "random-string");
    verify(installmentRepository, times((1))).findById(1L);
    verify(installmentRepository, times((1))).deleteById(1L);
  }

  @DisplayName("Should sum up the total price of installment and group by bank")
  @Test
  void totalPricePerMonth_ShouldSumUpTheTotalPriceOfInstallment_WhenSuccess() {
    SumInstallmentByBank sumInstallmentByBank = new SumInstallmentByBank() {
      @Override
      public BigDecimal getTotalAmount() {
        return BigDecimal.TEN;
      }

      @Override
      public String getName() {
        return "DBS Bank";
      }
    };

    when(installmentRepository.SumInstallmentGroupByBank("random-string"))
        .thenReturn(List.of(sumInstallmentByBank));

    List<InstallmentResponseDto> installments = installmentService.totalPricePerMonth("random-string");

    verify(installmentRepository, times((1))).SumInstallmentGroupByBank("random-string");

    assertFalse(installments.isEmpty());
    assertEquals(1, installments.size());
  }

  @DisplayName("Should sum up the total price of installment and group by bank")
  @Test
  void updateLeftoverTenureSchedule_ShouldCalculateTheTenure_WhenSuccess() {
    AllValidInstallmentsProjection allValidInstallmentsProjection = new AllValidInstallmentsProjection() {
      @Override
      public Long getId() {
        return 1L;
      }

      @Override
      public LocalDate getEndDate() {
        return LocalDate.of(2022, 5, 1);
      }

      @Override
      public Integer getLeftoverTenure() {
        return 24;
      }

      @Override
      public String getName() {
        return "Installment Name";
      }

      @Override
      public BigDecimal getPricePerMonth() {
        return BigDecimal.valueOf(1000L);
      }

      @Override
      public Card getCard() {
        return mockCard;
      }
    };

    when(installmentRepository.findAllValidInstallments())
        .thenReturn(List.of(allValidInstallmentsProjection));

    installmentService.updateLeftoverTenureSchedule();

    verify(installmentRepository, times((1))).findAllValidInstallments();
  }
}
