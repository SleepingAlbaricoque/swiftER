package kr.co.swiftER;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication // spring security login창 뜨면 @SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}) 으로 바꾸기
public class SwiftErApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftErApplication.class, args);
	}

}
