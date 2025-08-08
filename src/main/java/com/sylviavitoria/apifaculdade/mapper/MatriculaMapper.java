package com.sylviavitoria.apifaculdade.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.sylviavitoria.apifaculdade.dto.MatriculaRequestDTO;
import com.sylviavitoria.apifaculdade.dto.MatriculaResponseDTO;
import com.sylviavitoria.apifaculdade.model.Matricula;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MatriculaMapper {
    Matricula toEntity(MatriculaRequestDTO request);
    MatriculaResponseDTO toDTO(Matricula matricula);
}