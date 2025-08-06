package com.sylviavitoria.apifaculdade.model;

import com.sylviavitoria.apifaculdade.enums.StatusMatricula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Matricula Tests")
class MatriculaTest {

    private Matricula matricula;
    private Aluno aluno;
    private Disciplina disciplina;
    private Professor professor;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Prof. Maria Silva");
        professor.setEmail("maria.silva@universidade.com");
        professor.setSenha("senha123");

        disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setNome("Algoritmos");
        disciplina.setCodigo("ALG101");
        disciplina.setProfessor(professor);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João da Silva");
        aluno.setEmail("joao@email.com");
        aluno.setMatricula("2023001");
        aluno.setSenha("senha123");

        matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setDisciplina(disciplina);
        matricula.setDataMatricula(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    @Test
    @DisplayName("Deve calcular média e status APROVADO quando notas >= 7.0")
    void deveCalcularMediaEStatusAprovadoQuandoNotasMaiorOuIgualSete() {

        BigDecimal nota1 = new BigDecimal("8.5");
        BigDecimal nota2 = new BigDecimal("7.0");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("7.75"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }

    @Test
    @DisplayName("Deve calcular média e status APROVADO quando média exatamente 7.0")
    void deveCalcularMediaEStatusAprovadoQuandoMediaExatamenteSete() {

        BigDecimal nota1 = new BigDecimal("7.0");
        BigDecimal nota2 = new BigDecimal("7.0");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("7.00"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }

    @Test
    @DisplayName("Deve calcular média e status REPROVADO quando média < 7.0")
    void deveCalcularMediaEStatusReprovadoQuandoMediaMenorQueSete() {

        BigDecimal nota1 = new BigDecimal("5.0");
        BigDecimal nota2 = new BigDecimal("6.5");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("5.75"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.REPROVADO);
    }

    @Test
    @DisplayName("Deve calcular média corretamente com arredondamento HALF_UP")
    void deveCalcularMediaCorretamenteComArredondamento() {

        BigDecimal nota1 = new BigDecimal("7.15");
        BigDecimal nota2 = new BigDecimal("7.14");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("7.15"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }

    @Test
    @DisplayName("Deve manter status CURSANDO quando apenas nota1 está definida")
    void deveManterStatusCursandoQuandoApenasNota1Definida() {

        BigDecimal nota1 = new BigDecimal("8.0");
        matricula.setNota1(nota1);
        matricula.setNota2(null);
        matricula.setStatus(StatusMatricula.CURSANDO);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isNull();
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.CURSANDO);
    }

    @Test
    @DisplayName("Deve manter status CURSANDO quando apenas nota2 está definida")
    void deveManterStatusCursandoQuandoApenasNota2Definida() {

        BigDecimal nota2 = new BigDecimal("7.5");
        matricula.setNota1(null);
        matricula.setNota2(nota2);
        matricula.setStatus(StatusMatricula.CURSANDO);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isNull();
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.CURSANDO);
    }

    @Test
    @DisplayName("Deve manter status CURSANDO quando ambas as notas são nulas")
    void deveManterStatusCursandoQuandoAmbasNotasSaoNulas() {

        matricula.setNota1(null);
        matricula.setNota2(null);
        matricula.setStatus(StatusMatricula.CURSANDO);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isNull();
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.CURSANDO);
    }

    @Test
    @DisplayName("Deve ter status padrão CURSANDO ao criar nova matrícula")
    void deveTerStatusPadraoCursandoAoCriarNovaMatricula() {

        Matricula novaMatricula = new Matricula();

        assertThat(novaMatricula.getStatus()).isEqualTo(StatusMatricula.CURSANDO);
    }

    @Test
    @DisplayName("Deve definir data de matrícula automaticamente")
    void deveDefinirDataMatriculaAutomaticamente() {

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Matricula novaMatricula = new Matricula();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertThat(novaMatricula.getDataMatricula()).isNotNull();
        assertThat(novaMatricula.getDataMatricula()).isAfter(antes);
        assertThat(novaMatricula.getDataMatricula()).isBefore(depois);
    }

    @Test
    @DisplayName("Deve permitir definir relacionamentos com aluno e disciplina")
    void devePermitirDefinirRelacionamentos() {

        assertThat(matricula.getAluno()).isNotNull();
        assertThat(matricula.getAluno().getId()).isEqualTo(1L);
        assertThat(matricula.getAluno().getNome()).isEqualTo("João da Silva");

        assertThat(matricula.getDisciplina()).isNotNull();
        assertThat(matricula.getDisciplina().getId()).isEqualTo(1L);
        assertThat(matricula.getDisciplina().getNome()).isEqualTo("Algoritmos");
        assertThat(matricula.getDisciplina().getCodigo()).isEqualTo("ALG101");
    }

    @Test
    @DisplayName("Deve calcular média com notas decimais complexas")
    void deveCalcularMediaComNotasDecimaisComplexas() {

        BigDecimal nota1 = new BigDecimal("8.75");
        BigDecimal nota2 = new BigDecimal("6.25");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();


        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("7.50"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }

    @Test
    @DisplayName("Deve calcular corretamente nota limite para reprovação")
    void deveCalcularCorretamenteNotaLimiteParaReprovacao() {

        BigDecimal nota1 = new BigDecimal("6.99");
        BigDecimal nota2 = new BigDecimal("7.00");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("7.00"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }

    @Test
    @DisplayName("Deve calcular corretamente nota para reprovação por centésimos")
    void deveCalcularCorretamenteNotaParaReprovacaoPorCentesimos() {

        BigDecimal nota1 = new BigDecimal("6.98");
        BigDecimal nota2 = new BigDecimal("7.00");
        matricula.setNota1(nota1);
        matricula.setNota2(nota2);

        matricula.calcularMediaEStatus();

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("6.99"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.REPROVADO);
    }

    @Test
    @DisplayName("Deve manter valores definidos manualmente quando não recalcular")
    void deveManterValoresDefinidosManualmenteQuandoNaoRecalcular() {
        
        matricula.setMedia(new BigDecimal("8.50"));
        matricula.setStatus(StatusMatricula.APROVADO);
        matricula.setNota1(new BigDecimal("5.0"));
        matricula.setNota2(new BigDecimal("4.0"));

        assertThat(matricula.getMedia()).isEqualTo(new BigDecimal("8.50"));
        assertThat(matricula.getStatus()).isEqualTo(StatusMatricula.APROVADO);
    }
}
