/*
 * Copyright 2018-2024 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.repros.liquibaseviewderby;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.repros.liquibaseviewderby.HelloServlet;

class HelloServletTest {

    @Test
    void testDoGet() throws ServletException, IOException {
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/hello");
        var response = new MockHttpServletResponse();

        var servlet = new HelloServlet();
        servlet.setLogservice(logservice);

        servlet.doGet(request, response);

        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        assertThat(response.getOutputStreamContent()).isNotEmpty();
    }

    @Test
    void testDoGetWithError() throws ServletException, IOException {
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/hello");
        var response = spy(new MockHttpServletResponse());
        var writer = mock(PrintWriter.class);
        doThrow(RuntimeException.class).when(writer).print(anyString());
        when(response.getWriter()).thenReturn(writer);

        var servlet = new HelloServlet();
        servlet.setLogservice(logservice);

        servlet.doGet(request, response);

        assertEquals(500, response.getStatus());
        assertThat(response.getOutputStreamContent()).isEmpty();
    }
}
