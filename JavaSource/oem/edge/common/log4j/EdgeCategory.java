package oem.edge.common.log4j;

import org.apache.log4j.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

/**
   subclass from category 

 */
public class EdgeCategory extends Logger {

  // It's enough to instantiate a factory once and for all.
  private static EdgeCategoryFactory edgeFactory = new EdgeCategoryFactory();

  /**
     Just calls the parent constuctor.
   */
  public EdgeCategory(String name) {
    super(name);
  }


  /**
     This method overrides {@link Category#getInstance} by supplying
     its own factory type as a parameter.
  */

  public 
  static
  Category getInstance(String name) {
    return Logger.getLogger(name,edgeFactory); 
  }


}


