package gov.kh.mcr.inspectorate.mapper;
import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractOfficerMapper {

    @Mapping(target = "contractOfficerId", ignore = true)
    @Mapping(target = "department",        ignore = true)
    @Mapping(target = "statusCode",        ignore = true)
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "updatedAt",         ignore = true)
    ContractOfficer toEntity(ContractOfficerRequest request);

    @Mapping(target = "departmentName",
            source = "department.departmentName")
    @Mapping(target = "statusCode",
            source = "statusCode.statusCode")
    @Mapping(target = "statusLabel",
            source = "statusCode.labelKh")
    ContractOfficerResponse toResponse(ContractOfficer entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "contractOfficerId", ignore = true)
    @Mapping(target = "department",        ignore = true)
    @Mapping(target = "statusCode",        ignore = true)
    @Mapping(target = "createdAt",         ignore = true)
    @Mapping(target = "updatedAt",         ignore = true)
    void updateEntity(ContractOfficerRequest request,
                      @MappingTarget ContractOfficer entity);
}