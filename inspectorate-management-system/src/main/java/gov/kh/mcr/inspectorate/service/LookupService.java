package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.response.LookupResponse;

import java.util.List;

public interface LookupService {

    List<LookupResponse> getOfficerStatuses();

    List<LookupResponse> getDocumentStatuses();

    List<LookupResponse> getDocumentStatusesByTarget(
            String appliesTo);

    List<LookupResponse> getMeetingStatuses();

    List<LookupResponse> getAnnouncementStatuses();

    List<LookupResponse> getUserStatuses();
}