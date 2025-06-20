package com.idrsys.toyprojectbackend.dto.webrtc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    private String type;
    private String roomId;
    private String userId;
    private Map<String, Object> data;
    
    @JsonProperty("isBroadcaster")
    private Boolean isBroadcaster;
}
