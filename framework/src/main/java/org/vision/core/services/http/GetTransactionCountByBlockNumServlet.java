package org.vision.core.services.http;

import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vision.api.GrpcAPI.NumberMessage;
import org.vision.core.Wallet;


@Component
@Slf4j(topic = "API")
public class GetTransactionCountByBlockNumServlet extends RateLimiterServlet {

  @Autowired
  private Wallet wallet;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      long num = Long.parseLong(request.getParameter("num"));
      fillResponse(num, response);
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String input = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(input);
      boolean visible = Util.getVisiblePost(input);
      NumberMessage.Builder build = NumberMessage.newBuilder();
      JsonFormat.merge(input, build, visible);
      fillResponse(build.getNum(), response);
    } catch (Exception e) {
      Util.processError(e, response);
    }
  }

  private void fillResponse(long num, HttpServletResponse response) throws IOException {
    long count = wallet.getTransactionCountByBlockNum(num);
    response.getWriter().println("{\"count\": " + count + "}");
  }
}