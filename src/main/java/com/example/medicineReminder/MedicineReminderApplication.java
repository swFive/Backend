package com.example.medicineReminder;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicineReminderApplication
{
	public static void main(String[] args)
	{
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()   // .env가 없으면 넘어감
				.load();

		String dbUser = dotenv.get("DB_USERNAME", "");
		String dbPass = dotenv.get("DB_PASSWORD", "");

		if (!dbUser.isEmpty())
		{
			System.setProperty("DB_USERNAME", dbUser);
		}
		if (!dbPass.isEmpty())
		{
			System.setProperty("DB_PASSWORD", dbPass);
		}

		SpringApplication.run(MedicineReminderApplication.class, args);
	}
}
