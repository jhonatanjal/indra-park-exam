package com.indraparkapi.repository;

import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

    List<Operacao> findByDataHoraEntradaIsBetweenOrEstadoIs(LocalDateTime start,
                                                            LocalDateTime end,
                                                            EstadoOperacao estado);

    List<Operacao> findByDataHoraEntradaIsBetween(LocalDateTime start, LocalDateTime end);

    Integer countByDataHoraEntradaBetweenAndVeiculo_Modelo(LocalDateTime start,
                                                           LocalDateTime end,
                                                           ModeloVeiculo modelo);

    Optional<Operacao> findByVeiculoPlacaIgnoreCaseAndEstado(String placa, EstadoOperacao estado);

    List<Operacao> findByVeiculoPlacaIgnoreCase(String placaVeiculo);
}
