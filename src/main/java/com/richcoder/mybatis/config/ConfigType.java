package com.richcoder.mybatis.config;

public enum ConfigType {
  ENTITY("entity"),
  RES("res"),

  MAPPER("mapper"),

  MAPPER_CONFIG("mapperConfig"),

  RESULT("result"),

  SERVICE("service"),

  CONTROLLER("controller");

  public String key;

  ConfigType(String key) {
    this.key = key;
  }
}