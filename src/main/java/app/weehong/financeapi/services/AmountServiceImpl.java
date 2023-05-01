package app.weehong.financeapi.services;

import static app.weehong.financeapi.mappers.AmountMapper.mapAmountToAmountResponseDto;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.mappers.AmountMapper;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.repositories.UserRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AmountServiceImpl implements AmountService<AmountResponseDto, AmountRequestDto> {

  private final AmountRepository amountRepository;

  private final InstallmentRepository installmentRepository;

  private final UserRepository userRepository;

  private final InstallmentCalculator installmentCalculator;

  @Autowired
  public AmountServiceImpl(
      AmountRepository amountRepository,
      UserRepository userRepository,
      InstallmentRepository installmentRepository,
      InstallmentCalculator installmentCalculator) {
    this.amountRepository = amountRepository;
    this.userRepository = userRepository;
    this.installmentRepository = installmentRepository;
    this.installmentCalculator = installmentCalculator;
  }

  @Override
  public AmountResponseDto create(String userId, AmountRequestDto amountRequestDto) {
    Optional<User> user = userRepository.findById(userId);

    if (!user.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "AmountServiceImpl - create(): User ID doesn't exists.");
    }

    Amount amount = new Amount();
    amount.setName(amountRequestDto.getName());
    amount.setInitialAmount(amountRequestDto.getAmount());
    amount.setLeftoverAmount(amountRequestDto.getAmount());

    amount = amountRepository.save(amount);

    return mapAmountToAmountResponseDto(amount);
  }

  @Override
  public List<AmountResponseDto> all(String userId) {
    Iterable<Amount> amounts = amountRepository.findAllByUserId(userId);
    Stream<Amount> amountStream = StreamSupport.stream(amounts.spliterator(), false);

    return amountStream.map(AmountMapper::mapAmountToAmountResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public AmountResponseDto getById(Long id, String userId) {
    Optional<User> user = userRepository.findById(userId);
    Optional<Amount> amount = amountRepository.findByUserId(id, userId);

    if (!user.isPresent() || !amount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    return mapAmountToAmountResponseDto(amount.get());
  }

  @Override
  public AmountResponseDto update(Long id, String userId, AmountRequestDto amountRequestDto) {
    Optional<User> user = userRepository.findById(userId);
    Optional<Amount> amount = amountRepository.findByUserId(id, userId);

    if (!user.isPresent() || !amount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    BigDecimal totalAmount = installmentRepository.SumInstallmentByAmountId(amount.get().getId(),
        userId);

    amount.get().setName(amountRequestDto.getName());
    amount.get().setInitialAmount(amountRequestDto.getAmount());
    amount.get().setLeftoverAmount(
        installmentCalculator.calculateLeftoverAmount(totalAmount, amountRequestDto.getAmount()));

    Amount updatedAmount = amountRepository.save(amount.get());

    return mapAmountToAmountResponseDto(updatedAmount);
  }

  @Transactional
  @Override
  public boolean delete(Long id, String userId) {
    Optional<Amount> amount = amountRepository.findByUserId(id, userId);

    if (!amount.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    amountRepository.deleteById(id);

    amount = amountRepository.findById(id);
    return !amount.isPresent();
  }
}
