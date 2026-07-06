package gov.kh.mcr.inspectorate.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Initializes Firebase Admin and exposes a {@link FirebaseMessaging} bean for FCM push
 * notifications.
 *
 * <p>Credentials are supplied as the full service-account JSON via the {@code FIREBASE_CREDENTIALS}
 * environment variable (mapped to {@code firebase.credentials}). When it is blank — local dev or
 * a Render instance without Firebase configured — this whole config is skipped and the app starts
 * normally with no {@code FirebaseMessaging} bean. Inject it as {@code Optional<FirebaseMessaging>}
 * (or {@code @Autowired(required = false)}) at the call site so FCM is simply absent when disabled.
 */
@Configuration
@ConditionalOnExpression("!'${firebase.credentials:}'.isEmpty()")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials:}")
    private String credentialsJson;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        InputStream creds = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(creds))
                .build();

        FirebaseApp app = FirebaseApp.getApps().isEmpty()
                ? FirebaseApp.initializeApp(options)
                : FirebaseApp.getInstance();

        log.info("Firebase initialized — FCM push notifications enabled");
        return FirebaseMessaging.getInstance(app);
    }
}