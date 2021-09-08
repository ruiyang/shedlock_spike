package me.ryang.shedlock_spike;

import net.javacrumbs.shedlock.core.SimpleLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class LockController {
  private static List<SimpleLock> locks = Collections.synchronizedList(new ArrayList<>());

  LockService lockService;
  ExecutorService executor = Executors.newFixedThreadPool(100);

  @Autowired
  public LockController(LockService lockService) {
    this.lockService = lockService;
  }


  @GetMapping("/test/lock/get")
  public void getLocker(@RequestParam Integer loopForSeconds) {
    executor.execute(new GetLockerThread(lockService, loopForSeconds));
  }

  @GetMapping("/test/lock/release")
  public void releaseLocker() {
    synchronized (locks) {
      try {
        if (locks.size() > 0) {
          System.out.println("=== release the lock, size " + locks.size());
          try {
            locks.forEach((l) -> {
              lockService.releaseLock(l);
            });
          } catch (Exception e) {
          }
        }
        locks.clear();
      } catch (Exception e) {
        System.out.println("failed to release lock, pls retry");
        locks.clear();
      }
    }
  }

  @GetMapping("/test/lock/parallel")
  public void parallelLock(@RequestParam Long totalThread, @RequestParam Integer loopForSeconds) {
    for (int i = 0; i < totalThread; i++) {
      executor.execute(new GetLockerThread(lockService, loopForSeconds));
    }
  }

  private static class GetLockerThread implements Runnable {
    private LockService lockService;
    private int loopPerThread;
    private int sleepTime = 500;

    public GetLockerThread(LockService lockService, int loopForSeconds) {
      this.lockService = lockService;
      this.loopPerThread = loopForSeconds * 1000 / sleepTime;
    }

    @Override
    public void run() {
      int i = 0;
      Optional<SimpleLock> testLocker = lockService.getLock("testLocker");
      while (i < loopPerThread && !testLocker.isPresent()) {
        testLocker = lockService.getLock("testLocker");
        if (!testLocker.isPresent()) {
          try {
            Thread.sleep(sleepTime);
          } catch (InterruptedException e) {
            System.out.println("error===" + e.getMessage());
          }
          i++;
        } else {
          break;
        }
      }

      if (testLocker.isPresent()) {
        System.out.println("=== hold the lock, thread " + Thread.currentThread().getId());
        locks.add(testLocker.get());
      }
    }
  }
}
