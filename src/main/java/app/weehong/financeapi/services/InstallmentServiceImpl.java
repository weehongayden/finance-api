package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.Card;
import app.weehong.financeapi.entities.Installment;
import app.weehong.financeapi.mappers.InstallmentMapper;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.CardRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@PropertySource("classpath:application.yaml")
@Service
public class InstallmentServiceImpl implements InstallmentService<InstallmentResponseDto, InstallmentRequestDto> {

    private final InstallmentRepository installmentRepository;

    private final CardRepository cardRepository;

    private final AmountRepository amountRepository;

    @Autowired
    public InstallmentServiceImpl(InstallmentRepository installmentRepository, CardRepository cardRepository, AmountRepository amountRepository) {
        this.installmentRepository = installmentRepository;
        this.cardRepository = cardRepository;
        this.amountRepository = amountRepository;
    }

    @Override
    @Transactional
    public InstallmentResponseDto create(InstallmentRequestDto installmentRequestDto) {
        Optional<Card> card = cardRepository.findById(installmentRequestDto.getCardId());

        if (!card.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "InstallmentServiceImpl - create(): Card ID doesn't exists.");
        }

        LocalDate startDate = calculateStartDate(installmentRequestDto.getStartDate(), card.get().getStatementDate());
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
        installment.setPricePerMonth(calculatePricePerMonth(installmentRequestDto.getTotalAmount(), installmentRequestDto.getTenure()));
        installment.setActive(leftoverTenure > 0);
        installment = installmentRepository.save(installment);

        if (installment.getId() != null) {
            BigDecimal leftoverAmount = InstallmentCalculator.calculateLeftoverAmount(installmentRequestDto.getCardId(), installment.getCard().getAmount().getInitialAmount());
            amountRepository.updateLeftoverAmountById(installment.getCard().getAmount().getId(), leftoverAmount);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "InstallmentServiceImpl - create(): Unable to create installment.");
        }

        return InstallmentMapper.mapInstallmentToInstallmentResponseDto(installment);
    }

    @Override
    public List<InstallmentResponseDto> all() {
        Iterable<Installment> installments = installmentRepository.findAll();
        Stream<Installment> installmentStream = StreamSupport.stream(installments.spliterator(), false);

        return installmentStream.map(InstallmentMapper::mapInstallmentToInstallmentResponseDto).collect(Collectors.toList());
    }

    @Override
    public InstallmentResponseDto getById(Long id) {
        Optional<InstallmentResponseDto> installmentResponseDto = installmentRepository.findById(id).map(InstallmentMapper::mapInstallmentToInstallmentResponseDto);

        if (!installmentResponseDto.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "InstallmentServiceImpl - getById(): Installment ID doesn't exists.");
        }

        return installmentResponseDto.get();
    }

    @Override
    @Transactional
    public InstallmentResponseDto update(Long id, InstallmentRequestDto installmentRequestDto) {
        Optional<Installment> installment = installmentRepository.findById(id);

        if (!installment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "InstallmentServiceImpl - update(): Installment ID doesn't exists.");
        }

        LocalDate startDate = calculateStartDate(installmentRequestDto.getStartDate(), installment.get().getCard().getStatementDate());
        LocalDate endDate = calculateEndDate(startDate, installmentRequestDto.getTenure());

        installment.get().setName(installmentRequestDto.getName());
        installment.get().setTotalAmount(installmentRequestDto.getTotalAmount());
        installment.get().setTenure(installmentRequestDto.getTenure());
        installment.get().setLeftoverTenure(Math.toIntExact(calculateLeftoverTenure(endDate)));
        installment.get().setPricePerMonth(calculatePricePerMonth(installmentRequestDto.getTotalAmount(), installmentRequestDto.getTenure()));
        installment.get().setStartDate(startDate);
        installment.get().setEndDate(endDate);

        Installment updatedInstallment = installmentRepository.save(installment.get());

        BigDecimal leftoverAmount = InstallmentCalculator.calculateLeftoverAmount(installmentRequestDto.getCardId(), installment.get().getCard().getAmount().getInitialAmount());
        amountRepository.updateLeftoverAmountById(installment.get().getCard().getAmount().getId(), leftoverAmount);

        return InstallmentMapper.mapInstallmentToInstallmentResponseDto(updatedInstallment);
    }

    @Override
    public List<InstallmentResponseDto> totalPricePerMonth() {
        return installmentRepository.SumInstallmentGroupByBank()
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
    public boolean delete(Long id) {
        Optional<Installment> installment = installmentRepository.findById(id);

        if (!installment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "InstallmentServiceImpl - delete(): Installment ID doesn't exists.");
        }

        installmentRepository.deleteById(id);
        return true;
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
            LocalDate statementDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), unit.getCard().getStatementDate());

            if (currentDate.equals(statementDate)) {
                Optional<Installment> holder = installmentRepository.findById(unit.getId());

                int leftoverTenure = Math.toIntExact(calculateLeftoverTenure(unit.getEndDate()));

                log.info("Updating statement date of " + unit.getName() + " from " + unit.getLeftoverTenure() + " to " + leftoverTenure);

                if (holder.isPresent()) {
                    holder.get().setLeftoverTenure(leftoverTenure);
                    holder.get().setActive(leftoverTenure > 0);

                    installmentRepository.save(holder.get());
                }

                amountRepository.updateLeftoverAmountById(unit.getCard().getAmount().getId(), unit.getPricePerMonth().multiply(BigDecimal.valueOf(leftoverTenure)));
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
