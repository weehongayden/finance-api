package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.CardRequestDto;
import app.weehong.financeapi.dtos.response.CardResponseDto;
import app.weehong.financeapi.services.CardService;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService<CardResponseDto, CardRequestDto> cardService;

    public CardController(CardService<CardResponseDto, CardRequestDto> cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity getCards() {
        log.info("getCards() function called");

        List<CardResponseDto> cards = cardService.all();
        return ResponseUtil.ResponseMapping(cards, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getCard(@PathVariable Long id) {
        log.info("getCard(" + id + ") function called");

        CardResponseDto card = cardService.getById(id);
        return ResponseUtil.ResponseMapping(card, "Record has fetched successfully", HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCard(@Valid @RequestBody CardRequestDto cardRequestDto) throws ParseException {
        log.info("createCard() function called");

        CardResponseDto card = cardService.create(cardRequestDto);
        return ResponseUtil.ResponseMapping(card, "Successfully created card", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateCard(@PathVariable Long id, @Valid @RequestBody CardRequestDto cardRequestDto) throws ParseException {
        log.info("getCard(" + id + ") function called");

        CardResponseDto card = cardService.update(id, cardRequestDto);
        return ResponseUtil.ResponseMapping(card, "Successfully update card", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCard(@PathVariable Long id) {
        log.info("deleteCard(" + id + ") function called");

        boolean result = cardService.delete(id);
        return ResponseUtil.ResponseMapping(result, "Successfully update card", HttpStatus.OK);
    }
}
