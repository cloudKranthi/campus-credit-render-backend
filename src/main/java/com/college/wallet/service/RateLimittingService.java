package com.college.wallet.service;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Service
@Slf4j
public class RateLimittingService {
 private final Map<String,Bucket> hashedMap=new ConcurrentHashMap<>();
 public boolean isAllowed(String phoneNumber,String Path){
    String keys=phoneNumber+":"+Path;
    Bucket bucket=hashedMap.computeIfAbsent(keys,k->createBucket(Path));
    return bucket.tryConsume(1);
 }   
 public Bucket createBucket(String path ){
    int limit=path.contains("transfer")||path.contains("addmoney")?1:7;
    return Bucket.builder().addLimit(Bandwidth.classic(limit,Refill.intervally(limit,Duration.ofMinutes(1)))).build();
 }
 @Scheduled(cron = "0 0 * * * *")
 public void clearBucket(){
    hashedMap.clear();
    log.info("Hashed Map cleared for optimization for memory savings");
 }
}
