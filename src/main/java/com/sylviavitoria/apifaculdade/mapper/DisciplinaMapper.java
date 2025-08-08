package com.sylviavitoria.apifaculdade.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.sylviavitoria.apifaculdade.dto.DisciplinaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.DisciplinaResponseDTO;
import com.sylviavitoria.apifaculdade.model.Disciplina;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DisciplinaMapper {
    Disciplina toEntity(DisciplinaRequestDTO request);
    DisciplinaResponseDTO toDTO(Disciplina disciplina);
    void updateEntity(DisciplinaRequestDTO request, @MappingTarget Disciplina disciplina);
}
