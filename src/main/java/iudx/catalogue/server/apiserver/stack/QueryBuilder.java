package iudx.catalogue.server.apiserver.stack;

import static iudx.catalogue.server.apiserver.stack.StackConstants.CHECK_STACK_EXISTANCE_QUERY;
import static iudx.catalogue.server.apiserver.stack.StackConstants.CLOSED_QUERY;
import static iudx.catalogue.server.apiserver.stack.StackConstants.PATCH_QUERY;
import static iudx.catalogue.server.apiserver.stack.StackConstants.TITLE;
import static iudx.catalogue.server.apiserver.stack.StackConstants.TYPE;
import static iudx.catalogue.server.database.elastic.util.Constants.ID_KEYWORD;
import static iudx.catalogue.server.util.Constants.FIELD;
import static iudx.catalogue.server.util.Constants.VALUE;

import co.elastic.clients.json.JsonData;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import iudx.catalogue.server.database.elastic.model.QueryModel;
import iudx.catalogue.server.database.elastic.util.QueryType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder {

  QueryModel getQuery(String stackId) {
    QueryModel queryModel = new QueryModel(QueryType.BOOL);
    queryModel.addMustQuery(
        new QueryModel(QueryType.MATCH, Map.of(FIELD, ID_KEYWORD, VALUE, stackId)));
    return queryModel;
  }

  String getPatchQuery(JsonObject request) {
    StringBuilder query =
        new StringBuilder(
            PATCH_QUERY
                .replace("$1", request.getString("rel"))
                .replace("$2", request.getString("href")));
    if (request.containsKey("type")) {
      StringBuilder type = new StringBuilder(TYPE.replace("$1", request.getString("type")));
      query.append(',');
      query.append(type);
    }
    if (request.containsKey("title")) {
      StringBuilder title = new StringBuilder(TITLE.replace("$1", request.getString("title")));
      query.append(',');
      query.append(title);
    }
    query.append(CLOSED_QUERY);
    return query.toString();
  }

  QueryModel getQuery4CheckExistence(JsonObject request) {
    JsonObject json = getHref(request.getJsonArray("links"));
    StringBuilder query =
        new StringBuilder(
            CHECK_STACK_EXISTANCE_QUERY
                .replace("$1", json.getString("self"))
                .replace("$2", json.getString("root")));

    QueryModel selfQueryModel = new QueryModel(QueryType.BOOL);
    selfQueryModel.addMustQuery(
        new QueryModel(QueryType.MATCH, Map.of(FIELD, "links.rel.keyword", VALUE, "self")));
    selfQueryModel.addMustQuery(new QueryModel(QueryType.MATCH,
        Map.of(FIELD, "links.href.keyword", VALUE, json.getString("self"))));

    QueryModel rootQueryModel = new QueryModel(QueryType.BOOL);
    rootQueryModel.addMustQuery(
        new QueryModel(QueryType.MATCH, Map.of(FIELD, "links.rel.keyword", VALUE, "root")));
    rootQueryModel.addMustQuery(new QueryModel(QueryType.MATCH,
        Map.of(FIELD, "links.href.keyword", VALUE, json.getString("root"))));

    QueryModel queryModel = new QueryModel(QueryType.BOOL);
    queryModel.setShouldQueries(List.of(selfQueryModel, rootQueryModel));

    return queryModel;
  }

  private JsonObject getHref(JsonArray links) {
    JsonObject json = new JsonObject();
    links.stream()
        .map(JsonObject.class::cast)
        .forEach(
            child -> {
              if (child.getString("rel").equalsIgnoreCase("self")) {
                json.put("self", child.getString("href"));
              }
              if (child.getString("rel").equalsIgnoreCase("root")) {
                json.put("root", child.getString("href"));
              }
            });

    return json;
  }
}
