package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.entity.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> getAll();

    Permission getById(Integer id);

    List<Permission> getByModule(String module);
}