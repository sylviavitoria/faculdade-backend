package com.sylviavitoria.apifaculdade.mapper;


import com.sylviavitoria.apifaculdade.dto.AlunoRequestDTO;
import com.sylviavitoria.apifaculdade.dto.AlunoResponseDTO;
import com.sylviavitoria.apifaculdade.model.Aluno;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlunoMapper {
    Aluno toEntity(AlunoRequestDTO request);
    AlunoResponseDTO toDTO(Aluno aluno);
}
