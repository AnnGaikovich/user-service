package org.example.userservice.service;

import org.example.userservice.dto.PaymentCardRequestDTO;
import org.example.userservice.dto.PaymentCardResponseDTO;
import org.example.userservice.entity.PaymentCard;
import org.example.userservice.entity.User;
import org.example.userservice.exception.PaymentCardNotFoundException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.exception.BusinessRuleException;
import org.example.userservice.mapper.PaymentCardMapper;
import org.example.userservice.repository.PaymentCardRepository;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.specification.PaymentCardSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.example.userservice.entity.User.MAX_PAYMENT_CARDS;

@Service
public class PaymentCardService {

    private static final Logger log = LoggerFactory.getLogger(PaymentCardService.class);
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    public PaymentCardService(PaymentCardRepository paymentCardRepository,
                              UserRepository userRepository,
                              PaymentCardMapper paymentCardMapper) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
        this.paymentCardMapper = paymentCardMapper;
    }

    public PaymentCardResponseDTO getCardById(Long id) {
        log.info("Fetching payment card by ID: {}", id);
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        return paymentCardMapper.toResponseDTO(card);
    }

    public Page<PaymentCardResponseDTO> getAllCards(Pageable pageable, String holder, String number) {
        log.info("Fetching all payment cards");
        Specification<PaymentCard> spec = PaymentCardSpecifications.withFilters(holder, number);
        Page<PaymentCard> cardsPage = paymentCardRepository.findAll(spec, pageable);

        log.info("Found {} payment cards with given filters", cardsPage.getTotalElements());
        return cardsPage.map(paymentCardMapper::toResponseDTO);
    }

    public Page<PaymentCardResponseDTO> getCardsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching payment cards for user ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Page<PaymentCard> cardsPage = paymentCardRepository.findByUserId(userId, pageable);
        return cardsPage.map(paymentCardMapper::toResponseDTO);
    }

    @CacheEvict(value = "users", key = "#cardDTO.userId")
    @Transactional
    public PaymentCardResponseDTO createCard(PaymentCardRequestDTO cardDTO) {
        log.info("Creating new payment card for user ID: {}", cardDTO.getUserId());

        User user = userRepository.findById(cardDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(cardDTO.getUserId()));

        long cardCount = paymentCardRepository.countByUserId(user.getId());
        if (cardCount >= MAX_PAYMENT_CARDS) {
            throw new BusinessRuleException("User cannot have more than " + MAX_PAYMENT_CARDS + " payment cards");
        }

        if (paymentCardRepository.findByNumber(cardDTO.getNumber()).isPresent()) {
            throw new BusinessRuleException("Card with number " + cardDTO.getNumber() + " already exists");
        }

        PaymentCard card = paymentCardMapper.toEntity(cardDTO);

        try {

            user.addPaymentCard(card);
        } catch (IllegalStateException e) {
            throw new BusinessRuleException(e.getMessage());
        }

        userRepository.save(user);

        log.info("Created payment card with ID: {}", card.getId());

        return paymentCardMapper.toResponseDTO(card);
    }

    @CacheEvict(value = "users", key = "#cardDTO.userId")
    @Transactional
    public PaymentCardResponseDTO updateCard(Long id, PaymentCardRequestDTO cardDTO) {
        log.info("Updating payment card with ID: {}", id);

        PaymentCard existingCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));

        if (!existingCard.getNumber().equals(cardDTO.getNumber()) &&
                paymentCardRepository.findByNumber(cardDTO.getNumber()).isPresent()) {
            throw new BusinessRuleException("Card number " + cardDTO.getNumber() + " is already taken");
        }

        if (!existingCard.getUser().getId().equals(cardDTO.getUserId())) {
            User newUser = userRepository.findById(cardDTO.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(cardDTO.getUserId()));

            long cardCount = paymentCardRepository.countByUserId(newUser.getId());
            if (cardCount >= 5) {
                throw new BusinessRuleException("User cannot have more than 5 payment cards");
            }
            existingCard.setUser(newUser);
        }

        existingCard.setNumber(cardDTO.getNumber());
        existingCard.setHolder(cardDTO.getHolder());
        existingCard.setExpirationDate(cardDTO.getExpirationDate());
        if (cardDTO.getActive() != null) {
            existingCard.setActive(cardDTO.getActive());
        }

        PaymentCard updatedCard = paymentCardRepository.save(existingCard);
        log.info("Updated payment card with ID: {}", id);

        return paymentCardMapper.toResponseDTO(updatedCard);
    }

    @Transactional
    public void activateCard(Long id) {
        log.info("Activating payment card with ID: {}", id);
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        card.setActive(true);
        paymentCardRepository.save(card);
    }

    @Transactional
    public void deactivateCard(Long id) {
        log.info("Deactivating payment card with ID: {}", id);
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        card.setActive(false);
        paymentCardRepository.save(card);
    }

    @Transactional
    public void deleteCard(Long id) {
        log.info("Deleting payment card with ID: {}", id);
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
        paymentCardRepository.delete(card);
    }
}