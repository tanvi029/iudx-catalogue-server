package iudx.catalogue.server.apiserver.Item.model;

import static iudx.catalogue.server.util.Constants.UUID_PATTERN;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public class ResourceGroup implements Item {

  private static final String NAME_REGEX = "^[a-zA-Z0-9]([\\w-]*[a-zA-Z0-9 ])?$";
  private static final String PROVIDER_REGEX =
      "^[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}$";

  @NotEmpty(message = "Provider cannot be empty")
  @Pattern(regexp = PROVIDER_REGEX, message = "Invalid provider format")
  private String provider;
  private UUID id;
  @NotEmpty(message = "Type cannot be empty")
  private List<String> type;
  @NotEmpty(message = "Name cannot be empty")
  @Pattern(regexp = NAME_REGEX, message = "Invalid name format")
  private String name;
  private String description;
  @NotEmpty(message = "Type cannot be empty")
  private List<String> tags;
  private String context;
  private String itemStatus;
  private String itemCreatedAt;


  public ResourceGroup(JsonObject json) {
    this.context = json.getString("@context");
    this.id = UUID.fromString(json.getString("id", UUID.randomUUID().toString()));
    this.type = json.getJsonArray("type", new JsonArray().add("iudx:ResourceGroup")).getList();
    this.name = json.getString("name");
    this.description = json.getString("description");
    this.tags = json.getJsonArray("tags", new JsonArray()).getList();
    this.provider = json.getString("provider");
    this.itemStatus = json.getString("itemStatus");
    this.itemCreatedAt = json.getString("itemCreatedAt");
    validateFields();
  }

  private void validateFields() {
    if (id == null || !UUID_PATTERN.matcher(id.toString()).matches()) {
      throw new IllegalArgumentException("Invalid ID format");
    }
    if (name == null) {
      throw new IllegalArgumentException("[object has missing required properties ([\"name\"])])");
    }
    if (description == null) {
      throw new IllegalArgumentException(
          "[object has missing required properties ([\"description\"])])");
    }
    if (!name.matches(NAME_REGEX)) {
      throw new IllegalArgumentException(String.format(
          "[ECMA 262 regex \"%s\" does not match input string \"%s\"]",
          NAME_REGEX, name
      ));
    }
    if (provider == null){
      throw new IllegalArgumentException(
          "[object has missing required properties ([\"provider\"])])");
    }
    if (!provider.matches(PROVIDER_REGEX)) {
      throw new IllegalArgumentException(String.format(
          "[ECMA 262 regex \"%s\" does not match input string \"%s\"]",
          PROVIDER_REGEX, provider
      ));
    }
    if (tags == null) {
      throw new IllegalArgumentException("[object has missing required properties ([\"tags\"])])");
    }
  }


  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public List<String> getType() {
    return type;
  }

  @Override
  public void setType(List<String> type) {
    this.type = type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  @Override
  public String getContext() {
    return context;
  }

  @Override
  public void setContext(String context) {
    this.context = context;
  }

  @Override
  public String getItemStatus() {
    return itemStatus;
  }

  @Override
  public void setItemStatus(String itemStatus) {
    this.itemStatus = itemStatus;
  }

  @Override
  public String getItemCreatedAt() {
    return itemCreatedAt;
  }

  @Override
  public void setItemCreatedAt(String itemCreatedAt) {
    this.itemCreatedAt = itemCreatedAt;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("@context", context);
    json.put("id", id.toString());
    json.put("type", new JsonArray(type));
    json.put("name", name);
    json.put("description", description);
    json.put("tags", new JsonArray(tags));
    json.put("provider", provider);
    json.put("itemStatus", itemStatus);
    json.put("itemCreatedAt", itemCreatedAt);
    return json;
  }

}