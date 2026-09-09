package br.tec.db.votacao.mapper;

import br.tec.db.votacao.dto.Admin.ListarAdminDTO;
import br.tec.db.votacao.entity.Admin;

public final class AdminMapper {
    private AdminMapper() {
    }

    public static ListarAdminDTO toDto(Admin entity) {
        return ListarAdminDTO.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .dataCadastro(entity.getDataCadastro())
                .build();
    }
}
