package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.Card;
import app.weehong.financeapi.mappers.InstallmentMapper;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CardServiceImpl implements CardService<CardResponseDto, CardRequestDto> {

    private final AmountRepository amountRepository;

    private final CardRepository cardRepository;

    private final BankRepository bankRepository;

    @Autowired
    public CardServiceImpl(AmountRepository amountRepository, CardRepository cardRepository, BankRepository bankRepository) {
        this.amountRepository = amountRepository;
        this.cardRepository = cardRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public CardResponseDto create(CardRequestDto cardRequestDto) {
        Optional<Amount> amount = amountRepository.findById(cardRequestDto.getAmountId());
        Optional<Bank> bank = bankRepository.findById(cardRequestDto.getBankId());

        if (!amount.isPresent() || !bank.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - create(): Amount/Bank ID doesn't exists.");
        }

        Card card = new Card();
        card.setName(cardRequestDto.getName());
        card.setAmount(amount.get());
        card.setBank(bank.get());
        card.setStatementDate(cardRequestDto.getStatementDate());

        card = cardRepository.save(card);

        return mapCardToCardResponseDto(card);
    }

    @Override
    public List<CardResponseDto> all() {
        Iterable<Card> cards = cardRepository.findAll();
        return StreamSupport.stream(cards.spliterator(), false)
                .map(this::mapCardToCardResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CardResponseDto getById(Long id) {
        Optional<CardResponseDto> cardResponseDto = cardRepository.findById(id)
                .map(this::mapCardToCardResponseDto);

        if (!cardResponseDto.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - getById(): Card ID doesn't exists.");
        }

        return cardResponseDto.get();
    }

    @Override
    public CardResponseDto update(Long id, CardRequestDto cardRequestDto) {
        Optional<Card> card = cardRepository.findById(id);

        if (!card.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - update(): Card ID doesn't exists.");
        }

        Optional<Amount> amount = amountRepository.findById(cardRequestDto.getAmountId());
        Optional<Bank> bank = bankRepository.findById(cardRequestDto.getBankId());

        if (!amount.isPresent() || !bank.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - update(): Amount/Bank ID doesn't exists.");
        }

        card.get().setName(cardRequestDto.getName());
        card.get().setStatementDate(cardRequestDto.getStatementDate());
        Card updateCard = cardRepository.save(card.get());

        return mapCardToCardResponseDto(updateCard);
    }

    @Override
    public boolean delete(Long id) {
        Optional<Card> card = cardRepository.findById(id);

        if (!card.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CardServiceImpl - delete(): Card ID doesn't exists.");
        }

        cardRepository.deleteById(id);
        return true;
    }

    private CardResponseDto mapCardToCardResponseDto(Card card) {
        CardResponseDto response = new CardResponseDto();

        response.setId(card.getId());
        response.setBankId(card.getBank().getId());
        response.setName(card.getName());
        response.setStatementDate(card.getStatementDate());
        response.setInitialAmount(card.getAmount().getInitialAmount());
        response.setLeftoverAmount(card.getAmount().getLeftoverAmount());

        if (card.getInstallments() != null && !card.getInstallments().isEmpty()) {
            Set<InstallmentResponseDto> installments = new HashSet<>();
            card.getInstallments().forEach(unit -> {
                InstallmentResponseDto installment = InstallmentMapper.mapInstallmentToInstallmentResponseDto(unit);

                installments.add(installment);
            });
            response.setInstallments(installments);
        }
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());

        return response;
    }
}
