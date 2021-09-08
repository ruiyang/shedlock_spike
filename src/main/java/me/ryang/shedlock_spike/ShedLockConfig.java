package me.ryang.shedlock_spike;

import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ShedLockConfig {

  @Autowired
  DataSource dataSource;

  @Autowired
  PlatformTransactionManager transactionManager;

  @Bean
  public JdbcTemplateLockProvider lockProvider() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    JdbcTemplateLockProvider.Configuration.Builder builder = new JdbcTemplateLockProvider.Configuration.Builder();
    return new JdbcTemplateLockProvider(builder.withJdbcTemplate(jdbcTemplate).withTransactionManager(transactionManager)
                                               .usingDbTime()
                                               .build());
  }
}
