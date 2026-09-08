package br.tec.db.votacao.service;

import br.tec.db.votacao.dto.Associado.CriarAssociadoDTO;
import br.tec.db.votacao.dto.Associado.ListarAssociadoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.repository.AssociadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AssociadoServiceTest {

    @Mock
    private AssociadoRepository associadoRepository;

    @InjectMocks
    private AssociadoService associadoService;

    @Test
    void deveCriarAssociadoComSucesso() {
        CriarAssociadoDTO dto = new CriarAssociadoDTO(
                "João da Silva",
                "12345678901"
        );

        Associado associado = Associado.builder()
                .id(UUID.randomUUID())
                .nome("João da Silva")
                .cpf("12345678901")
                .build();

        when(associadoRepository.saveAndFlush(any(Associado.class)))
                .thenReturn(associado);

        ListarAssociadoDTO resultado = associadoService.create(dto);

        assertNotNull(resultado);
        assertEquals(associado.getId(), resultado.id());
        assertEquals("João da Silva", resultado.nome());
        assertEquals("12345678901", resultado.cpf());

        verify(associadoRepository).saveAndFlush(any(Associado.class));
        verify(associadoRepository, never()).save(any(Associado.class));
    }

    @Test
    void deveLancarConflictExceptionQuandoCpfJaExistir() {
        CriarAssociadoDTO dto = new CriarAssociadoDTO(
                "João da Silva",
                "12345678901"
        );

        when(associadoRepository.saveAndFlush(any(Associado.class)))
                .thenThrow(new DataIntegrityViolationException("CPF duplicado"));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> associadoService.create(dto)
        );

        assertEquals("Já existe um associado com este CPF.", exception.getMessage());

        verify(associadoRepository).saveAndFlush(any(Associado.class));
    }
}