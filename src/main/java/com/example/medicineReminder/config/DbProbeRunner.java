package com.example.medicineReminder.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbProbeRunner implements CommandLineRunner
{
    private final JdbcTemplate jdbc;

    public DbProbeRunner(JdbcTemplate jdbc)
    {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args)
    {
        try
        {
            String db = jdbc.queryForObject("SELECT DATABASE()", String.class);
            Integer users = jdbc.queryForObject("SELECT COUNT(*) FROM AppUsers", Integer.class);
            Integer meds = jdbc.queryForObject("SELECT COUNT(*) FROM UserMedications", Integer.class);
            Integer schedules = jdbc.queryForObject("SELECT COUNT(*) FROM IntakeSchedules", Integer.class);
            System.out.println("[DB-PROBE] database=" + db +
                    " users=" + users + " meds=" + meds + " schedules=" + schedules);
        }
        catch (Exception e)
        {
            System.out.println("[DB-PROBE] failed: " + e.getMessage());
        }
    }
}
