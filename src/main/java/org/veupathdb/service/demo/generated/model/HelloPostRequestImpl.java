package org.veupathdb.service.demo.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "greet",
    "userId",
    "config"
})
public class HelloPostRequestImpl implements HelloPostRequest {
  @JsonProperty("greet")
  private String greet;

  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("config")
  private Object config;

  @JsonProperty("greet")
  public String getGreet() {
    return this.greet;
  }

  @JsonProperty("greet")
  public void setGreet(String greet) {
    this.greet = greet;
  }

  @JsonProperty("userId")
  public Long getUserId() {
    return this.userId;
  }

  @JsonProperty("userId")
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @JsonProperty("config")
  public Object getConfig() {
    return this.config;
  }

  @JsonProperty("config")
  public void setConfig(Object config) {
    this.config = config;
  }
}
