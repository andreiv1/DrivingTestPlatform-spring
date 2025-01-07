package ro.andrei.drivingtestplatform.dataimport;

import ro.andrei.drivingtestplatform.request.QuestionRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface QuestionFileProcessor {
    List<QuestionRequest> processFile(InputStream inputStream, Long examConfigId) throws IOException;
}
