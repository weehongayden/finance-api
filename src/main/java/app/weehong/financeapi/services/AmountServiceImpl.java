package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.InstallmentRepository;
import app.weehong.financeapi.utils.InstallmentCalculator;
import jakarta.transaction.Transactional;
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
public class AmountServiceImpl implements AmountService<AmountResponseDto, AmountRequestDto>{

    private final AmountRepository amountRepository;
    private final InstallmentRepository installmentRepository;

    @Autowired
    public AmountServiceImpl(AmountRepository amountRepository, InstallmentRepository installmentRepository) {
        this.amountRepository = amountRepository;
        this.installmentRepository = installmentRepository;
    }

    @Override
    public AmountResponseDto create(AmountRequestDto amountRequestDto) {
        Amount amount = new Amount();
        amount.setName(amountRequestDto.getName());
        amount.setInitialAmount(amountRequestDto.getAmount());
        amount.setLeftoverAmount(amountRequestDto.getAmount());

        amount = amountRepository.save(amount);

        return mapAmountToAmountResponseDto(amount);
    }

    @Override
    public List<AmountResponseDto> all() {
        Iterable<Amount> amounts = amountRepository.findAll();
        Stream<Amount> amountStream = StreamSupport.stream(amounts.spliterator(), false);

        return amountStream.map(amount -> mapAmountToAmountResponseDto(amount))
                .collect(Collectors.toList());
    }

    @Override
    public AmountResponseDto getById(Long id) {
        Optional<AmountResponseDto> amountResponseDto = amountRepository.findById(id)
                .map(card -> mapAmountToAmountResponseDto(card));
        if (!amountResponseDto.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AmountServiceImpl - getById(): Amount ID doesn't exists.");
        }
        return amountResponseDto.get();
    }

    @Override
    public AmountResponseDto update(Long id, AmountRequestDto amountRequestDto) {
        Optional<Amount> amount = amountRepository.findById(id);
        if (!amount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AmountServiceImpl - update(): Amount ID doesn't exists.");
        }

        amount.get().setName(amountRequestDto.getName());
        amount.get().setInitialAmount(amountRequestDto.getAmount());
        amount.get().setLeftoverAmount(InstallmentCalculator.calculateLeftoverAmount(id, amountRequestDto.getAmount()));

        Amount updatedAmount = amountRepository.save(amount.get());

        return mapAmountToAmountResponseDto(updatedAmount);
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        Optional<Amount> amount = amountRepository.findById(id);

        if (!amount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AmountServiceImpl - delete(): Amount ID doesn't exists.");
        }

        amountRepository.deleteById(id);
        return true;
    }

    private AmountResponseDto mapAmountToAmountResponseDto(Amount amount) {
        AmountResponseDto response = new AmountResponseDto();

        response.setId(amount.getId());
        response.setName(amount.getName());
        response.setInitialAmount(amount.getInitialAmount());
        response.setLeftoverAmount(amount.getLeftoverAmount());
        response.setCreatedAt(amount.getCreatedAt());
        response.setUpdatedAt(amount.getUpdatedAt());

        return response;
    }
}
