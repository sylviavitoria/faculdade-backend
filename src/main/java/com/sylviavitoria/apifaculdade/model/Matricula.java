package com.sylviavitoria.apifaculdade.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.sylviavitoria.apifaculdade.enums.StatusMatricula;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_matricula")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota1;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota2;

    @Column(precision = 4, scale = 2)
    private BigDecimal media;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusMatricula status = StatusMatricula.CURSANDO;

    @Column(name = "data_matricula")
    private LocalDateTime dataMatricula = LocalDateTime.now();

    public void calcularMediaEStatus() {
        if (nota1 != null && nota2 != null) {
            this.media = nota1.add(nota2)
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

            if (this.media.compareTo(BigDecimal.valueOf(7.0)) >= 0) {
                this.status = StatusMatricula.APROVADO;
            } else {
                this.status = StatusMatricula.REPROVADO;
            }
        }
    }
}