package authservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRecoveryAccountEvent {
    private String eventId;
    private String userId;
    private String email;
    private String username;
}
