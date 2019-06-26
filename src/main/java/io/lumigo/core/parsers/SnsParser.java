package io.lumigo.core.parsers;

import com.amazonaws.Request;
import com.amazonaws.Response;
import io.lumigo.core.utils.XmlUtils;
import io.lumigo.models.HttpSpan;
import java.util.List;
import org.pmw.tinylog.Logger;
import org.w3c.dom.Document;

public class SnsParser implements IAwsParser {
    @Override
    public void parse(HttpSpan span, Request request, Response response) {
        String topicArn = getParameter(request, "TopicArn");
        span.getInfo().setResourceName(topicArn);
        span.getInfo().setTargetArn(topicArn);
        span.getInfo().setMessageId(extractMessageId(response.getAwsResponse()));
    }

    private String extractMessageId(Object response) {
        try {
            Document document = XmlUtils.convertStringToDocument(response.toString());
            return XmlUtils.getXpathFirstTextValue(
                    document, "/PublishResponse/PublishResult/MessageId/text()");
        } catch (Exception e) {
            Logger.error(e, "Failed to extract messageId for SNS response");
            return null;
        }
    }

    private String getParameter(Request request, String key) {

        if (request.getParameters() != null
                && request.getParameters().get(key) != null
                && ((List) request.getParameters().get(key)).size() > 0) {
            return ((List) request.getParameters().get(key)).get(0).toString();
        }
        return null;
    }
}
