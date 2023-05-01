package app.weehong.financeapi.services;

import static app.weehong.financeapi.mappers.InstallmentMapper.mapInstallmentToInstallmentResponseDto;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.Card;
import app.weehong.financeapi.entities.Installment;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.mappers.InstallmentMapper;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.CardRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.repositories.UserRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@PropertySource("classpath:application.yaml")
@Service
public class InstallmentServiceImpl implements
    InstallmentService<InstallmentResponseDto, InstallmentRequestDto> {

  private final InstallmentRepository installmentRepository;

  private final CardRepository cardRepository;

  private final AmountRepository amountRepository;

  private final UserRepository userRepository;

  private final InstallmentCalculator installmentCalculator;

  @Autowired
  public InstallmentServiceImpl(InstallmentRepository installmentRepository,
      CardRepository cardRepository,
      AmountRepository amountRepository,
      UserRepository userRepository,
      InstallmentCalculator installmentCalculator) {
    this.installmentRepository = installmentRepository;
    this.cardRepository = cardRepository;
    this.amountRepository = amountRepository;
    this.userRepository = userRepository;
    this.installmentCalculator = installmentCalculator;
  }

  @Override
  @Transactional
  public InstallmentResponseDto create(String userId, InstallmentRequestDto installmentRequestDto) {
    Optional<User> user = userRepository.findById(userId);
    Optional<Card> card = cardRepository.findByUserId(installmentRequestDto.getCardId(), userId);

    if (!user.isPresent() || !card.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    LocalDate startDate = calculateStartDate(installmentRequestDto.getStartDate(),
        card.get().getStatementDate());
    LocalDate endDate = calculateEndDate(startDate, installmentRequestDto.getTenure());
    int leftoverTenure = Math.toIntExact(calculateLeftoverTenure(endDate));

    Installment installment = new Installment();
    installment.setCard(card.get());
    installment.setName(installmentRequestDto.getName());
    installment.setStartDate(startDate);
    installment.setEndDate(endDate);
    installment.setTenure(installmentRequestDto.getTenure());
    installment.setLeftoverTenure(leftoverTenure);
    installment.setTotalAmount(installmentRequestDto.getTotalAmount());
    installment.setPricePerMonth(calculatePricePerMonth(installmentRequestDto.getTotalAmount(),
        installmentRequestDto.getTenure()));
    installment.setActive(leftoverTenure > 0);
    installment = installmentRepository.save(installment);

    if (installment.getId() != null) {
      BigDecimal totalAmount = installmentRepository.SumInstallmentByAmountId(
          installmentRequestDto.getCardId(), userId);
      BigDecimal leftoverAmount = installmentCalculator.calculateLeftoverAmount(totalAmount,
          installment.getCard().getAmount().getInitialAmount());
      amountRepository.updateLeftoverAmountById(installment.getCard().getAmount().getId(),
          leftoverAmount);
    } else {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "InstallmentServiceImpl - create(): Unable to create installment.");
    }

    return mapInstallmentToInstallmentResponseDto(installment);
  }

  @Override
  public List<InstallmentResponseDto> all(String userId) {
    Iterable<Installment> installments = installmentRepository.findAllByUserId(userId);
    return StreamSupport.stream(installments.spliterator(), false)
        .map(InstallmentMapper::mapInstallmentToInstallmentResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public InstallmentResponseDto getById(Long id, String userId) {
    Optional<Installment> installment = installmentRepository.findByUserId(id, userId);

    if (!installment.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    return mapInstallmentToInstallmentResponseDto(installment.get());
  }

  @Override
  @Transactional
  public InstallmentResponseDto update(Long id, String userId,
      InstallmentRequestDto installmentRequestDto) {
    Optional<Installment> installment = installmentRepository.findByUserId(id, userId);

    if (!installment.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    LocalDate startDate = calculateStartDate(installmentRequestDto.getStartDate(),
        installment.get().getCard().getStatementDate());
    LocalDate endDate = calculateEndDate(startDate, installmentRequestDto.getTenure());

    installment.get().setName(installmentRequestDto.getName());
    installment.get().setTotalAmount(installmentRequestDto.getTotalAmount());
    installment.get().setTenure(installmentRequestDto.getTenure());
    installment.get().setLeftoverTenure(Math.toIntExact(calculateLeftoverTenure(endDate)));
    installment.get().setPricePerMonth(
        calculatePricePerMonth(installmentRequestDto.getTotalAmount(),
            installmentRequestDto.getTenure()));
    installment.get().setStartDate(startDate);
    installment.get().setEndDate(endDate);

    Installment updatedInstallment = installmentRepository.save(installment.get());

    BigDecimal totalAmount = installmentRepository.SumInstallmentByAmountId(
        installmentRequestDto.getCardId(), userId);
    BigDecimal leftoverAmount = installmentCalculator.calculateLeftoverAmount(totalAmount,
        installment.get().getCard().getAmount().getInitialAmount());
    amountRepository.updateLeftoverAmountById(installment.get().getCard().getAmount().getId(),
        leftoverAmount);

    return mapInstallmentToInstallmentResponseDto(updatedInstallment);
  }

  @Override
  public List<InstallmentResponseDto> totalPricePerMonth(String userId) {
    return installmentRepository.SumInstallmentGroupByBank(userId)
        .stream()
        .map(unit -> {
          InstallmentResponseDto holder = new InstallmentResponseDto();
          holder.setTotalAmount(unit.getTotalAmount());
          holder.setName(unit.getName());
          holder.setIsActive(null);
          return holder;
        })
        .collect(Collectors.toList());
  }

  @Override
  public boolean delete(Long id, String userId) {
    Optional<Installment> installment = installmentRepository.findByUserId(id, userId);

    if (!installment.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    installmentRepository.deleteById(id);

    installment = installmentRepository.findById(id);
    return !installment.isPresent();
  }

  @Transactional
  @Scheduled(cron = "${installment.cron}")
  public void updateLeftoverTenureSchedule() {
    log.info("updateLeftoverTenureSchedule(): " + LocalDateTime.now());

    List<Installment> installmentStream = installmentRepository.findAllValidInstallments()
        .stream()
        .map(installment -> {
          Installment holder = new Installment();

          holder.setId(installment.getId());
          holder.setName(installment.getName());
          holder.setEndDate(installment.getEndDate());
          holder.setLeftoverTenure(installment.getLeftoverTenure());
          holder.setPricePerMonth(installment.getPricePerMonth());
          holder.setCard(installment.getCard());

          return holder;
        }).toList();

    installmentStream.forEach((unit) -> {
      LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Singapore"));
      LocalDate statementDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),
          unit.getCard().getStatementDate());

      if (currentDate.equals(statementDate)) {
        Optional<Installment> holder = installmentRepository.findById(unit.getId());

        int leftoverTenure = Math.toIntExact(calculateLeftoverTenure(unit.getEndDate()));

        log.info(
            "Updating statement date of " + unit.getName() + " from " + unit.getLeftoverTenure()
                + " to " + leftoverTenure);

        if (holder.isPresent()) {
          holder.get().setLeftoverTenure(leftoverTenure);
          holder.get().setActive(leftoverTenure > 0);

          installmentRepository.save(holder.get());
        }

        amountRepository.updateLeftoverAmountById(unit.getCard().getAmount().getId(),
            unit.getPricePerMonth().multiply(BigDecimal.valueOf(leftoverTenure)));
      }
    });
  }

  private BigDecimal calculatePricePerMonth(BigDecimal totalAmount, int tenure) {
    return totalAmount.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);
  }

  private Long calculateLeftoverTenure(LocalDate endDate) {
    LocalDate current = LocalDate.now();
    long months = ChronoUnit.MONTHS.between(current, endDate);

    return endDate.getDayOfMonth() == current.getDayOfMonth()
        ? months - 1
        : months;
  }

  private LocalDate calculateStartDate(LocalDate startDate, int statementDate) {
    return statementDate >= startDate.getDayOfMonth()
        ? LocalDate.of(startDate.getYear(), startDate.getMonthValue(), statementDate)
        : LocalDate.of(startDate.getYear(), startDate.getMonthValue(), statementDate).plusMonths(1);
  }

  private LocalDate calculateEndDate(LocalDate startDate, int tenure) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = LocalDate.parse(startDate.toString(), formatter);
    Period period = Period.ofMonths(tenure);
    return date.plus(period);
  }
}
