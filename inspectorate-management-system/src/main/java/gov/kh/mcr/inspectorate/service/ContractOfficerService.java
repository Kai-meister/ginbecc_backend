package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ContractOfficerService {


    PageResponse<ContractOfficerResponse> getAll(int page, int size, String status, Integer deptId, Integer expiringWithinDays);

    ContractOfficerResponse getById(Integer id);

    ContractOfficerResponse create(ContractOfficerRequest request);

    ContractOfficerResponse update(Integer id, ContractOfficerRequest request);

//    ContractOfficerResponse updateStatus(Integer id, StatusRequest request);
    String getProfileImageUrl(Integer contractOfficerId);

    void delete(Integer id);
    ContractOfficerResponse uploadProfileImage(Integer contractOfficerId, MultipartFile file);

    ContractOfficerResponse deleteProfileImage(Integer contractOfficerId);
}
