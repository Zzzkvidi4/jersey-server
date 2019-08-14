package com.zzzkvidi4.server;

import com.zzzkvidi4.server.api.ProductAPI;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.QoSFilter;
import org.eclipse.jetty.util.security.Constraint;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public final class Starter {
    public static void main(@NotNull String[] args) throws Exception {
        final Server server = new Server();
        final HttpConfiguration httpConfig = new HttpConfiguration();
        final HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
        final ServerConnector serverConnector = new ServerConnector(server, httpConnectionFactory);
        serverConnector.setHost("localhost");
        serverConnector.setPort(8080);
        server.setConnectors(new Connector[]{serverConnector});

        ServletContextHandler defaultContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        final ServletHolder servletHolder = defaultContext.addServlet(ServletContainer.class, "/*");
        final Map<String, String> param = new HashMap<>();
        param.put(ServletProperties.JAXRS_APPLICATION_CLASS, Configuration.class.getName());
        param.put(ServerProperties.PROVIDER_PACKAGES, ProductAPI.class.getPackage().getName());
        param.put(ServerProperties.PROVIDER_CLASSNAMES, JacksonFeature.class.getName());
        servletHolder.setInitParameters(param);

        configureFilters(defaultContext);
        configureSecurity(server, defaultContext);

        server.start();
    }

    private static void configureFilters(@NotNull ServletContextHandler defaultContext) {
        FilterHolder filterHolder = new FilterHolder(new QoSFilter() {
            @Override
            public void doFilter(@NotNull ServletRequest request, @NotNull ServletResponse response, @NotNull FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
                    super.doFilter(request, response, chain);
                } else {
                    chain.doFilter(request, response);
                }
            }
        });
        filterHolder.setInitParameter("maxRequests", "1");
        defaultContext.addFilter(filterHolder, "/product", EnumSet.of(DispatcherType.REQUEST));
    }

    private static void configureSecurity(@NotNull Server server, @NotNull ServletContextHandler defaultContext) throws IOException {
        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        LoginService loginService = new JDBCLoginService(
                "LoginService",
                "src/main/resources/login-service.config"
        );
        securityHandler.setLoginService(loginService);
        securityHandler.setHandler(defaultContext);
        server.setHandler(securityHandler);
        server.addBean(loginService);
        Authenticator authenticator = new BasicAuthenticator();
        securityHandler.setAuthenticator(authenticator);
        securityHandler.setConstraintMappings(
                asList(
                        createConstraintMapping(
                                "productGet",
                                asList(Role.GUEST, Role.MANAGER),
                                "/product",
                                "GET"
                        ),
                        createConstraintMapping(
                                "productPost",
                                singletonList(Role.MANAGER),
                                "/product",
                                "*"
                        )
                )
        );
    }

    @NotNull
    private static ConstraintMapping createConstraintMapping(@NotNull String constraintName, @NotNull List<Role> roles, @NotNull String path, @Nullable String methodName) {
        Constraint constraint = new Constraint();
        constraint.setName(constraintName);
        constraint.setAuthenticate(true);
        constraint.setRoles(roles.stream().map(Enum::name).toArray(String[]::new));
        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setPathSpec(path);
        if (methodName != null) {
            constraintMapping.setMethod(methodName);
        }
        constraintMapping.setConstraint(constraint);
        return constraintMapping;
    }
}
