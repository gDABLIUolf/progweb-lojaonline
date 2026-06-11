package com.vesteBem.repository;

import com.vesteBem.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByProdutoId(Long produtoId);
    List<Avaliacao> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
}