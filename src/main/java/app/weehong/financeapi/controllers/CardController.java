package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.services.CardService;
import app.weehong.financeapi.utils.JwtUtil;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

  private final CardService<CardResponseDto, CardRequestDto> cardService;

  private final JwtUtil jwtUtil;

  @Autowired
  public CardController(CardService<CardResponseDto, CardRequestDto> cardService, JwtUtil jwtUtil) {
    this.cardService = cardService;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping
  public ResponseEntity getCards(
      @RequestHeader(name = "Authorization") String authorizationHeader) {
    log.info("getCards() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    List<CardResponseDto> cards = cardService.all(userId);
    return ResponseUtil.ResponseMapping(cards, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity getCard(@RequestHeader(name = "Authorization") String authorizationHeader,
      @PathVariable Long id) {
    log.info("getCard(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    CardResponseDto card = cardService.getById(id, userId);
    return ResponseUtil.ResponseMapping(card, "Record has fetched successfully", HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createCard(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @Valid @RequestBody CardRequestDto cardRequestDto) {
    log.info("createCard() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    CardResponseDto card = cardService.create(userId, cardRequestDto);
    return ResponseUtil.ResponseMapping(card, "Successfully created card", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateCard(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id,
      @Valid @RequestBody CardRequestDto cardRequestDto) {
    log.info("getCard(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    CardResponseDto card = cardService.update(id, userId, cardRequestDto);
    return ResponseUtil.ResponseMapping(card, "Successfully update card", HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteCard(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id) {
    log.info("deleteCard(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    boolean result = cardService.delete(id, userId);
    return ResponseUtil.ResponseMapping(result, "Successfully update card", HttpStatus.OK);
  }
}
