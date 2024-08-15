package ru.alexnika.faker.http.server.response.processors;

import ru.alexnika.faker.http.server.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Processor implements RequestProcessor {
    final Logger logger = LogManager.getLogger(Processor.class.getName());
    final TemplateRequestPreprocessor templateRequest;

    public Processor() {
        this.templateRequest = new TemplateRequestPreprocessor();
    }

    @Override
    public void execute(HttpRequest request, OutputStream out) throws IOException {
    }
}
