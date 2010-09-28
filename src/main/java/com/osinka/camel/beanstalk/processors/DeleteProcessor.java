package com.osinka.camel.beanstalk.processors;

import com.osinka.camel.beanstalk.BeanstalkEndpoint;
import com.osinka.camel.beanstalk.BeanstalkExchangeHelper;
import com.osinka.camel.beanstalk.Headers;
import com.surftools.BeanstalkClient.Client;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchHeaderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeleteProcessor extends DefaultProcessor {
    private final transient Log LOG = LogFactory.getLog(DeleteProcessor.class);

    public DeleteProcessor(BeanstalkEndpoint endpoint) {
        super(endpoint);
    }

    public DeleteProcessor(BeanstalkEndpoint endpoint, Client client) {
        super(endpoint, client);
    }

    @Override
    public void process(Exchange exchange) throws NoSuchHeaderException {
        clientNotNull(exchange);

        final Long jobId = BeanstalkExchangeHelper.getJobID(exchange);
        final boolean result = client.delete(jobId.longValue());
        if (!result && LOG.isWarnEnabled())
            LOG.warn(String.format("Failed to delete job %d", jobId));
        else if (LOG.isDebugEnabled())
            LOG.debug(String.format("Job %d deleted. Result is %b", jobId, result));

        answerWith(exchange, Headers.RESULT, result);
    }
}