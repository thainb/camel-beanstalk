/**
 * Copyright (C) 2010 Alexander Azarov <azarov@osinka.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osinka.camel.beanstalk.processors;

import com.osinka.camel.beanstalk.BeanstalkEndpoint;
import com.osinka.camel.beanstalk.BeanstalkExchangeHelper;
import com.osinka.camel.beanstalk.Headers;
import com.surftools.BeanstalkClient.Client;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.NoSuchHeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutCommand extends DefaultCommand {
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    public PutCommand(BeanstalkEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void act(final Client client, final Exchange exchange) throws NoSuchHeaderException {
        final Message in = exchange.getIn();

        final long priority = BeanstalkExchangeHelper.getPriority(endpoint, in);
        final int delay = BeanstalkExchangeHelper.getDelay(endpoint, in);
        final int timeToRun = BeanstalkExchangeHelper.getTimeToRun(endpoint, in);

        final long jobId = client.put(priority, delay, timeToRun, in.getBody(byte[].class));
        if (log.isDebugEnabled())
            log.debug(String.format("Created job %d with priority %d, delay %d seconds and time to run %d", jobId, priority, delay, timeToRun));

        answerWith(exchange, Headers.JOB_ID, jobId);
    }
}
