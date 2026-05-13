package gov.kh.mcr.inspectorate.util;

public final class SecurityConstants {

    private SecurityConstants() {}

    public static final String BEARER_PREFIX    = "Bearer ";
    public static final String AUTH_HEADER      = "Authorization";
    public static final String REFRESH_HEADER   = "Refresh-Token";
    public static final String BLACKLIST_PREFIX  = "blacklist:";
    public static final String DEFAULT_PASSWORD  = "Pass@1234";
    public static final String SUPER_ADMIN_ROLE  = "SUPER_ADMIN";
    public static final String ADMIN_ROLE        = "ADMIN";
    public static final String MANAGER_ROLE      = "MANAGER";
    public static final String OFFICER_ROLE      = "OFFICER";
    public static final String AUDITOR_ROLE      = "AUDITOR";
    public static final long   BCRYPT_STRENGTH   = 12L;
}