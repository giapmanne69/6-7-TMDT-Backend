package ptit.tmdt.lop6nhom7.baodientu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "vip_packages", schema = "pthttmdt")
public class VipPackage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;
  
  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;
  
  @NotNull
  @Column(name = "duration_days", nullable = false)
  private Integer durationDays;
  
  @NotNull
  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;
  
  @Column(name = "description")
  private String description;
  
}