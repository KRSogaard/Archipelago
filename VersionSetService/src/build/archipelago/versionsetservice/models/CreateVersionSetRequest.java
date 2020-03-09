package build.archipelago.versionsetservice.models;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateVersionSetRequest {
    private String name;
    private List<String> targets;
    private Optional<String> parent;

    public void validate() throws IllegalArgumentException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        if (parent.isPresent()) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(parent.get()), "Parent is required");
        }
    }
}
