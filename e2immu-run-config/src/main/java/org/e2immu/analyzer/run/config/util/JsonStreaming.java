package org.e2immu.analyzer.run.config.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.e2immu.language.cst.api.element.SourceSet;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.api.resource.MD5FingerPrint;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.e2immu.language.inspection.resource.SourceSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JsonStreaming {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonStreaming.class);

    public static ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule("CustomModel", Version.unknownVersion());

        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(SourceSet.class, SourceSetImpl.class);
        resolver.addMapping(InputConfiguration.class, InputConfigurationImpl.class);
        module.setAbstractTypes(resolver);

        // only because we want to get the order straight: a correct linearization of the dependencies between the
        // source sets
        module.addSerializer(new InputConfigurationSerializer(InputConfigurationImpl.class));
        module.addSerializer(new SourceSetSerializer(SourceSetImpl.class));
        module.addDeserializer(SourceSetImpl.class, new SourceSetDeserializer(SourceSetImpl.class));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

    static class SourceSetDeserializer extends StdDeserializer<SourceSetImpl> {

        public SourceSetDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public SourceSetImpl deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode node = jp.getCodec().readTree(jp);
            String name = node.get("name").asText();
            String sourceDirectoryString = node.get("sourceDirectory").asText("");
            Path sourceDirectory = sourceDirectoryString.isBlank() ? null : Path.of(sourceDirectoryString);
            String uriString = node.get("uri").asText("");
            URI uri = uriString.isBlank() ? null : URI.create(uriString);
            String sourceEncodingString = node.get("sourceEncoding").asText("");
            Charset sourceEncoding = sourceEncodingString.isBlank() ? StandardCharsets.UTF_8 :
                    Charset.forName(sourceEncodingString);
            boolean test = node.get("test").asBoolean(false);
            boolean library = node.get("library").asBoolean(false);
            boolean externalLibrary = node.get("externalLibrary").asBoolean(false);
            boolean partOfJdk = node.get("partOfJdk").asBoolean(false);
            boolean runtimeOnly = node.get("runtimeOnly").asBoolean(false);
            Set<String> restrictToPackages = new HashSet<>();
            Iterator<JsonNode> restrictIterator = node.get("restrictToPackages").elements();
            while (restrictIterator.hasNext()) {
                restrictToPackages.add(restrictIterator.next().asText());
            }
            Set<SourceSet> dependencies = new HashSet<>();
            Iterator<JsonNode> iterator = node.get("dependencies").elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                String key = subNode.asText();
                SourceSet dependency = (SourceSet) ctxt.getAttribute(key);
                if (dependency != null) {
                    dependencies.add(dependency);
                } else {
                    LOGGER.warn("dependency named '{}' unknown", key);
                }
            }
            SourceSetImpl ssi = new SourceSetImpl(name, sourceDirectory, uri, sourceEncoding, test, library, externalLibrary,
                    partOfJdk, runtimeOnly, Set.copyOf(restrictToPackages), Set.copyOf(dependencies));
            String fingerPrintToString = node.get("fingerPrint").asText("");
            if (!fingerPrintToString.isBlank()) {
                ssi.setFingerPrint(MD5FingerPrint.from(fingerPrintToString));
            }
            String analysisFingerPrintToString = node.get("analysisFingerPrint").asText("");
            if (!analysisFingerPrintToString.isBlank()) {
                ssi.setAnalysisFingerPrint(MD5FingerPrint.from(analysisFingerPrintToString));
            }
            ctxt.setAttribute(name, ssi);

            return ssi;
        }
    }

    static class SourceSetSerializer extends StdSerializer<SourceSetImpl> {

        public SourceSetSerializer(Class<SourceSetImpl> t) {
            super(t);
        }

        @Override
        public void serialize(SourceSetImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("sourceEncoding", value.sourceEncoding() == null ? null
                    : value.sourceEncoding().name());
            gen.writeStringField("name", value.name());
            gen.writeStringField("sourceDirectory", value.sourceDirectory() == null ? null
                    : value.sourceDirectory().toString());
            gen.writeStringField("uri", value.uri().toString());
            gen.writeBooleanField("test", value.test());
            gen.writeBooleanField("library", value.library());
            gen.writeBooleanField("externalLibrary", value.externalLibrary());
            gen.writeBooleanField("partOfJdk", value.partOfJdk());
            gen.writeBooleanField("runtimeOnly", value.runtimeOnly());
            gen.writeArrayFieldStart("restrictToPackages");
            for (String pkg : value.restrictToPackages()) gen.writeString(pkg);
            gen.writeEndArray();
            gen.writeArrayFieldStart("dependencies");
            for (SourceSet d : value.dependencies()) gen.writeString(d.name());
            gen.writeEndArray();
            gen.writeStringField("fingerPrint", value.fingerPrintOrNull() == null ? null
                    : value.fingerPrintOrNull().toString());
            gen.writeStringField("analysisFingerPrint", value.analysisFingerPrintOrNull() == null ? null
                    : value.analysisFingerPrintOrNull().toString());
            gen.writeEndObject();
        }
    }

    static class InputConfigurationSerializer extends StdSerializer<InputConfigurationImpl> {
        public InputConfigurationSerializer(Class<InputConfigurationImpl> t) {
            super(t);
        }

        @Override
        public void serialize(InputConfigurationImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeArrayFieldStart("classPathParts");
            for (SourceSet cpp : value.classPathParts()) {
                gen.writeObject(cpp);
            }
            gen.writeEndArray();
            gen.writeArrayFieldStart("sourceSets");
            for (SourceSet cpp : value.sourceSets()) {
                gen.writeObject(cpp);
            }
            gen.writeEndArray();
            gen.writeStringField("alternativeJREDirectory", value.alternativeJREDirectory() == null ? null
                    : value.alternativeJREDirectory().toString());
            gen.writeEndObject();
        }
    }
}
