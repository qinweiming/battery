package utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import models.Customer;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.regression.LabeledPoint;

/**
 * @author <a href="mailto:yongxiaozhao@gmail.com">zhaoxiaoyong</a>
 * @version Revision: 1.0
 *          date 2016/4/26 15:11
 */
public class LabeledPointBuilder {
    public static LabeledPoint of(Customer customer){
         return new LabeledPoint(customer.getRating(),
                 new DenseVector(new double[]{customer.getCapital(),Nominal(customer.getCategory())
         ,customer.getRegisterMonths()}));
    }

    //标称类型 ，标称属性具有有穷多个不同值（但可能很多），值之间无序。
    public static double  Nominal(String val){
        //todo: use OneHotEncoder to instead
        return   Math.log10(Math.abs(Hashing.md5().hashString(val, Charsets.UTF_8).asInt()));
    }

}
