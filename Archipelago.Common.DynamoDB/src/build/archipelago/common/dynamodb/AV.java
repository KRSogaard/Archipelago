package build.archipelago.common.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.time.Instant;
import java.util.List;

public class AV {
    public static AttributeValue of(String v) {
        return new AttributeValue(v);
    }
    public static AttributeValue of(int v) {
        return new AttributeValue().withN(String.valueOf(v));
    }
    public static AttributeValue of(Long v) {
        return new AttributeValue().withN(String.valueOf(v));
    }
    public static AttributeValue of(Instant v) {
        return new AttributeValue().withN(String.valueOf(v.toEpochMilli()));
    }
    public static AttributeValue of(List<String> v) {
        return new AttributeValue().withSS(v);
    }

    public static Instant toInstant(AttributeValue av) {
        return Instant.ofEpochMilli(Long.parseLong(av.getN()));
    }
}
