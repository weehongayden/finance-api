package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.BankRequestDto;
import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.mappers.BankMapper;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BankServiceImpl implements BankService<BankResponseDto, BankRequestDto> {

  private final BankRepository bankRepository;

  private final UserRepository userRepository;

  @Autowired
  public BankServiceImpl(BankRepository bankRepository, UserRepository userRepository) {
    this.bankRepository = bankRepository;
    this.userRepository = userRepository;
  }

  @Override
  public BankResponseDto create(String userId, BankRequestDto bankRequestDto) {
    Optional<User> user = userRepository.findById(userId);

    if (!user.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID doesn't exist.");
    }

    Bank bank = new Bank();
    bank.setUser(user.get());
    bank.setName(bankRequestDto.getName());

    bank = bankRepository.save(bank);

    return BankMapper.mapBankToBankResponseDto(bank);
  }

  @Override
  public List<BankResponseDto> all(String userId) {
    Iterable<Bank> banks = bankRepository.findAllByUserId(userId);
    return StreamSupport.stream(banks.spliterator(), false)
        .map(BankMapper::mapBankToBankResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public BankResponseDto getById(Long id, String userId) {
    Optional<Bank> bank = bankRepository.findByUserId(id, userId);

    if (!bank.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID doesn't exist.");
    }

    return BankMapper.mapBankToBankResponseDto(bank.get());
  }

  @Override
  public BankResponseDto update(Long id, String userId, BankRequestDto bankRequestDto) {
    Optional<Bank> bank = bankRepository.findByUserId(id, userId);

    if (!bank.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID doesn't exist.");
    }

    bank.get().setName(bankRequestDto.getName());

    Bank updatedBank = bankRepository.save(bank.get());

    return BankMapper.mapBankToBankResponseDto(updatedBank);
  }

  @Override
  public boolean delete(Long id, String userId) {
    Optional<Bank> bank = bankRepository.findByUserId(id, userId);

    if (!bank.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID doesn't exist.");
    }

    bankRepository.deleteById(id);

    bank = bankRepository.findById(id);
    return !bank.isPresent();
  }
}
