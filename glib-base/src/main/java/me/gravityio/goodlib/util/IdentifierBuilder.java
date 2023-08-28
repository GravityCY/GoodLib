package me.gravityio.goodlib.util;

import net.minecraft.util.Identifier;

public class IdentifierBuilder {

  private final String namespace;
  private final String key;

  public IdentifierBuilder(String namespace) {
    this(namespace, "");
  }

  public IdentifierBuilder(String namespace, String key) {
    this.namespace = namespace;
    this.key = key;
  }

  public Identifier build(String key) {
    return new Identifier(this.namespace, key);
  }

  public Identifier buildAdd(String key) {
    return new Identifier(this.namespace, this.key + key);
  }


}
