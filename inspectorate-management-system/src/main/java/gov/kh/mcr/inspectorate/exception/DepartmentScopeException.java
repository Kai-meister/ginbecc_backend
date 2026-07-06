package gov.kh.mcr.inspectorate.exception;

import lombok.Getter;

@Getter
public class DepartmentScopeException
        extends RuntimeException {

    private final String ownDepartment;
    private final String targetDepartment;

    public DepartmentScopeException(
            String ownDepartment,
            String targetDepartment) {
        super(buildMessage(
                ownDepartment,
                targetDepartment));
        this.ownDepartment = ownDepartment;
        this.targetDepartment =
                targetDepartment;
    }

    private static String buildMessage(
            String ownDept,
            String targetDept) {
        return "អ្នកគ្រប់គ្រងតែ"
                + " នាយកដ្ឋាន \""
                + ownDept + "\""
                + " ប៉ុណ្ណោះ"
                + " — មិនអាចចូលប្រើ/"
                + "កែប្រែទិន្នន័យ"
                + " នាយកដ្ឋាន \""
                + targetDept + "\""
                + " បានទេ — សូមទាក់ទង"
                + " Admin ប្រសិនបើ"
                + " ត្រូវការសិទ្ធបន្ថែម";
    }
}