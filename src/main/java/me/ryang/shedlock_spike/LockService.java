package me.ryang.shedlock_spike;

import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.SimpleLock;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
public class LockService {
  private JdbcTemplateLockProvider lockProvider;

  @Autowired
  public LockService(JdbcTemplateLockProvider lockProvider) {
    this.lockProvider = lockProvider;
  }

  public Optional<SimpleLock> getLock(String lockName) {
    LockConfiguration config = new LockConfiguration(Instant.now(), lockName, Duration.ofMillis(15000), Duration.ofMillis(15000));
    return this.lockProvider.lock(config);
  }

  public void releaseLock(SimpleLock lock) {
    SimpleLock realLock = lock;
    Optional<SimpleLock> extended = realLock.extend(Duration.ofMillis(0), Duration.ofMillis(0));
    if (extended.isPresent()) {
      extended.get().unlock();
    }
  }
}
