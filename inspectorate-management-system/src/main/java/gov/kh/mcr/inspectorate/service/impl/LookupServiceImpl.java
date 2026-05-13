package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.LookupResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookupServiceImpl
        implements LookupService {

    private final LookupOfficerStatusRepository
            officerStatusRepo;
    private final LookupDocumentStatusRepository
            documentStatusRepo;
    private final LookupMeetingStatusRepository
            meetingStatusRepo;
    private final LookupAnnouncementStatusRepository
            announcementStatusRepo;
    private final LookupUserStatusRepository
            userStatusRepo;

    @Override
    @Cacheable(
            value     = "lookups",
            key       = "'officer_status'")
    public List<LookupResponse> getOfficerStatuses() {
        return officerStatusRepo
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lookups",
            key   = "'document_status'")
    public List<LookupResponse> getDocumentStatuses() {
        return documentStatusRepo
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lookups",
            key   = "'document_status_' + #appliesTo")
    public List<LookupResponse>
    getDocumentStatusesByTarget(
            String appliesTo) {

        // Return BOTH + specific target
        return documentStatusRepo
                .findByAppliesToInAndIsActiveTrueOrderBySortOrderAsc(
                        List.of("BOTH", appliesTo))
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Cacheable(
            value = "lookups",
            key   = "'meeting_status'")
    public List<LookupResponse> getMeetingStatuses() {
        return meetingStatusRepo
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Cacheable(
            value = "lookups",
            key   = "'announcement_status'")
    public List<LookupResponse>
    getAnnouncementStatuses() {
        return announcementStatusRepo
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    @Cacheable(
            value = "lookups",
            key   = "'user_status'")
    public List<LookupResponse> getUserStatuses() {
        return userStatusRepo
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private LookupResponse toResponse(
            LookupOfficerStatus s) {
        return LookupResponse.builder()
                .statusCode(s.getStatusCode())
                .labelKh(s.getLabelKh())
                .labelEn(s.getLabelEn())
                .sortOrder(s.getSortOrder())
                .isActive(s.getIsActive())
                .build();
    }

    private LookupResponse toResponse(
            LookupDocumentStatus s) {
        return LookupResponse.builder()
                .statusCode(s.getStatusCode())
                .labelKh(s.getLabelKh())
                .labelEn(s.getLabelEn())
                .sortOrder(s.getSortOrder())
                .isActive(s.getIsActive())
                .appliesTo(s.getAppliesTo())
                .build();
    }

    private LookupResponse toResponse(
            LookupMeetingStatus s) {
        return LookupResponse.builder()
                .statusCode(s.getStatusCode())
                .labelKh(s.getLabelKh())
                .labelEn(s.getLabelEn())
                .sortOrder(s.getSortOrder())
                .isActive(s.getIsActive())
                .build();
    }

    private LookupResponse toResponse(
            LookupAnnouncementStatus s) {
        return LookupResponse.builder()
                .statusCode(s.getStatusCode())
                .labelKh(s.getLabelKh())
                .labelEn(s.getLabelEn())
                .sortOrder(s.getSortOrder())
                .isActive(s.getIsActive())
                .build();
    }

    private LookupResponse toResponse(
            LookupUserStatus s) {
        return LookupResponse.builder()
                .statusCode(s.getStatusCode())
                .labelKh(s.getLabelKh())
                .labelEn(s.getLabelEn())
                .sortOrder(s.getSortOrder())
                .isActive(s.getIsActive())
                .blockReason(s.getBlockReason())
                .build();
    }
}