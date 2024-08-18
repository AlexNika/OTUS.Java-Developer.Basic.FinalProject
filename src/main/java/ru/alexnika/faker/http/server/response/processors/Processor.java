package ru.alexnika.faker.http.server.response.processors;

import org.jetbrains.annotations.NotNull;
import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"FieldMayBeFinal", "FieldMayBeLocal"})
public class Processor implements RequestProcessor {
    protected final Logger logger = LogManager.getLogger(Processor.class.getName());
    protected final TemplateRequestPreprocessor templateRequest;
    FakeItemsRepository fakeItemsRepository;

    public Processor(FakeItemsRepository fakeItemsRepository) {
        this.templateRequest = new TemplateRequestPreprocessor();
        this.fakeItemsRepository = fakeItemsRepository;
    }

    public Processor() {
        this.templateRequest = new TemplateRequestPreprocessor();
    }

    @Override
    public void execute(HttpRequest request, OutputStream out) throws IOException {
    }

    public static void send(@NotNull OutputStream out, @NotNull String response) {
        final Logger logger = LogManager.getLogger(Processor.class.getName());
        try {
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("I/O error occurs", e);
        }
    }
}
