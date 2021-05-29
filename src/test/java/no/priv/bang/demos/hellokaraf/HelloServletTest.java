/*
 * Copyright 2018-2021 Steinar Bang
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
package no.priv.bang.demos.hellokaraf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.demos.hellokaraf.mocks.MockLogService;

public class HelloServletTest {

    @Test
    public void testDoGet() throws ServletException, IOException {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/hello");
        MockHttpServletResponse response = new MockHttpServletResponse();

        HelloServlet servlet = new HelloServlet();
        servlet.setLogservice(logservice);

        servlet.doGet(request, response);

        assertEquals("text/html", response.getContentType());
        assertEquals(200, response.getStatus());
        assertThat(response.getOutputStreamContent()).isNotEmpty();
    }

    @Test
    public void testDoGetWithError() throws ServletException, IOException {
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("http://localhost:8181/hello");
        MockHttpServletResponse response = spy(new MockHttpServletResponse());
        PrintWriter writer = mock(PrintWriter.class);
        doThrow(RuntimeException.class).when(writer).print(anyString());
        when(response.getWriter()).thenReturn(writer);

        HelloServlet servlet = new HelloServlet();
        servlet.setLogservice(logservice);

        servlet.doGet(request, response);

        assertEquals(500, response.getStatus());
        assertThat(response.getOutputStreamContent()).isEmpty();
    }
}
