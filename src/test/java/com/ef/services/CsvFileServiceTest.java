package com.ef.services;

import com.ef.AbstractApplicationTest;
import com.ef.model.AccessLog;
import com.ef.repository.AccessLogRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class CsvFileServiceTest extends AbstractApplicationTest {

    @Autowired
    private CsvFileService csvFileService;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Test
    public void processFileTest() {
        String path = getResource(REQUESTS_LOG_FILE);
        csvFileService.processFile(path);
        List<String> ips = getInsertedIpAddresses();
        List<AccessLog> accessLogs = accessLogRepository.getAccessLog(ips);
        Assert.assertTrue(accessLogs.stream()
            .map(AccessLog::getIpAddress)
            .collect(Collectors.toList())
            .containsAll(ips));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileNotFoundTest() {
        String fakePath = "data/fake.log";
        csvFileService.processFile(fakePath);
    }
}