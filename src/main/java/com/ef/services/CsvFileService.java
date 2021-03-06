package com.ef.services;

import com.ef.model.AccessLog;
import com.ef.repository.AccessLogRepository;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

@Slf4j
public class CsvFileService {

    private static final int MAX_SIZE = 1_000;
    private final AccessLogRepository accessLogRepository;

    public CsvFileService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public void processFile(Path file) {

        try (Stream<String> lines = Files.lines(file)) {
            Flux.using(() -> lines, Flux::fromStream, BaseStream::close)
                .map(this::parseAccessLog)
                .buffer(MAX_SIZE)
                .doOnNext(accessLogRepository::insertAccessLogs)
                .subscribe();
        } catch (IOException e) {
            throw new RuntimeException("Could not process file: " + file, e);
        }
    }

    private AccessLog parseAccessLog(String line) {
        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        CsvSchema schema = mapper.schemaFor(AccessLog.class);
        schema = schema.withColumnSeparator('|');

        try {
            return mapper.readerFor(AccessLog.class).with(schema).readValue(line);
        } catch (IOException e) {
            throw new RuntimeException("Invalid log line: " + e.getMessage(), e);
        }
    }
}
