package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.request
        .ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.response
        .ContractOfficerResponse;
import gov.kh.mcr.inspectorate.entity
        .ContractOfficer;
import org.mapstruct.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy =
                ReportingPolicy.IGNORE)
public interface ContractOfficerMapper {

    @Mapping(target = "contractOfficerId",
            ignore = true)
    @Mapping(target = "department",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    ContractOfficer toEntity(
            ContractOfficerRequest request);

    @Mapping(target = "departmentName",
            source = "department.departmentName")
    @Mapping(target = "departmentId",
            source = "department.departmentId")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    @Mapping(target = "age",
            expression =
                    "java(calcAge("
                            + "entity.getDob()))")
    @Mapping(target = "daysUntilExpiry",
            expression =
                    "java(calcDays("
                            + "entity.getEndDate()))")
    @Mapping(target = "expiryLabel",
            expression =
                    "java(calcLabel("
                            + "entity.getEndDate()))")
    ContractOfficerResponse toResponse(
            ContractOfficer entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy
                            .IGNORE)
    @Mapping(target = "contractOfficerId",
            ignore = true)
    @Mapping(target = "department",
            ignore = true)
    @Mapping(target = "statusCode",
            ignore = true)
    @Mapping(target = "createdAt",
            ignore = true)
    @Mapping(target = "updatedAt",
            ignore = true)
    void updateEntity(
            ContractOfficerRequest request,
            @MappingTarget ContractOfficer entity);

    default Integer calcAge(LocalDate dob) {
        if (dob == null) return null;
        return Period.between(
                dob, LocalDate.now()).getYears();
    }

    default Long calcDays(LocalDate endDate) {
        if (endDate == null) return null;
        long days = ChronoUnit.DAYS.between(
                LocalDate.now(), endDate);
        return days < 0 ? 0L : days;
    }

    default String calcLabel(LocalDate endDate) {
        if (endDate == null) return null;
        long days = ChronoUnit.DAYS.between(
                LocalDate.now(), endDate);

        if (days < 0) {
            return "ផុតកំណត់រួច "
                    + Math.abs(days) + " ថ្ងៃ";
        }
        if (days == 0) {
            return "ផុតកំណត់ថ្ងៃនេះ ";
        }
        if (days <= 7) {
            return "នៅសល់ " + days
                    + " ថ្ងៃ ";
        }
        if (days <= 30) {
            return "នៅសល់ " + days
                    + " ថ្ងៃ ";
        }
        return "នៅសល់ " + days + " ថ្ងៃ";
    }
}