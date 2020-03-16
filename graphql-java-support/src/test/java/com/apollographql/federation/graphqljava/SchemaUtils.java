package com.apollographql.federation.graphqljava;

import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static graphql.ExecutionInput.newExecutionInput;
import static graphql.GraphQL.newGraphQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class SchemaUtils {
    static final Set<String> standardDirectives =
            new HashSet<>(Arrays.asList("deprecated", "include", "skip"));

    private SchemaUtils() {
    }

    static String printWithoutStandardDirectiveDefinitions(GraphQLSchema schema) {
        return new FederationSdlPrinter(FederationSdlPrinter.Options.defaultOptions()
                .includeDirectives(directive -> !standardDirectives.contains(directive.getName()))
        ).print(schema);
    }

    static ExecutionResult execute(GraphQLSchema schema, String query) {
        return newGraphQL(schema).build().execute(newExecutionInput().query(query).build());
    }

    static void assertSDL(GraphQLSchema schema, String expected) {
        final ExecutionResult inspect = execute(schema, "{_service{sdl}}");
        assertEquals(0, inspect.getErrors().size(), "No errors");
        final Map<String, Object> data = inspect.getData();
        assertNotNull(data);
        @SuppressWarnings("unchecked") final Map<String, Object> _service = (Map<String, Object>) data.get("_service");
        assertNotNull(_service);
        final String sdl = (String) _service.get("sdl");
        assertEquals(expected.trim(), sdl.trim());
    }
}
