package gov.kh.mcr.inspectorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling

public class InspectorateManagementSystemApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Phnom_Penh"));
		SpringApplication.run(InspectorateManagementSystemApplication.class, args);
	}

}
