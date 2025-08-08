package com.sylviavitoria.apifaculdade.mapper;

import org.mapstruct.Mapper;

import com.sylviavitoria.apifaculdade.dto.ProfessorRequestDTO;
import com.sylviavitoria.apifaculdade.dto.ProfessorResponseDTO;
import com.sylviavitoria.apifaculdade.model.Professor;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorMapper {
    Professor toEntity(ProfessorRequestDTO request);
    ProfessorResponseDTO toDTO(Professor professor);
}