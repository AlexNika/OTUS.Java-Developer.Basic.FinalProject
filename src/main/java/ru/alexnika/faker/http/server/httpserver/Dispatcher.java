package ru.alexnika.faker.http.server.httpserver;

import ru.alexnika.faker.http.server.domain.FakeItemsRepository;
import ru.alexnika.faker.http.server.request.HttpAccept;
import ru.alexnika.faker.http.server.request.HttpRequest;
import ru.alexnika.faker.http.server.response.HttpResponse;
import ru.alexnika.faker.http.server.response.Response;
import ru.alexnika.faker.http.server.exceptions.BadRequestException;
import ru.alexnika.faker.http.server.exceptions.DefaultErrorDto;
import ru.alexnika.faker.http.server.response.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "CallToPrintStackTrace"})
public class Dispatcher {
    private static final Logger logger = LogManager.getLogger(Dispatcher.class.getName());
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundRequestProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private FakeItemsRepository fakeItemsRepository;

    public Dispatcher() {
        this.fakeItemsRepository = new FakeItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("OPTIONS /", new OptionsProcessor());
        this.processors.put("GET /", new HomePageRequestProcessor());
        this.processors.put("GET /import.this", new FileRequestProcessor());
        this.processors.put("GET /fakeitem", new GetFakeItemProcessor(fakeItemsRepository));
        this.processors.put("GET /fakeitems", new GetAllFakeItemsProcessor(fakeItemsRepository));
        this.processors.put("POST /fakeitems", new AddNewFakeItemProcessor(fakeItemsRepository));
        this.processors.put("PUT /fakeitems", new UpdateFakeItemProcessor(fakeItemsRepository));
        this.processors.put("DELETE /fakeitems", new DeleteItemProcessor(fakeItemsRepository));

        this.defaultNotFoundRequestProcessor = new DefaultNotFoundRequestProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorRequestProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        TemplateRequestPreprocessor templateRequest = new TemplateRequestPreprocessor();
        try {
            if (!processors.containsKey(request.getRoutingKey())) {
                defaultNotFoundRequestProcessor.execute(request, out);
                return;
            }
            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (BadRequestException e) {
            e.printStackTrace();
            DefaultErrorDto defaultErrorDto = new DefaultErrorDto("CLIENT_DEFAULT_ERROR", e.getMessage());
            String responseError = new Gson().toJson(defaultErrorDto);
            HttpAccept acceptType = request.getAcceptType();
            Response httpresponse = HttpResponse.error400(acceptType, responseError);
            String response = templateRequest.prepareResponse(httpresponse);
            out.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("I/O error occurs", e);
            defaultInternalServerErrorProcessor.execute(request, out);
        }
    }
}
