package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.Amount;
import app.weehong.financeapi.entities.Bank;
import app.weehong.financeapi.entities.Card;
import app.weehong.financeapi.entities.User;
import app.weehong.financeapi.mappers.InstallmentMapper;
import app.weehong.financeapi.repositories.AmountRepository;
import app.weehong.financeapi.repositories.BankRepository;
import app.weehong.financeapi.repositories.CardRepository;
import app.weehong.financeapi.repositories.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CardServiceImpl implements CardService<CardResponseDto, CardRequestDto> {

  private final AmountRepository amountRepository;

  private final CardRepository cardRepository;

  private final BankRepository bankRepository;

  private final UserRepository userRepository;

  @Autowired
  public CardServiceImpl(AmountRepository amountRepository, CardRepository cardRepository,
      BankRepository bankRepository, UserRepository userRepository) {
    this.amountRepository = amountRepository;
    this.cardRepository = cardRepository;
    this.bankRepository = bankRepository;
    this.userRepository = userRepository;
  }

  @Override
  public CardResponseDto create(String userId, CardRequestDto cardRequestDto) {
    Optional<User> user = userRepository.findById(userId);

    if (!user.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID doesn't exist.");
    }

    Optional<Amount> amount = amountRepository.findByUserId(cardRequestDto.getAmountId(), userId);
    Optional<Bank> bank = bankRepository.findByUserId(cardRequestDto.getBankId(), userId);

    if (!amount.isPresent() || !bank.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
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
  public List<CardResponseDto> all(String userId) {
    Iterable<Card> cards = cardRepository.findAllByUserId(userId);
    return StreamSupport.stream(cards.spliterator(), false)
        .map(this::mapCardToCardResponseDto)
        .collect(Collectors.toList());
  }

  @Override
  public CardResponseDto getById(Long id, String userId) {
    Optional<Card> card = cardRepository.findByUserId(id, userId);

    if (!card.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    return mapCardToCardResponseDto(card.get());
  }

  @Override
  public CardResponseDto update(Long id, String userId, CardRequestDto cardRequestDto) {
    Optional<Card> card = cardRepository.findByUserId(id, userId);
    Optional<Amount> amount = amountRepository.findByUserId(id, userId);
    Optional<Bank> bank = bankRepository.findByUserId(id, userId);

    if (!card.isPresent() || !amount.isPresent() || !bank.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    card.get().setName(cardRequestDto.getName());
    card.get().setStatementDate(cardRequestDto.getStatementDate());
    card.get().setAmount(amount.get());
    card.get().setBank(bank.get());
    Card updateCard = cardRepository.save(card.get());

    return mapCardToCardResponseDto(updateCard);
  }

  @Override
  public boolean delete(Long id, String userId) {
    Optional<Card> card = cardRepository.findByUserId(id, userId);

    if (!card.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record ID doesn't exist.");
    }

    cardRepository.deleteById(id);

    card = cardRepository.findById(id);
    return !card.isPresent();
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
        InstallmentResponseDto installment = InstallmentMapper.mapInstallmentToInstallmentResponseDto(
            unit);

        installments.add(installment);
      });
      response.setInstallments(installments);
    }
    response.setCreatedAt(card.getCreatedAt());
    response.setUpdatedAt(card.getUpdatedAt());

    return response;
  }
}
