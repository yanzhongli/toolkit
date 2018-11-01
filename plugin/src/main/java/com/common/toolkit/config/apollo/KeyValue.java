package com.common.toolkit.config.apollo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ewen
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<V> {

  private String key;

  private V value;

}
