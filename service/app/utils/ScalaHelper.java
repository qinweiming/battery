package utils;

import scala.collection.JavaConverters$;


/**
 * Created by xiaoyong on 21/10/15.
 */
public class ScalaHelper {
    private ScalaHelper(){}
     public static <K, V> scala.collection.immutable.Map<K, V> convert(java.util.Map<K, V> m) {
        return JavaConverters$.MODULE$.mapAsScalaMapConverter(m).asScala().toMap(
                scala.Predef$.MODULE$.<scala.Tuple2<K, V>>conforms()
        );
    }
    public static <K> scala.collection.Set<K> convert(java.util.Set<K> m) {
        return JavaConverters$.MODULE$.asScalaSetConverter(m).asScala().toSet();
    }
//    public static <K> scala.collection.Seq<K> convert(java.util.List<K> m) {
//        return JavaConverters$.MODULE$.asScalaBufferConverter(m).asScala().toSeq();
//    }
}
