package com.richcoder.mybatis.config;

import java.util.Set;

/**
 * @author richcoder
 */
public class PackageConfigTypes {

  /**
   * 生成文件类型
   */
  private String type;

  /**
   * 包类型配置
   */
  private Set<PackageConfigType> packageConfigTypeSet;

  public PackageConfigTypes() {
  }

  public PackageConfigTypes(String type, Set<PackageConfigType> packageConfigTypeSet) {
    this.type = type;
    this.packageConfigTypeSet = packageConfigTypeSet;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Set<PackageConfigType> getPackageConfigTypeSet() {
    return packageConfigTypeSet;
  }

  public void setPackageConfigTypeSet(Set<PackageConfigType> packageConfigTypeSet) {
    this.packageConfigTypeSet = packageConfigTypeSet;
  }


}
