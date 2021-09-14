/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package oem.edge.common.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
   A factory that makes new {@link EdgeCategory} objects.

   See <b><a href="EdgeCategoryFactory.java">source
   code</a></b> for more details.

 */
public class EdgeCategoryFactory implements LoggerFactory {
  /**
     The constructor should be public as it will be called by
     configurators in different packages.  */
  public
  EdgeCategoryFactory() {
  }

  public
  Logger makeNewLoggerInstance(String name) {
    return new EdgeCategory(name);
  }
}
