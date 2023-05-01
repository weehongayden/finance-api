package app.weehong.financeapi.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Entity
@Table(name = "installments")
public class Installment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  @Column(name = "amount")
  private BigDecimal totalAmount;

  @Column(name = "tenure")
  private Integer tenure;

  @Column(name = "leftover_tenure")
  private Integer leftoverTenure;

  @Column(name = "price_per_month")
  private BigDecimal pricePerMonth;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Column(name = "start_date")
  private LocalDate startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;
}
