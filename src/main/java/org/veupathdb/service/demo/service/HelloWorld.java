package org.veupathdb.service.demo.service;

import jakarta.ws.rs.core.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.lib.container.jaxrs.model.UserInfo;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import org.veupathdb.lib.container.jaxrs.server.annotations.AdminRequired;
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import org.veupathdb.service.demo.generated.model.*;
import org.veupathdb.service.demo.generated.model.HelloResponse.GreetingType;
import org.veupathdb.service.demo.generated.resources.Hello;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class HelloWorld implements Hello {

  private static final Logger LOG = LogManager.getLogger(HelloWorld.class);

  @Context
  private ContainerRequest req;

  @Override
  public GetHelloResponse getHello() {
    var out = new HelloResponseImpl();
    out.setGreeting(GreetingType.HELLOWORLD);
    return GetHelloResponse.respond200WithApplicationJson(out);
  }

  @Override
  @Authenticated
  public PostHelloResponse postHello(HelloPostRequest entity) {
    return handleHello(entity,
        PostHelloResponse::respond200WithApplicationJson,
        PostHelloResponse::respond500WithApplicationJson);
  }

  @Override
  @Authenticated(allowGuests = true)
  public PostHelloAllowGuestsResponse postHelloAllowGuests(HelloPostRequest entity) {
    return handleHello(entity,
        PostHelloAllowGuestsResponse::respond200WithApplicationJson,
        PostHelloAllowGuestsResponse::respond500WithApplicationJson);
  }

  @Override
  @Authenticated(adminOverride = Authenticated.AdminOverrideOption.ALLOW_ALWAYS)
  public PostHelloUserOverrideResponse postHelloUserOverride(HelloPostRequest entity) {
    return handleHello(entity,
        PostHelloUserOverrideResponse::respond200WithApplicationJson,
        PostHelloUserOverrideResponse::respond500WithApplicationJson);
  }

  private <T> T handleHello(HelloPostRequest entity,
    Function<HelloPostResponse,T> successResponse, Function<ServerError,T> errorResponse) {

    // find the person to be greeted
    String target = entity.getGreet();
    if (target == null) {
      Long userId = entity.getUserId();
      target = userId == null ? "World" :
          Optional.ofNullable(UserProvider.getUsersById(List.of(userId)).get(userId))
            .map(UserInfo::getFirstName).orElse("Unknown User");
    }

    // find the sender (authenticated user or God)
    String sender = UserProvider.lookupUser(req).map(UserInfo::getFirstName).orElse("God");

    // demonstrate how to handle unknown request property types
    Object config = entity.getConfig();
    if (config != null) {
      LOG.info("config object is " + config.getClass().getName());
      if (config instanceof List<?> list) {
        for (int i = 0; i < list.size(); i++) {
          LOG.info("config[" + i + "] is " + list.get(i).getClass().getName());
        }
      } else if (config instanceof Map<?, ?> map) {
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
          LOG.info("config entry " + i + ": key is " + entry.getKey().getClass().getName() + ", value is " + entry.getValue().getClass().getName());
          i++;
        }
      }
    }

    // Throw a 500 every once in a while for fun.
    var rand = new Random();
    if (rand.nextInt(4) == 2) {
      var out = new ServerErrorImpl();
      out.setMessage("Whoops!  Occasionally we throw an error for fun.  Please try again.");
      return errorResponse.apply(out);
    }

    var out = new HelloPostResponseImpl();
    out.setMessage(String.format("Hello %s from %s!", target, sender));
    return successResponse.apply(out);

  }

  @Override
  @AdminRequired
  public GetHelloAdminOnlyResponse getHelloAdminOnly() {
    return GetHelloAdminOnlyResponse.respond204();
  }
}
