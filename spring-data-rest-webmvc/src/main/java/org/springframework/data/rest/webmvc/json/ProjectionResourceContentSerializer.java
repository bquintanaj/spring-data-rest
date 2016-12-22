package org.springframework.data.rest.webmvc.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.io.IOException;

/**
 * Serializer that will delegate serializing projection to
 * serializer for {@link ProjectionResourceContent#getProjectionType() projection type}.
 *
 * @author Oliver Gierke
 * @author Anton Koscejev
 * @author Juan Manuel de Blas
 */
@SuppressWarnings("serial")
class ProjectionResourceContentSerializer extends StdSerializer<ProjectionResourceContent> {

	private final boolean unwrapping;
	private final NameTransformer unwrapper;

	ProjectionResourceContentSerializer() {
		this(false, null);
	}

	ProjectionResourceContentSerializer(boolean unwrapping) {
		this(unwrapping, null);
	}

	ProjectionResourceContentSerializer(boolean unwrapping, NameTransformer unwrapper) {

		super(ProjectionResourceContent.class);
		this.unwrapping = unwrapping;
		this.unwrapper = unwrapper;
	}

	@Override
	public void serialize(ProjectionResourceContent value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {

		getContentSerializer(value, serializers)
						.serialize(value.getProjection(), gen, serializers);
	}

	@Override
	public void serializeWithType(ProjectionResourceContent value, JsonGenerator gen,
																SerializerProvider serializers, TypeSerializer typeSer)
					throws IOException {

		getContentSerializer(value, serializers)
						.serializeWithType(value.getProjection(), gen, serializers, typeSer);
	}

	private JsonSerializer<Object> getContentSerializer(
					ProjectionResourceContent value, SerializerProvider serializers) throws JsonMappingException {

		JsonSerializer<Object> serializer = serializers.findValueSerializer(value.getProjectionType(), null);
		if (unwrapping) {
			return serializer.unwrappingSerializer(unwrapper);
		}
		return serializer;
	}

	@Override
	public boolean isUnwrappingSerializer() {
		return unwrapping;
	}

	@Override
	public JsonSerializer<ProjectionResourceContent> unwrappingSerializer(NameTransformer unwrapper) {
		return new ProjectionResourceContentSerializer(true, unwrapper);
	}
}
