package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.BankRequestDto;
import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class BankServiceImpl implements BankService<BankResponseDto, BankRequestDto>{

    private final BankRepository bankRepository;
    private final InstallmentRepository installmentRepository;

    @Autowired
    public BankServiceImpl(BankRepository bankRepository, InstallmentRepository installmentRepository) {
        this.bankRepository = bankRepository;
        this.installmentRepository = installmentRepository;
    }

    @Override
    public BankResponseDto create(BankRequestDto bankRequestDto) {
        User user = new User();
        user.setId(bankRequestDto.getUserId());
        user.setName("Wee Hong KOH");

        Bank bank = new Bank();
        bank.setUser(user);
        bank.setName(bankRequestDto.getName());

        bank = bankRepository.save(bank);

        return mapBankToBankResponseDto(bank);
    }

    @Override
    public List<BankResponseDto> all() {
        Iterable<Bank> banks = bankRepository.findAll();
        Stream<Bank> bankStream = StreamSupport.stream(banks.spliterator(), false);

        return bankStream.map(bank -> mapBankToBankResponseDto(bank))
                .collect(Collectors.toList());
    }

    @Override
    public BankResponseDto getById(Long id) {
        Optional<BankResponseDto> bankResponseDto = bankRepository.findById(id)
                .map(card -> mapBankToBankResponseDto(card));
        if (!bankResponseDto.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankServiceImpl - getById(): Bank ID doesn't exists.");
        }
        return bankResponseDto.get();
    }

    @Override
    public BankResponseDto update(Long id, BankRequestDto bankRequestDto) {
        Optional<Bank> bank = bankRepository.findById(id);
        if (!bank.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankServiceImpl - update(): Bank ID doesn't exists.");
        }

        bank.get().setName(bankRequestDto.getName());

        Bank updatedBank = bankRepository.save(bank.get());

        return mapBankToBankResponseDto(updatedBank);
    }

    @Override
    public boolean delete(Long id) {
        Optional<Bank> bank = bankRepository.findById(id);
        if (!bank.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankServiceImpl - delete(): Bank ID doesn't exists.");
        }
        bankRepository.deleteById(id);
        return true;
    }

    private BankResponseDto mapBankToBankResponseDto(Bank bank) {
        BankResponseDto response = new BankResponseDto();

        response.setId(bank.getId());
        response.setName(bank.getName());
        response.setCreatedAt(bank.getCreatedAt());
        response.setUpdatedAt(bank.getUpdatedAt());

        return response;
    }
}
