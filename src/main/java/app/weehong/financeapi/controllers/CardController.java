package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.services.CardService;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

  private final CardService<CardResponseDto, CardRequestDto> cardService;

  @Autowired
  public CardController(CardService<CardResponseDto, CardRequestDto> cardService) {
    this.cardService = cardService;
  }

  @GetMapping
  public ResponseEntity getCards(
      Authentication authentication) {
    log.info("getCards() function called");
    List<CardResponseDto> cards = cardService.all(authentication.getName());
    return ResponseUtil.ResponseMapping(cards, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity getCard(Authentication authentication,
      @PathVariable Long id) {
    log.info("getCard(" + id + ") function called");
    CardResponseDto card = cardService.getById(id, authentication.getName());
    return ResponseUtil.ResponseMapping(card, "Record has fetched successfully", HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createCard(
      Authentication authentication,
      @Valid @RequestBody CardRequestDto cardRequestDto) {
    log.info("createCard() function called");
    CardResponseDto card = cardService.create(authentication.getName(), cardRequestDto);
    return ResponseUtil.ResponseMapping(card, "Successfully created card", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateCard(
      Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody CardRequestDto cardRequestDto) {
    log.info("getCard(" + id + ") function called");
    CardResponseDto card = cardService.update(id, authentication.getName(), cardRequestDto);
    return ResponseUtil.ResponseMapping(card, "Successfully update card", HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteCard(
          Authentication authentication, @PathVariable Long id) {
    log.info("deleteCard(" + id + ") function called");
    boolean result = cardService.delete(id, authentication.getName());
    return ResponseUtil.ResponseMapping(result, "Successfully update card", HttpStatus.OK);
  }
}
