package org.veupathdb.service.demo.container.middleware;

import com.devskiller.friendly_id.FriendlyId;
import org.apache.logging.log4j.ThreadContext;
import org.veupathdb.service.demo.container.Globals;
import org.veupathdb.service.demo.container.utils.RequestKeys;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.UUID;

/**
 * Assigns a unique ID to each request for logging, error tracing purposes.
 */
@Provider
@Priority(0)
@PreMatching
public class RequestIdFilter implements ContainerRequestFilter,
  ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext req) {
    var id = FriendlyId.createFriendlyId();
    req.setProperty(RequestKeys.REQUEST_ID, id);
    ThreadContext.put(Globals.CONTEXT_ID, id);
  }

  @Override
  public void filter(ContainerRequestContext req, ContainerResponseContext res) {
    ThreadContext.remove((String) req.getProperty(
      Globals.CONTEXT_ID));
  }
}
