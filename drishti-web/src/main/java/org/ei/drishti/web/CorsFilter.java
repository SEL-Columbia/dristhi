package org.ei.drishti.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getHeader("Access-Control-Request-Method") != null) {
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:9000");
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            Enumeration requestHeaderNames = request.getHeaderNames();
            List<String> allowedHeaders = new ArrayList<>();
            while (requestHeaderNames.hasMoreElements()) {
                String header = (String) requestHeaderNames.nextElement();
                allowedHeaders.add(header);
            }
            response.addHeader("Access-Control-Allow-Headers", StringUtils.join(allowedHeaders, ", "));
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Max-Age", "1");// 30 min
        }
        filterChain.doFilter(request, response);
    }
}
